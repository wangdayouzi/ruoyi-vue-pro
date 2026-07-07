-- ============================================
-- PostgreSQL 版本 - WMS 数据库表结构
-- 从 MySQL 转换而来
-- ============================================

-- 删除表（注意顺序，先删有外键依赖的）
DROP TABLE IF EXISTS wms_check_order_detail;
DROP TABLE IF EXISTS wms_check_order;
DROP TABLE IF EXISTS wms_inventory_history;
DROP TABLE IF EXISTS wms_inventory;
DROP TABLE IF EXISTS wms_movement_order_detail;
DROP TABLE IF EXISTS wms_movement_order;
DROP TABLE IF EXISTS wms_receipt_order_detail;
DROP TABLE IF EXISTS wms_receipt_order;
DROP TABLE IF EXISTS wms_shipment_order_detail;
DROP TABLE IF EXISTS wms_shipment_order;
DROP TABLE IF EXISTS wms_item_sku;
DROP TABLE IF EXISTS wms_item;
DROP TABLE IF EXISTS wms_item_brand;
DROP TABLE IF EXISTS wms_item_category;
DROP TABLE IF EXISTS wms_merchant;
DROP TABLE IF EXISTS wms_warehouse;

-- ============================================
-- 1. wms_warehouse
-- ============================================
DROP TABLE IF EXISTS wms_warehouse;
CREATE TABLE wms_warehouse (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    sort INTEGER NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_warehouse IS 'WMS 仓库';
COMMENT ON COLUMN wms_warehouse.id IS '编号';
COMMENT ON COLUMN wms_warehouse.code IS '仓库编号';
COMMENT ON COLUMN wms_warehouse.name IS '名称';
COMMENT ON COLUMN wms_warehouse.remark IS '备注';
COMMENT ON COLUMN wms_warehouse.sort IS '排序';
COMMENT ON COLUMN wms_warehouse.creator IS '创建者';
COMMENT ON COLUMN wms_warehouse.create_time IS '创建时间';
COMMENT ON COLUMN wms_warehouse.updater IS '更新者';
COMMENT ON COLUMN wms_warehouse.update_time IS '更新时间';
COMMENT ON COLUMN wms_warehouse.deleted IS '是否删除';
COMMENT ON COLUMN wms_warehouse.tenant_id IS '租户编号';

-- ============================================
-- 2. wms_item_category
-- ============================================
DROP TABLE IF EXISTS wms_item_category;
CREATE TABLE wms_item_category (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(30) NOT NULL,
    sort INTEGER NOT NULL DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_item_category IS 'WMS 商品分类';
COMMENT ON COLUMN wms_item_category.id IS '编号';
COMMENT ON COLUMN wms_item_category.parent_id IS '父分类编号';
COMMENT ON COLUMN wms_item_category.code IS '分类编号';
COMMENT ON COLUMN wms_item_category.name IS '分类名称';
COMMENT ON COLUMN wms_item_category.sort IS '显示顺序';
COMMENT ON COLUMN wms_item_category.status IS '状态（0 停用，1 正常）';
COMMENT ON COLUMN wms_item_category.creator IS '创建者';
COMMENT ON COLUMN wms_item_category.create_time IS '创建时间';
COMMENT ON COLUMN wms_item_category.updater IS '更新者';
COMMENT ON COLUMN wms_item_category.update_time IS '更新时间';
COMMENT ON COLUMN wms_item_category.deleted IS '是否删除';
COMMENT ON COLUMN wms_item_category.tenant_id IS '租户编号';

-- ============================================
-- 3. wms_item_brand
-- ============================================
DROP TABLE IF EXISTS wms_item_brand;
CREATE TABLE wms_item_brand (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(30) NOT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_item_brand IS 'WMS 商品品牌';
COMMENT ON COLUMN wms_item_brand.id IS '编号';
COMMENT ON COLUMN wms_item_brand.code IS '品牌编号';
COMMENT ON COLUMN wms_item_brand.name IS '品牌名称';
COMMENT ON COLUMN wms_item_brand.creator IS '创建者';
COMMENT ON COLUMN wms_item_brand.create_time IS '创建时间';
COMMENT ON COLUMN wms_item_brand.updater IS '更新者';
COMMENT ON COLUMN wms_item_brand.update_time IS '更新时间';
COMMENT ON COLUMN wms_item_brand.deleted IS '是否删除';
COMMENT ON COLUMN wms_item_brand.tenant_id IS '租户编号';

-- ============================================
-- 4. wms_item
-- ============================================
DROP TABLE IF EXISTS wms_item;
CREATE TABLE wms_item (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) DEFAULT NULL,
    name VARCHAR(60) NOT NULL,
    category_id BIGINT NOT NULL,
    unit VARCHAR(20) DEFAULT NULL,
    brand_id BIGINT DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_item IS 'WMS 商品';
COMMENT ON COLUMN wms_item.id IS '编号';
COMMENT ON COLUMN wms_item.code IS '商品编号';
COMMENT ON COLUMN wms_item.name IS '商品名称';
COMMENT ON COLUMN wms_item.category_id IS '商品分类编号';
COMMENT ON COLUMN wms_item.unit IS '单位';
COMMENT ON COLUMN wms_item.brand_id IS '商品品牌编号';
COMMENT ON COLUMN wms_item.remark IS '备注';
COMMENT ON COLUMN wms_item.creator IS '创建者';
COMMENT ON COLUMN wms_item.create_time IS '创建时间';
COMMENT ON COLUMN wms_item.updater IS '更新者';
COMMENT ON COLUMN wms_item.update_time IS '更新时间';
COMMENT ON COLUMN wms_item.deleted IS '是否删除';
COMMENT ON COLUMN wms_item.tenant_id IS '租户编号';

-- ============================================
-- 5. wms_item_sku
-- ============================================
DROP TABLE IF EXISTS wms_item_sku;
CREATE TABLE wms_item_sku (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    item_id BIGINT NOT NULL,
    bar_code VARCHAR(64) DEFAULT NULL,
    code VARCHAR(64) DEFAULT NULL,
    length DECIMAL(10, 1) DEFAULT NULL,
    width DECIMAL(10, 1) DEFAULT NULL,
    height DECIMAL(10, 1) DEFAULT NULL,
    gross_weight DECIMAL(10, 3) DEFAULT NULL,
    net_weight DECIMAL(10, 3) DEFAULT NULL,
    cost_price DECIMAL(16, 2) DEFAULT NULL,
    selling_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_item_sku IS 'WMS 商品 SKU';
COMMENT ON COLUMN wms_item_sku.id IS '编号';
COMMENT ON COLUMN wms_item_sku.name IS '规格名称';
COMMENT ON COLUMN wms_item_sku.item_id IS '商品编号';
COMMENT ON COLUMN wms_item_sku.bar_code IS '条码';
COMMENT ON COLUMN wms_item_sku.code IS '规格编号';
COMMENT ON COLUMN wms_item_sku.length IS '长，单位 cm';
COMMENT ON COLUMN wms_item_sku.width IS '宽，单位 cm';
COMMENT ON COLUMN wms_item_sku.height IS '高，单位 cm';
COMMENT ON COLUMN wms_item_sku.gross_weight IS '毛重，单位 kg';
COMMENT ON COLUMN wms_item_sku.net_weight IS '净重，单位 kg';
COMMENT ON COLUMN wms_item_sku.cost_price IS '成本价（单位：元）';
COMMENT ON COLUMN wms_item_sku.selling_price IS '销售价（单位：元）';
COMMENT ON COLUMN wms_item_sku.creator IS '创建者';
COMMENT ON COLUMN wms_item_sku.create_time IS '创建时间';
COMMENT ON COLUMN wms_item_sku.updater IS '更新者';
COMMENT ON COLUMN wms_item_sku.update_time IS '更新时间';
COMMENT ON COLUMN wms_item_sku.deleted IS '是否删除';
COMMENT ON COLUMN wms_item_sku.tenant_id IS '租户编号';

-- ============================================
-- 6. wms_merchant
-- ============================================
DROP TABLE IF EXISTS wms_merchant;
CREATE TABLE wms_merchant (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(60) NOT NULL,
    type SMALLINT NOT NULL,
    level VARCHAR(10) DEFAULT NULL,
    bank_name VARCHAR(255) DEFAULT NULL,
    bank_account VARCHAR(40) DEFAULT NULL,
    address VARCHAR(200) DEFAULT NULL,
    mobile VARCHAR(13) DEFAULT NULL,
    telephone VARCHAR(13) DEFAULT NULL,
    contact VARCHAR(30) DEFAULT NULL,
    email VARCHAR(50) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_merchant IS 'WMS 往来企业';
COMMENT ON COLUMN wms_merchant.id IS '编号';
COMMENT ON COLUMN wms_merchant.code IS '往来企业编号';
COMMENT ON COLUMN wms_merchant.name IS '往来企业名称';
COMMENT ON COLUMN wms_merchant.type IS '往来企业类型';
COMMENT ON COLUMN wms_merchant.level IS '级别';
COMMENT ON COLUMN wms_merchant.bank_name IS '开户行';
COMMENT ON COLUMN wms_merchant.bank_account IS '银行账户';
COMMENT ON COLUMN wms_merchant.address IS '地址';
COMMENT ON COLUMN wms_merchant.mobile IS '手机号';
COMMENT ON COLUMN wms_merchant.telephone IS '座机号';
COMMENT ON COLUMN wms_merchant.contact IS '联系人';
COMMENT ON COLUMN wms_merchant.email IS 'Email';
COMMENT ON COLUMN wms_merchant.remark IS '备注';
COMMENT ON COLUMN wms_merchant.creator IS '创建者';
COMMENT ON COLUMN wms_merchant.create_time IS '创建时间';
COMMENT ON COLUMN wms_merchant.updater IS '更新者';
COMMENT ON COLUMN wms_merchant.update_time IS '更新时间';
COMMENT ON COLUMN wms_merchant.deleted IS '是否删除';
COMMENT ON COLUMN wms_merchant.tenant_id IS '租户编号';

-- ============================================
-- 7. wms_inventory
-- ============================================
DROP TABLE IF EXISTS wms_inventory;
CREATE TABLE wms_inventory (
    id BIGSERIAL PRIMARY KEY,
    sku_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    remark VARCHAR(255) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_sku_id_warehouse_id UNIQUE (sku_id, warehouse_id)
);

COMMENT ON TABLE wms_inventory IS 'WMS 库存';
COMMENT ON COLUMN wms_inventory.id IS '编号';
COMMENT ON COLUMN wms_inventory.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_inventory.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_inventory.area_id IS '库区编号';
COMMENT ON COLUMN wms_inventory.quantity IS '库存数量';
COMMENT ON COLUMN wms_inventory.remark IS '备注';
COMMENT ON COLUMN wms_inventory.creator IS '创建者';
COMMENT ON COLUMN wms_inventory.create_time IS '创建时间';
COMMENT ON COLUMN wms_inventory.updater IS '更新者';
COMMENT ON COLUMN wms_inventory.update_time IS '更新时间';
COMMENT ON COLUMN wms_inventory.deleted IS '是否删除';
COMMENT ON COLUMN wms_inventory.tenant_id IS '租户编号';

CREATE INDEX idx_inventory_warehouse_id ON wms_inventory (warehouse_id);
CREATE INDEX idx_inventory_sku_id ON wms_inventory (sku_id);

-- ============================================
-- 8. wms_inventory_history
-- ============================================
DROP TABLE IF EXISTS wms_inventory_history;
CREATE TABLE wms_inventory_history (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    sku_id BIGINT NOT NULL,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    before_quantity DECIMAL(20, 2) DEFAULT NULL,
    after_quantity DECIMAL(20, 2) DEFAULT NULL,
    batch_no VARCHAR(64) DEFAULT NULL,
    production_date TIMESTAMP DEFAULT NULL,
    expiration_date TIMESTAMP DEFAULT NULL,
    price DECIMAL(16, 2) DEFAULT NULL,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    order_id BIGINT DEFAULT NULL,
    order_no VARCHAR(64) DEFAULT NULL,
    order_type INTEGER DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_inventory_history IS 'WMS 库存流水';
COMMENT ON COLUMN wms_inventory_history.id IS '编号';
COMMENT ON COLUMN wms_inventory_history.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_inventory_history.area_id IS '库区编号';
COMMENT ON COLUMN wms_inventory_history.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_inventory_history.quantity IS '库存变化数量';
COMMENT ON COLUMN wms_inventory_history.before_quantity IS '变化前库存数量';
COMMENT ON COLUMN wms_inventory_history.after_quantity IS '变化后库存数量';
COMMENT ON COLUMN wms_inventory_history.batch_no IS '批号';
COMMENT ON COLUMN wms_inventory_history.production_date IS '生产日期';
COMMENT ON COLUMN wms_inventory_history.expiration_date IS '过期日期';
COMMENT ON COLUMN wms_inventory_history.price IS '单价';
COMMENT ON COLUMN wms_inventory_history.total_price IS '库存变化金额';
COMMENT ON COLUMN wms_inventory_history.remark IS '备注';
COMMENT ON COLUMN wms_inventory_history.order_id IS '操作单编号';
COMMENT ON COLUMN wms_inventory_history.order_no IS '操作单号';
COMMENT ON COLUMN wms_inventory_history.order_type IS '操作类型';
COMMENT ON COLUMN wms_inventory_history.creator IS '创建者';
COMMENT ON COLUMN wms_inventory_history.create_time IS '创建时间';
COMMENT ON COLUMN wms_inventory_history.updater IS '更新者';
COMMENT ON COLUMN wms_inventory_history.update_time IS '更新时间';
COMMENT ON COLUMN wms_inventory_history.deleted IS '是否删除';
COMMENT ON COLUMN wms_inventory_history.tenant_id IS '租户编号';

CREATE INDEX idx_inventory_history_warehouse_time ON wms_inventory_history (warehouse_id, create_time);
CREATE INDEX idx_inventory_history_sku_time ON wms_inventory_history (sku_id, create_time);

-- ============================================
-- 9. wms_check_order
-- ============================================
DROP TABLE IF EXISTS wms_check_order;
CREATE TABLE wms_check_order (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(64) NOT NULL,
    order_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL DEFAULT 0,
    remark VARCHAR(255) DEFAULT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    total_quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    actual_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_check_order IS 'WMS 盘库单';
COMMENT ON COLUMN wms_check_order.id IS '编号';
COMMENT ON COLUMN wms_check_order.no IS '盘库单号';
COMMENT ON COLUMN wms_check_order.order_time IS '单据日期';
COMMENT ON COLUMN wms_check_order.status IS '盘库状态';
COMMENT ON COLUMN wms_check_order.remark IS '备注';
COMMENT ON COLUMN wms_check_order.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_check_order.area_id IS '库区编号';
COMMENT ON COLUMN wms_check_order.total_quantity IS '盈亏数量';
COMMENT ON COLUMN wms_check_order.total_price IS '总金额（账面）';
COMMENT ON COLUMN wms_check_order.actual_price IS '实际金额（盘点）';
COMMENT ON COLUMN wms_check_order.creator IS '创建者';
COMMENT ON COLUMN wms_check_order.create_time IS '创建时间';
COMMENT ON COLUMN wms_check_order.updater IS '更新者';
COMMENT ON COLUMN wms_check_order.update_time IS '更新时间';
COMMENT ON COLUMN wms_check_order.deleted IS '是否删除';
COMMENT ON COLUMN wms_check_order.tenant_id IS '租户编号';

CREATE INDEX idx_check_order_no ON wms_check_order (no);
CREATE INDEX idx_check_order_warehouse_id ON wms_check_order (warehouse_id);

-- ============================================
-- 10. wms_check_order_detail
-- ============================================
DROP TABLE IF EXISTS wms_check_order_detail;
CREATE TABLE wms_check_order_detail (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    inventory_id BIGINT DEFAULT NULL,
    inventory_detail_id BIGINT DEFAULT NULL,
    batch_no VARCHAR(64) DEFAULT NULL,
    production_date TIMESTAMP DEFAULT NULL,
    expiration_date TIMESTAMP DEFAULT NULL,
    receipt_time TIMESTAMP DEFAULT NULL,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    check_quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_check_order_detail IS 'WMS 盘库单明细';
COMMENT ON COLUMN wms_check_order_detail.id IS '编号';
COMMENT ON COLUMN wms_check_order_detail.order_id IS '盘库单编号';
COMMENT ON COLUMN wms_check_order_detail.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_check_order_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_check_order_detail.area_id IS '库区编号';
COMMENT ON COLUMN wms_check_order_detail.inventory_id IS '库存编号';
COMMENT ON COLUMN wms_check_order_detail.inventory_detail_id IS '库存明细编号';
COMMENT ON COLUMN wms_check_order_detail.batch_no IS '批号';
COMMENT ON COLUMN wms_check_order_detail.production_date IS '生产日期';
COMMENT ON COLUMN wms_check_order_detail.expiration_date IS '过期日期';
COMMENT ON COLUMN wms_check_order_detail.receipt_time IS '入库时间';
COMMENT ON COLUMN wms_check_order_detail.quantity IS '账面数量';
COMMENT ON COLUMN wms_check_order_detail.check_quantity IS '实盘数量';
COMMENT ON COLUMN wms_check_order_detail.price IS '单价';
COMMENT ON COLUMN wms_check_order_detail.creator IS '创建者';
COMMENT ON COLUMN wms_check_order_detail.create_time IS '创建时间';
COMMENT ON COLUMN wms_check_order_detail.updater IS '更新者';
COMMENT ON COLUMN wms_check_order_detail.update_time IS '更新时间';
COMMENT ON COLUMN wms_check_order_detail.deleted IS '是否删除';
COMMENT ON COLUMN wms_check_order_detail.tenant_id IS '租户编号';

CREATE INDEX idx_check_order_detail_order_id ON wms_check_order_detail (order_id);
CREATE INDEX idx_check_order_detail_sku_id ON wms_check_order_detail (sku_id);

-- ============================================
-- 11. wms_receipt_order
-- ============================================
DROP TABLE IF EXISTS wms_receipt_order;
CREATE TABLE wms_receipt_order (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(64) NOT NULL,
    type INTEGER NOT NULL,
    order_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL DEFAULT 0,
    biz_order_no VARCHAR(64) DEFAULT NULL,
    merchant_id BIGINT DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    total_quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_receipt_order IS 'WMS 入库单';
COMMENT ON COLUMN wms_receipt_order.id IS '编号';
COMMENT ON COLUMN wms_receipt_order.no IS '入库单号';
COMMENT ON COLUMN wms_receipt_order.type IS '入库类型';
COMMENT ON COLUMN wms_receipt_order.order_time IS '单据日期';
COMMENT ON COLUMN wms_receipt_order.status IS '入库状态';
COMMENT ON COLUMN wms_receipt_order.biz_order_no IS '业务订单号';
COMMENT ON COLUMN wms_receipt_order.merchant_id IS '往来企业编号';
COMMENT ON COLUMN wms_receipt_order.remark IS '备注';
COMMENT ON COLUMN wms_receipt_order.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_receipt_order.area_id IS '库区编号';
COMMENT ON COLUMN wms_receipt_order.total_quantity IS '总数量';
COMMENT ON COLUMN wms_receipt_order.total_price IS '总金额';
COMMENT ON COLUMN wms_receipt_order.creator IS '创建者';
COMMENT ON COLUMN wms_receipt_order.create_time IS '创建时间';
COMMENT ON COLUMN wms_receipt_order.updater IS '更新者';
COMMENT ON COLUMN wms_receipt_order.update_time IS '更新时间';
COMMENT ON COLUMN wms_receipt_order.deleted IS '是否删除';
COMMENT ON COLUMN wms_receipt_order.tenant_id IS '租户编号';

CREATE INDEX idx_receipt_order_no ON wms_receipt_order (no);
CREATE INDEX idx_receipt_order_warehouse_id ON wms_receipt_order (warehouse_id);

-- ============================================
-- 12. wms_receipt_order_detail
-- ============================================
DROP TABLE IF EXISTS wms_receipt_order_detail;
CREATE TABLE wms_receipt_order_detail (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    batch_no VARCHAR(64) DEFAULT NULL,
    production_date TIMESTAMP DEFAULT NULL,
    expiration_date TIMESTAMP DEFAULT NULL,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    price DECIMAL(16, 2) DEFAULT NULL,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_receipt_order_detail IS 'WMS 入库单明细';
COMMENT ON COLUMN wms_receipt_order_detail.id IS '编号';
COMMENT ON COLUMN wms_receipt_order_detail.order_id IS '入库单编号';
COMMENT ON COLUMN wms_receipt_order_detail.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_receipt_order_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_receipt_order_detail.area_id IS '库区编号';
COMMENT ON COLUMN wms_receipt_order_detail.batch_no IS '批号';
COMMENT ON COLUMN wms_receipt_order_detail.production_date IS '生产日期';
COMMENT ON COLUMN wms_receipt_order_detail.expiration_date IS '过期日期';
COMMENT ON COLUMN wms_receipt_order_detail.quantity IS '入库数量';
COMMENT ON COLUMN wms_receipt_order_detail.price IS '单价';
COMMENT ON COLUMN wms_receipt_order_detail.total_price IS '行金额';
COMMENT ON COLUMN wms_receipt_order_detail.creator IS '创建者';
COMMENT ON COLUMN wms_receipt_order_detail.create_time IS '创建时间';
COMMENT ON COLUMN wms_receipt_order_detail.updater IS '更新者';
COMMENT ON COLUMN wms_receipt_order_detail.update_time IS '更新时间';
COMMENT ON COLUMN wms_receipt_order_detail.deleted IS '是否删除';
COMMENT ON COLUMN wms_receipt_order_detail.tenant_id IS '租户编号';

CREATE INDEX idx_receipt_order_detail_order_id ON wms_receipt_order_detail (order_id);
CREATE INDEX idx_receipt_order_detail_sku_id ON wms_receipt_order_detail (sku_id);

-- ============================================
-- 13. wms_shipment_order
-- ============================================
DROP TABLE IF EXISTS wms_shipment_order;
CREATE TABLE wms_shipment_order (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(64) NOT NULL,
    type INTEGER NOT NULL,
    order_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL DEFAULT 0,
    biz_order_no VARCHAR(64) DEFAULT NULL,
    merchant_id BIGINT DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    total_quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_shipment_order IS 'WMS 出库单';
COMMENT ON COLUMN wms_shipment_order.id IS '编号';
COMMENT ON COLUMN wms_shipment_order.no IS '出库单号';
COMMENT ON COLUMN wms_shipment_order.type IS '出库类型';
COMMENT ON COLUMN wms_shipment_order.order_time IS '单据日期';
COMMENT ON COLUMN wms_shipment_order.status IS '出库状态';
COMMENT ON COLUMN wms_shipment_order.biz_order_no IS '业务订单号';
COMMENT ON COLUMN wms_shipment_order.merchant_id IS '客户编号';
COMMENT ON COLUMN wms_shipment_order.remark IS '备注';
COMMENT ON COLUMN wms_shipment_order.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_shipment_order.area_id IS '库区编号';
COMMENT ON COLUMN wms_shipment_order.total_quantity IS '总数量';
COMMENT ON COLUMN wms_shipment_order.total_price IS '总金额';
COMMENT ON COLUMN wms_shipment_order.creator IS '创建者';
COMMENT ON COLUMN wms_shipment_order.create_time IS '创建时间';
COMMENT ON COLUMN wms_shipment_order.updater IS '更新者';
COMMENT ON COLUMN wms_shipment_order.update_time IS '更新时间';
COMMENT ON COLUMN wms_shipment_order.deleted IS '是否删除';
COMMENT ON COLUMN wms_shipment_order.tenant_id IS '租户编号';

CREATE INDEX idx_shipment_order_no ON wms_shipment_order (no);
CREATE INDEX idx_shipment_order_warehouse_id ON wms_shipment_order (warehouse_id);

-- ============================================
-- 14. wms_shipment_order_detail
-- ============================================
DROP TABLE IF EXISTS wms_shipment_order_detail;
CREATE TABLE wms_shipment_order_detail (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    area_id BIGINT NOT NULL DEFAULT 0,
    inventory_detail_id BIGINT DEFAULT NULL,
    batch_no VARCHAR(64) DEFAULT NULL,
    production_date TIMESTAMP DEFAULT NULL,
    expiration_date TIMESTAMP DEFAULT NULL,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    price DECIMAL(16, 2) DEFAULT NULL,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_shipment_order_detail IS 'WMS 出库单明细';
COMMENT ON COLUMN wms_shipment_order_detail.id IS '编号';
COMMENT ON COLUMN wms_shipment_order_detail.order_id IS '出库单编号';
COMMENT ON COLUMN wms_shipment_order_detail.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_shipment_order_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN wms_shipment_order_detail.area_id IS '库区编号';
COMMENT ON COLUMN wms_shipment_order_detail.inventory_detail_id IS '库存明细编号';
COMMENT ON COLUMN wms_shipment_order_detail.batch_no IS '批号';
COMMENT ON COLUMN wms_shipment_order_detail.production_date IS '生产日期';
COMMENT ON COLUMN wms_shipment_order_detail.expiration_date IS '过期日期';
COMMENT ON COLUMN wms_shipment_order_detail.quantity IS '出库数量';
COMMENT ON COLUMN wms_shipment_order_detail.price IS '单价';
COMMENT ON COLUMN wms_shipment_order_detail.total_price IS '行金额';
COMMENT ON COLUMN wms_shipment_order_detail.creator IS '创建者';
COMMENT ON COLUMN wms_shipment_order_detail.create_time IS '创建时间';
COMMENT ON COLUMN wms_shipment_order_detail.updater IS '更新者';
COMMENT ON COLUMN wms_shipment_order_detail.update_time IS '更新时间';
COMMENT ON COLUMN wms_shipment_order_detail.deleted IS '是否删除';
COMMENT ON COLUMN wms_shipment_order_detail.tenant_id IS '租户编号';

CREATE INDEX idx_shipment_order_detail_order_id ON wms_shipment_order_detail (order_id);
CREATE INDEX idx_shipment_order_detail_sku_id ON wms_shipment_order_detail (sku_id);

-- ============================================
-- 15. wms_movement_order
-- ============================================
DROP TABLE IF EXISTS wms_movement_order;
CREATE TABLE wms_movement_order (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(64) NOT NULL,
    order_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status INTEGER NOT NULL DEFAULT 0,
    remark VARCHAR(255) DEFAULT NULL,
    source_warehouse_id BIGINT NOT NULL,
    source_area_id BIGINT NOT NULL DEFAULT 0,
    target_warehouse_id BIGINT NOT NULL,
    target_area_id BIGINT NOT NULL DEFAULT 0,
    total_quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_movement_order IS 'WMS 移库单';
COMMENT ON COLUMN wms_movement_order.id IS '编号';
COMMENT ON COLUMN wms_movement_order.no IS '移库单号';
COMMENT ON COLUMN wms_movement_order.order_time IS '单据日期';
COMMENT ON COLUMN wms_movement_order.status IS '移库状态';
COMMENT ON COLUMN wms_movement_order.remark IS '备注';
COMMENT ON COLUMN wms_movement_order.source_warehouse_id IS '来源仓库编号';
COMMENT ON COLUMN wms_movement_order.source_area_id IS '来源库区编号';
COMMENT ON COLUMN wms_movement_order.target_warehouse_id IS '目标仓库编号';
COMMENT ON COLUMN wms_movement_order.target_area_id IS '目标库区编号';
COMMENT ON COLUMN wms_movement_order.total_quantity IS '总数量';
COMMENT ON COLUMN wms_movement_order.total_price IS '总金额';
COMMENT ON COLUMN wms_movement_order.creator IS '创建者';
COMMENT ON COLUMN wms_movement_order.create_time IS '创建时间';
COMMENT ON COLUMN wms_movement_order.updater IS '更新者';
COMMENT ON COLUMN wms_movement_order.update_time IS '更新时间';
COMMENT ON COLUMN wms_movement_order.deleted IS '是否删除';
COMMENT ON COLUMN wms_movement_order.tenant_id IS '租户编号';

CREATE INDEX idx_movement_order_no ON wms_movement_order (no);
CREATE INDEX idx_movement_order_source_warehouse ON wms_movement_order (source_warehouse_id);
CREATE INDEX idx_movement_order_target_warehouse ON wms_movement_order (target_warehouse_id);

-- ============================================
-- 16. wms_movement_order_detail
-- ============================================
DROP TABLE IF EXISTS wms_movement_order_detail;
CREATE TABLE wms_movement_order_detail (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    source_warehouse_id BIGINT NOT NULL,
    source_area_id BIGINT NOT NULL DEFAULT 0,
    inventory_detail_id BIGINT DEFAULT NULL,
    target_warehouse_id BIGINT NOT NULL,
    target_area_id BIGINT NOT NULL DEFAULT 0,
    batch_no VARCHAR(64) DEFAULT NULL,
    production_date TIMESTAMP DEFAULT NULL,
    expiration_date TIMESTAMP DEFAULT NULL,
    quantity DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
    price DECIMAL(16, 2) DEFAULT NULL,
    total_price DECIMAL(16, 2) DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE wms_movement_order_detail IS 'WMS 移库单明细';
COMMENT ON COLUMN wms_movement_order_detail.id IS '编号';
COMMENT ON COLUMN wms_movement_order_detail.order_id IS '移库单编号';
COMMENT ON COLUMN wms_movement_order_detail.sku_id IS '商品 SKU 编号';
COMMENT ON COLUMN wms_movement_order_detail.source_warehouse_id IS '来源仓库编号';
COMMENT ON COLUMN wms_movement_order_detail.source_area_id IS '来源库区编号';
COMMENT ON COLUMN wms_movement_order_detail.inventory_detail_id IS '库存明细编号';
COMMENT ON COLUMN wms_movement_order_detail.target_warehouse_id IS '目标仓库编号';
COMMENT ON COLUMN wms_movement_order_detail.target_area_id IS '目标库区编号';
COMMENT ON COLUMN wms_movement_order_detail.batch_no IS '批号';
COMMENT ON COLUMN wms_movement_order_detail.production_date IS '生产日期';
COMMENT ON COLUMN wms_movement_order_detail.expiration_date IS '过期日期';
COMMENT ON COLUMN wms_movement_order_detail.quantity IS '移库数量';
COMMENT ON COLUMN wms_movement_order_detail.price IS '单价';
COMMENT ON COLUMN wms_movement_order_detail.total_price IS '行金额';
COMMENT ON COLUMN wms_movement_order_detail.creator IS '创建者';
COMMENT ON COLUMN wms_movement_order_detail.create_time IS '创建时间';
COMMENT ON COLUMN wms_movement_order_detail.updater IS '更新者';
COMMENT ON COLUMN wms_movement_order_detail.update_time IS '更新时间';
COMMENT ON COLUMN wms_movement_order_detail.deleted IS '是否删除';
COMMENT ON COLUMN wms_movement_order_detail.tenant_id IS '租户编号';

CREATE INDEX idx_movement_order_detail_order_id ON wms_movement_order_detail (order_id);
CREATE INDEX idx_movement_order_detail_sku_id ON wms_movement_order_detail (sku_id);

-- ============================================
-- 数据导入（使用 COPY 或 INSERT）
-- ============================================
-- 注意：由于数据量很大，这里只展示 INSERT 语句的格式
-- 实际使用时，可以使用 pg_dump 的 --data-only 或 COPY 命令
--
-- 例如导入 wms_warehouse 数据：
-- INSERT INTO wms_warehouse (id, code, name, remark, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
-- (1, 'SH', '上海仓', NULL, 2, '1', '2026-05-10 00:29:09', '1', '2026-05-10 00:52:42', FALSE, 1),
-- (2, 'BJ', '北京仓', NULL, 1, '1', '2026-05-10 00:29:16', '1', '2026-05-10 00:29:16', FALSE, 1),
-- ...
--
-- 或者使用 COPY 命令从 CSV 文件导入
-- COPY wms_warehouse FROM '/path/to/warehouse.csv' WITH CSV HEADER;
-- ============================================
-- Sequences for auto-increment IDs
-- ============================================
CREATE SEQUENCE IF NOT EXISTS wms_inventory_seq;
CREATE SEQUENCE IF NOT EXISTS wms_inventory_history_seq;
CREATE SEQUENCE IF NOT EXISTS wms_item_brand_seq;
CREATE SEQUENCE IF NOT EXISTS wms_item_category_seq;
CREATE SEQUENCE IF NOT EXISTS wms_item_seq;
CREATE SEQUENCE IF NOT EXISTS wms_item_sku_seq;
CREATE SEQUENCE IF NOT EXISTS wms_merchant_seq;
CREATE SEQUENCE IF NOT EXISTS wms_warehouse_seq;
CREATE SEQUENCE IF NOT EXISTS wms_check_order_detail_seq;
CREATE SEQUENCE IF NOT EXISTS wms_check_order_seq;
CREATE SEQUENCE IF NOT EXISTS wms_movement_order_detail_seq;
CREATE SEQUENCE IF NOT EXISTS wms_movement_order_seq;
CREATE SEQUENCE IF NOT EXISTS wms_receipt_order_detail_seq;
CREATE SEQUENCE IF NOT EXISTS wms_receipt_order_seq;
CREATE SEQUENCE IF NOT EXISTS wms_shipment_order_detail_seq;
CREATE SEQUENCE IF NOT EXISTS wms_shipment_order_seq;
