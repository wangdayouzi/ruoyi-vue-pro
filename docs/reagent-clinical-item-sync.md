# 试剂管理：临床方案 / 项目号同步（2026-11-03）

> 在「试剂管理」下新增两个基础数据页面：**临床方案**、**项目号**。
> 数据来源：老样本系统 MySQL（库 `yangben_com`，账号 `yangben_com`，密码自行填写）。
> 每天 00:00 全量同步一次，也支持页面「立即同步」与定时任务手动触发。

---

## 1. 数据流

```
老样本系统 MySQL yangben_com                本地 PostgreSQL（yudao）
  think_clinical  ──────────────┐
                                ├─ 定时任务 reagentBasicDataSyncJob（每天 00:00）
  think_items ── left join ─────┘        │
        think_clinical                   ├─ upsert → reagent_clinical
                                         └─ upsert → reagent_item
```

- 本地表 `id` 直接沿用源表主键，便于与源系统对账。
- 同步为 **全量 + 按 id 幂等 upsert**：源表已有行更新、没有的行停用（`status=1`），不物理删除。
- `think_items` 同步时 `left join think_clinical`，把 **临床方案名称 / 编号** 一并落到 `reagent_item`，项目号页面无需再关联查询。
- 源表 `create_time / update_time` 是 unix 秒，同步时按 `Asia/Shanghai` 转为 `timestamp`，分别存 `src_create_time / src_update_time`，页面展示为「添加时间 / 编辑时间」。

## 2. 不同步的字段（本地维护）

`think_clinical.is_auto_receive`（**是否自动发送寄送通知邮件**）不参与同步：

- 本地表有 `reagent_clinical.is_auto_receive`，默认 `0`；
- 同步 upsert 的 INSERT 列和 `ON CONFLICT DO UPDATE` 都不包含该列 → 页面手工配置的值不会被夜间任务覆盖；
- 页面「编辑」弹窗只允许改这个本地字段（`PUT /reagent/clinical/update`）。

## 3. 源库数据源配置

已加入 `application-local.yaml` / `application-dev.yaml` / `application-prod.yaml`：

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        yangben: # 临床方案/项目号同步 - 老样本系统 MySQL（只读），库 yangben_com
          lazy: true
          url: jdbc:mysql://${YANGBEN_DB_HOST:192.168.10.236}:${YANGBEN_DB_PORT:3306}/${YANGBEN_DB_NAME:yangben_com}?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true
          driver-class-name: com.mysql.cj.jdbc.Driver
          username: ${YANGBEN_DB_USERNAME:yangben_com}
          password: ${YANGBEN_DB_PASSWORD:} # TODO 填写数据库密码
```

- **密码**：本地开发直接改 `password`；生产环境建议用环境变量 `YANGBEN_DB_PASSWORD` 注入。
- 主机 / 端口 / 库名 / 账号都可用环境变量覆盖，默认值取自导出 SQL 头部的源库信息（192.168.10.236:3306 / yangben_com）。
- Docker 部署：`script/docker/.env`、`script/docker-test/.env` 已加 `YANGBEN_*` 配置，对应 `docker-compose.yml` 已透传到容器。
- 源库 Mapper 使用 `@DS("yangben")`，读源库与写本地库互不影响；**同步方法不要加 `@Transactional`**（动态数据源在事务内切换不生效）。

## 4. 本地表

建表脚本：`sql/postgresql/增量/2026-11-04-02-reagent.sql`（可重复执行）。

| 表 | 说明 | 关键列 |
|---|---|---|
| `reagent_clinical` | 临床方案 | `id`、`name`、`number`、`sponsor_id`、`is_upload`、`effective_date`、`version_number`、`is_auto_receive`（本地）、`src_create_time`、`src_update_time`、`status`、`sync_batch`、`sync_time` |
| `reagent_item` | 项目号 | `id`、`city_id`、`base_id`、`clinical_id`、`clinical_name`、`clinical_number`、`person_id`、`type_id`、`is_aging`、`review_status`、`reviewer`、`is_report`、`is_pm`、`src_create_time`、`src_update_time`、`status`、`sync_batch`、`sync_time` |

## 5. 定时任务

- Handler：`reagentBasicDataSyncJob`（`ReagentBasicDataSyncJob implements JobHandler`）
- 默认 cron：`0 0 0 * * ?`（每天 00:00），无参数
- **自动登记**：`ReagentBasicDataJobInstaller` 在应用启动完成后调用 `JobService.createJob` 写入 `infra_job` 并注册到 Quartz（仅当 `infra_job` 无同 handler 记录时）。这样避免“只插 infra_job 但 Quartz 无调度”的坑
- 需要改时间/暂停：到「基础设施 → 定时任务」编辑「临床方案/项目号同步 Job」
- 失败策略：源库连不上 / 两张源表都为空时抛异常，任务日志标记失败；只有单张源表为空时跳过该表并记 WARN，**不会把本地数据全部停用**

## 6. 接口与页面

### 6.1 业务页面接入（2026-11-03）

> 加急入库、领用台账（PC + 手机端）的完整业务说明见 [sample-urgent.md](sample-urgent.md)。

- **加急入库**：
  - 临床方案：单选，`reagent_clinical`，下拉只展示临床方案编号，支持按编号模糊搜索，也允许直接输入新编号；
  - BAS 号（项目号）：多选，来源 `reagent_item.base_id`，按已选临床方案过滤，允许输入后缀（如 `22BAS0055-Blind`），保存仍按「、」拼接；BAS 号就是项目号，不再单独设项目号字段；
  - 新增字段仅 `sample_urgent.clinical_id`，历史 `project_no` / `bas_nos` 原样保留。
- **领用台账（PC + 手机端）**：列表搜索框改成 BAS 模糊提示（仍可按输入值查询）；新增/修改表单的 BAS 号改成同源模糊提示/下拉，允许输入特殊 BAS。
- 迁移脚本：`sql/postgresql/增量/2026-11-04-02-reagent.sql`。

### 6.2 接口

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/admin-api/reagent/clinical/page` | `reagent:clinical:query` | 临床方案分页 |
| PUT | `/admin-api/reagent/clinical/update` | `reagent:clinical:update` | 修改 `isAutoReceive`（本地） |
| GET | `/admin-api/reagent/item/page` | `reagent:item:query` | 项目号分页 |
| POST | `/admin-api/reagent/basic-data/sync` | `reagent:basic-data:sync` | 手动全量同步（两张表） |
| GET | `/admin-api/reagent/basic-data/clinical-options` | `reagent:basic-data:select` 或加急/临床查看权限 | 临床方案下拉（名称/编号模糊） |
| GET | `/admin-api/reagent/basic-data/item-options` | `reagent:basic-data:select` 或项目号/加急/领用查看权限 | 项目号/BAS 下拉（可传 `clinicalId` 过滤） |

前端：

- `src/api/reagent/basicData.ts`
- `src/views/reagent/clinical/index.vue` → 菜单「临床方案」（`reagent/clinical/index`）
- `src/views/reagent/item/index.vue` → 菜单「项目号」（`reagent/item/index`）

菜单挂在「试剂管理」目录下，增量 SQL 会自动定位该目录并插入两个菜单 + 按钮权限；菜单 ID 从 `system_menu` 当前最大值 +1 顺序分配，并同步 `system_menu_seq`，避免之后页面新增菜单时 ID 冲突。**不会自动给角色授权**，需要在「系统管理 → 角色」里勾选新菜单/按钮。

## 7. 部署步骤

1. 停止后端，先执行钉钉发布 SQL `2026-11-04-01-dingtalk.sql`，再执行 `sql/postgresql/增量/2026-11-04-02-reagent.sql`（建表、菜单、加急入库字段及通知迁移；定时任务由后端启动自动登记）。

2. 在 `application-local.yaml`（或生产环境变量 `YANGBEN_DB_PASSWORD`）填写源库密码。
3. 重新打包部署后端；前端发布 `yudao-ui-admin-vue3-xn`。
4. 启动后确认「基础设施 → 定时任务」出现「临床方案/项目号同步 Job」（cron `0 0 0 * * ?`），手动执行一次，确认结果摘要与页面数据。
5. 给相关角色勾选「临床方案 / 项目号」菜单及 `reagent:*` 按钮权限；加急/领用角色已有 query 权限即可使用下拉，不必额外授权。

## 8. 验收要点

- 临床方案页面能按名称/编号搜索，展示源表数据；「自动寄送通知」可在编辑弹窗修改。
- 项目号页面能按项目号/方案编号/方案名称/城市/复核状态筛选，方案名称与编号来自 `think_clinical`。
- 手动同步返回摘要（如：临床方案 1059 行 / 项目号 2624 行）。
- 修改某个方案的「自动寄送通知」后再次同步，值保持不变。
- 在源库删除一条测试数据后同步，本地对应行 `status=1` 且页面显示「已停用」。
- 加急入库：临床方案下拉只显示编号，可按编号搜索；BAS 多选按项目号搜索，并支持输入 `-Blind` 等后缀。
- 领用台账（PC + 手机端）：列表输入 BAS 前缀能出现建议；新增/修改表单能从提示中选 BAS，也能输入不在同步表里的特殊 BAS。
