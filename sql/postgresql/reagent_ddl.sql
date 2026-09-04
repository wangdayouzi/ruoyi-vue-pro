-- =============================================
-- 生物试剂管理模块（Reagent）- PostgreSQL 全新建表 + 菜单
-- 数据库: PostgreSQL 14+
-- 可直接重复执行（ON CONFLICT DO NOTHING）
-- =============================================

-- ==================== 0. 清理旧表（开发阶段可重复执行） ====================

DROP TABLE IF EXISTS reagent_shipment_item CASCADE;
DROP TABLE IF EXISTS reagent_shipment CASCADE;
DROP TABLE IF EXISTS reagent_apply_item CASCADE;
DROP TABLE IF EXISTS reagent_apply CASCADE;
DROP TABLE IF EXISTS reagent_base_lot CASCADE;
DROP TABLE IF EXISTS reagent_base CASCADE;

DROP SEQUENCE IF EXISTS reagent_base_seq;
DROP SEQUENCE IF EXISTS reagent_base_lot_seq;
DROP SEQUENCE IF EXISTS reagent_apply_seq;
DROP SEQUENCE IF EXISTS reagent_apply_item_seq;
DROP SEQUENCE IF EXISTS reagent_shipment_seq;
DROP SEQUENCE IF EXISTS reagent_shipment_item_seq;

-- ==================== 1. 建序列 ====================

CREATE SEQUENCE reagent_base_seq          INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reagent_base_lot_seq      INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reagent_apply_seq         INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reagent_apply_item_seq    INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reagent_shipment_seq      INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE reagent_shipment_item_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

-- ==================== 2. 建表 ====================

-- 2.1 试剂主表
CREATE TABLE reagent_base (
    id               BIGINT          NOT NULL    DEFAULT nextval('reagent_base_seq'::regclass) PRIMARY KEY,
    bas_id           VARCHAR(64)     NOT NULL,
    reagent_name     VARCHAR(128)    NOT NULL,
    vendor           VARCHAR(128),
    cat_no           VARCHAR(64),
    storage_temp     VARCHAR(32),
    storage_location VARCHAR(128),
    status           INT2            NOT NULL    DEFAULT 0,
    tenant_id        BIGINT          DEFAULT 0,
    creator          VARCHAR(64)     DEFAULT '',
    create_time      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)     DEFAULT '',
    update_time      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted          INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_base                    IS '试剂主表';
COMMENT ON COLUMN reagent_base.id                  IS '主键编号';
COMMENT ON COLUMN reagent_base.bas_id              IS '生物试剂编号（BAS-xxx）';
COMMENT ON COLUMN reagent_base.reagent_name        IS '试剂名称';
COMMENT ON COLUMN reagent_base.vendor              IS '供应商';
COMMENT ON COLUMN reagent_base.cat_no              IS '货号';
COMMENT ON COLUMN reagent_base.storage_temp        IS '储存温度（如 2-8°C、-20°C）';
COMMENT ON COLUMN reagent_base.storage_location    IS '储存位置（如 A区-3号冰箱）';
COMMENT ON COLUMN reagent_base.status              IS '状态：0-正常, 1-停用';
COMMENT ON COLUMN reagent_base.creator             IS '创建者';
COMMENT ON COLUMN reagent_base.create_time         IS '创建时间';
COMMENT ON COLUMN reagent_base.updater             IS '更新者';
COMMENT ON COLUMN reagent_base.update_time         IS '更新时间';
COMMENT ON COLUMN reagent_base.deleted             IS '是否删除';
COMMENT ON COLUMN reagent_base.tenant_id           IS '租户编号';

-- 2.2 试剂批号表
CREATE TABLE reagent_base_lot (
    id               BIGINT          NOT NULL    DEFAULT nextval('reagent_base_lot_seq'::regclass) PRIMARY KEY,
    base_id          BIGINT          NOT NULL,
    lot_no           VARCHAR(64)     NOT NULL,
    content          VARCHAR(64),
    expiration_date  TIMESTAMP,
    amount_left      VARCHAR(64),
    status           INT2            NOT NULL    DEFAULT 0,
    tenant_id        BIGINT          DEFAULT 0,
    creator          VARCHAR(64)     DEFAULT '',
    create_time      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)     DEFAULT '',
    update_time      TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted          INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_base_lot                 IS '试剂批号表';
COMMENT ON COLUMN reagent_base_lot.id               IS '主键编号';
COMMENT ON COLUMN reagent_base_lot.base_id          IS '关联 reagent_base.id';
COMMENT ON COLUMN reagent_base_lot.lot_no           IS '批号（如 LOT20240001）';
COMMENT ON COLUMN reagent_base_lot.content          IS '规格/浓度（如 50g、100mM）';
COMMENT ON COLUMN reagent_base_lot.expiration_date  IS '过期日期';
COMMENT ON COLUMN reagent_base_lot.amount_left      IS '参考剩余量（仅供展示，不强校验）';
COMMENT ON COLUMN reagent_base_lot.status           IS '状态：0-正常, 1-停用';
COMMENT ON COLUMN reagent_base_lot.creator          IS '创建者';
COMMENT ON COLUMN reagent_base_lot.create_time      IS '创建时间';
COMMENT ON COLUMN reagent_base_lot.updater          IS '更新者';
COMMENT ON COLUMN reagent_base_lot.update_time      IS '更新时间';
COMMENT ON COLUMN reagent_base_lot.deleted          IS '是否删除';
COMMENT ON COLUMN reagent_base_lot.tenant_id        IS '租户编号';

-- 2.3 试剂申请主表
CREATE TABLE reagent_apply (
    id                  BIGINT          NOT NULL    DEFAULT nextval('reagent_apply_seq'::regclass) PRIMARY KEY,
    apply_no            VARCHAR(64)     NOT NULL,
    consignor_unit      VARCHAR(128)    NOT NULL    DEFAULT '精翰生物',
    consignor_address   VARCHAR(256)    NOT NULL    DEFAULT '上海市浦东新区张江高科技园区XXX号',
    consignor_name      VARCHAR(64)     NOT NULL    DEFAULT '仓库管理员',
    consignor_phone     VARCHAR(32)     NOT NULL    DEFAULT '021-XXXXXXXX',
    region              VARCHAR(16),    -- 发货区域：上海/宁波（前端选地址按钮写入，邮件按区域定向）
    receiver_unit       VARCHAR(128)    NOT NULL,
    receiver_address    VARCHAR(256)    NOT NULL,
    receiver_name       VARCHAR(64)     NOT NULL,
    receiver_phone      VARCHAR(32)     NOT NULL,
    status              INT2            NOT NULL    DEFAULT 0,
    process_instance_id VARCHAR(64),
    creator_user_id     BIGINT,
    remark              VARCHAR(256),
    freight_settlement  VARCHAR(64),
    project_no          VARCHAR(64),
    transport_temp      VARCHAR(32),
    has_temp_logger     INT2            DEFAULT 0,
    tenant_id           BIGINT          DEFAULT 0,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     DEFAULT '',
    update_time         TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted             INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_apply                         IS '试剂申请主表';
COMMENT ON COLUMN reagent_apply.id                       IS '主键编号';
COMMENT ON COLUMN reagent_apply.apply_no                 IS '申请单号（APL-yyyyMMddHHmmss）';
COMMENT ON COLUMN reagent_apply.consignor_unit           IS '发货方单位';
COMMENT ON COLUMN reagent_apply.consignor_address        IS '发货方地址';
COMMENT ON COLUMN reagent_apply.consignor_name           IS '发货联系人';
COMMENT ON COLUMN reagent_apply.consignor_phone          IS '发货联系电话';
COMMENT ON COLUMN reagent_apply.region                   IS '发货区域：上海/宁波（前端选地址按钮写入，邮件按区域定向）';
COMMENT ON COLUMN reagent_apply.receiver_unit            IS '接收方单位';
COMMENT ON COLUMN reagent_apply.receiver_address         IS '接收方地址';
COMMENT ON COLUMN reagent_apply.receiver_name            IS '接收联系人';
COMMENT ON COLUMN reagent_apply.receiver_phone           IS '接收联系电话';
COMMENT ON COLUMN reagent_apply.status                   IS '状态：0-草稿, 1-待发货, 2-部分发货, 3-已完成, 4-已拒单退回';
COMMENT ON COLUMN reagent_apply.process_instance_id      IS '关联 Flowable 流程实例 ID';
COMMENT ON COLUMN reagent_apply.creator_user_id            IS '提单人用户ID（供邮件通知用）';
COMMENT ON COLUMN reagent_apply.remark                   IS '备注/拒绝退回理由';
COMMENT ON COLUMN reagent_apply.freight_settlement       IS '运费结算方式';
COMMENT ON COLUMN reagent_apply.project_no               IS '项目号';
COMMENT ON COLUMN reagent_apply.transport_temp           IS '运输温度';
COMMENT ON COLUMN reagent_apply.has_temp_logger          IS '温度记录仪: 0-无, 1-有';
COMMENT ON COLUMN reagent_apply.creator                  IS '创建者（提单人）';
COMMENT ON COLUMN reagent_apply.create_time              IS '创建时间';
COMMENT ON COLUMN reagent_apply.updater                  IS '更新者';
COMMENT ON COLUMN reagent_apply.update_time              IS '更新时间';
COMMENT ON COLUMN reagent_apply.deleted                  IS '是否删除';
COMMENT ON COLUMN reagent_apply.tenant_id                IS '租户编号';

-- 2.4 试剂申请明细表
CREATE TABLE reagent_apply_item (
    id                BIGINT          NOT NULL    DEFAULT nextval('reagent_apply_item_seq'::regclass) PRIMARY KEY,
    apply_id          BIGINT          NOT NULL,
    bas_id            VARCHAR(64)     NOT NULL,
    bas_no            VARCHAR(64),
    reagent_name      VARCHAR(128)    NOT NULL,
    cat_no            VARCHAR(64),
    vendor            VARCHAR(255),
    brand             VARCHAR(255),
    content           VARCHAR(255),
    lot_no            VARCHAR(64),
    storage_temp      VARCHAR(32),
    storage_location  VARCHAR(500),
    expiration_date   TIMESTAMP,
    requested_qty     INT4            NOT NULL    DEFAULT 1,
    shipped_qty_total INT4            NOT NULL    DEFAULT 0,
    tenant_id         BIGINT          DEFAULT 0,
    creator           VARCHAR(64)     DEFAULT '',
    create_time       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater           VARCHAR(64)     DEFAULT '',
    update_time       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted           INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_apply_item                    IS '试剂申请明细表';
COMMENT ON COLUMN reagent_apply_item.id                  IS '主键编号';
COMMENT ON COLUMN reagent_apply_item.apply_id            IS '关联 reagent_apply.id';
COMMENT ON COLUMN reagent_apply_item.bas_id              IS '试剂编号';
COMMENT ON COLUMN reagent_apply_item.bas_no               IS 'BAS号/采购入库单号 pm02603（选择弹窗带入）';
COMMENT ON COLUMN reagent_apply_item.reagent_name        IS '试剂名称（冗余）';
COMMENT ON COLUMN reagent_apply_item.cat_no              IS '货号（冗余，可手改）';
COMMENT ON COLUMN reagent_apply_item.vendor               IS '供应商（选择弹窗带入，可手改）';
COMMENT ON COLUMN reagent_apply_item.brand                IS '品牌（选择弹窗带入，可手改）';
COMMENT ON COLUMN reagent_apply_item.content             IS '规格/浓度（文本，可手改）';
COMMENT ON COLUMN reagent_apply_item.lot_no              IS '批号（可手改）';
COMMENT ON COLUMN reagent_apply_item.storage_temp        IS '储存温度（文本，可手改）';
COMMENT ON COLUMN reagent_apply_item.storage_location    IS '储存位置（文本，可手改）';
COMMENT ON COLUMN reagent_apply_item.expiration_date     IS '过期日期（选择批号联动带出，可手改）';
COMMENT ON COLUMN reagent_apply_item.requested_qty       IS '需求总数量';
COMMENT ON COLUMN reagent_apply_item.shipped_qty_total   IS '已累计发货数量（每次发货后累加）';
COMMENT ON COLUMN reagent_apply_item.creator             IS '创建者';
COMMENT ON COLUMN reagent_apply_item.create_time         IS '创建时间';
COMMENT ON COLUMN reagent_apply_item.updater             IS '更新者';
COMMENT ON COLUMN reagent_apply_item.update_time         IS '更新时间';
COMMENT ON COLUMN reagent_apply_item.deleted             IS '是否删除';
COMMENT ON COLUMN reagent_apply_item.tenant_id           IS '租户编号';

-- 兼容已建表（幂等）：补齐可手改文本字段
ALTER TABLE reagent_apply_item ADD COLUMN IF NOT EXISTS content          VARCHAR(255);
ALTER TABLE reagent_apply_item ADD COLUMN IF NOT EXISTS storage_temp     VARCHAR(32);
ALTER TABLE reagent_apply_item ADD COLUMN IF NOT EXISTS storage_location VARCHAR(500);
COMMENT ON COLUMN reagent_apply_item.content          IS '规格/浓度（文本，可手改）';
COMMENT ON COLUMN reagent_apply_item.storage_temp     IS '储存温度（文本，可手改）';
COMMENT ON COLUMN reagent_apply_item.storage_location IS '储存位置（文本，可手改）';

-- 2.5 发货单主表（支持 1 对 N 分批发货）
CREATE TABLE reagent_shipment (
    id                  BIGINT          NOT NULL    DEFAULT nextval('reagent_shipment_seq'::regclass) PRIMARY KEY,
    shipment_no         VARCHAR(64)     NOT NULL,
    apply_id            BIGINT          NOT NULL,
    tracking_number     VARCHAR(64),
    express_company     VARCHAR(64),
    freight_settlement  VARCHAR(64),
    project_no          VARCHAR(64),
    transport_temp      VARCHAR(32),
    has_temp_logger     INT2            NOT NULL    DEFAULT 0,
    shipment_date       TIMESTAMP,
    tenant_id           BIGINT          DEFAULT 0,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     DEFAULT '',
    update_time         TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted             INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_shipment                      IS '发货单主表';
COMMENT ON COLUMN reagent_shipment.id                    IS '主键编号';
COMMENT ON COLUMN reagent_shipment.shipment_no           IS '发货单号（SHIP-yyyyMMddHHmmss）';
COMMENT ON COLUMN reagent_shipment.apply_id              IS '关联 reagent_apply.id';
COMMENT ON COLUMN reagent_shipment.tracking_number       IS '快递单号';
COMMENT ON COLUMN reagent_shipment.express_company       IS '物流公司';
COMMENT ON COLUMN reagent_shipment.freight_settlement    IS '运费结算方式（如 月结、到付）';
COMMENT ON COLUMN reagent_shipment.has_temp_logger       IS '是否放置温度记录仪：0-否, 1-是';
COMMENT ON COLUMN reagent_shipment.shipment_date         IS '发货时间';
COMMENT ON COLUMN reagent_shipment.creator               IS '创建者（样品组操作人）';
COMMENT ON COLUMN reagent_shipment.create_time           IS '创建时间';
COMMENT ON COLUMN reagent_shipment.updater               IS '更新者';
COMMENT ON COLUMN reagent_shipment.update_time           IS '更新时间';
COMMENT ON COLUMN reagent_shipment.deleted               IS '是否删除';
COMMENT ON COLUMN reagent_shipment.tenant_id             IS '租户编号';

-- 2.6 发货单明细表
CREATE TABLE reagent_shipment_item (
    id                BIGINT          NOT NULL    DEFAULT nextval('reagent_shipment_item_seq'::regclass) PRIMARY KEY,
    shipment_id       BIGINT          NOT NULL,
    apply_item_id     BIGINT          NOT NULL,
    lot_no            VARCHAR(64),
    quantity_shipped  INT4            NOT NULL    DEFAULT 0,
    tenant_id         BIGINT          DEFAULT 0,
    creator           VARCHAR(64)     DEFAULT '',
    create_time       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updater           VARCHAR(64)     DEFAULT '',
    update_time       TIMESTAMP       NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    deleted           INT2            NOT NULL    DEFAULT 0
);
COMMENT ON TABLE  reagent_shipment_item                 IS '发货单明细表';
COMMENT ON COLUMN reagent_shipment_item.id               IS '主键编号';
COMMENT ON COLUMN reagent_shipment_item.shipment_id      IS '关联 reagent_shipment.id';
COMMENT ON COLUMN reagent_shipment_item.apply_item_id    IS '关联 reagent_apply_item.id';
COMMENT ON COLUMN reagent_shipment_item.lot_no           IS '批号（继承申请明细）';
COMMENT ON COLUMN reagent_shipment_item.quantity_shipped IS '本次实际发货数量';
COMMENT ON COLUMN reagent_shipment_item.creator          IS '创建者';
COMMENT ON COLUMN reagent_shipment_item.create_time      IS '创建时间';
COMMENT ON COLUMN reagent_shipment_item.updater          IS '更新者';
COMMENT ON COLUMN reagent_shipment_item.update_time      IS '更新时间';
COMMENT ON COLUMN reagent_shipment_item.deleted          IS '是否删除';
COMMENT ON COLUMN reagent_shipment_item.tenant_id        IS '租户编号';


-- ==================== 3. 索引 ====================

-- 试剂主表
CREATE UNIQUE INDEX IF NOT EXISTS uk_reagent_base_bas_id      ON reagent_base (bas_id)          WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_base_name       ON reagent_base (reagent_name);
CREATE INDEX        IF NOT EXISTS idx_reagent_base_status     ON reagent_base (status)          WHERE deleted = 0;

-- 试剂批号表
CREATE INDEX        IF NOT EXISTS idx_reagent_base_lot_base   ON reagent_base_lot (base_id)     WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_base_lot_no     ON reagent_base_lot (lot_no);
CREATE INDEX        IF NOT EXISTS idx_reagent_base_lot_expiry ON reagent_base_lot (expiration_date) WHERE deleted = 0;

-- 申请主表
CREATE UNIQUE INDEX IF NOT EXISTS uk_reagent_apply_no         ON reagent_apply (apply_no)       WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_apply_status    ON reagent_apply (status)         WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_apply_receiver  ON reagent_apply (receiver_unit)  WHERE deleted = 0;

-- 申请明细表
CREATE INDEX        IF NOT EXISTS idx_reagent_apply_item_aid  ON reagent_apply_item (apply_id)  WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_apply_item_bas  ON reagent_apply_item (bas_id)    WHERE deleted = 0;

-- 发货单主表
CREATE INDEX        IF NOT EXISTS idx_reagent_shipment_aid    ON reagent_shipment (apply_id)    WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_shipment_no     ON reagent_shipment (shipment_no) WHERE deleted = 0;

-- 发货单明细表
CREATE INDEX        IF NOT EXISTS idx_reagent_ship_item_sid   ON reagent_shipment_item (shipment_id) WHERE deleted = 0;
CREATE INDEX        IF NOT EXISTS idx_reagent_ship_item_aiid  ON reagent_shipment_item (apply_item_id) WHERE deleted = 0;


-- ==================== 4. 菜单数据 ====================

-- 4.1 一级菜单：试剂管理 (type=1 目录, parent_id=0)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7000, '试剂管理', '', 1, 330, 0, '/reagent', 'ep:takeaway-box', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 4.2 二级菜单：基础数据 (type=2 菜单, parent_id=7000)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7001, '基础数据', '', 2, 1, 7000, 'base', 'ep:box', 'reagent/base/index', 'ReagentBase', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 4.3 三级按钮：基础数据-操作权限 (type=3 按钮, parent_id=7001)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7011, '试剂查询', 'reagent:base:query', 3, 1, 7001, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7012, '试剂创建', 'reagent:base:create', 3, 2, 7001, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7013, '试剂更新', 'reagent:base:update', 3, 3, 7001, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7014, '试剂删除', 'reagent:base:delete', 3, 4, 7001, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 4.4 二级菜单：申请单管理 (type=2 菜单, parent_id=7000)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7002, '申请单管理', '', 2, 2, 7000, 'apply', 'ep:document-checked', 'reagent/apply/index', 'ReagentApply', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 4.5 三级按钮：申请单-操作权限 (type=3 按钮, parent_id=7002)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7021, '申请单查询', 'reagent:apply:query', 3, 1, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7022, '申请单创建', 'reagent:apply:create', 3, 2, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7023, '申请单更新', 'reagent:apply:update', 3, 3, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7024, '申请单删除', 'reagent:apply:delete', 3, 4, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7025, '发货确认', 'reagent:shipment:create', 3, 5, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7026, '发货单查询', 'reagent:shipment:query', 3, 6, 7002, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;


-- ==================== 5. 演示数据（可选执行） ====================

-- 演示试剂
INSERT INTO reagent_base (id, bas_id, reagent_name, vendor, cat_no, storage_temp, storage_location, status, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (1, 'BAS-001', 'Anti-CD3 Antibody (clone UCHT1)', 'BioLegend', '300402', '2-8°C', 'A区-1号冰箱-2层', 0, '1', NOW(), '1', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reagent_base (id, bas_id, reagent_name, vendor, cat_no, storage_temp, storage_location, status, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (2, 'BAS-002', 'FBS (Fetal Bovine Serum)', 'Gibco', '10099141C', '-20°C', 'B区-冷冻柜-1层', 0, '1', NOW(), '1', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 演示批号
INSERT INTO reagent_base_lot (id, base_id, lot_no, content, expiration_date, amount_left, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (1, 1, 'LOT20240001', '100μg', '2025-12-31', '300μg', '1', NOW(), '1', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reagent_base_lot (id, base_id, lot_no, content, expiration_date, amount_left, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (2, 1, 'LOT20240002', '100μg', '2026-06-30', '500μg', '1', NOW(), '1', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reagent_base_lot (id, base_id, lot_no, content, expiration_date, amount_left, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (3, 2, 'LOT20240301', '500mL', '2026-03-15', '10L', '1', NOW(), '1', NOW(), 0, 0)
ON CONFLICT (id) DO NOTHING;

-- 演示数据插入了固定 ID，需要将序列推进到当前最大值之后
SELECT setval('reagent_base_seq', COALESCE((SELECT MAX(id) FROM reagent_base), 0) + 1, false);
SELECT setval('reagent_base_lot_seq', COALESCE((SELECT MAX(id) FROM reagent_base_lot), 0) + 1, false);

-- ==================== 6. BPM 模块缺失序列补丁（PostgreSQL bpm.sql 未建） ====================

CREATE SEQUENCE IF NOT EXISTS bpm_process_definition_info_seq START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_process_instance_copy_seq  START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_oa_leave_seq              START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_category_seq              START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_user_group_seq            START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_process_listener_seq      START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_process_expression_seq    START 1;
CREATE SEQUENCE IF NOT EXISTS bpm_form_seq                  START 1;

-- 推进到现有最大值
SELECT setval('bpm_process_definition_info_seq', COALESCE((SELECT MAX(id) FROM bpm_process_definition_info), 0) + 1, false);
SELECT setval('bpm_process_instance_copy_seq',  COALESCE((SELECT MAX(id) FROM bpm_process_instance_copy),  0) + 1, false);
SELECT setval('bpm_oa_leave_seq',              COALESCE((SELECT MAX(id) FROM bpm_oa_leave),               0) + 1, false);
SELECT setval('bpm_category_seq',              COALESCE((SELECT MAX(id) FROM bpm_category),               0) + 1, false);
SELECT setval('bpm_user_group_seq',            COALESCE((SELECT MAX(id) FROM bpm_user_group),             0) + 1, false);
SELECT setval('bpm_process_listener_seq',      COALESCE((SELECT MAX(id) FROM bpm_process_listener),       0) + 1, false);
SELECT setval('bpm_process_expression_seq',    COALESCE((SELECT MAX(id) FROM bpm_process_expression),     0) + 1, false);
SELECT setval('bpm_form_seq',                  COALESCE((SELECT MAX(id) FROM bpm_form),                   0) + 1, false);
