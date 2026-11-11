#!/bin/sh
set -eu
umask 077
renew_before=2592000
ca_warning_before=15552000
mode=${1:-once}

certificate_health() {
  [ -s /private/certificate-renewal.ok ] || return 1
  last=$(cat /private/certificate-renewal.ok)
  case "$last" in ''|*[!0-9]*) return 1 ;; esac
  [ $(( $(date +%s) - last )) -lt 90000 ] || return 1
  openssl x509 -in /certs/ca.pem -noout -checkend "$ca_warning_before" >/dev/null || return 1
  openssl verify -CAfile /certs/ca.pem /certs/node.pem /private/admin.pem >/dev/null || return 1
  openssl x509 -in /certs/node.pem -noout -checkend "$renew_before" >/dev/null || return 1
  openssl x509 -in /private/admin.pem -noout -checkend "$renew_before" >/dev/null || return 1
}

if [ "$mode" = health ]; then
  certificate_health
  exit
fi
case "$mode" in once|watch) ;; *) echo "Unknown certificate command" >&2; exit 1 ;; esac

sign_certificate() {
  name=$1 subject=$2 extensions=$3 directory=$4
  key="$directory/$name-key.pem"
  if [ ! -s "$key" ]; then
    openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out "$key.tmp" 2>/dev/null || return 1
    if [ "$name" = node ]; then chown 1000:1000 "$key.tmp" || return 1; fi
    chmod 0600 "$key.tmp" || return 1
    mv "$key.tmp" "$key" || return 1
  fi
  # 续签时复用原私钥，热加载期间不存在证书和私钥更替不同步的问题。
  openssl req -new -key "$key" -subj "$subject" -out "/private/$name.csr" || return 1
  printf '%s
' "$extensions" > "/private/$name.ext"
  ca_end=$(openssl x509 -in /certs/ca.pem -noout -enddate -dateopt iso_8601 | cut -d= -f2 | sed 's/Z$//')
  ca_seconds=$(( $(date -u -d "$ca_end" +%s) - $(date +%s) ))
  days=$(( ca_seconds / 86400 - 1 ))
  if [ "$days" -gt 825 ]; then days=825; fi
  if [ "$days" -le 0 ]; then echo "CA expired or expires within one day. Restore or rotate the CA." >&2; return 1; fi
  openssl x509 -req -sha256 -days "$days" -in "/private/$name.csr"     -CA /certs/ca.pem -CAkey /private/ca-key.pem -CAserial /private/ca.srl -CAcreateserial     -extfile "/private/$name.ext" -out "$directory/$name.pem.tmp" || return 1
  openssl verify -CAfile /certs/ca.pem "$directory/$name.pem.tmp" >/dev/null || return 1
  chmod 0644 "$directory/$name.pem.tmp" || return 1
  if [ "$name" = admin ]; then chmod 0600 "$directory/$name.pem.tmp" || return 1; fi
  mv "$directory/$name.pem.tmp" "$directory/$name.pem" || return 1
  echo "Renewed $name certificate"
}

check_and_renew() {
  openssl verify -CAfile /certs/ca.pem /certs/ca.pem >/dev/null || return 1
  cert_public=$(openssl x509 -in /certs/ca.pem -pubkey -noout | openssl sha256)
  key_public=$(openssl pkey -in /private/ca-key.pem -pubout | openssl sha256) || return 1
  [ "$cert_public" = "$key_public" ] || { echo "CA certificate and signing key do not match" >&2; return 1; }
  for name in node admin; do
    directory=/certs
    subject=/CN=opensearch
    extensions="$(printf '%s
' 'subjectAltName=DNS:opensearch' 'extendedKeyUsage=serverAuth,clientAuth')"
    if [ "$name" = admin ]; then
      directory=/private
      subject=/CN=opensearch-security-admin
      extensions=extendedKeyUsage=clientAuth
    elif [ -n "$OPENSEARCH_PRIVATE_IP" ]; then
      extensions="$(printf '%s
' "subjectAltName=DNS:opensearch,IP:$OPENSEARCH_PRIVATE_IP" 'extendedKeyUsage=serverAuth,clientAuth')"
    fi
    if [ ! -s "$directory/$name.pem" ] || [ ! -s "$directory/$name-key.pem" ]       || ! openssl x509 -in "$directory/$name.pem" -noout -checkend "$renew_before" >/dev/null; then
      sign_certificate "$name" "$subject" "$extensions" "$directory" || return 1
    fi
    openssl verify -CAfile /certs/ca.pem "$directory/$name.pem" >/dev/null || return 1
    cert_public=$(openssl x509 -in "$directory/$name.pem" -pubkey -noout | openssl sha256)
    key_public=$(openssl pkey -in "$directory/$name-key.pem" -pubout | openssl sha256) || return 1
    [ "$cert_public" = "$key_public" ] || { echo "$name certificate and key do not match" >&2; return 1; }
  done
  if [ -n "$OPENSEARCH_PRIVATE_IP" ]; then
    openssl verify -CAfile /certs/ca.pem -verify_ip "$OPENSEARCH_PRIVATE_IP" /certs/node.pem >/dev/null || return 1
  fi
  chmod 0755 /certs || return 1
  chmod 0644 /certs/node.pem /certs/ca.pem || return 1
  chown 1000:1000 /certs/node-key.pem || return 1
  chmod 0600 /certs/node-key.pem || return 1
  date +%s > /private/certificate-renewal.ok || return 1
  if ! openssl x509 -in /certs/ca.pem -noout -checkend "$ca_warning_before" >/dev/null; then
    echo "CA expires within 180 days. Schedule CA rotation and update client trust." >&2
  fi
}

mkdir -p /private
chmod 0700 /private
# 初始化和后台续签共用文件锁，避免同时写入。
if [ "$mode" = once ]; then
  chown 1000:1000 /data
  chmod 0750 /data
  (
    flock -x 9
    if [ ! -s /certs/ca.pem ]; then
      if [ -s /private/ca-key.pem ]; then
        echo "CA certificate is missing. Restore ca.pem from backup before starting." >&2
        exit 1
      fi
      openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out /private/ca-key.pem 2>/dev/null
      openssl req -new -x509 -sha256 -days 3650 -key /private/ca-key.pem         -subj '/CN=OpenSearch Internal CA' -addext 'basicConstraints=critical,CA:TRUE'         -addext 'keyUsage=critical,keyCertSign,cRLSign' -out /certs/ca.pem.tmp
      chmod 0644 /certs/ca.pem.tmp
      mv /certs/ca.pem.tmp /certs/ca.pem
    fi
    check_and_renew
    install -m 0644 /certs/ca.pem /public-ca/ca.pem
  ) 9>/private/certificates.lock
  exit
fi

trap 'exit 0' TERM INT
while :; do
  interval=86400
  (
    flock -x 9
    # watch 模式持续用现有 CA 签发，CA 损坏时报告错误以便恢复。
    check_and_renew
  ) 9>/private/certificates.lock || {
    rm -f /private/certificate-renewal.ok
    echo "Certificate renewal failed; previous certificate files are retained." >&2
    interval=300
  }
  sleep "$interval" &
  wait $!
done
