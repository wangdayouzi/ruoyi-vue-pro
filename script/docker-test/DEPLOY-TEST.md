# 测试库部署执行清单（同一台服务器，与生产完全隔离）

> 目的：在同服务器 `/home/yudao-test/` 搭一套 yudao 测试环境。
> 前提（用户已确认）：不接钉钉、不跑老ERP同步；数据库已手动建好 `yudao_test` 并迁移完成；密码沿用生产。
> 隔离设计：容器/项目名带 `-test`、端口 48081/8081/16380、独立 `.env`、独立数据目录 —— 与生产（`/home/yudao/`，端口 48080/8080/16379）零冲突。
> 本目录文件对应服务器目录：本机 `ruoyi-vue-pro/script/docker-test/` → 服务器 `/home/yudao-test/`。

> ⚠️ **本服务器 PG 命令执行方式**：`sudo -u postgres xxx` 会报 `找不到命令`（PGDG 装在 `/usr/pgsql-18/bin`，不在 sudo PATH）。下方所有 PG 操作统一用 **`su - postgres -c "..."`** 执行，不要用 `sudo -u postgres`。

---

## 0. 前置确认（手动准备）

- [ ] 服务器已装 Docker + Compose v2、PostgreSQL 18（复用生产同机）
- [ ] 端口 `48081 / 8081 / 16380` 未被占用（`ss -ltnp | grep -E '48081|8081|16380'`）
- [ ] 能连宿主机 PostgreSQL：`psql -U postgres -h 127.0.0.1 -c 'select version();'`
- [ ] 能访问生产库（同机直连即可）

## 1. 数据库（已完成：手动建库迁移）

> ✅ 已于 2026-09-03 手动建好 `yudao_test` 并完成数据迁移，本步只需做两项确认。

```bash
# 1) 确认 staging 占位空库存在（没有就建，仅占位不导入）
su - postgres -c "createdb yudao_stg_pm_test 2>/dev/null" || echo "已存在，跳过"

# 2) 迁移后校验（mes/public schema 必须都在）
su - postgres -c "psql -d yudao_test -c '\\dn'"
su - postgres -c "psql -d yudao_test -c 'select count(*) from information_schema.tables;'"
```

> 以后如需从生产重新整库刷新，参考命令（Fc 自定义格式）：
> ```bash
> su - postgres -c "pg_dump -d <生产库名> -Fc -f /tmp/yudao_prod.dump"
> su - postgres -c "pg_restore -d yudao_test -j 4 /tmp/yudao_prod.dump"
> ```
> `<生产库名>` 以服务器 `/home/yudao/.env` 的 `DB_NAME` 为准。

## 2. 建服务器目录 + 上传文件

```bash
mkdir -p /home/yudao-test/{redis/data,uploadfile,logs/server,yudao-server,yudao-ui-admin/dist}
```

| 本地路径 | 服务器路径 |
|---|---|
| `ruoyi-vue-pro/script/docker-test/docker-compose.yml` | `/home/yudao-test/docker-compose.yml` |
| `ruoyi-vue-pro/script/docker-test/.env` | `/home/yudao-test/.env` |
| `ruoyi-vue-pro/script/docker-test/yudao-ui-admin/nginx.conf` | `/home/yudao-test/yudao-ui-admin/nginx.conf` |
| `yudao-server/target/yudao-server.jar` | `/home/yudao-test/yudao-server/yudao-server.jar` |
| `yudao-ui-admin-vue3-xn/dist/*` | `/home/yudao-test/yudao-ui-admin/dist/` |

> 管理后台前端产物可直接复用生产同一套 `dist`（API 由 nginx 代理决定，前端不写死）。
> 手机端 H5 / OnlyOffice 测试环境默认不部署（nginx.conf 未含 `/h5/` 与 ssl）。

## 3. 核对 `.env`（服务器上）

```bash
cd /home/yudao-test
grep -E '^(DB_NAME|SLAVE_DB_NAME|STG_PM_DB_NAME|REDIS_HOST|REDIS_PORT)=' .env
```
预期：
- `DB_NAME=yudao_test`、`SLAVE_DB_NAME=yudao_test`、`STG_PM_DB_NAME=yudao_stg_pm_test`
- `REDIS_HOST=yudao-test-redis`（compose 内部服务名）、`REDIS_PORT=6379`
- 密码沿用生产（默认即可，无需改）

## 4. 启动（务必在测试目录内执行）

```bash
cd /home/yudao-test
docker compose --env-file .env up -d
docker compose ps
```

⚠️ **注意**：`up -d` / `down` / `restart` 等操作**必须在 `/home/yudao-test` 目录内**执行；`cd` 错了会作用于生产（`/home/yudao`）。

## 5. 验证

| 项 | 地址 | 说明 |
|---|---|---|
| 管理后台 | `http://服务器IP:8081` | 登录、各模块冒烟 |
| 后端 API | `http://服务器IP:48081/doc.html` | Swagger |
| 日志 | `docker compose logs -f server` | 后端实时日志（在 `/home/yudao-test` 下） |
| 落库检查 | `su - postgres -c "psql -d yudao_test -c 'select 1'"` | 确认走的是测试库 |

冒烟清单：登录 → 菜单（含 reagent/mes/wms/erp 模块页面）→ 打印/导出 → 上传下载（验证 `uploadfile` 挂载）。确认后端日志无连接测试库失败的报错。

## 6. 常用运维（隔离命令）

```bash
cd /home/yudao-test
docker compose --env-file .env up -d                          # 启动全部
docker compose --env-file .env down                           # 停止全部
docker compose --env-file .env restart server                 # 重启后端
docker compose --env-file .env logs -f server                 # 后端日志
# 改了 .env 后必须重建容器：
docker compose --env-file .env up -d --force-recreate server
```

## 7. 停用 / 清理（可选）

```bash
cd /home/yudao-test
docker compose --env-file .env down
# 删库（确认不要数据后再跑）：
# su - postgres -c "dropdb --force yudao_test"
# su - postgres -c "dropdb --force yudao_stg_pm_test"
```

## 8. 注意事项

- **不要**在生产目录 `/home/yudao` 里执行任何 compose 命令来操作测试环境。
- 测试环境**不接老ERP同步**：`erpSyncJob/erpPushJob` 不要创建/触发；`sqlserver`、`stgpm` 数据源均为 lazy，不影响启动。
- 需要升级测试库到最新生产结构时：重新 `pg_dump` 生产 → 清空/重建 `yudao_test` → `pg_restore`（数据量大时用 `-j 4` 并行加速）。
- 文件上传数据独立在 `/home/yudao-test/uploadfile/`，不会污染生产。
