# 老ERP → 中间库(staging) → 正式表 同步设计

> 整理日期：2026-08-26（当前方向：**采购入库单 / 采购订单 数据页**，非 MES 库存推送）
> 关联文档：`老ERP字段词典.md`（字段语义）、`老ERP正式库上线-执行清单.md`（上线执行）、`老ERP核对-两单数量.sql` / `老ERP同步-验收.sql` / `老ERP同步-手动核对.sql` / `老ERP核对-单号.sql`（验证）。

---

## 1. 方向与目标（当前）

**业务需求**：试剂寄送基础信息 + 标签打印基础信息 + 采购订单剩余量。领导当前要的不是"上新 MES 库存系统"。

**因此**：
- ✅ 做：采购入库单正式主子表 + 采购订单数据（含 已入库/领用/退料/采购退货/剩余）→ 两个页面
- ❌ 不做：MES 库存/出入库流水推送（原阶段2 MES 方向**已停用**，`pushInbound/pushRequisition` 等方法保留但**不调用**，不写 `mes_wm_*` 流水/库存表）
- ❌ 不做：标签打印（后续整改项）、自动定时（手动触发为主）

**页面**（MES 仓库管理 5780 下）：
| 页面 | 数据源 | 说明 |
|---|---|---|
| PM采购入库单明细数据 | `mes_pm_inbound` + `mes_pm_inbound_line` | 每行按 `src_po_line_id`(pm02712) 行级带出关联订单行的 已入库/领用/剩余 |
| PM采购订单数据 | `mes_pm_po` | 采购数量/已入库/领用/退料/采购退货/剩余；**无"最大/最小剩余量"筛选** |

---

## 2. 总体链路

```
老ERP(SQLServer, SHJH_Prj, 只读, 全表 NOLOCK)
   │  erpSyncJob（yudao-module-system，@DS("sqlserver")，按日期窗口拉取）
   ▼
staging 独立库（PostgreSQL, yudao_stg_pm, 表前缀 stg_pm_）
   │  erpPushJob（yudao-module-mes，@DS("stgpm") 读 staging → 落地）
   ▼
正式表（yudao, mes schema：mes_pm_inbound/_line、mes_pm_po）
   │
   ▼
两个页面（前端 views/mes/pm/{inbound,po}/index.vue）
```

**依赖约束**：`mes → system`（mes 依赖 system，可复用 staging Mapper/DTO）；system 不能依赖 mes → 阶段1 拉取放 system，阶段2 落地放 mes。

---

## 3. 阶段1：老ERP → staging（system 模块）

**同步的表**（全部只读 + NOLOCK + 按各自期间窗口）：

| staging 表 | 源 | 用途 |
|---|---|---|
| `stg_pm_inbound` | sdpm026 主 + sdpm027 明细 | 采购入库单明细（扁平行） |
| `stg_pm_po_line` | sdpm013 主 + sdpm014 明细 | 采购订单明细（采购数量 + 已入库） |
| `stg_pm_item_category` | sdpm001 | 物料分类（全量，行少） |
| `stg_pm_requisition` | sdpm020 主 + sdpm021 明细 | 领料（**用于已领用聚合**） |
| `stg_pm_return_in` | sdpm024 主 + sdpm025 明细 | 退料（**用于已领用聚合**） |
| `stg_pm_return_out` | sdpm022 主 + sdpm023 明细 | 采购退货（**用于已领用聚合**） |

**关键字段 / 关联键**：
- 入库行：`src_line_id`=pm02702、**`src_po_line_id`=pm02712**（→订单行 pm01402，行级关联）、`po_id`=pm02612、`qty`=pm02706×pm02705
- 采购订单行：`line_id`=pm01402、`po_id`=pm01401、`qty_ordered`=pm01406、**`qty_received`=pm01411**
- 领料行：`src_line_receipt_id`=pm02108（→入库行 pm02702）
- 退料行：`src_line_receipt_id`=pm02507（→领料行，经领料行到入库行）
- 采购退货行：`src_line_receipt_id`=pm02311（→入库行 pm02702）

**过滤条件**：
- 入库：`pm02620=0`(未作废)、`pm02617=1`(已审核)、`pm02623 IN (1,6)`、`NOT EXISTS(调拨生成)`；窗口 `pm02614`
- 采购订单：窗口按订单日期 `pm01302`；**累积 upsert（去窗口快照）** —— 历史回填的订单行保留，增量只新增/更新
- 领料：`pm02022=0`(非调拨)、`pm02019=1`(已审核)；窗口 `pm02016`
- 退料：`pm02419=1`(已审核)；窗口 `pm02416`
- 采购退货：`pm02217=1`、`pm02215=1`(已审核)；窗口 `pm02214`
- GUID 大小写不敏感：join 一律 `UPPER()` 归一

**参数格式（erpSyncJob）**：
- `N` —— 回溯 N 天（默认 1）
- `N:offset` —— 分段回填（如 `60:0`、`60:60`…）
- `begin~end` —— 显式日期范围
- 每次自动同步 6 张表，结果记 `stg_pm_sync_log.result`

---

## 4. 阶段2：staging → 正式表（mes 模块）

### 4.1 landInbound —— 采购入库单
`stg_pm_inbound` → `mes_pm_inbound`（主，按 src_receipt_id 去重）+ `mes_pm_inbound_line`（明细，**带 `src_po_line_id`=pm02712**）。
幂等：主表 `ON CONFLICT (src_receipt_id)`，明细 `ON CONFLICT (src_line_id)`。

### 4.2 landPo —— 采购订单 + 现算数量
`stg_pm_po_line`（**累积** upsert，不清空；`landPo` 时 `deleteAll mes_pm_po` 后全量重写）+ 从 staging 三张单据表按订单行聚合：

| mes_pm_po 列 | 来源 |
|---|---|
| `qty_ordered` | pm01406（存列） |
| `qty_received` | pm01411（存列） |
| `qty_requisition` | Σ 领料（stg_pm_requisition → 入库行 → 订单行） |
| `qty_return` | Σ 退料（stg_pm_return_in → 领料行 → 入库行 → 订单行） |
| `qty_return_out` | Σ 采购退货（stg_pm_return_out → 入库行 → 订单行） |
| `remaining_qty` | **已入库 − 领用 + 退料 − 采购退货**（现算） |

聚合在 **yudao 侧**（读 staging）做，**不加重老库实时查询**；老库只做窗口增量拉取。10 分钟一同步也不压老库。

### 4.3 页面行级关联
入库单明细页 `LEFT JOIN mes_pm_po po ON UPPER(po.line_id) = UPPER(l.src_po_line_id)`：
- 有 `src_po_line_id` 且订单行已同步（累积，无窗口限制）→ 显示 已入库/领用/剩余
- 无关联（其他入库/无 PO）→ 显示 `-`

### 4.4 landMasterData —— 基础数据按需补缺（2026-08-26 新增）
`erpPushJob` 落地最前先跑 `landMasterData`，**只补"业务用到但主数据缺失"的**，不建全量、不覆盖已有、不碰批次/库存/流水：

| 落地表 | 数据源 | 策略 |
|---|---|---|
| `mes_md_item_type` 分类 | `stg_pm_item_category`（stage1 已全量同步） | 全量落地（含 parent_id 层级），code=pm00101 |
| `mes_md_item` 物料 | 业务编码 = `stg_pm_inbound ∪ stg_pm_po_line` 去重 → **缺失**编码 → 老库 `sdpm002` 按编码点查 → resolveItem 插入 | 只补缺失；已有不覆盖；复用 resolveItem（含分类/单位解析） |
| `mes_md_vendor` 供应商 | `stg_pm_inbound` 去重 (vendor_id, vendor_name) | 按需 resolveVendor（code=vendor_id） |
| `mes_md_unit_measure` 单位 | `stg_pm_inbound` 去重 unit_name | 按需 resolveUnit（code/name=unit_name） |

要点：
- **老库只在"有缺失物料编码"时才被点查**（按 `IN (缺失编码)`，量小）；无缺失则完全不碰老库。
- 分类先落地（物料需要分类 ID）；`stg_pm_item/vendor/unit` 三张"预留" staging 表继续不用（按需补缺直接读业务表）。
- 目的：物料产品管理/供应商/单位页面有"业务真实用到的"基础数据；老库有但业务没用的不进系统。

---

## 5. 字段口径勘误（2026-08-25 实测，重要）

- ❌ `sdpm014.pm01420` **不是"已领用"**（全库≈0）
- ✅ `sdpm014.pm01411` = **已入库数量**（存列）
- 入库单/订单行上**没有"已领用"存列** → 已领用只能由 领料(sdpm021) − 退料(sdpm025) + 采购退货(sdpm023) 现算（老ERP视图 `sdvw_pm027` 同口径）
- 行级关联：入库行 `pm02712` = 订单行 `pm01402`（老ERP视图 `LEFT JOIN sdpm014 ON pm01402=C.pm02712` 证实）
- 原 `qty_issued`(pm01420) / `qty_substitute`(pm01424) 列**已废弃**

---

## 6. SQL / 部署

**执行 SQL（以此为准，已合并）**：
| 文件 | 库 | 内容 |
|---|---|---|
| `老ERP同步-SQL汇总-1-stage库.sql` | yudao_stg_pm | staging 建表/补列（幂等） |
| `老ERP同步-SQL汇总-2-yudao库.sql` | yudao | 正式表 + 两个菜单 + 授权（幂等） |
| `老ERP清理-清空重来-新方向.sql` | 两库 | 清空重来（可选） |

**定时任务**（基础设施 → 定时任务，手动触发）：
- `erpSyncJob`：首次 `30` 验证；历史回填 `60:0`→`60:60`→…；监控超时 `120000`
- `erpPushJob`：`5000`（参数无实质影响，本方向幂等）；监控超时 `120000`

**执行顺序**：跑汇总 SQL → 重启后端 → `erpSyncJob` → `erpPushJob` → 验收。完整清单见 `老ERP正式库上线-执行清单.md`。

---

## 7. 验证

| 方式 | 文件 | 说明 |
|---|---|---|
| 三方对数 | `老ERP核对-两单数量.sql` | 源 / staging / 正式表 行数一致（采购入库单、采购订单） |
| 上线验收 | `老ERP同步-验收.sql` | 四部分验收 |
| 手动核对 | `老ERP同步-手动核对.sql` | 抽样核对 |
| 单号抽查 | `老ERP核对-单号.sql` | 某 BAS 对老系统 |
| 同步日志 | `stg_pm_sync_log` | 各表行数（inbound/category/poLine/requisition/returnIn/returnOut） |

---

## 8. 注意 / 局限

- **采购订单已去窗口快照**（累积 upsert），分段回填/10 分钟增量都不会覆盖历史订单行。
- **剩余量正确性仍依赖 领料/退料/采购退货 的同步窗口**：这三张按各自期间窗口同步，窗口外的出库记录不参与聚合 → 超老订单（领用发生在未同步期间）的剩余可能偏大。10 分钟滚动近期用法没问题；如需历史准确，erpSyncJob 回溯窗口调大（如 `30` 天）或分段回填覆盖到对应期间。
- 已领用/退料/采购退货 **单独存列**（不做合成"已领用"），口径更清晰、和老系统入库单界面一致。
- 阶段2 只落地 PM 两张正式表，**不碰 mes_wm_\* 流水/库存**（老方向方法保留未调用）。
