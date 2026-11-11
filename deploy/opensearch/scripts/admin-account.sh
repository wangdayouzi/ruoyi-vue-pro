#!/bin/bash
set -euo pipefail
if [[ "${1:-}" == shared-pms-role ]]; then
  curl --fail-with-body --silent --show-error --max-time 30 \
    --cacert /ca.pem --cert /admin.pem --key /admin-key.pem \
    -H 'Content-Type: application/json' -X PUT \
    --data-binary '{"cluster_permissions":["cluster_composite_ops"],"index_permissions":[{"index_patterns":["pms-kb-v1-*","pms-test-kb-v1-*"],"allowed_actions":["indices_all"]}]}' \
    https://opensearch:9200/_plugins/_security/api/roles/pms_kb_index
  printf '\n'
  exit 0
fi
export LC_ALL=C
if [[ ! "$OPENSEARCH_ADMIN_PASSWORD" =~ ^[[:graph:]]{8,72}$ ]]; then
  echo "OPENSEARCH_ADMIN_PASSWORD must contain 8 to 72 printable ASCII characters without spaces." >&2
  exit 1
fi
hash=$(bash plugins/opensearch-security/tools/hash.sh -p "$OPENSEARCH_ADMIN_PASSWORD")
printf '{"hash":"%s","opendistro_security_roles":["all_access","kibana_user"]}' "$hash" |
  curl --fail-with-body --silent --show-error --max-time 30 \
    --cacert /ca.pem --cert /admin.pem --key /admin-key.pem \
    -H 'Content-Type: application/json' -X PUT --data-binary @- \
    https://opensearch:9200/_plugins/_security/api/internalusers/opensearch_admin
printf '\n'
