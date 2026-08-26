-- =============================================================
-- 老ERP同步 - 阶段2 推送 DDL
-- 说明：
--   第 1~2 节：在 staging 库（yudao_stg_pm）执行（给已建业务表补推送标记列 + 建推送日志表）
--   第 3 节：在 master 库（yudao，MES 表所在）执行（mes_wm_batch 补 src_line_no 列）
-- 全部幂等，可重复执行。
-- =============================================================

-- ============ 1) staging 业务表补推送标记列（幂等） ============
ALTER TABLE stg_pm_inbound    ADD COLUMN IF NOT EXISTS pushed smallint NOT NULL DEFAULT 0;
ALTER TABLE stg_pm_inbound    ADD COLUMN IF NOT EXISTS push_batch bigint;
ALTER TABLE stg_pm_inbound    ADD COLUMN IF NOT EXISTS push_time timestamp;
ALTER TABLE stg_pm_inbound    ADD COLUMN IF NOT EXISTS item_category varchar(500);
ALTER TABLE stg_pm_requisition ADD COLUMN IF NOT EXISTS pushed smallint NOT NULL DEFAULT 0;
ALTER TABLE stg_pm_requisition ADD COLUMN IF NOT EXISTS push_batch bigint;
ALTER TABLE stg_pm_requisition ADD COLUMN IF NOT EXISTS push_time timestamp;
ALTER TABLE stg_pm_return_in  ADD COLUMN IF NOT EXISTS pushed smallint NOT NULL DEFAULT 0;
ALTER TABLE stg_pm_return_in  ADD COLUMN IF NOT EXISTS push_batch bigint;
ALTER TABLE stg_pm_return_in  ADD COLUMN IF NOT EXISTS push_time timestamp;
ALTER TABLE stg_pm_return_out ADD COLUMN IF NOT EXISTS pushed smallint NOT NULL DEFAULT 0;
ALTER TABLE stg_pm_return_out ADD COLUMN IF NOT EXISTS push_batch bigint;
ALTER TABLE stg_pm_return_out ADD COLUMN IF NOT EXISTS push_time timestamp;

-- ============ 1.5) 列宽修正（varchar(100/255) 装不下长值，报"值太长(100)"；幂等可重跑） ============
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

-- ============ 2) 推送日志表（幂等） ============
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

-- ============ 3) MES 表补列（在 master 库执行！） ============
-- 注意：MES 表在 mes schema（连接串 currentSchema=mes,public），不在 public
ALTER TABLE mes.mes_wm_batch ADD COLUMN IF NOT EXISTS src_line_no varchar(40);
-- 批次 BAS号/入库单单号（pm02603，独立字段，按BAS可查）
ALTER TABLE mes.mes_wm_batch ADD COLUMN IF NOT EXISTS bas_id varchar(40);
-- 批次 存储位置（pm02726，独立字段，不再塞备注）
ALTER TABLE mes.mes_wm_batch ADD COLUMN IF NOT EXISTS storage_location varchar(500);
-- 物料 品牌（pm00221，独立字段，不再塞备注）
ALTER TABLE mes.mes_md_item ADD COLUMN IF NOT EXISTS brand varchar(500);
