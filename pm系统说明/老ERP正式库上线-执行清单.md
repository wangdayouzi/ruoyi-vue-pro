# 老ERP同步 → 正式库上线 执行清单（待办事项2）

> 目的：从当前测试/开发状态，把 老ERP 同步（阶段1 staging + 阶段2 正式表落地）落到**正式库**。
> 不含部署/打包/发版。只列 **SQL + 定时任务 + 手动准备** 三件事，按序执行即可。
> 全部 SQL 幂等，可重复跑。日期基准：2026-08-25。
>
> ⚠️ **方向变更（2026-08-25 晚）**：已停用 MES 库存/出入库流水方向，阶段2 改为
> **staging → 正式表 `mes_pm_inbound`/`_line`（采购入库单）+ `mes_pm_po`（采购订单）→ 两个页面**
> （PM采购入库单明细数据 + PM采购订单数据，供 试剂寄送/标签打印 基础信息）。领料/退料/退货同步、原 MES 推送 方法保留但不调用。
>
> ✅ **SQL 已汇总成两份**，下方只需执行：`老ERP同步-SQL汇总-1-stage库.sql`（stage）→ `老ERP同步-SQL汇总-2-yudao库.sql`（master）。

## 0. 前置确认（手动准备）

- [ ] 确认正式库连接串：staging 独立库 = `yudao_stg_pm`；master 库 = `yudao`（MES 表在 `mes` schema）
- [ ] 确认老 ERP（SQLServer `SHJH_Prj`）可读，同步统一 `WITH (NOLOCK)`，避开业务高峰
- [ ] 确认应用配置里 `sqlserver`（老ERP）+ `stgpm`（staging）两个数据源指向正式库（部署项，确认即可）

## 1. SQL（按序执行，就两份）

### 1.1 staging 库（yudao_stg_pm）—— 整份执行
执行 `老ERP同步-SQL汇总-1-stage库.sql`
（建 全部 stg_pm_* 表：入库/领料/退料/退货/分类/采购订单明细/字典 + sync_log/push_log + 列宽补列；幂等）

### 1.2 master 库（yudao）—— 整份执行
执行 `老ERP同步-SQL汇总-2-yudao库.sql`
（建 正式表 `mes.mes_pm_inbound`/`mes_pm_inbound_line`/`mes_pm_po` + 两个菜单（自动清旧“出入库流水”+授权超管）；幂等）
> 旧的 `erp-sync-*.sql`、`mes-wm-transaction-menu-pg.sql` 等不再单独跑。

### 1.3 （可选）全新开始 / 清脏数据
若正式库要**从零同步**或之前测试留了脏数据，执行 `老ERP清理-清空重来-新方向.sql`
（Part A 清空 staging 业务表+日志；Part B 清空 MES 里 ERP 推送的流水/批次/库存/孤儿物料。破坏性，确认后跑）

## 2. 定时任务（手动创建，基础设施→定时任务）

> 两个任务都是**手动触发为主**，不配自动 cron（可配 CRON 但建议手动）。
> 监控超时建议 **120000 ms**，避免任务卡住误显示"运行中"。

### 2.1 erpSyncJob —— 老ERP → staging 同步
| 配置项 | 值 |
|---|---|
| Handler | `erpSyncJob` |
| 参数（首次验证） | `30`（最近30天） |
| 参数（历史回填） | `60:0` → `60:60` → `60:120` …（每段60天往前挪）或显式 `2023-04-01~2023-08-31` |
| CRON | 留空，手动触发 |
| 监控超时 | `120000` |

### 2.2 erpPushJob —— staging → 正式表 落地（采购入库单 + 采购订单）
| 配置项 | 值 |
|---|---|
| Handler | `erpPushJob` |
| 参数 | `5000`（默认1000，本方向无实质影响） |
| CRON | 留空，手动触发 |
| 监控超时 | `120000` |

> 现做 `stg_pm_inbound` → `mes_pm_inbound/_line` + `stg_pm_po_line` → `mes_pm_po` 落地（幂等 upsert）；原 MES 库存/流水推送仍停用。
> 领料/退料/采购退货 已随 erpSyncJob 同步到 staging（`stg_pm_requisition`/`stg_pm_return_in`/`stg_pm_return_out`），`landPo` 时按订单行聚合 领用/退料/采购退货，现算 `remaining_qty`。
> ⚠️ 采购订单字段（订单日期/供应商/品牌/规格/分类/已入库）改了源 SQL，**需先重跑 erpSyncJob** 刷新 staging，再 erpPushJob 落地。

## 3. 上线执行顺序（手动操作）

1. **首轮验证**：跑 `erpSyncJob(30)` → 看 `stg_pm_sync_log` 最新行 `status=SUCCESS` 与 `result`（采购入库单/分类/采购订单明细/领料/退料/采购退货 计数）
2. **落地**：跑 `erpPushJob(5000)` → 看 `stg_pm_push_log` `status=SUCCESS`（result=`inboundMaster`+`poLines`）
3. **分段回填历史**：`erpSyncJob` 依次 `60:0`、`60:60`、`60:120` … 每段确认 SUCCESS，直到覆盖 **2023-04-27**（约 1220 天 ≈ 21 段；老库脆弱就用小段）；每段后可再跑一次 `erpPushJob` 落地
4. **全量落地**：`erpPushJob(0)` 多跑几轮（幂等，可反复）；确认 `mes_pm_inbound` 主表张数 = `stg_pm_inbound` 去重后一致，`mes_pm_po` 行数 = `stg_pm_po_line` 行数
5. **验收**：master 库跑 `老ERP同步-验收.sql`（含 采购入库单主/明细 汇总、采购订单数据）
6. **页面查看**：`PM采购入库单明细数据`（BAS/物料/供应商/批号/过期/存储位置/数量/金额）+ `PM采购订单数据`（订单日期/PO号/物料/品牌/规格/分类/供应商/数量/剩余），导出核对

## 4. 上线后注意事项

- **标签打印**：正式库上线后仍查老 ERP（现状不变）；后续再切（试剂寄送/标签打印 基础信息取 `mes_pm_inbound/_line`、`mes_pm_po`）
- **老库健康**：若发现老 ERP 有 `sp_trace_getdata` 长运行会话（疑似 Profiler/跟踪），找供应商排查，避免拖慢
- **权限**：给需要的角色在 系统管理→菜单管理 勾选“PM采购入库单明细数据”和“PM采购订单数据”
- **采购订单剩余量**：`mes_pm_po.remaining_qty = 已入库(pm01411) − 领用 + 退料 − 采购退货`（领用/退料/退货由 staging 聚合，行级 pm02712）。原 pm01420/pm01424 口径废弃（实测 pm01420≈0，非已领用）
- **改了同步源字段后**：必须重跑 erpSyncJob → erpPushJob 才有新列值
