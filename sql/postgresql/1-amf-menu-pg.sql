-- =============================================
-- 分析方法文件管理模块（AMF）- PostgreSQL 全新建表 + 菜单
-- 先删旧表再建新表，适用于开发阶段重建
-- =============================================

-- ==================== 0. 清理旧表 ====================

DROP TABLE IF EXISTS amf_file_version CASCADE;
DROP TABLE IF EXISTS amf_file CASCADE;
DROP TABLE IF EXISTS amf_business CASCADE;
DROP SEQUENCE IF EXISTS amf_business_seq;
DROP SEQUENCE IF EXISTS amf_file_seq;
DROP SEQUENCE IF EXISTS amf_file_version_seq;

-- ==================== 1. 建序列 ====================

CREATE SEQUENCE amf_business_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE amf_file_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE amf_file_version_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

-- ==================== 2. 建表 ====================

-- 业务单据主表
CREATE TABLE amf_business (
    id              BIGINT          NOT NULL    DEFAULT nextval('amf_business_seq'::regclass) PRIMARY KEY,
    method_no       VARCHAR(100)    NOT NULL,
    method_version  VARCHAR(100),
    method_name     VARCHAR(200),
    test_article    VARCHAR(500),
    matrix_type     VARCHAR(200),
    sd              VARCHAR(100),
    effective_date  DATE,
    status          SMALLINT        DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted         INT             DEFAULT 0
);
COMMENT ON TABLE amf_business IS '分析方法文件-业务单据主表';
COMMENT ON COLUMN amf_business.method_no IS '方法编号';
COMMENT ON COLUMN amf_business.method_version IS '版本号';
COMMENT ON COLUMN amf_business.method_name IS '方法名称';
COMMENT ON COLUMN amf_business.test_article IS '测试物';
COMMENT ON COLUMN amf_business.matrix_type IS '基质类型';
COMMENT ON COLUMN amf_business.sd IS 'SD';
COMMENT ON COLUMN amf_business.effective_date IS '签字生效日期';
COMMENT ON COLUMN amf_business.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN amf_business.creator IS '创建者';
COMMENT ON COLUMN amf_business.create_time IS '创建时间';
COMMENT ON COLUMN amf_business.updater IS '更新者';
COMMENT ON COLUMN amf_business.update_time IS '更新时间';
COMMENT ON COLUMN amf_business.deleted IS '是否删除';
COMMENT ON COLUMN amf_business.tenant_id IS '租户编号';

-- 文件表（一个业务单据可有多个文件）
CREATE TABLE amf_file (
    id              BIGINT          NOT NULL    DEFAULT nextval('amf_file_seq'::regclass) PRIMARY KEY,
    business_id     BIGINT          NOT NULL,
    file_name       VARCHAR(255)    NOT NULL,
    file_url        VARCHAR(500),
    file_version    VARCHAR(50)     DEFAULT '',
    effective_date  DATE,
    tenant_id       BIGINT          DEFAULT 0,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE amf_file IS '分析方法文件-文件表';
COMMENT ON COLUMN amf_file.business_id IS '关联业务单据ID';
COMMENT ON COLUMN amf_file.file_name IS '文件名';
COMMENT ON COLUMN amf_file.file_url IS '当前文件URL';
COMMENT ON COLUMN amf_file.file_version IS '当前版本号';
COMMENT ON COLUMN amf_file.effective_date IS '签字生效日期';
COMMENT ON COLUMN amf_file.creator IS '创建者';
COMMENT ON COLUMN amf_file.create_time IS '创建时间';
COMMENT ON COLUMN amf_file.tenant_id IS '租户编号';

-- 文件版本记录表
CREATE TABLE amf_file_version (
    id                  BIGINT          NOT NULL    DEFAULT nextval('amf_file_version_seq'::regclass) PRIMARY KEY,
    file_id             BIGINT          NOT NULL,
    business_id         BIGINT          NOT NULL,
    version_no          VARCHAR(50)     NOT NULL,
    file_name           VARCHAR(255)    NOT NULL,
    file_url            VARCHAR(500)    NOT NULL,
    file_size           BIGINT          DEFAULT 0,
    file_type           VARCHAR(20),
    change_description  VARCHAR(500),
    tenant_id           BIGINT          DEFAULT 0,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE amf_file_version IS '分析方法文件-文件版本记录表';
COMMENT ON COLUMN amf_file_version.file_id IS '关联文件ID';
COMMENT ON COLUMN amf_file_version.business_id IS '关联业务单据ID（冗余）';
COMMENT ON COLUMN amf_file_version.version_no IS '版本号';
COMMENT ON COLUMN amf_file_version.file_name IS '文件名';
COMMENT ON COLUMN amf_file_version.file_url IS '文件URL / OnlyOffice存储key';
COMMENT ON COLUMN amf_file_version.file_size IS '文件大小（字节）';
COMMENT ON COLUMN amf_file_version.file_type IS '文件类型（扩展名）';
COMMENT ON COLUMN amf_file_version.change_description IS '变更说明';
COMMENT ON COLUMN amf_file_version.creator IS '创建者';
COMMENT ON COLUMN amf_file_version.create_time IS '创建时间';
COMMENT ON COLUMN amf_file_version.tenant_id IS '租户编号';

-- ==================== 3. 索引 ====================

CREATE UNIQUE INDEX uk_amf_business_method_no ON amf_business (method_no) WHERE deleted = 0;
CREATE INDEX idx_amf_file_business_id ON amf_file (business_id);
CREATE INDEX idx_amf_file_version_file_id ON amf_file_version (file_id);
CREATE INDEX idx_amf_file_version_business_id ON amf_file_version (business_id);

-- ==================== 4. 菜单 ====================

-- ==================== 2. 菜单数据 ====================

-- 一级菜单：分析方法文件管理 (parent_id=0, type=1 目录)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6800, '分析方法文件', '', 1, 320, 0, '/amf', 'ep:document', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 二级菜单：业务单据管理 (type=2 菜单)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6801, '业务单据管理', '', 2, 1, 6800, 'business', 'ep:files', 'amf/business/index', 'AmfBusiness', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 三级按钮：查询 (type=3 按钮)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6811, '分析方法文件查询', 'amf:business:query', 3, 1, 6801, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 三级按钮：创建
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6812, '分析方法文件创建', 'amf:business:create', 3, 2, 6801, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 三级按钮：更新
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6813, '分析方法文件更新', 'amf:business:update', 3, 3, 6801, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 三级按钮：删除
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6814, '分析方法文件删除', 'amf:business:delete', 3, 4, 6801, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 三级按钮：上传文件
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (6815, '分析方法文件上传', 'amf:business:upload', 3, 5, 6801, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;
