-- =============================================================
-- 老ERP同步 - 采购入库单正式表 DDL（在 master 库 yudao 执行！mes schema）
-- 用途：老ERP 采购入库单 落地成正式主子表，页面读取（替代 出入库流水/库存）
-- 幂等：IF NOT EXISTS，可重复执行
-- =============================================================

-- 主表：一行=一张采购入库单/BAS（pm02603=单号）
CREATE TABLE IF NOT EXISTS mes.mes_pm_inbound (
  id                bigserial PRIMARY KEY,
  src_receipt_id    varchar(40) NOT NULL UNIQUE,    -- pm02601
  bas_id            varchar(40),                    -- pm02603 单号/BAS
  receipt_date      varchar(10),                    -- pm02602
  period            varchar(20),                    -- pm02614
  doc_type          smallint,                       -- pm02623
  vendor_id         varchar(40),                    -- pm02604
  vendor_name       varchar(500),
  warehouse_id      varchar(40),                    -- pm02605
  warehouse_name    varchar(500),
  po_id             varchar(40),                    -- pm02612
  po_code           varchar(40),                    -- pm01303
  project_id        varchar(40),                    -- pm02723
  project_name      varchar(500),
  project_code      varchar(500),
  applicant         varchar(40),                    -- pm02609
  applicant_name    varchar(500),
  auditor           varchar(40),                    -- pm02616
  auditor_name      varchar(500),
  sync_batch        bigint,
  sync_time         timestamp NOT NULL DEFAULT now()
);

-- 明细表：一行=入库明细行
CREATE TABLE IF NOT EXISTS mes.mes_pm_inbound_line (
  id                bigserial PRIMARY KEY,
  inbound_id        bigint NOT NULL,                -- 主表 id
  src_receipt_id    varchar(40) NOT NULL,           -- pm02601
  src_line_id       varchar(40) NOT NULL UNIQUE,    -- pm02702
  line_no           smallint,                       -- pm02728
  src_item_id       varchar(200),                   -- pm02703
  item_code         varchar(200),                   -- pm00201
  item_name         varchar(500),                   -- pm00202
  brand             varchar(500),                   -- pm00221
  spec              varchar(1000),                  -- pm00205
  unit_name         varchar(500),                   -- pa01302
  item_category     varchar(500),                   -- pm00203→pm00102
  batch_no          varchar(200),                   -- pm02631 批号
  expire_date       varchar(40),                    -- pm02725 过期(清洗)
  storage_location  varchar(500),                   -- pm02726 存储位置
  qty               numeric(18,4),                  -- pm02706*pm02705 接收数量
  price_tax_in      numeric(18,4),                  -- pm02707
  amount_tax_in     numeric(18,4),                  -- pm02708
  price_ex_tax      numeric(18,4),                  -- pm02709
  amount_ex_tax     numeric(18,4),                  -- pm02710
  sync_batch        bigint,
  sync_time         timestamp NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_mes_pm_inbound_line_inbound ON mes.mes_pm_inbound_line (inbound_id);
CREATE INDEX IF NOT EXISTS idx_mes_pm_inbound_line_item   ON mes.mes_pm_inbound_line (src_item_id);

COMMENT ON TABLE mes.mes_pm_inbound IS '老ERP同步-采购入库单主表(正式)';
COMMENT ON TABLE mes.mes_pm_inbound_line IS '老ERP同步-采购入库单明细表(正式)';

-- 采购订单数据表（staging stg_pm_po_line → 这里；页面读此表）
CREATE TABLE IF NOT EXISTS mes.mes_pm_po (
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
CREATE INDEX IF NOT EXISTS idx_mes_pm_po_code ON mes.mes_pm_po (po_code);
CREATE INDEX IF NOT EXISTS idx_mes_pm_po_item ON mes.mes_pm_po (src_item_id);
COMMENT ON TABLE mes.mes_pm_po IS '老ERP同步-采购订单明细数据(正式)';
