# PMS 知识库 OpenSearch

OpenSearch 单独用 Compose 管理。PMS 在现有知识库全局搜索入口查询索引，再由 PostgreSQL 校验租户、知识库和文档权限，并处理其他条件、排序及分页。OpenSearch 同时支持连续子串匹配和 ICU 中文分词匹配。搜「样品 流程」时，两词出现在同一文档即可命中。连续短语、单字和文档编号仍可按子串查找。

## 本机

在本目录执行以下命令。首次构建会拉取基础镜像并安装 OpenSearch 官方的 `analysis-icu` 插件。

```sh
docker compose -f compose.local.yaml up -d --build
docker compose -f compose.local.yaml ps
docker compose -f compose.local.yaml stop
```

本机端口为 `http://127.0.0.1:19200`，仅供本机开发，关闭了安全插件。容器配置 4 核 CPU 上限、5 GiB 内存上限和 2 GiB JVM 堆。重启策略为 `no`，停止或重启 Docker 后可再次手动执行 `up -d`。数据保存在 Compose 命名卷中。

执行 [索引同步任务增量 SQL](../../sql/postgresql/增量/2026-11-10-pms-kb-search-sync.sql) 后，以 `local` 配置重启 Yudao 后端。后台首次建索引成功时，后端会记录「PMS 知识库搜索索引已同步」。可访问 `http://127.0.0.1:19200/_cat/aliases/pms-kb-v1-*-current?v` 查看当前索引。多租户关闭时使用 `pms-kb-v1-all-current`。

Windows 本地运行 Java 17 时，如果日志出现 `Unable to establish loopback connection`，在 IDEA 后端运行配置的 VM options 里加 `-Djdk.net.unixdomain.tmpdir=D:/software/jdk17`，或改成机器上已有且当前用户可写的短路径目录。

## 共享 OpenSearch 部署

OpenSearch 独立放在 `/home/opensearch`。服务器使用 Docker Compose 2.23.1 或以上。首次部署手动准备账号配置，启动时自动创建专用网络、生成证书并启动续签服务。同一服务器上的测试和正式芋道共用这个实例，共用后端服务账号，各用独立索引。默认不发布搜索端口。

### 1. 上传文件并填写密码

在服务器上准备目录。若 OpenSearch 数据盘不在 `/home`，在下面的 `server.env` 中改为专用的索引目录。初始化容器会把该目录的属主设为 UID 1000。

```sh
sudo install -d -o "$(id -u)" -g "$(id -g)" -m 0750 /home/opensearch /home/opensearch/build-context /home/opensearch/sql
cd /home/opensearch
```

| 仓库内文件 | 上传到服务器 |
| --- | --- |
| `deploy/opensearch/compose.server.yaml` | `/home/opensearch/compose.server.yaml` |
| `deploy/opensearch/scripts/` | `/home/opensearch/scripts/` |
| `deploy/opensearch/server.env`（本地配置文件） | `/home/opensearch/server.env` |
| `sql/postgresql/增量/2026-11-10-pms-kb-search-sync.sql` | `/home/opensearch/sql/2026-11-10-pms-kb-search-sync.sql` |
| `sql/postgresql/增量/2026-11-10-pms-kb-drop-legacy-trgm-index.sql` | `/home/opensearch/sql/2026-11-10-pms-kb-drop-legacy-trgm-index.sql` |

Yudao 后端 JAR 按现有发布流程打包。OpenSearch 构建使用空的 `build-context` 目录，避免把密码和数据发送给构建器。

打开 `/home/opensearch/server.env`。如果是从 Git 重新检出，按下面的内容创建该文件，`server.env` 由 Git 忽略，避免提交生产密码。填写共享 PMS 服务账号、面板连接账号及面板管理员的密码。配置文件保存后执行 `chmod 600 server.env`。如需调整资源，默认堆内存为 2 GiB、容器内存上限 5 GiB、CPU 上限 4 核。

```dotenv
OPENSEARCH_PMS_PASSWORD=替换为正式PMS服务账号密码
OPENSEARCH_DASHBOARDS_PASSWORD=替换为面板连接OpenSearch的密码
# 按需填写，面板管理账号密码使用 8 至 72 个无空格的可打印 ASCII 字符。
OPENSEARCH_ADMIN_PASSWORD=
OPENSEARCH_DATA_DIR=/home/opensearch/data

# 按需调整
# OPENSEARCH_HEAP=2g
# OPENSEARCH_MEMORY_LIMIT=5g
# OPENSEARCH_CPUS=4
# OPENSEARCH_DASHBOARDS_PORT=5601
```

### 2. 启动 OpenSearch

检查 `sysctl -n vm.max_map_count`。若低于 262144，执行下面两条命令。宿主机只需设置一次。

```sh
printf '%s\n' 'vm.max_map_count=262144' | sudo tee /etc/sysctl.d/99-opensearch.conf
sudo sysctl -p /etc/sysctl.d/99-opensearch.conf
```

首次部署或使用空数据目录恢复时，先构建带 ICU 插件的镜像，再准备账号和角色：

```sh
cd /home/opensearch
docker compose --env-file server.env -f compose.server.yaml build opensearch
docker compose --env-file server.env -f compose.server.yaml run --rm --no-deps bootstrap
```

已有数据目录的实例可以直接执行下面的启动命令：

```sh
docker compose --env-file server.env -f compose.server.yaml up -d --wait --wait-timeout 300
```

`certificates` 显示 `Exited (0)` 表示证书检查已完成，`opensearch` 应显示 `healthy`。用以下命令查看状态或排查启动问题。

```sh
docker compose --env-file server.env -f compose.server.yaml ps -a
docker compose --env-file server.env -f compose.server.yaml logs --tail=100 certificates certificate-renewer opensearch
```

公开 CA 在 `/home/opensearch/certs/ca.pem`，签发和管理私钥保存在仅 root 可读的 `/home/opensearch/security-bootstrap`。迁移时备份数据、证书、私钥和 `server.env`。已有数据目录中的账号权限会保留，修改 `server.env` 不会重置已有账号密码。需要轮换时，应先更新 OpenSearch 中的账号，再同步相关服务的环境变量。服务器上的 OpenSearch 主服务采用 `unless-stopped`，Docker 或服务器重启后自动恢复。手动停止后需要重新启动。证书续签服务随 Docker 恢复，初始化服务只在部署时执行，Dashboards 按需启动。已有实例上传新版 Compose 后，可用 `docker update --restart unless-stopped yudao-opensearch-server-opensearch-1` 给当前容器应用该策略，无需当场重建。若改过 Compose 项目名，使用实际容器名。

默认共享网络名为 `yudao-search`，由 OpenSearch Compose 管理。如果旧部署已由 Yudao Compose 创建了同名网络，在 OpenSearch 的 `server.env` 和每个接入环境的 `.env` 中同时设置 `OPENSEARCH_NETWORK=yudao-search-internal`，使用新的专用网络。

## 先接入测试环境

已经跑通 OpenSearch 搜索的环境，本次升级只需执行新的索引同步 SQL、替换后端 JAR 并重建后端容器。

| 环境 | 芋道目录 | PostgreSQL | OpenSearch 账号 | 索引范围 |
| --- | --- | --- | --- | --- |
| 测试 | `/home/yudao-test` | `yudao_test` | `pms_search` | `pms-test-kb-v1-*` |
| 正式 | `/home/yudao` | 正式 `DB_NAME` | `pms_search` | `pms-kb-v1-*` |

如果共享 OpenSearch 已经启动，直接从第 2 步继续。以下操作先只更新测试环境。

### 1. 启动共享 OpenSearch

按上面的共享部署步骤启动一次。已有实例应用新版 Compose 后，先在 `/home/opensearch` 执行下面的命令，把共享账号关联的角色扩展到两个索引前缀，再更新测试后端配置。两套后端的 `YUDAO_OPENSEARCH_PASSWORD` 都使用现有 `pms_search` 的实际密码。

```sh
docker compose --env-file server.env -f compose.server.yaml run --rm --no-deps admin-account shared-pms-role
```

两套搜索和面板管理员登录验证通过后，可在 Dashboards 的 Security → Internal users 删除旧的 `pms_search_test` 和 `pms_dashboard`。已有 Security 配置保存在集群中，更新 Compose 不会自动删除这两个旧账号。

### 2. 手动上传测试部署文件

用你平时的文件传输工具，从本机后端仓库把下列文件拖到服务器。测试 `.env` 已填好共享 OpenSearch 密码。覆盖前先备份服务器原有的 `.env`。

| 从本机拖这个文件 | 服务器上的目标文件 |
| --- | --- |
| `script/docker-test/docker-compose.yml` | `/home/yudao-test/docker-compose.yml` |
| `script/docker-test/.env` | `/home/yudao-test/.env` |
| `yudao-server/target/yudao-server.jar`（本次自行打包的新 JAR） | `/home/yudao-test/yudao-server/yudao-server.jar` |
| `sql/postgresql/增量/2026-11-10-pms-kb-search-sync.sql` | `/home/opensearch/sql/2026-11-10-pms-kb-search-sync.sql` |
| `sql/postgresql/增量/2026-11-10-pms-kb-drop-legacy-trgm-index.sql` | `/home/opensearch/sql/2026-11-10-pms-kb-drop-legacy-trgm-index.sql` |

SQL 目录不存在时，先在服务器运行 `mkdir -p /home/opensearch/sql`。此时先上传文件，等测试库增量完成后再重建后端。

### 3. 手动更新测试后端

1. 在 `yudao_test` 的 `pms_knowledge_document` 所在 schema 手动执行 `2026-11-10-pms-kb-search-sync.sql`。如果迁移和后端使用不同数据库账号，按正式库增量章节的权限说明授权。
2. 将新 JAR 拖到第 2 步列出的路径。首次接入时同时上传测试 `.env` 和 Compose。
3. 用你平时的部署方式重建 `/home/yudao-test` 项目里的 `server` 服务（容器名 `yudao-test-server`）。

### 4. 验证搜索

等测试后端日志出现 `PMS 知识库搜索索引已同步`，再登录测试入口 `https://cms.accurantbio.com:8081`。用文档编号和中文词确认历史文档可搜，再新增一篇文档、修改正文加入独特关键词，最后下架或删除该文档。每次保存后留几秒给后台同步，再验收搜索结果。搜索验证通过后，可在测试库的知识库 schema 手动执行旧索引清理 SQL。

## 正式环境接入

测试验收后，再执行下面的正式库增量和后端更新。沿用已经启动的 OpenSearch。

### 1. 执行正式库增量

在 PostgreSQL 服务器执行。下面的 `xining` 来自项目生产部署示例。**以线上 `/home/yudao/.env` 里的 `DB_NAME`、`DB_PORT`、`DB_USERNAME` 为准**。在数据库宿主机执行时，主机使用 `127.0.0.1`。输入 `-W` 提示的 PostgreSQL 密码，避免把密码写进命令。

```sh
PMS_DB_HOST=127.0.0.1
PMS_DB_PORT=5432
PMS_DB_NAME=xining
PMS_DB_USER=xining

psql -h "$PMS_DB_HOST" -p "$PMS_DB_PORT" -U "$PMS_DB_USER" -d "$PMS_DB_NAME" -W -v ON_ERROR_STOP=1 \
  -c "SELECT n.nspname AS schema_name FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'pms_knowledge_document' AND c.relkind IN ('r', 'p');"
```

把下面的 `public` 换成查询得到、且 Yudao 主库实际使用的 schema。生产后端按 `mes,public` 的顺序查找。用有权修改知识库文档表的账号执行 SQL。如果数据库在另一台机器上，把 SQL 文件复制过去并调整 `-f` 路径。

```sh
PMS_KB_SCHEMA=public

PGOPTIONS="-c search_path=$PMS_KB_SCHEMA" psql \
  -h "$PMS_DB_HOST" -p "$PMS_DB_PORT" -U "$PMS_DB_USER" -d "$PMS_DB_NAME" -W -v ON_ERROR_STOP=1 \
  -f /home/opensearch/sql/2026-11-10-pms-kb-search-sync.sql
```

测试和正式都需要在各自的知识库 schema 执行这份新 SQL。若迁移账号与后端数据库账号不同，在相同 schema 给后端账号授予 `pms_kb_search_sync` 的 `SELECT, INSERT, UPDATE, DELETE` 和对应标识列序列 `pms_kb_search_sync_version_seq` 的 `USAGE`。已有旧版同步表和触发器保留，供旧 JAR 回退。

### 2. 手动更新正式后端

正式 `.env` 已填好正式 OpenSearch 密码。测试验收后，先备份服务器原有的正式 `.env`，再手动拖下面三个文件。

| 从本机拖这个文件 | 服务器上的目标文件 |
| --- | --- |
| `script/docker/docker-compose.yml` | `/home/yudao/docker-compose.yml` |
| `script/docker/.env` | `/home/yudao/.env` |
| `yudao-server/target/yudao-server.jar`（测试验收过的 JAR） | `/home/yudao/yudao-server/yudao-server.jar` |

上传后，用你平时的部署方式重建 `/home/yudao` 项目里的 `server` 服务。

### 3. 验证搜索并清理旧索引

在 Yudao 的 PMS 知识库全局搜索输入一个已知的文档编号，或输入文档里分开出现的两个中文词。等待后端后台任务完成首次建索引。确认页面能搜到预期结果，并检查后端日志与 OpenSearch 索引。

```sh
cd /home/yudao
docker compose --env-file .env logs --tail=200 server | grep 'PMS 知识库搜索索引已同步'

cd /home/opensearch
docker compose --env-file server.env -f compose.server.yaml exec opensearch \
  curl -fsS --cacert config/certs/ca.pem -u pms_search 'https://opensearch:9200/pms-kb-v1-*-current/_search?size=0'
```

搜索通过后，用正式库增量步骤的数据库连接和 schema 运行旧索引清理脚本。这个脚本要在事务之外执行，旧索引已不存在时也可以执行。

```sh
PGOPTIONS="-c search_path=$PMS_KB_SCHEMA" psql \
  -h "$PMS_DB_HOST" -p "$PMS_DB_PORT" -U "$PMS_DB_USER" -d "$PMS_DB_NAME" -W -v ON_ERROR_STOP=1 \
  -f /home/opensearch/sql/2026-11-10-pms-kb-drop-legacy-trgm-index.sql
```

## 启动并打开管理面板

```sh
cd /home/opensearch
docker compose --env-file server.env -f compose.server.yaml up -d dashboards
```

如希望首次启动时一起开启面板，在第 2 步的 `up` 前加 `--profile dashboards`。面板默认绑定服务器的 `127.0.0.1:5601`。需要内网直连时，在服务器 `server.env` 设置 `OPENSEARCH_DASHBOARDS_BIND_IP=192.168.11.158`，重建面板后访问 `http://192.168.11.158:5601/`。使用默认绑定时，从自己的电脑建立 SSH 转发。

```sh
ssh -N -L 127.0.0.1:15601:127.0.0.1:5601 部署账号@服务器IP
```

保持 SSH 会话打开，在浏览器访问 `http://127.0.0.1:15601`。面板统一使用 `opensearch_admin` 登录。若更改服务器面板端口，同步调整 SSH 命令最后的端口。

将新版 `compose.server.yaml` 和 `scripts/` 上传到 `/home/opensearch`，在 `server.env` 填写单独生成的 `OPENSEARCH_ADMIN_PASSWORD`，按下方升级步骤应用 Compose。然后在该目录执行：

```sh
docker compose --env-file server.env -f compose.server.yaml run --rm --no-deps admin-account
```

这个命令通过管理证书创建或更新 `opensearch_admin`。再次执行会把此账号密码设为 `server.env` 中的值。Linux `root` 用于管理服务器，`opensearch_admin` 用于管理 OpenSearch，`kibanaserver` 供 Dashboards 服务连接集群。管理证书仍用于必须使用证书的操作。

## 按需改为内网 IP 访问

同机部署保持 `https://opensearch:9200`。OpenSearch 迁到另一台机器或需要供其他服务器调用时，在 OpenSearch 的 `server.env` 设置实际的 `OPENSEARCH_PRIVATE_IP`，让节点证书包含这个 IP。例如 OPENSEARCH_PRIVATE_IP=192.168.1.20。然后在同一份 Compose 的 OpenSearch 服务增加端口映射和用于发布端口的桥接网络。

```yaml
services:
  opensearch:
    ports:
      - "${OPENSEARCH_PRIVATE_IP}:9200:9200"
    networks:
      - search
      - ip_access
networks:
  ip_access:
    driver: bridge
```

已有证书没有该 IP 时，先停止服务并备份证书，再将 `certs/node.pem` 和 `certs/node-key.pem` 移到备份目录，保留原 CA 和签发私钥，然后按第 2 步重新启动。开放端口前，在服务器防火墙限制为指定调用方。

调用方连接 `https://内网IP:9200`，安装或挂载同一份公开 CA。芋道与 OpenSearch 分别部署时，若 Yudao Compose 仍声明共享网络，在 Yudao 主机执行一次 `docker network create --internal yudao-search`，或使用实际的 `OPENSEARCH_NETWORK` 名称。跨主机连接走内网 IP。

## 证书续期与故障恢复

### 自动续签节点和管理证书

`certificate-renewer` 在启动时检查一次，以后每天检查。节点或管理证书剩余不足 30 天时，它用现有 CA 和原私钥续签，验证新证书后原子替换 PEM 文件。OpenSearch 开启证书热加载，通常在几秒内加载新的节点证书。芋道和面板继续信任同一个公开 CA。正常续签无需修改账号或重启后端。

CA 初次签发有效期为 3650 天，节点和管理证书最长 825 天。续签后的有效期会限制在 CA 到期之前。CA 本身的更换需要同步客户端信任文件，自动续签服务在 CA 剩余不足 180 天时记录警告并标记为 `unhealthy`。OpenSearch 主服务仍可运行，需在这个窗口内安排 CA 轮换。

已有服务器升级时，先备份 `certs`、`security-bootstrap` 和 `server.env`，再将新版 `compose.server.yaml` 和 `scripts/` 拖到 `/home/opensearch`，保持相对路径。已有 `server.env` 沿用。首次应用热加载配置需要重建一次 OpenSearch 容器，安排在维护窗口执行。

```sh
cd /home/opensearch
docker compose --env-file server.env -f compose.server.yaml config --quiet
docker compose --env-file server.env -f compose.server.yaml up -d --wait --wait-timeout 300
```

确认 `opensearch` 和 `certificate-renewer` 显示 `healthy`，再从测试及正式知识库各搜索一次。首次初始化服务显示 `Exited (0)` 属于正常状态。续签服务的文件检查和日志可以这样查看：

```sh
docker compose --env-file server.env -f compose.server.yaml exec certificate-renewer sh /certificates.sh health
docker compose --env-file server.env -f compose.server.yaml logs --tail=100 certificate-renewer
```

签发失败会保留已发布的证书，续签容器的健康状态会报错，每五分钟重试。修复签发私钥、目录权限或 CA 后，重启 `certificate-renewer` 可以立即重试。CA 私钥和已有 CA 证书应成对恢复。健康状态需要接入现有的容器监控才能主动通知维护人，当前 Compose 不向钉钉、邮件等渠道发通知。

### 备份和恢复

- 优先备份测试和正式 PostgreSQL，PMS 索引可以从数据库重建。
- 将 `compose.server.yaml`、`server.env`、`certs` 和 `security-bootstrap` 一起备份到受限位置。密钥与环境文件包含敏感配置。
- 需要保留索引时，可以使用 OpenSearch 快照。安全配置应另用 Security 管理工具导出。也可以在停机窗口备份完整的数据目录，其中的安全索引保留通过 API 补建的账号和角色。
- 故障恢复时，先恢复证书、配置和数据目录，再启动 OpenSearch 并验证测试与正式账号。若使用空数据目录，先按第 2 步运行 `bootstrap`，再启动 OpenSearch 初始化账号，PMS 后台检测到搜索别名缺失后，从各自 PostgreSQL 重建索引。额外通过 API 创建的账号和角色需从安全配置备份恢复。
- 将恢复演练安排在隔离实例上，使用独立网络和数据目录。验收项包括证书校验、测试和正式账号权限、中文关键词搜索和首次索引重建。

## 作为长期共用服务使用

当前部署提供单节点 OpenSearch、ICU 分词插件、TLS、PMS 共享服务账号和面板管理员。继续接入其他业务时，需要按业务创建账号、角色、索引及映射。

| 使用场景 | 需要配置的内容 |
| --- | --- |
| 新服务或 MCP 接入 | 加入同机 `yudao-search` 网络，挂载公开 `ca.pem`，用独立账号，只授权对应索引前缀。 |
| 在 Dashboards 里管理索引、分析器和其他 API | 按面板章节创建 `opensearch_admin`。仅需操作试验索引时，可另建账号并按试验范围授权。 |
| 新建其他业务的中文搜索索引 | 显式配置 `text` 字段使用 `icu_analyzer`。安装插件后，新建字段仍要选择分析器。编号或精确过滤字段使用 `keyword`。 |
| 单机上的试验索引 | 对自己的业务前缀设置 1 个主分片、0 个副本，避免单节点副本无法分配。用精确的索引模板匹配范围，系统索引继续采用原有设置。 |
| 验证向量、混合检索、聚合、高亮或排序 | 按使用场景设计字段映射和查询。PMS 当前实现文本子串及 ICU 多词匹配，最终结果按 PostgreSQL 中的业务规则排序，候选上限为 2000。 |
| 让 MCP 按 PMS 用户权限搜索 | 调用芋道知识库搜索 API。直接连接 OpenSearch 时，访问范围由 OpenSearch 服务账号权限决定。 |
| 保存试验成果或其他业务原始数据 | 配置快照仓库和定期快照。PMS 索引有 PostgreSQL 来源可重建，其他业务数据应另有恢复来源。 |

已有数据目录中的账号和权限以 OpenSearch 安全索引为准。修改 `server.env` 或重跑初始化容器只会改变初始化输入，现有账号需通过 Security API 或管理工具更新。特别是上线新服务时，应先创建角色和账号，再配置该服务。

## 搜索行为

文档新增、正文写入、状态、权限或租户变更时，PostgreSQL 在同一事务内把文档 ID 写入待同步表。后台每两秒尝试领取已提交任务，读取文档的最新状态，再写入或删除 OpenSearch 中对应记录。失败任务会退避重试。查询仍由 PostgreSQL 校验文档状态和访问权限。

首次启动时，后台构建新索引，补写构建期间已进入队列的变更，刷新后再原子切换搜索别名。构建失败时保留已发布索引。切换后仍待处理的变更由后台继续补写。旧 JAR 使用的同名物理索引保留作回退。需要手动重建某个范围时，在对应数据库和 schema 把 `pms_kb_search_sync` 中该范围的维护记录（`document_id=0`）重新置为待重建。单租户模式的范围为 `tenant_id=0`。

```sql
UPDATE pms_kb_search_sync
SET version = DEFAULT, force_rebuild = true, failures = 0, next_attempt_at = now()
WHERE tenant_id = 0 AND document_id = 0;
```

关键词搜索优先读取已发布索引。OpenSearch 无法返回有效查询结果时，使用 PostgreSQL 的 `title LIKE … OR content LIKE …` 兜底，按连续文本匹配标题和富文本正文，仍按原权限、状态和筛选条件分页。OpenSearch 正常返回零条时直接返回空结果。匹配超过 2,000 条时，页面会提示缩小范围。仅按编号、属性、审阅结论等结构化条件筛选时，查询由 PostgreSQL 完成。`search_text` 仍供 OpenSearch 写入索引和生成搜索摘要。

参考 [OpenSearch TLS 配置](https://docs.opensearch.org/latest/security/configuration/tls/) 和 [OpenSearch Docker 部署](https://docs.opensearch.org/latest/install-and-configure/install-opensearch/docker/)。
