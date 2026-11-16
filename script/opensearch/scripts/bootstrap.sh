#!/bin/bash
set -euo pipefail
umask 077
export LC_ALL=C
password_names=(OPENSEARCH_PMS_PASSWORD OPENSEARCH_DASHBOARDS_PASSWORD)
for name in "${password_names[@]}"; do
  if [[ ! ${!name} =~ ^[[:graph:]]{8,72}$ ]]; then
    echo "$name must contain 8 to 72 printable ASCII characters without spaces" >&2
    exit 1
  fi
done
# 节点只在安全索引不存在时读取这些文件。已有账号及权限由安全索引保留。
cp config/opensearch-security/{action_groups,allowlist,audit,config,nodes_dn,tenants}.yml /security-config/
hash_password() {
  bash plugins/opensearch-security/tools/hash.sh -p "$1"
}
pms_hash=$(hash_password "$OPENSEARCH_PMS_PASSWORD")
dashboards_hash=$(hash_password "$OPENSEARCH_DASHBOARDS_PASSWORD")
cat > /security-config/internal_users.yml <<EOF
_meta:
  type: internalusers
  config_version: 2
pms_search:
  hash: "$pms_hash"
kibanaserver:
  hash: "$dashboards_hash"
EOF
cat > /security-config/roles.yml <<'EOF'
_meta:
  type: roles
  config_version: 2
pms_kb_index:
  cluster_permissions: [cluster_composite_ops]
  index_permissions:
    - index_patterns: ['pms-kb-v1-*', 'pms-test-kb-v1-*']
      allowed_actions: [indices_all]
EOF
cat > /security-config/roles_mapping.yml <<'EOF'
_meta:
  type: rolesmapping
  config_version: 2
pms_kb_index:
  users: [pms_search]
kibana_server:
  users: [kibanaserver]
EOF
chgrp -R 1000 /security-config
chmod 0750 /security-config
chmod 0640 /security-config/*.yml
