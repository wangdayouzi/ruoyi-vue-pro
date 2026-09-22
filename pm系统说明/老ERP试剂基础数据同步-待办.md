# 老ERP 试剂基础数据同步（待办）

> 状态：**待办，先不改**（2026-08-26 方案已定，未实施）
> 关联：老ERP同步设计-staging.md（主同步链路）、老ERP字段词典.md

---

## 1. 目标

把老ERP采购入库单（**正式库** `mes_pm_inbound_line`，erpPushJob 已落地，与入库单明细页一致）里、**分类名命中试剂关键词（含其子孙分类）** 的物料，清洗成一份**试剂基础数据**给前端展示/后续使用。

## 1.1 决策：放弃现有手工表（2026-08-26）

- **试剂基础信息页面**：不再用现有 `reagent_base` / `reagent_base_lot`（手工维护的试剂管理主/子表），改由同步扁平表 `reagent_base_flat` 支撑，页面**只读+可编辑**（一行=一批）。
- `reagent_base` / `reagent_base_lot`：**已可删除**（前端/申请单/打印均已改用扁平表，无活跃引用）→ 清理 SQL：`老ERP试剂清理-旧主子表.sql`。
- 手工"新增/修改/删除试剂、新增/修改批号"功能：随页面重写一并移除（数据全部来自同步）。

## 2. 关键设计决策（已定）

- **bas_id = 采购入库单号（BAS，pm02603）**
- **试剂编号 = 材料编号（item_code，pm00201）**
- **不区分主子表**：只展示一张扁平表，**一行 = 一批 = 一个入库明细行**
- 幂等键：`src_line_id`（pm02702）

## 3. 扁平表 DDL（新建 `reagent_base_flat`）

> 不碰现有 `reagent_base` / `reagent_base_lot`（手工维护的试剂管理表）

```sql
CREATE TABLE IF NOT EXISTS reagent_base_flat (
  id               bigserial PRIMARY KEY,
  bas_id           varchar(40)  NOT NULL,            -- 采购入库单号 pm02603
  reagent_code     varchar(200),                     -- 试剂编号 = 材料编号 pm00201
  reagent_name     varchar(500),                     -- 试剂名称 pm00202
  vendor           varchar(500),                     -- 供应商 vendor_name
  cat_no           varchar(200),                     -- 货号（从 spec 清洗"货号：xxx"）
  spec             varchar(1000),                    -- 规格 pm00205
  lot_no           varchar(200),                     -- 批号 pm02631
  expire_date      varchar(40),                      -- 过期日期 pm02725
  amount_left      varchar(64),                      -- 参考剩余量（见 §5 待确认）
  storage_location varchar(500),                     -- 存储位置 pm02726
  storage_temp     varchar(32),                      -- 储存温度（老库无来源，先空/手填）
  item_category    varchar(500),                     -- 分类名 pm00102
  category_key     varchar(8),                       -- 分类键 pm00203（试剂分类识别/过滤用）
  src_line_id      varchar(40) NOT NULL UNIQUE,      -- 入库明细行ID pm02702（幂等键）
  src_receipt_id   varchar(40),                      -- 入库主ID pm02601
  status           smallint NOT NULL DEFAULT 0,
  sync_batch       bigint,
  sync_time        timestamp NOT NULL DEFAULT now()
);
```

## 4. 字段映射（staging stg_pm_inbound → 扁平表）

| 扁平表列 | 来源 | 说明 |
|---|---|---|
| `bas_id` | pm02603 入库单号 | |
| `reagent_code` | pm00201 材料编码 | 试剂编号 |
| `reagent_name` | pm00202 | |
| `vendor` | vendor_name | |
| `cat_no` | 从 `spec` 清洗 | "货号：xxx"→下一个键前；NA/无→空 |
| `spec` | pm00205 | 原样 |
| `lot_no` | pm02631 批号 | |
| `expire_date` | pm02725 | 清洗 NA/空 |
| `amount_left` | **批次级剩余** `mes_pm_inbound_line.remaining_qty` = 接收−领用+退料−采购退货（按明细行 src_line_id 聚合） | 每批精确剩余（已落地） |
| `storage_location` | pm02726 | |
| `storage_temp` | 无来源 | 空/手填 |
| `item_category` | pm00102 | |
| `category_key` | pm00203（staging 的 catalog_no 列） | |

## 5. 待确认 / 风险点

- [x] **参考剩余量 = 批次级剩余**（每批精确）：`mes_pm_inbound_line.remaining_qty` = 接收 − 领用 + 退料 − 采购退货，按**入库明细行 src_line_id** 聚合（staging 领料 pm02108 命中入库行；退料→领料行→入库行；采购退货直接命中入库行）。`landInbound` 计算落地，试剂同步直接读该列（不再 join 采购订单 PO 行）
- [ ] 存储温度：老库（sdpm026/027/002）**没有温度字段** → 只能空/手填（已做字典下拉 `reagent_storage_condition`），或另找来源
- [ ] 货号：`catalog_no`（pm00203）实为**分类键**不是货号；真货号混在 `spec` 文本里，需正则清洗
- [x] **试剂分类识别**：分类名命中关键词（试剂/标准品/血清/基质/切片/耗材/药品/危化/化学品/PBMC/生物/样本）+ 子孙展开（`mes_md_item_type` 递归 parent_id）。~~原"10"根假设已废弃~~（实测分类键是 `05F5174E` 这种字符串，人类编码在名称 `（2001.2）` 里）

## 6. 实施清单

**已完成（2026-08-26）：**
- [x] 建表 SQL：`老ERP试剂基础数据-建表.sql`（正式库执行）
- [x] 后端：`ReagentBaseFlatDO` / `ReagentBaseFlatMapper`（分页 + 关键词搜索 + `batchUpsertFlat` XML upsert） / `ReagentBaseFlatService(+Impl)` / `ReagentBaseFlatController`（`GET /page`、`PUT /update`、`GET /simple-list`，复用 `reagent:base:query/update` 权限）
- [x] **同步定时任务**：``（JobHandler，基础设施-定时任务 Handler 名 `reagentBaseFlatSyncJob`）+ `ReagentBaseFlatSyncService(+Impl)`：**读正式库** `mes_pm_inbound_line`（join `mes_pm_inbound` 取入库单号/供应商）→ **分类名命中试剂关键词 + 子孙展开**（`mes_md_item_type` 层级）过滤 → 清洗（货号从 spec 抠"货号：xxx"、过期 NA/空）→ 按 `src_line_id` upsert；
- [x] **关键词可配**：`ReagentBaseFlatSyncJob` 的 **Handler 参数必填**分类关键词（逗号/顿号/分号/空格分隔，如 `试剂,标准品`），参数为空会报错提示——改关键词不用改代码，直接改任务参数**无需 staging/erpSyncJob**；reagent pom 已加 `yudao-spring-boot-starter-job`
- [x] `mes_pm_inbound_line` 补 `category_key`（DO/XML/landInbound 落地时带 pm00203；正式库建表 SQL 里有 ALTER）
- [x] 前端：`api/reagent/index.ts` 加 `ReagentBaseFlatVO` + `getBaseFlatPage/updateBaseFlat/getBaseFlatSimpleList`；`views/reagent/base/index.vue` **重写为扁平表**（一行=一批，搜索 + 编辑）
- [x] **基础信息可编辑**：入库单号/试剂编号/批号只读；可改 = 试剂名称/供应商/货号/规格/储存温度/储存位置/参考剩余量/状态
- [x] **申请单选批号**：`DeliveryForm.vue` 改**弹窗选择**（关键词搜 试剂名称/编号/入库单号/货号 → 表格点选一批 → 自动带出试剂编号/名称/货号/规格/批号/温度/位置/过期）；需求数量仍手动填
- [x] **打印修复**：`ReagentPrintService` 原按 `reagent_base`/`reagent_base_lot` 反查名称/供应商/温度/规格/过期（改扁平表后对不上会丢字段）→ 改为用申请明细自带字段（名称/编号/货号/过期）+ 扁平表 `selectByReagentCodeAndLotNo` 补 供应商/温度/规格
- [x] `mvn compile` EXIT=0、前端无 lint 错误

**已完成（2026-08-27）：**
- [x] **剩余量口径落地（批次级）**：`amount_left` = `mes_pm_inbound_line.remaining_qty`（接收−领用+退料−采购退货，按明细行聚合）；试剂同步与入库单明细页均读批次剩余
- [x] **变更检测**：`stg_pm_inbound` 加 `data_hash`（行业务字段 MD5），`batchUpsertInbound` 变更时置 `pushed=0` → 老系统改单自动重推 → master `sync_time` 刷新 → 试剂表更新
- [x] **水位增量**：`reagentBaseFlatSyncJob` 默认增量（按 `reagent_base_flat` 最大 `sync_time` 水位过滤 `mes_pm_inbound_line.sync_time`）；参数 `full:关键词` = 全量（忽略水位 + 停用缺失行）
- [x] **源缺失/作废标停用**：全量同步后 `disableMissingBatch` 把本次未出现的正常行标 `status=1`
- [x] **链式任务**：`ErpReagentChainJob`（mes 模块）一条任务串 erpSync→erpPush→reagent；参数格式 `[full:][<erpSync窗口>]<关键词>`（窗口缺省 1 天、关键词缺省 试剂），如 `1:试剂`、`3650:试剂`、`365:365:试剂`（一年前~两年前）；`full:` 前缀=试剂全量+停用缺失（仅手动用）；2026-08-28 起支持 erpSync 窗口透传（N / N:offset / begin~end）
- [x] **基础信息页列显隐**：表格右上角「列设置」弹层勾选显隐
- [x] **申请单明细可编辑文本**：`reagent_apply_item` 补 `content/storage_temp/storage_location` 落库；「选择」按钮移到最前只作文本填充；试剂字段可手改（不动源表）；储存温度改字典下拉
- [x] **试剂扁平表加仓库**：`reagent_base_flat.warehouse`（join 主表 pm00402）+ 页面/弹窗展示

**待部署 SQL（幂等）：**
- [ ] staging 库：`老ERP同步-SQL汇总-1-stage库.sql`（新增 `stg_pm_inbound.data_hash` 列）
- [ ] 正式库：`老ERP试剂基础数据-建表.sql`（`reagent_base_flat.warehouse` + `mes_pm_inbound_line` 的 `category_key`/`remaining_qty`/`qty_requisition` ALTER）
- [ ] 正式库：`reagent_ddl.sql` 中 `reagent_apply_item` 补 `content/storage_temp/storage_location` 三条 ALTER
- [ ] 正式库：`reagent_storage_condition_dict.sql`（储存温度字典，若未跑过）
- [ ] 重新打包部署后端 + 触发 `erpReagentChainJob` 验收

**定时任务配置（最终）：**
> ⚠️ yudao 一个 Handler 名只能配一条任务，所以 10 分钟 和 每晚 用两个独立 Handler（逻辑相同，参数格式一致 `[full:][<erpSync窗口>]<关键词>`）。
>
> erpSync窗口 支持三种：`N`/`N天`=回溯 N 天；`N:offset`=N 天段、终点往前推 offset 天（如 `365:365`=一年前~两年前）；`begin~end`=显式日期范围（如 `2024-08-28~2025-08-28`）。

| Handler | cron | 处理参数 | 说明 |
|---|---|---|---|
| `erpReagentChainJob` | `0 */10 * * * ?` | `1:试剂` | 每10分钟：回溯1天 + 试剂增量 |
| `erpReagentChainNightlyJob` | `0 0 3 * * ?` | `3650:试剂` | 每晚03:00：回溯全历史(≈10年) + 试剂增量（老系统改任何旧单都能跟上，data_hash 只重推改动行） |

> 多个关键词：复制任务改参数（如 `1:标准品`）。`reagentBaseFlatSyncJob` 留手动，填 `full:试剂` 才触发全量+停用缺失；`erpSyncJob`/`erpPushJob` 不配 cron（链式已包）。

**待办/待确认：**
- [ ] 旧 `reagent_base` / `reagent_base_lot`：无活跃引用，**已删除** → 执行 `老ERP试剂清理-旧主子表.sql`（删表）
