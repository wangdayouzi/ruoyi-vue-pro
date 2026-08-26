-- =============================================================
-- 老ERP同步 staging 中间库 DDL
-- 库：yudao_stg_pm（需先执行 CREATE DATABASE yudao_stg_pm;）
-- 表前缀：stg_pm_
-- 用途：老 ERP(PM) 单据 → 中间库，供核对 + 后续推送 MES 仓库模块
-- 日期：2026-08-24
-- =============================================================

-- 1. 字典表 ------------------------------------------------------

-- 物料
CREATE TABLE IF NOT EXISTS stg_pm_item (
  id          bigserial PRIMARY KEY,
  src_id      varchar(100) NOT NULL UNIQUE,          -- pm00200
  code        varchar(200),                          -- pm00201 物料编码
  name        varchar(500),                          -- pm00202 名称
  catalog_no  varchar(40),                           -- pm00203 货号
  brand       varchar(500),                          -- pm00221 品牌
  spec        varchar(1000),                         -- pm00205 规格/容量
  unit_code   varchar(18),                           -- sdpm003/sdpa013
  unit_name   varchar(100),                          -- pa01302
  sync_batch  bigint,
  update_time timestamp NOT NULL DEFAULT now()
);

-- 仓库
CREATE TABLE IF NOT EXISTS stg_pm_warehouse (
  id          bigserial PRIMARY KEY,
  src_id      varchar(40) NOT NULL UNIQUE,           -- pm00401
  code        varchar(40),
  name        varchar(100),                          -- pm00402
  keeper      varchar(20),                           -- pm00404 仓管员
  sync_batch  bigint,
  update_time timestamp NOT NULL DEFAULT now()
);

-- 供应商
CREATE TABLE IF NOT EXISTS stg_pm_vendor (
  id          bigserial PRIMARY KEY,
  src_id      varchar(40) NOT NULL UNIQUE,           -- pf00301
  code        varchar(40),
  name        varchar(255),                          -- pf00302
  sync_batch  bigint,
  update_time timestamp NOT NULL DEFAULT now()
);

-- 单位
CREATE TABLE IF NOT EXISTS stg_pm_unit (
  id          bigserial PRIMARY KEY,
  src_id      varchar(18) NOT NULL UNIQUE,           -- pa01301
  code        varchar(18),
  name        varchar(100),                          -- pa01302
  sync_batch  bigint,
  update_time timestamp NOT NULL DEFAULT now()
);

-- 2. 业务表 ------------------------------------------------------

-- 入库行（一行=入库明细行）
CREATE TABLE IF NOT EXISTS stg_pm_inbound (
  id               bigserial PRIMARY KEY,
  src_receipt_id   varchar(40) NOT NULL,             -- pm02601
  src_line_id      varchar(40) NOT NULL UNIQUE,      -- pm02702
  bas_id           varchar(40),                      -- pm02603 入库单号/BAS
  receipt_date     varchar(10),                      -- pm02602
  period           varchar(20),                      -- pm02614
  doc_type         smallint,                         -- pm02623
  batch_no         varchar(200),                     -- pm02631
  expire_date      varchar(40),                      -- pm02725 (清洗 NA/空)
  storage_location varchar(500),                     -- pm02726
  line_no          smallint,                         -- pm02728
  src_item_id      varchar(100),                     -- pm02703
  item_code        varchar(200),
  item_name        varchar(500),
  catalog_no       varchar(40),
  brand            varchar(500),
  spec             varchar(1000),
  unit_name        varchar(100),
  item_category    varchar(500),                    -- 物料分类 pm00203→sdpm001.pm00102
  qty              numeric(18,4),                    -- pm02706*pm02705
  price_tax_in     numeric(18,4),                    -- pm02707
  amount_tax_in    numeric(18,4),                    -- pm02708
  price_ex_tax     numeric(18,4),                    -- pm02709
  amount_ex_tax    numeric(18,4),                    -- pm02710
  vendor_id        varchar(40),                      -- pm02604
  vendor_name      varchar(255),
  warehouse_id     varchar(40),                      -- pm02605
  warehouse_name   varchar(100),
  po_id            varchar(40),                      -- pm02612
  po_code          varchar(40),                      -- pm01303
  project_id       varchar(40),                      -- pm02723
  project_name     varchar(500),
  project_code     varchar(100),
  applicant        varchar(40),                      -- pm02609
  applicant_name   varchar(100),
  auditor          varchar(40),                      -- pm02616
  auditor_name     varchar(100),
  sync_batch       bigint,
  sync_time        timestamp NOT NULL DEFAULT now(),
  pushed           smallint NOT NULL DEFAULT 0,
  push_batch       bigint,
  push_time        timestamp,
  CONSTRAINT uk_stg_pm_inbound UNIQUE (src_receipt_id, line_no)
);
CREATE INDEX IF NOT EXISTS idx_stg_pm_inbound_period ON stg_pm_inbound (period);
CREATE INDEX IF NOT EXISTS idx_stg_pm_inbound_item  ON stg_pm_inbound (src_item_id);
CREATE INDEX IF NOT EXISTS idx_stg_pm_inbound_bas   ON stg_pm_inbound (bas_id);

-- 领料行（一行=领料明细行）
CREATE TABLE IF NOT EXISTS stg_pm_requisition (
  id                 bigserial PRIMARY KEY,
  src_req_id         varchar(40) NOT NULL,           -- pm02001
  src_line_id        varchar(40) NOT NULL UNIQUE,    -- pm02102
  req_code           varchar(40),                    -- pm02003
  req_date           varchar(10),                    -- pm02002
  period             varchar(20),                    -- pm02016
  src_item_id        varchar(100),                   -- pm02103
  item_code          varchar(200),
  item_name          varchar(500),
  qty                numeric(18,4),                  -- pm02104
  price              numeric(18,4),                  -- pm02105
  price_tax_in       numeric(18,4),                  -- pm02107
  project_id         varchar(40),                    -- pm02004
  project_name       varchar(500),
  project_code       varchar(100),
  vendor_id          varchar(40),                    -- pm02008
  vendor_name        varchar(255),
  warehouse_id       varchar(40),                    -- pm02006
  warehouse_name     varchar(100),
  applicant          varchar(40),                    -- pm02012
  applicant_name     varchar(100),
  auditor            varchar(40),                    -- pm02017
  auditor_name       varchar(100),
  src_receipt_id     varchar(40),                    -- pm02015
  src_line_receipt_id varchar(40),                   -- pm02108 → pm02702
  sync_batch         bigint,
  sync_time          timestamp NOT NULL DEFAULT now(),
  pushed             smallint NOT NULL DEFAULT 0,
  push_batch         bigint,
  push_time          timestamp,
  CONSTRAINT uk_stg_pm_requisition UNIQUE (src_req_id, src_line_id)
);
CREATE INDEX IF NOT EXISTS idx_stg_pm_requisition_period ON stg_pm_requisition (period);
CREATE INDEX IF NOT EXISTS idx_stg_pm_requisition_item  ON stg_pm_requisition (src_item_id);

-- 退料行（sdpm024/025；同步逻辑后接）
CREATE TABLE IF NOT EXISTS stg_pm_return_in (
  id                 bigserial PRIMARY KEY,
  src_ret_id         varchar(40) NOT NULL,           -- pm02401
  src_line_id        varchar(40) NOT NULL UNIQUE,    -- pm02502
  ret_code           varchar(40),                    -- pm02403
  ret_date           varchar(10),                    -- pm02402
  period             varchar(20),                    -- pm02416
  src_item_id        varchar(100),                   -- pm02503
  item_code          varchar(200),
  item_name          varchar(500),
  qty                numeric(18,4),                  -- pm02504
  amount             numeric(18,4),                  -- pm02506
  project_id         varchar(40),                    -- pm02404
  project_name       varchar(500),
  warehouse_id       varchar(40),                    -- pm02406
  warehouse_name     varchar(100),
  vendor_id          varchar(40),                    -- pm02408
  vendor_name        varchar(255),
  applicant          varchar(40),                    -- pm02412
  applicant_name     varchar(100),
  auditor            varchar(40),                    -- pm02417
  auditor_name       varchar(100),
  src_line_receipt_id varchar(40),                   -- pm02507 → pm02702
  sync_batch         bigint,
  sync_time          timestamp NOT NULL DEFAULT now(),
  pushed             smallint NOT NULL DEFAULT 0,
  push_batch         bigint,
  push_time          timestamp,
  CONSTRAINT uk_stg_pm_return_in UNIQUE (src_ret_id, src_line_id)
);
CREATE INDEX IF NOT EXISTS idx_stg_pm_return_in_period ON stg_pm_return_in (period);

-- 采购退货行（sdpm022/023, pm02217=1；同步逻辑后接）
CREATE TABLE IF NOT EXISTS stg_pm_return_out (
  id                 bigserial PRIMARY KEY,
  src_ro_id          varchar(40) NOT NULL,           -- pm02201
  src_line_id        varchar(40) NOT NULL UNIQUE,    -- pm02302
  ro_code            varchar(40),                    -- pm02203
  ro_date            varchar(10),                    -- pm02202
  period             varchar(20),                    -- pm02214
  src_item_id        varchar(100),                   -- pm02303
  item_code          varchar(200),
  item_name          varchar(500),
  qty                numeric(18,4),                  -- pm02304
  price              numeric(18,4),                  -- pm02305
  vendor_id          varchar(40),                    -- pm02204
  vendor_name        varchar(255),
  warehouse_id       varchar(40),                    -- pm02205
  warehouse_name     varchar(100),
  po_id              varchar(40),                    -- pm02212
  po_code            varchar(40),
  applicant          varchar(40),                    -- pm02209
  applicant_name     varchar(100),
  auditor            varchar(40),                    -- pm02213
  auditor_name       varchar(100),
  src_line_receipt_id varchar(40),                   -- pm02311 → pm02702
  sync_batch         bigint,
  sync_time          timestamp NOT NULL DEFAULT now(),
  pushed             smallint NOT NULL DEFAULT 0,
  push_batch         bigint,
  push_time          timestamp,
  CONSTRAINT uk_stg_pm_return_out UNIQUE (src_ro_id, src_line_id)
);
CREATE INDEX IF NOT EXISTS idx_stg_pm_return_out_period ON stg_pm_return_out (period);

-- 物料分类（sdpm001 全量；pm00103=父分类键 → MES 分类层级）
CREATE TABLE IF NOT EXISTS stg_pm_item_category (
  id         bigserial PRIMARY KEY,
  cat_key    varchar(8) NOT NULL UNIQUE,      -- pm00101 分类键
  cat_name   varchar(100),                    -- pm00102 分类名
  parent_key varchar(8),                      -- pm00103 父分类键（顶级为空）
  sync_batch bigint,
  sync_time  timestamp NOT NULL DEFAULT now(),
  pushed     smallint NOT NULL DEFAULT 0,
  push_batch bigint,
  push_time  timestamp
);

-- 采购订单明细（sdpm014 全量；剩余量=采购数量+替代调整−已领用，替代手动盘点表）
CREATE TABLE IF NOT EXISTS stg_pm_po_line (
  id              bigserial PRIMARY KEY,
  po_id           varchar(40) NOT NULL,       -- pm01401 采购订单主ID(关联sdpm013.pm01301)
  po_code         varchar(50),                -- pm01303 PO号
  line_id         varchar(40) NOT NULL UNIQUE, -- pm01402 明细行ID(唯一)
  order_date      varchar(10),                -- pm01302 订单日期
  vendor_name     varchar(500),               -- 供应商(合同 sdpd004→sdpf003)
  src_item_id     varchar(200),               -- pm01403 物料ID
  item_code       varchar(200),               -- pm00201 物料编码
  item_name       varchar(500),               -- pm00202 物料名称
  brand           varchar(500),               -- pm00221 品牌
  spec            varchar(1000),              -- pm00205 规格
  unit_code       varchar(18),                -- pm01404 单位编码
  unit_name       varchar(500),               -- pa01302 单位名称
  item_category   varchar(500),               -- pm00203→sdpm001.pm00102 分类
  qty_ordered     numeric(18,4),              -- pm01406 采购数量
  qty_issued      numeric(18,4),              -- pm01420 已领用数量
  qty_substitute  numeric(18,4),              -- pm01424 替代品调整
  remaining_qty   numeric(18,4),              -- 剩余 = 采购 + 替代 − 已领用
  sync_batch      bigint,
  sync_time       timestamp NOT NULL DEFAULT now()
);

-- 3. 同步日志 ------------------------------------------------------
CREATE TABLE IF NOT EXISTS stg_pm_sync_log (
  id            bigserial PRIMARY KEY,
  sync_batch    bigint NOT NULL,
  backfill_days int NOT NULL DEFAULT 1,
  start_time    timestamp NOT NULL DEFAULT now(),
  end_time      timestamp,
  result        jsonb,
  status        varchar(20) NOT NULL DEFAULT 'RUNNING',  -- RUNNING/SUCCESS/FAIL
  error_msg     text
);

-- =============================================================
-- 字段注释（COMMENT ON）
-- =============================================================

COMMENT ON TABLE stg_pm_item IS '老ERP同步-物料字典(staging)';
COMMENT ON COLUMN stg_pm_item.id IS '主键';
COMMENT ON COLUMN stg_pm_item.src_id IS '源物料主键 pm00200';
COMMENT ON COLUMN stg_pm_item.code IS '物料编码 pm00201';
COMMENT ON COLUMN stg_pm_item.name IS '物料名称 pm00202';
COMMENT ON COLUMN stg_pm_item.catalog_no IS '货号 pm00203';
COMMENT ON COLUMN stg_pm_item.brand IS '品牌/厂商 pm00221';
COMMENT ON COLUMN stg_pm_item.spec IS '规格/容量 pm00205';
COMMENT ON COLUMN stg_pm_item.unit_code IS '单位编码 sdpm003/sdpa013';
COMMENT ON COLUMN stg_pm_item.unit_name IS '单位名称 pa01302';
COMMENT ON COLUMN stg_pm_item.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_item.update_time IS '更新时间';

COMMENT ON TABLE stg_pm_warehouse IS '老ERP同步-仓库字典(staging)';
COMMENT ON COLUMN stg_pm_warehouse.id IS '主键';
COMMENT ON COLUMN stg_pm_warehouse.src_id IS '源仓库主键 pm00401';
COMMENT ON COLUMN stg_pm_warehouse.code IS '仓库编码 pm00401';
COMMENT ON COLUMN stg_pm_warehouse.name IS '仓库名称 pm00402';
COMMENT ON COLUMN stg_pm_warehouse.keeper IS '仓管员 pm00404';
COMMENT ON COLUMN stg_pm_warehouse.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_warehouse.update_time IS '更新时间';

COMMENT ON TABLE stg_pm_vendor IS '老ERP同步-供应商字典(staging)';
COMMENT ON COLUMN stg_pm_vendor.id IS '主键';
COMMENT ON COLUMN stg_pm_vendor.src_id IS '源供应商主键 pf00301';
COMMENT ON COLUMN stg_pm_vendor.code IS '供应商编码 pf00301';
COMMENT ON COLUMN stg_pm_vendor.name IS '供应商名称 pf00302';
COMMENT ON COLUMN stg_pm_vendor.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_vendor.update_time IS '更新时间';

COMMENT ON TABLE stg_pm_unit IS '老ERP同步-计量单位字典(staging)';
COMMENT ON COLUMN stg_pm_unit.id IS '主键';
COMMENT ON COLUMN stg_pm_unit.src_id IS '源单位主键 pa01301';
COMMENT ON COLUMN stg_pm_unit.code IS '单位编码 pa01301';
COMMENT ON COLUMN stg_pm_unit.name IS '单位名称 pa01302';
COMMENT ON COLUMN stg_pm_unit.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_unit.update_time IS '更新时间';

COMMENT ON TABLE stg_pm_inbound IS '老ERP同步-入库明细行(staging, 源sdpm026+027)';
COMMENT ON COLUMN stg_pm_inbound.id IS '主键';
COMMENT ON COLUMN stg_pm_inbound.src_receipt_id IS '入库主内部ID pm02601';
COMMENT ON COLUMN stg_pm_inbound.src_line_id IS '入库明细行号 pm02702';
COMMENT ON COLUMN stg_pm_inbound.bas_id IS '入库单号/BAS pm02603';
COMMENT ON COLUMN stg_pm_inbound.receipt_date IS '单据日期 pm02602';
COMMENT ON COLUMN stg_pm_inbound.period IS '年月 pm02614';
COMMENT ON COLUMN stg_pm_inbound.doc_type IS '单据类型 pm02623(1采购入库/6其他)';
COMMENT ON COLUMN stg_pm_inbound.batch_no IS '批号 pm02631';
COMMENT ON COLUMN stg_pm_inbound.expire_date IS '过期日期 pm02725(已清洗NA/空)';
COMMENT ON COLUMN stg_pm_inbound.storage_location IS '存放位置 pm02726';
COMMENT ON COLUMN stg_pm_inbound.line_no IS '行序 pm02728';
COMMENT ON COLUMN stg_pm_inbound.src_item_id IS '物料主键 pm02703';
COMMENT ON COLUMN stg_pm_inbound.item_code IS '物料编码 pm00201';
COMMENT ON COLUMN stg_pm_inbound.item_name IS '物料名称 pm00202';
COMMENT ON COLUMN stg_pm_inbound.catalog_no IS '货号 pm00203';
COMMENT ON COLUMN stg_pm_inbound.brand IS '品牌 pm00221';
COMMENT ON COLUMN stg_pm_inbound.spec IS '规格 pm00205';
COMMENT ON COLUMN stg_pm_inbound.unit_name IS '单位名称 pa01302';
COMMENT ON COLUMN stg_pm_inbound.item_category IS '物料分类 pm00203→sdpm001.pm00102';
COMMENT ON COLUMN stg_pm_inbound.qty IS '数量 pm02706*pm02705';
COMMENT ON COLUMN stg_pm_inbound.price_tax_in IS '含税单价 pm02707';
COMMENT ON COLUMN stg_pm_inbound.amount_tax_in IS '含税金额 pm02708';
COMMENT ON COLUMN stg_pm_inbound.price_ex_tax IS '不含税单价 pm02709';
COMMENT ON COLUMN stg_pm_inbound.amount_ex_tax IS '不含税金额 pm02710';
COMMENT ON COLUMN stg_pm_inbound.vendor_id IS '供应商 pm02604';
COMMENT ON COLUMN stg_pm_inbound.vendor_name IS '供应商名称 pf00302';
COMMENT ON COLUMN stg_pm_inbound.warehouse_id IS '仓库 pm02605';
COMMENT ON COLUMN stg_pm_inbound.warehouse_name IS '仓库名称 pm00402';
COMMENT ON COLUMN stg_pm_inbound.po_id IS '采购订单 pm02612';
COMMENT ON COLUMN stg_pm_inbound.po_code IS '采购订单号 pm01303';
COMMENT ON COLUMN stg_pm_inbound.project_id IS '项目 pm02723';
COMMENT ON COLUMN stg_pm_inbound.project_name IS '项目名称 pa00102';
COMMENT ON COLUMN stg_pm_inbound.project_code IS '项目编号 pa00140';
COMMENT ON COLUMN stg_pm_inbound.applicant IS '申请人 pm02609';
COMMENT ON COLUMN stg_pm_inbound.applicant_name IS '申请人名称 pj00402';
COMMENT ON COLUMN stg_pm_inbound.auditor IS '审核人 pm02616';
COMMENT ON COLUMN stg_pm_inbound.auditor_name IS '审核人名称 pj00402';
COMMENT ON COLUMN stg_pm_inbound.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_inbound.sync_time IS '同步时间';

COMMENT ON TABLE stg_pm_requisition IS '老ERP同步-领料明细行(staging, 源sdpm020+021)';
COMMENT ON COLUMN stg_pm_requisition.id IS '主键';
COMMENT ON COLUMN stg_pm_requisition.src_req_id IS '领料主内部ID pm02001';
COMMENT ON COLUMN stg_pm_requisition.src_line_id IS '领料明细行号 pm02102';
COMMENT ON COLUMN stg_pm_requisition.req_code IS '领料单号 pm02003';
COMMENT ON COLUMN stg_pm_requisition.req_date IS '单据日期 pm02002';
COMMENT ON COLUMN stg_pm_requisition.period IS '年月 pm02016';
COMMENT ON COLUMN stg_pm_requisition.src_item_id IS '物料主键 pm02103';
COMMENT ON COLUMN stg_pm_requisition.item_code IS '物料编码 pm00201';
COMMENT ON COLUMN stg_pm_requisition.item_name IS '物料名称 pm00202';
COMMENT ON COLUMN stg_pm_requisition.qty IS '数量 pm02104';
COMMENT ON COLUMN stg_pm_requisition.price IS '单价 pm02105';
COMMENT ON COLUMN stg_pm_requisition.price_tax_in IS '含税单价 pm02107';
COMMENT ON COLUMN stg_pm_requisition.project_id IS '项目 pm02004';
COMMENT ON COLUMN stg_pm_requisition.project_name IS '项目名称 pa00102';
COMMENT ON COLUMN stg_pm_requisition.project_code IS '项目编号 pa00140';
COMMENT ON COLUMN stg_pm_requisition.vendor_id IS '供应商 pm02008';
COMMENT ON COLUMN stg_pm_requisition.vendor_name IS '供应商名称 pf00302';
COMMENT ON COLUMN stg_pm_requisition.warehouse_id IS '仓库 pm02006';
COMMENT ON COLUMN stg_pm_requisition.warehouse_name IS '仓库名称 pm00402';
COMMENT ON COLUMN stg_pm_requisition.applicant IS '申请人 pm02012';
COMMENT ON COLUMN stg_pm_requisition.applicant_name IS '申请人名称 pj00402';
COMMENT ON COLUMN stg_pm_requisition.auditor IS '审核人 pm02017';
COMMENT ON COLUMN stg_pm_requisition.auditor_name IS '审核人名称 pj00402';
COMMENT ON COLUMN stg_pm_requisition.src_receipt_id IS '来源入库主 pm02015';
COMMENT ON COLUMN stg_pm_requisition.src_line_receipt_id IS '来源入库明细行 pm02108(→pm02702, 定位批次)';
COMMENT ON COLUMN stg_pm_requisition.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_requisition.sync_time IS '同步时间';

COMMENT ON TABLE stg_pm_return_in IS '老ERP同步-项目退料行(staging, 源sdpm024+025)';
COMMENT ON COLUMN stg_pm_return_in.id IS '主键';
COMMENT ON COLUMN stg_pm_return_in.src_ret_id IS '退料主内部ID pm02401';
COMMENT ON COLUMN stg_pm_return_in.src_line_id IS '退料明细行号 pm02502';
COMMENT ON COLUMN stg_pm_return_in.ret_code IS '退料单号 pm02403';
COMMENT ON COLUMN stg_pm_return_in.ret_date IS '单据日期 pm02402';
COMMENT ON COLUMN stg_pm_return_in.period IS '年月 pm02416';
COMMENT ON COLUMN stg_pm_return_in.src_item_id IS '物料主键 pm02503';
COMMENT ON COLUMN stg_pm_return_in.item_code IS '物料编码 pm00201';
COMMENT ON COLUMN stg_pm_return_in.item_name IS '物料名称 pm00202';
COMMENT ON COLUMN stg_pm_return_in.qty IS '数量(回库+) pm02504';
COMMENT ON COLUMN stg_pm_return_in.amount IS '金额 pm02506';
COMMENT ON COLUMN stg_pm_return_in.project_id IS '项目 pm02404';
COMMENT ON COLUMN stg_pm_return_in.project_name IS '项目名称 pa00102';
COMMENT ON COLUMN stg_pm_return_in.warehouse_id IS '仓库 pm02406';
COMMENT ON COLUMN stg_pm_return_in.warehouse_name IS '仓库名称 pm00402';
COMMENT ON COLUMN stg_pm_return_in.vendor_id IS '供应商 pm02408';
COMMENT ON COLUMN stg_pm_return_in.vendor_name IS '供应商名称 pf00302';
COMMENT ON COLUMN stg_pm_return_in.applicant IS '申请人 pm02412';
COMMENT ON COLUMN stg_pm_return_in.applicant_name IS '申请人名称 pj00402';
COMMENT ON COLUMN stg_pm_return_in.auditor IS '审核人 pm02417';
COMMENT ON COLUMN stg_pm_return_in.auditor_name IS '审核人名称 pj00402';
COMMENT ON COLUMN stg_pm_return_in.src_line_receipt_id IS '来源入库明细行 pm02507(→pm02702)';
COMMENT ON COLUMN stg_pm_return_in.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_return_in.sync_time IS '同步时间';

COMMENT ON TABLE stg_pm_return_out IS '老ERP同步-采购退货行(staging, 源sdpm022+023 pm02217=1)';
COMMENT ON COLUMN stg_pm_return_out.id IS '主键';
COMMENT ON COLUMN stg_pm_return_out.src_ro_id IS '退货主内部ID pm02201';
COMMENT ON COLUMN stg_pm_return_out.src_line_id IS '退货明细行号 pm02302';
COMMENT ON COLUMN stg_pm_return_out.ro_code IS '退货单号 pm02203';
COMMENT ON COLUMN stg_pm_return_out.ro_date IS '单据日期 pm02202';
COMMENT ON COLUMN stg_pm_return_out.period IS '年月 pm02214';
COMMENT ON COLUMN stg_pm_return_out.src_item_id IS '物料主键 pm02303';
COMMENT ON COLUMN stg_pm_return_out.item_code IS '物料编码 pm00201';
COMMENT ON COLUMN stg_pm_return_out.item_name IS '物料名称 pm00202';
COMMENT ON COLUMN stg_pm_return_out.qty IS '数量(出库-) pm02304';
COMMENT ON COLUMN stg_pm_return_out.price IS '单价 pm02305';
COMMENT ON COLUMN stg_pm_return_out.vendor_id IS '供应商 pm02204';
COMMENT ON COLUMN stg_pm_return_out.vendor_name IS '供应商名称 pf00302';
COMMENT ON COLUMN stg_pm_return_out.warehouse_id IS '仓库 pm02205';
COMMENT ON COLUMN stg_pm_return_out.warehouse_name IS '仓库名称 pm00402';
COMMENT ON COLUMN stg_pm_return_out.po_id IS '采购订单 pm02212';
COMMENT ON COLUMN stg_pm_return_out.po_code IS '采购订单号 pm01303';
COMMENT ON COLUMN stg_pm_return_out.applicant IS '申请人 pm02209';
COMMENT ON COLUMN stg_pm_return_out.applicant_name IS '申请人名称 pj00402';
COMMENT ON COLUMN stg_pm_return_out.auditor IS '审核人 pm02213';
COMMENT ON COLUMN stg_pm_return_out.auditor_name IS '审核人名称 pj00402';
COMMENT ON COLUMN stg_pm_return_out.src_line_receipt_id IS '来源入库明细行 pm02311(→pm02702)';
COMMENT ON COLUMN stg_pm_return_out.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_return_out.sync_time IS '同步时间';

COMMENT ON TABLE stg_pm_sync_log IS '老ERP同步-同步日志(staging)';
COMMENT ON COLUMN stg_pm_sync_log.id IS '主键';
COMMENT ON COLUMN stg_pm_sync_log.sync_batch IS '同步批次';
COMMENT ON COLUMN stg_pm_sync_log.backfill_days IS '回溯天数';
COMMENT ON COLUMN stg_pm_sync_log.start_time IS '开始时间';
COMMENT ON COLUMN stg_pm_sync_log.end_time IS '结束时间';
COMMENT ON COLUMN stg_pm_sync_log.result IS '结果(jsonb, 各表行数)';
COMMENT ON COLUMN stg_pm_sync_log.status IS '状态(RUNNING/SUCCESS/FAIL)';
COMMENT ON COLUMN stg_pm_sync_log.error_msg IS '错误信息';

-- 4. 推送日志（阶段2：staging→MES） -------------------------------------------------
CREATE TABLE IF NOT EXISTS stg_pm_push_log (
  id         bigserial PRIMARY KEY,
  push_batch bigint NOT NULL,
  start_time timestamp NOT NULL DEFAULT now(),
  end_time   timestamp,
  result     jsonb,
  status     varchar(20) NOT NULL DEFAULT 'RUNNING',  -- RUNNING/SUCCESS/FAIL
  error_msg  text
);
COMMENT ON TABLE stg_pm_push_log IS '老ERP同步-推送日志(staging→MES)';
COMMENT ON COLUMN stg_pm_push_log.id IS '主键';
COMMENT ON COLUMN stg_pm_push_log.push_batch IS '推送批次';
COMMENT ON COLUMN stg_pm_push_log.start_time IS '开始时间';
COMMENT ON COLUMN stg_pm_push_log.end_time IS '结束时间';
COMMENT ON COLUMN stg_pm_push_log.result IS '结果(jsonb, 各表推送行数)';
COMMENT ON COLUMN stg_pm_push_log.status IS '状态(RUNNING/SUCCESS/FAIL)';
COMMENT ON COLUMN stg_pm_push_log.error_msg IS '错误信息';

-- 5. 列宽修正（varchar(100/255) 装不下长值，报"值太长了(100)"；幂等可重跑） ------------------
-- 30 天窗口同步发现：warehouse_name/project_code/applicant_name/auditor_name 有超 100 的源文本，
-- 统一加宽到 500（vendor_name 255→500，src_item_id 100→200），避免回填历史窗口时再次报错。
ALTER TABLE stg_pm_inbound     ALTER COLUMN src_item_id     TYPE varchar(200);
ALTER TABLE stg_pm_inbound     ALTER COLUMN unit_name       TYPE varchar(500);
ALTER TABLE stg_pm_inbound     ALTER COLUMN vendor_name     TYPE varchar(500);
ALTER TABLE stg_pm_inbound     ALTER COLUMN warehouse_name  TYPE varchar(500);
ALTER TABLE stg_pm_inbound     ALTER COLUMN project_code    TYPE varchar(500);
ALTER TABLE stg_pm_inbound     ALTER COLUMN applicant_name  TYPE varchar(500);
ALTER TABLE stg_pm_inbound     ALTER COLUMN auditor_name    TYPE varchar(500);
ALTER TABLE stg_pm_requisition ALTER COLUMN src_item_id     TYPE varchar(200);
ALTER TABLE stg_pm_requisition ALTER COLUMN vendor_name     TYPE varchar(500);
ALTER TABLE stg_pm_requisition ALTER COLUMN warehouse_name  TYPE varchar(500);
ALTER TABLE stg_pm_requisition ALTER COLUMN project_code    TYPE varchar(500);
ALTER TABLE stg_pm_requisition ALTER COLUMN applicant_name  TYPE varchar(500);
ALTER TABLE stg_pm_requisition ALTER COLUMN auditor_name    TYPE varchar(500);
ALTER TABLE stg_pm_return_in   ALTER COLUMN src_item_id     TYPE varchar(200);
ALTER TABLE stg_pm_return_in   ALTER COLUMN vendor_name     TYPE varchar(500);
ALTER TABLE stg_pm_return_in   ALTER COLUMN warehouse_name  TYPE varchar(500);
ALTER TABLE stg_pm_return_in   ALTER COLUMN applicant_name  TYPE varchar(500);
ALTER TABLE stg_pm_return_in   ALTER COLUMN auditor_name    TYPE varchar(500);
ALTER TABLE stg_pm_return_out  ALTER COLUMN src_item_id     TYPE varchar(200);
ALTER TABLE stg_pm_return_out  ALTER COLUMN vendor_name     TYPE varchar(500);
ALTER TABLE stg_pm_return_out  ALTER COLUMN warehouse_name  TYPE varchar(500);
ALTER TABLE stg_pm_return_out  ALTER COLUMN applicant_name  TYPE varchar(500);
ALTER TABLE stg_pm_return_out  ALTER COLUMN auditor_name    TYPE varchar(500);
