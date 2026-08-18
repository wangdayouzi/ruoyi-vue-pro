# =============================================
# RuoYi-Vue-Pro Docker 部署指南
# =============================================
# 
# 架构说明：
#   - 数据库（PostgreSQL）：部署在 Linux 服务器上，不纳入 Docker
#   - Redis：Docker 容器运行
#   - OnlyOffice Document Server：Docker 容器运行
#   - 后端（yudao-server）：Docker 容器运行
#   - 前端（yudao-ui-admin Nginx）：托管管理后台 + 手机端 H5（统一端口 8080）
#
# 前置条件：
#   1. Linux 服务器已安装 Docker + Docker Compose v2
#   2. Linux 服务器已安装并运行 PostgreSQL 18
#   3. 本地（Windows）已安装：JDK 17、Maven、Node.js 20+、pnpm

---

## 一、目录结构（服务器上）

```
/home/yudao/
├── docker-compose.yml          # 编排文件
├── .env                        # 环境变量（从 docker.env 重命名）
├── redis/
│   └── data/                   # Redis 持久化数据
├── onlyoffice/
│   ├── data/                   # OnlyOffice 数据
│   ├── log/                    # OnlyOffice 日志
│   └── fonts/                  # OnlyOffice 字体
├── uploadfile/                 # 文件上传存储目录
├── logs/
│   └── server/                 # 后端日志
├── yudao-server/
│   └── yudao-server.jar        # 后端 JAR 包
├── yudao-ui-admin/
│   ├── nginx.conf              # Nginx 配置（管理后台 + /h5/ 手机端）
│   └── dist/                   # 管理后台静态文件
└── yudao-ui-uniapp/
    └── dist/
        └── build/
            └── h5/             # 手机端 H5 静态文件
```

> 所有数据目录都在项目内，方便备份和迁移。

---

## 二、服务器初始化（仅首次）

### 2.1 PostgreSQL 配置

```bash
# 允许 Docker 容器访问 PostgreSQL
sudo sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/" /etc/postgresql/18/main/postgresql.conf
sudo sh -c 'echo "host    all    all    172.17.0.0/16    md5" >> /etc/postgresql/18/main/pg_hba.conf'
sudo systemctl restart postgresql
sudo systemctl enable postgresql   # 开机自启
```

### 2.2 创建数据库并导入 SQL

```bash
# 创建数据库
psql -U postgres -c "CREATE DATABASE xining;"

# 导入初始 SQL（sql/postgresql/ 目录下的文件）
psql -U postgres -d xining -f /path/to/ruoyi-vue-pro.sql
psql -U postgres -d xining -f /path/to/1-amf-menu-pg.sql
```

### 2.3 创建项目目录

```bash
mkdir -p /home/yudao/{redis/data,onlyoffice/{data,log,fonts},uploadfile,logs/server,yudao-server,yudao-ui-admin/dist,yudao-ui-uniapp/dist/build/h5}
```

### 2.4 创建 PostgreSQL 自启服务（如不存在）

```bash
sudo tee /etc/systemd/system/postgresql.service << 'EOF'
[Unit]
Description=PostgreSQL 18
After=network.target

[Service]
Type=forking
User=postgres
Group=postgres
PIDFile=/var/lib/postgresql/18/main/postmaster.pid
ExecStart=/usr/lib/postgresql/18/bin/pg_ctl start -D /var/lib/postgresql/18/main -l /var/log/postgresql/postgresql-18.log
ExecStop=/usr/lib/postgresql/18/bin/pg_ctl stop -D /var/lib/postgresql/18/main
ExecReload=/usr/lib/postgresql/18/bin/pg_ctl reload -D /var/lib/postgresql/18/main
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable postgresql
```

---

## 三、构建与上传

### 3.1 本地打包后端（Windows）

```powershell
cd ruoyi-vue-pro
mvn clean package -Dmaven.test.skip=true
```
产物：`yudao-server\target\yudao-server.jar`

### 3.2 本地打包管理后台（Windows）

```powershell
cd yudao-ui-admin-vue3-xn
pnpm install
pnpm build:prod
```
产物：`dist\`

### 3.3 本地打包手机端（Windows）

```powershell
cd yudao-ui-admin-uniapp
pnpm install
pnpm build:h5:prod
```
产物：`dist\build\h5\`

### 3.4 上传文件到服务器

| 本地路径 | 服务器路径 |
|---|---|
| `script/docker/docker-compose.yml` | `/home/yudao/docker-compose.yml` |
| `script/docker/.env` | `/home/yudao/.env` |
| `yudao-server/target/yudao-server.jar` | `/home/yudao/yudao-server/yudao-server.jar` |
| `yudao-ui-admin-vue3-xn/nginx.conf` | `/home/yudao/yudao-ui-admin/nginx.conf` |
| `yudao-ui-admin-vue3-xn/dist/*` | `/home/yudao/yudao-ui-admin/dist/` |
| `yudao-ui-admin-uniapp/dist/build/h5/*` | `/home/yudao/yudao-ui-uniapp/dist/build/h5/` |

> 上传 `dist/` 目录下的**所有文件**到服务器对应目录。

---

## 四、环境变量

编辑 `/home/yudao/.env`：

```ini
# 数据库（宿主机 PostgreSQL，Docker 通过 172.17.0.1 访问）
DB_HOST=172.17.0.1
DB_PORT=5432
DB_NAME=xining
DB_USERNAME=xining
DB_PASSWORD=your_password

# Redis
REDIS_HOST=yudao-redis
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JVM 参数
JAVA_OPTS=-Xms512m -Xmx1024m -Dspring.profiles.active=prod -Djava.security.egd=file:/dev/./urandom -XX:+UseG1GC

# OnlyOffice（前端动态拼接地址，此值为后端默认）
ONLYOFFICE_DOC_URL=http://localhost:8088
ONLYOFFICE_CALLBACK_URL=http://yudao-server:48080/admin-api/amf/onlyoffice/callback
ONLYOFFICE_JWT_SECRET=your-jwt-secret

# 文件存储
FILE_STORAGE_PATH=/data/yudao/uploadfile
```

---

## 五、启动服务

```bash
cd /home/yudao
docker compose --env-file .env up -d
```

---

## 六、服务访问

| 服务 | 地址 | 说明 |
|---|---|---|
| 管理后台 | `http://服务器IP:8080` | PC 端管理页面 |
| 手机端 | `http://服务器IP:8080/h5/` | 移动端 H5 页面 |
| 后端 API | `http://服务器IP:48080` | Swagger：`/doc.html` |
| OnlyOffice | `http://服务器IP:8088` | 在线文档编辑 |

---

## 七、常用运维命令

```bash
cd /home/yudao

# ===== 启停 =====
docker compose --env-file .env up -d              # 启动全部
docker compose --env-file .env down               # 停止全部
docker compose --env-file .env restart server     # 重启后端（仅重启进程，不更新环境变量）

# ===== 更新 =====
# 更新后端 JAR 包后（环境变量没变）：
docker compose --env-file .env restart server
docker compose --env-file .env restart admin

# 改了 .env 后（环境变量变了，必须重建容器才能生效）：
docker compose --env-file .env up -d --force-recreate server

# 更新管理后台/手机端静态文件：上传覆盖后刷新浏览器即可

# ===== 日志 =====
docker compose logs -f server                     # 后端实时日志
docker compose logs -f --tail=100 server          # 后端最近 100 行
docker compose logs admin                         # 前端日志
docker logs yudao-redis                           # Redis 日志
docker logs yudao-onlyoffice                      # OnlyOffice 日志

# ===== 状态 =====
docker compose ps                                  # 查看所有容器状态
docker stats                                       # 查看资源占用

# ===== 调试 =====
docker exec -it yudao-server sh                   # 进入后端容器
docker exec -it yudao-redis redis-cli -a 密码      # 进入 Redis CLI
```

---

## 八、故障排查

| 问题 | 检查 |
|---|---|
| 后端连不上数据库 | `psql -U postgres -h 172.17.0.1 -d xining` 测试连通性 |
| 后端连不上数据库 | 确认 `pg_hba.conf` 有 `172.17.0.0/16` 规则 |
| 后端连不上数据库 | `systemctl status postgresql` 确认 PostgreSQL 已启动 |
| Redis 不健康 | `docker logs yudao-redis` 查看日志 |
| 手机端 403 | 确认 `/home/yudao/yudao-ui-uniapp/dist/build/h5/` 有 `index.html` |
| 管理后台 404 | 确认 `/home/yudao/yudao-ui-admin/dist/` 有 `index.html` |
| OnlyOffice 下载失败 | 确认 `ONLYOFFICE_CALLBACK_URL` 为 `http://yudao-server:48080/admin-api/amf/onlyoffice/callback` |
| OnlyOffice 打不开 | 确认 `ONLYOFFICE_DOC_URL` 为服务器实际 IP |
| OnlyOffice "文档安全令牌格式不正确" | 确认两边 JWT 密钥一致，改了 `.env` 后必须 `--force-recreate server` |

1. **数据库不走 Docker**：PostgreSQL 部署在宿主机上，通过 `host.docker.internal` 域名访问（由 `extra_hosts: host-gateway` 提供），不受 Docker 网络段变化影响。
2. **首次启动需要初始化数据库**：确保 SQL 已导入，否则后端启动会报错。
3. **OnlyOffice 回调/下载**：`ONLYOFFICE_CALLBACK_URL` 用 Docker 内部地址 `http://yudao-server:48080`，`ONLYOFFICE_DOC_URL` 用服务器外网 IP。OnlyOffice 容器通过前者下载文件、回调保存，浏览器通过后者加载编辑器。
4. **文件存储**：上传文件存储在宿主机的 `/home/yudao/uploadfile/`，通过 bind mount 挂载到容器内 `/data/yudao/uploadfile`。容器重建不会丢失文件，也方便直接运维管理。
5. **前端 API 代理**：前端 nginx 将 `/admin-api/` 代理到后端 `yudao-server:48080`，所以前端 `.env.prod` 中的 `VITE_BASE_URL` 配置不影响生产环境。
6. **restart vs force-recreate**：`restart` 只重启进程，不更新环境变量；改了 `.env` 必须用 `--force-recreate` 重建容器才能让新环境变量生效。
7. **日志查看**：
   - 后端日志：挂载到 `/home/yudao/logs/server/`，`tail -f /home/yudao/logs/server/yudao-server.log` 实时查看
   - 容器日志：`docker compose logs -f server|admin|onlyoffice` 查看各容器 stdout/stderr
