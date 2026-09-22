# 老ERP(SHJH_Prj) 字段词典 & MES 映射参考

> 用途：入库单/领料单/库存 同步到 yudao-MES 的设计依据（只读源库）。
> 更新：2026-08-25（基于 出入库流水/仓库存货 视图 + 两轮诊断采样确认 + 阶段1/2落地）
> 字段标注：✓=已确认语义；~(疑似)=待复核；未列出的字段=本方案未用到。

## 0. 概览

| 项 | 值 |
|---|---|
| 库 | `SHJH_Prj`（schema=dbo） |
| 只读连接 | yudao `@DS("sqlserver")`（已用于试剂标签打印） |
| 同步范围 | 采购入库/其他入库(`sdpm026/027`) + 项目领料(`sdpm020/021`) + 项目退料(`sdpm024/025`) + 采购退货(`sdpm022/023`) + 库存 |
| 不做(v1) | 调拨出入(10/11)、盘库(`sdpm015/016`)、费用入库(34) |
| 底座 | MES wm：`mes_md_item` / `mes_wm_batch` / `mes_wm_transaction` / `mes_wm_material_stock` |
| 主数据 | 不单独同步，随单据冗余实际值 |
| 源库保护 | 日常只走索引增量（`pm02614`/`pm02016`），不跑 `sdvw_*` 巨型视图 |

## 1. 表规模（sys.partitions）

| 表 | 行数 | 说明 |
|---|---|---|
| sdpm026 | 36,919 | 采购入库主表 |
| sdpm027 | 43,282 | 采购入库明细 |
| sdpm020 | 109,717 | 领料单主表 |
| sdpm021 | 126,382 | 领料明细 |
| sdpm002 | 42,502 | 物料主数据 |
| sdpm003 | 50,914 | 物料-单位关联 |
| sdpa013 | 97 | 计量单位 |
| sdpm004 | 23 | 仓库 |

## 2. 核心表字段

### 2.1 sdpm026 采购入库【主表】—— 入库同步源头
| 字段 | 类型 | 语义 |
|---|---|---|
| pm02601 | varchar(40) | ✓ PK，入库单内部主键(GUID) —— **同步唯一键** |
| pm02602 | varchar(10) | ✓ 单据日期（如 2025-02-18） |
| pm02603 | varchar(40) | ✓ 入库单编码 = **BAS号**（rk20230725011 / FAJH-562 / SH-Cell-1408 / SP2-028），**有重复，不可当唯一键** |
| pm02604 | varchar(40) | ✓ 供应商 → sdpf003.pf00301 |
| pm02605 | varchar(40) | ✓ 仓库 → sdpm004.pm00401 |
| pm02606 | varchar(255) | ✓ 备注 |
| pm02609 | varchar(40) | ✓ 制单人 → sdpj004 |
| pm02612 | varchar(40) | ✓ 采购订单 → sdpm013.pm01301（冗余 PO 号用） |
| pm02614 | varchar(20) | ✓ 年月（如 2023-07-25）—— **增量索引 IX_sdpm026_pm02614** |
| pm02616 | varchar(40) | ✓ 审核人 |
| pm02617 | int | ✓ TCheck |
| pm02620 | tinyint | ✓ 作废标记，0=有效 |
| pm02623 | tinyint | ✓ 单据类型：1=采购入库(36,519)，6=其他入库(402) |
| pm02631 | varchar(200) | ✓ 批号（常为空） |
| pm02632 | tinyint | ✓ IsFullRequisition |

### 2.2 sdpm027 采购入库【明细】—— 批次/过期源头
| 字段 | 类型 | 语义 |
|---|---|---|
| pm02701 | varchar(40) | ✓ 主表 → sdpm026.pm02601 |
| pm02702 | varchar(40) | ✓ PK，明细行号(GUID) —— **领料 pm02108 的命中键** |
| pm02703 | varchar(40) | ✓ 物料 → sdpm002.pm00200 |
| pm02704 | varchar(18) | ~ 单位 → sdpa013.pa01301（疑似，待复核） |
| pm02705 | decimal | ✓ 换算率（通常 1.0） |
| pm02706 | decimal | ✓ 数量（入库量 = pm02706 × pm02705） |
| pm02707/02708 | decimal | ✓ 含税单价 / 含税金额 |
| pm02709/02710 | decimal | ✓ 不含税单价 / 不含税金额 |
| pm02723 | varchar(40) | ✓ 项目 → sdpa001.pa00101 |
| pm02725 | varchar(40) | ✓ 过期日期（'2024-04-30' / 'NA' / 空，**需清洗**） |
| pm02726 | varchar(500) | ✓ 存放位置（如 FRZ-04-014-01） |
| pm02728 | smallint | ✓ 行序（批次身份拼串用） |

### 2.3 sdpm020 领料单【主表】—— 出库同步源头
| 字段 | 类型 | 语义 |
|---|---|---|
| pm02001 | varchar(40) | ✓ PK，领料单内部主键(GUID) |
| pm02002 | varchar(10) | ✓ 单据日期 |
| pm02003 | varchar(40) | ✓ 领料单号（ll20250218056） |
| pm02004 | varchar(40) | ✓ 项目 → sdpa001.pa00101（**冗余项目名**） |
| pm02006 | varchar(40) | ✓ 仓库 → sdpm004.pm00401 |
| pm02008 | varchar(40) | ✓ 供应商 → sdpf003.pf00301（冗余供应商名） |
| pm02012 | varchar(40) | ✓ 领料人 → sdpj004 |
| pm02015 | varchar(40) | ✓ 来源入库单 → sdpm026.pm02601（调拨/关联） |
| pm02016 | varchar(20) | ✓ 年月 —— **增量索引 IX_sdpm020_pm02016** |
| pm02017 | varchar(40) | ✓ 审核人 |
| pm02019 | int | ✓ TCheck（采样=1） |
| pm02022 | tinyint | ✓ 调拨标记，0=非调拨 |
| pm02024 | varchar(40) | ✓ 调拨来源入库单（空=非调拨） |

### 2.4 sdpm021 领料【明细】—— 扣减批次定位
| 字段 | 类型 | 语义 |
|---|---|---|
| pm02101 | varchar(40) | ✓ 主表 → sdpm020.pm02001 |
| pm02102 | varchar(40) | ✓ PK，明细行号(GUID) |
| pm02103 | varchar(40) | ✓ 物料 → sdpm002.pm00200 |
| pm02104 | decimal | ✓ 数量（出库量，正数） |
| pm02105 | decimal | ✓ 单价（不含税） |
| pm02107 | decimal | ✓ 含税单价 |
| pm02108 | varchar(40) | ✓ 入库明细行 → sdpm027.pm02702（**扣到哪个批**，索引 IX_sdpm021_recv） |

### 2.5 sdpm002 物料主数据 —— 字典
| 字段 | 类型 | 语义 |
|---|---|---|
| pm00200 | varchar(100) | ✓ PK，物料内部主键(GUID) |
| pm00201 | varchar(200) | ✓ 物料编码（人读，1001.2-00858）→ **mes_md_item.code** |
| pm00202 | nvarchar(500) | ✓ 物料名称（Mouse Anti-RC208Mab / 个体基质）→ **mes_md_item.name** |
| pm00203 | varchar(40) | ✓ 已确认=**分类键**（join sdpm001.pm00101→分类名 pm00102，如 C503B5C9=“（1001.2）合作方寄送抗体/蛋白”）；**不是货号**（真货号混在 pm00205 规格文本里，如“货号：NA 容量：0.6mL/Vial”） |
| pm00205 | nvarchar(1000) | ✓ 规格/容量（"货号：NA  容量：0.6 mL/Vial"）→ **mes_md_item.specification** |
| pm00207 | decimal | ~ 参考价（采样=0） |
| pm00218/19 | varchar(40) | ✓ 已确认=人员ID（创建人/修改人，join sdpj004.pj00401），**不是分类** |
| pm00221 | varchar(500) | ✓ 品牌/厂商（Sino Biological / 上海精翰生物） |

### 2.6 关联小表
| 表 | 字段 | 语义 |
|---|---|---|
| sdpm003 | pm00301→物料；pm00302→单位；pm00303=1 启用 | 物料单位关联 |
| sdpa013 | pa01301=单位编码；pa01302=单位名称（件/盒/ug…） | 计量单位 |
| sdpm004 | pm00401=仓库GUID；pm00402=仓库名称；pm00404=仓管员 | 仓库 |
| sdpf003 | pf00301=供应商编码；pf00302=供应商名称 | 供应商 |
| sdpa001 | pa00101=项目GUID；pa00102=项目名称；pa00140=项目号 | 项目（只冗余名称） |
| sdpm013 | pm01301=PK；pm01302=日期；pm01303=采购订单号(PO260616024)；pm01305=合同(sdpd004.pd00401) | 采购订单（只冗余PO号） |

### 2.7 物料分类 sdpm001（分类键 → mes_md_item_type）
| 字段 | 类型 | 语义 |
|---|---|---|
| pm00101 | varchar(8) | ✓ 分类键（唯一，如 C503B5C9；物料 pm00203 指向它）→ **mes_md_item_type.code** |
| pm00102 | varchar(100) | ✓ 分类名（“（2001.2）合作方寄送实验室常规耗材”）→ **mes_md_item_type.name** |
| pm00103 | varchar(8) | ✓ **父分类键**（顶级为空；如 1001.2 的父=1001）→ **mes_md_item_type.parent_id** |
| pm00104 | varchar(80) | 祖先链（顶级键+父键+自身键 拼接，如 79fe2b2dA4537F4EC503B5C9） |
| pm00105 | varchar(40) | 层级编号（如 1001.2 / 10 / 20 …；部分行有脏值/为空） |
| pm00106 | varchar(8) | 人员/启用 相关（a645182d…，未深究） |
| pm00107 | decimal(17) | 排序/备用（采样全 0） |

> 分类层级：顶级=(10)试剂/(20)耗材/(30)设备/(40)备品件/(50)基质/(60)服务/(70)设施管理/(80)计算机化系统/(90)其他/(99)询价，共约 160 条，2~3 级，**父级用 pm00103 直接关联**（不用点号猜）。
> 同步链路：sdpm001 全量 → `stg_pm_item_category`（cat_key/cat_name/parent_key）→ 推送建 `mes_md_item_type` 两趟（先建全部 parentId=0，再按父键挂 parent_id）。

## 3. 关键口径 & 决策

| # | 决策 | 依据 |
|---|---|---|
| 1 | 同步唯一键 = `pm02601`（入库） / `pm02001`（领料） | pm02603 有重复 |
| 2 | 批次身份 `mes_wm_batch.code` = `pm02601 + '_' + pm02728` | pm02601 唯一 + 行序唯一 |
| 3 | batch 增自定义列存 `pm02702`，供领料 `pm02108` 直接命中批次 | 避免跨表反查 |
| 4 | batch 冗余 BAS号(pm02603) / 原批号(pm02631) / 过期(pm02725，清NA/空) / 存放位置(pm02726) | 标签打印 & 基础数据页用 |
| 5 | 入库：建批次 + 流水 type=1（正数）；领料：流水 type=2（负数） | 对齐 mes_wm_transaction |
| 6 | 库存 `mes_wm_material_stock` 按 物料+批次(+仓库) 聚合，由流水驱动 | 剩余量=入库-累计出库 |
| 7 | 入库单冗余：供应商名 + 仓库名 + PO号(pm01303)；领料单冗余：项目名 + 供应商名 | "随单据拿实际值" |
| 8 | 合同号(sdpd004) 首期不做 | 标签/基础页用不到 |
| 9 | 类型过滤：入库取 pm02623 IN (1,6) 且 pm02620=0；领料取 pm02022=0（非调拨） | 采样确认 |
| 10 | 日常同步只走索引增量：入库按 pm02614，领料按 pm02016 | 保护慢源库 |
| 11 | 老库存快照(sdvw_StockQty) **不每日同步**：新库存由单据推导(mes_wm_material_stock)；首期历史单据全量回填做期初基线，sdvw_StockQty 仅低频对账 | 快照重/丢批次/拉会卡源库 |

## 3.x 单据类型(TType)映射与同步范围

| TType | 业务名 | 源表 | 方向 | 同步? |
|---|---|---|---|---|
| 30 / 32 / 33 / 35 | 采购入库 | sdpm026/027 (pm02623=0/1/2/3/4) | 入 | ✅ 必须 |
| 41 | 库存调整/其他入库 | sdpm026/027 (pm02623=6) | 入 | ✅ 必须（同表零成本） |
| 20 | 项目领料 | sdpm020/021 (pm02022=0) | 出 | ✅ 必须 |
| 21 | 项目退料 | sdpm024/025 | 入·回库 | ✅ 建议（表小保剩余准确） |
| 37 / 40 / 81 / 36 | 采购退货出库 | sdpm022/023 (pm02217=1/2/3/4) | 出 | ✅ 建议（表小保剩余准确） |
| 11 | 调拨入库 | sdpm026/027（调拨领料自动生成） | 入 | ⚠️ v1 忽略（净额0，按仓看剩余才需） |
| 10 | 调拨出库 | sdpm020/021 (pm02022=1) | 出 | ⚠️ v1 忽略 |
| 34 | 费用入库 | sdpm017/018 | 入 | ❌ 不做 |
| 60 / 61 | 盘点入/出 | sdpm015/016 | 入/出 | ❌ 不做 |

> 剩余量口径 = 入库(30/32/33/35/41) − 领料(20) + 退料(21) − 退货(37)，对齐出入库流水视图 receiveQty。
> 调拨排除口径（v1）：调拨出入靠**类型过滤**排除——入库取 `pm02623 IN (1,6)`（调拨出入 10/11 不在此列）；领料只取 `pm02022=0`（pm02022=1 为调拨出库，排除）。
> **入库单不再按 sdpm020 调拨关联过滤**：调拨自动生成的采购入库单（如 SH-BAS262888，无 PO/无供应商、docType=1）也要正常同步，避免误过滤其他形式生成的采购入库单。
> 流水账 UI 默认 TType=[41,10,11,20,21,30,37] 只是"全类型显示"，≠ 必须全部同步。
> 已确认：出入库流水账 UI 的每个菜单类型都有对应源表——采购入库/库存调整(30/41)→sdpm026/027；项目领料(20)→sdpm020/021；项目退料(21)→sdpm024/025；采购退货(37)→sdpm022/023；调拨出库(10)→sdpm020/021(pm02022=1)；调拨入库(11)→sdpm026/027(调拨自动生成)。

## 4. 索引清单（增量/join 依据）

| 表 | 索引 | 键 | 用途 |
|---|---|---|---|
| sdpm026 | IX_sdpm026_pm02614 | pm02614(key) + pm02601/20/23/05/17(incl) | 入库按年月增量 |
| sdpm026 | PK_sdpm026 | pm02601 | 唯一键 |
| sdpm027 | PK_sdpm027 | pm02702 | 明细唯一键 |
| sdpm027 | IX_sdpm027_detail | pm02702(key)+pm02703(key)+其余(incl) | 领料→明细 join |
| sdpm020 | IX_sdpm020_pm02016 | pm02016(key)+pm02001/19/22(incl) | 领料按年月增量 |
| sdpm020 | IX_sdpm020_pm02002 | pm02002 | 按日期 |
| sdpm020 | PK_sdpm020 | pm02001 | 唯一键 |
| sdpm021 | IX_sdpm021_recv | pm02108(key)+pm02103(key)+pm02101/04(incl) | 领料→入库明细 join |
| sdpm021 | PK_sdpm021 | pm02102 | 明细唯一键 |
| sdpm002 | PK_sdpm002 | pm00200 | 物料字典 |

## 5. 老 → MES 映射（落地）

| 老ERP | MES | 映射 |
|---|---|---|
| sdpm002 | mes_md_item | code=pm00201, name=pm00202, specification=pm00205, 分类键=pm00203→item_type_id |
| sdpa013 | mes_md_unit_measure | **实际=推送时 resolveUnit 按名称建**（code=name=pa01302）；stg_pm_unit 字典表未启用 |
| sdpm004 | mes_wm_warehouse | code=pm00401, name=pm00402, 仓管员=pm00404(备注) |
| sdpf003 | mes_md_vendor | code=pf00301, name=pf00302 |
| sdpm001 | mes_md_item_type | code=pm00101, name=pm00102, parent_id←pm00103 |
| sdpm026+027 | mes_wm_batch | code=pm02601_+pm02728；bas_id=pm02603；lot_number=pm02631；expire_date=pm02725；receipt_date=pm02614；vendor=pm02604；warehouse=pm02605；area=该仓默认库位；storage_location=pm02726；src_line_no=pm02702 |
| sdpm026+027 | mes_wm_transaction(type=1) | 数量=pm02706×pm02705；biz_code=pm02603；warehouse=pm02605；area=默认库位 |
| sdpm020+021 | mes_wm_transaction(type=2) | 数量=−pm02104；biz_code=pm02003；batch 由 pm02108 定位；warehouse/area=领料单仓库+默认库位；冗余项目名/供应商名 |
| 聚合 | mes_wm_material_stock | 按 item+batch(+warehouse) 聚合 quantity |

## 5.1 落库字段集（用户定：字段尽可能全）

| MES 表 | 落库字段 |
|---|---|
| mes_md_item | code=pm00201, name=pm00202, specification=pm00205(含货号文本), 分类=item_type_id(分类键pm00203), brand=pm00221 独立列；货号不单独存（见规格文本） |
| mes_md_item_type | code=pm00101, name=pm00102, parent_id←pm00103（两趟：先建全部再挂父） |
| mes_wm_batch | code=pm02601_行序, bas_id=pm02603(备注), lot_number=pm02631, expire_date=pm02725, receipt_date=pm02614, vendor_id, warehouse_id, storage_location=pm02726 独立列, src_line_no=pm02702 |
| mes_wm_transaction | type, item_id, batch_id, warehouse_id, quantity, biz_code, erp_time(业务日期=单据日期), receipt_time(入库时间)；剩余数量=mes_wm_material_stock 按 batch 聚合 |
| mes_md_vendor | code=pf00301, name=pf00302 |
| mes_md_unit_measure | code=name=pa01302（推送时 resolveUnit 按名称建，非 pa01301；换算率 pm00304 不同步） |
| mes_wm_warehouse | code=pm00401, name=pm00402, 仓管员=pm00404(备注) |
| mes_wm_material_stock | 不落（单据自动推导） |

> bas_id(pm02603) 必须落：标签打印/基础数据页查"某 BAS 的批与剩余"的入口。
> 库位(location/area)：**B 方案**——每仓建 1 个默认库位(mes_wm_warehouse_area)承接老数据；老系统无真实库位（sdpj015=地理大区+核算区，23仓 pm00411 全空）。
> 存放位置(storage_location)：mes_wm_batch **独立列**（pm02726），不再塞备注，页面明细可显示。
> 品牌(brand)：mes_md_item **独立列**（pm00221），不再塞备注。
> 货号：老系统**无独立货号**，混在 pm00205 规格文本里（如"货号：NA 容量：0.6mL/Vial"）；pm00203 是**分类键**不是货号，页面看规格即可。
> 项目维度：v1 不落，后续再加。

## 5.2 出入库流水查询页（MES 仓库管理→出入库流水，2026-08-25 新增）

- 后端：`GET /mes/wm/transaction/page`（分页+导出） + `/get`（详情），`MesWmTransactionController`（mes 模块）
- 前端：`yudao-ui-admin-vue3-xn/src/views/mes/wm/transaction/index.vue`
- 菜单：`sql/postgresql/mes-wm-transaction-menu-pg.sql`（挂 5780 仓库管理 下、库存现有量同级；**动态取 id** 防冲突）
- 默认展示**采购入库**（bizType=125）；顶部 全部/采购入库/领料/退料/退货 快捷切换
- 搜索：物料(编码/名称模糊)、原批号、仓库、单号、时间(按**业务日期 erpTime** 过滤)；更多筛选：分类(名称模糊)、供应商、数量范围
- 明细弹窗：流水(类型/业务类型/单号/数量/**剩余数量**/业务日期/入库时间/推送时间) + 物料(编码/名称/规格/分类/单位/品牌) + 批次(原批号/存放位置/有效期/入库日期/备注/MES批次号/来源行号) + 位置(仓库/库区/库位/供应商)
- **剩余数量** remainingQty = 该批次当前库存（mes_wm_material_stock 按 batch 求和）
- MES批次号/来源行号：仅明细底部展示，不占列表列

## 5.3 采购入库单正式表（master mes schema，替代 出入库流水/库存 作为基础信息源，2026-08-25）

> 方向：不做库存/流水，页面读**正式落地的采购入库单**（主子表），替代手动生物试剂表格。

**mes_pm_inbound 主表**（一行=一张采购入库单/BAS）
| 列 | 源 | 说明 |
|---|---|---|
| id | | bigserial PK |
| src_receipt_id | pm02601 | 主唯一键 |
| bas_id | pm02603 | 单号/BAS（=采购收货入库单单号） |
| receipt_date / period | pm02602 / pm02614 | 日期/年月 |
| doc_type | pm02623 | 1采购入库/6其他 |
| vendor_id / vendor_name | pm02604 / pf00302 | 供应商 |
| warehouse_id / warehouse_name | pm02605 / pm00402 | 仓库 |
| po_id / po_code | pm02612 / pm01303 | 采购订单 |
| project_id / project_name / project_code | pm02723 / pa00102 / pa00140 | 项目 |
| applicant / applicant_name / auditor / auditor_name | pm02609 / pm02616 | 申请人/审核人 |
| sync_batch / sync_time | | |

**mes_pm_inbound_line 明细表**（一行=入库明细行，字段对齐原出入库流水明细 + 盘点方案）
| 列 | 源 | 说明 |
|---|---|---|
| id | | bigserial PK |
| inbound_id | | 主表 id |
| src_receipt_id | pm02601 | |
| src_line_id | pm02702 | 明细行唯一键 |
| line_no | pm02728 | 行序 |
| src_item_id | pm02703 | 物料ID |
| item_code / item_name | pm00201 / pm00202 | 物料编码/名称 |
| brand | pm00221 | 品牌 |
| spec | pm00205 | 规格(含货号文本) |
| unit_name | pa01302 | 单位 |
| item_category | pm00203→pm00102 | 分类 |
| batch_no | pm02631 | 批号(Lot No) |
| expire_date | pm02725 | 过期(清洗NA/空) |
| storage_location | pm02726 | 存储位置/冰箱号 |
| qty | pm02706×pm02705 | 接收数量 |
| price_tax_in/amount_tax_in/price_ex_tax/amount_ex_tax | pm02707~10 | 含税/未税 单价金额 |
| sync_batch / sync_time | | |

> 数据链路：老ERP sdpm026/027 → staging stg_pm_inbound（扁平）→ 落地 master mes_pm_inbound/_line（主子）→ 页面读 master。
> 停用：出入库流水页、领料/退料/退货同步、阶段2 MES 库存推送（方法保留）。
> 落地与菜单 SQL 已汇总：`老ERP同步-SQL汇总-2-yudao库.sql`（含 mes_pm_inbound/_line + mes_pm_po + 两个菜单）。

## 5.4 采购订单正式表（master mes schema，查"采购订单同步过来的数据"页面，2026-08-25）

> 页面：**PM采购订单数据**（MES 仓库管理 5780 下），字段按原出入库流水排，**没有"最大/最小剩余量"筛选**。
> 数据链路：老ERP sdpm014(+sdpm013 主/sdpm002 物料/sdpa013 单位/sdpd004+sdpf003 供应商/sdpm001 分类) → staging stg_pm_po_line（扁平）→ 落地 master **mes_pm_po** → 页面读 master。

**mes_pm_po 表**（一行=采购订单明细行）
| 列 | 源 | 说明 |
|---|---|---|
| id | | bigserial PK |
| po_id | pm01401 | 采购订单主ID（关联 sdpm013.pm01301） |
| po_code | pm01303 | 采购订单号 |
| order_date | pm01302 | 订单日期 |
| vendor_name | 合同 sdpd004.pd00404 → sdpf003.pf00302 | 供应商（老系统口径） |
| line_id | pm01402 | 明细行ID（唯一） |
| src_item_id | pm01403 | 物料ID |
| item_code / item_name | pm00201 / pm00202 | 物料编码/名称 |
| brand | pm00221 | 品牌 |
| spec | pm00205 | 规格 |
| unit_code / unit_name | pm01404 / pa01302 | 单位 |
| item_category | pm00203→pm00102 | 分类 |
| qty_ordered | pm01406 | 采购数量 |
| qty_received | pm01411 | 已入库数量 |
| qty_requisition | staging 领料聚合 | 领用数量（按订单行 pm02712） |
| qty_return | staging 退料聚合 | 退料数量（按订单行 pm02712） |
| qty_return_out | staging 采购退货聚合 | 采购退货数量（按订单行 pm02712） |
| remaining_qty | 已入库−领用+退料−采购退货 | 剩余数量（库存口径） |
| sync_batch / sync_time | | |

> **字段勘误（2026-08-25 实测）**：`sdpm014.pm01420` 全库基本为 0，**不是"已领用"**；`sdpm014.pm01411`=**已入库数量**（存列）。入库单/订单行上**没有"已领用"存列**，只能由 领料(sdpm021)−退料(sdpm025)+采购退货(sdpm023) 现算（老ERP视图 sdvw_pm027 同口径）。故 `mes_pm_po` 的 已领用相关改为：staging 同步 领料/退料/采购退货 三张扁平表 → yudao landPo 按订单行聚合 → 现算 剩余=已入库−领用+退料−采购退货。原 `qty_issued`(pm01420)/`qty_substitute`(pm01424) 列已废弃。

> 注意：改了 PO 源字段后要**重新 erpSyncJob** 才会刷新 staging 的 order_date/vendor_name/brand/spec/item_category。

## 6. 待办 / 待确认

- [x] pm02623=6（其他入库 402 条）：按"建库存的入库"处理，纳入
- [x] 退料(21)/退货(37) 纳入同步 → 剩余量直接算准；sdvw_StockQty 仅做期初对账
- [x] 同步任务：Java Quartz Job 落 yudao 内（复用 DingTalkSync 模式），不写 Python；**手动触发为主**（不做自动 cron）
- [x] 增量策略：**同步当天，预留可配置回溯天数**（默认1天，可按需补拉N天）；每日全量太重不做
- [x] 落库方式：**两阶段手动推送**：先拉 staging → 人工点"推送"调 MES 事务服务(createTransaction 系) 进 MES，保库存一致
- [x] 库位：**B 方案**——每仓建 1 默认库位承接老数据（老系统无真实库位，区域=地理大区且23仓全空）
- [x] 存放位置：batch 增 storage_location 字段，同步带 pm02726 + 新系统可手填维护
- [x] 源库读取统一加 WITH (NOLOCK)，避免影响生产
- [x] 标签打印：先不动，后续整改项（新库存上线后切到 MES 查）
- [x] 用真实单号(NB-BAS261782)核对通过：字段映射/数量/剩余量口径与界面一致
- [ ] pm02704 是否就是单位（待复核）
- [x] 首期：历史单据全量回填做期初基线 + sdvw_StockQty 对账一次（确认调拨/盘点/月结差异）——回填方式=erpSyncJob 分段（60:0, 60:60, …覆盖到 2023-04-27）
- [x] 区域(sdpj015) 诊断完成：无结构化库位（区域=地理大区+核算区，23仓全空）→ 库位走 B 方案默认库位
- [x] 替代材料(替代品)：**不纳入同步**（v1），主料 sdpm027 直接同步（天然不含替代行 sdpm201/203/206/207）；对账时留意“被替代主料行”的剩余差异
- [x] 产出源端增量同步 SQL + MES 落库设计（已完成：ErpSyncService/ErpPushServiceImpl + 验收 SQL）
- [x] 分类层级：sdpm001.pm00103=父分类键已确认，分类全量同步+两趟挂父 已实现

## 7. 验收 / 核对方法

- 源库读取统一 `WITH (NOLOCK)`（核对/同步 SQL 均加），避免影响生产；核对 SQL 已含 NOLOCK
- 一级·单号核对：挑一个 BAS，用下列 SQL 拿老系统"每行入库/累计出库/剩余"，与 MES 该 BAS 批次库存对比：
  （SQL 见 /Desktop/yudao/老ERP核对-单号.sql，按 @bas 走索引，轻量，已加 NOLOCK）
- 二级·汇总对账：首期回填后按物料汇总对比老系统剩余 vs MES 库存；差异=调拨/盘点/月结调整，逐项确认或补期初调整
