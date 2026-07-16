-- =============================================
-- 分析方法文件管理模块（AMF）- MySQL 全新建表 + 菜单
-- 先删旧表再建新表，适用于开发阶段重建
-- =============================================

-- ==================== 0. 清理旧表 ====================

DROP TABLE IF EXISTS amf_file_version;
DROP TABLE IF EXISTS amf_file;
DROP TABLE IF EXISTS amf_business;

-- ==================== 1. 建表 ====================

-- 业务单据主表
CREATE TABLE amf_business (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    method_no       VARCHAR(100)    NOT NULL,
    method_version  VARCHAR(100),
    method_name     VARCHAR(200),
    test_article    VARCHAR(500),
    matrix_type     VARCHAR(200),
    sd              VARCHAR(100),
    effective_date  DATE,
    status          TINYINT         DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         DEFAULT 0
) COMMENT '分析方法文件-业务单据主表';

-- 文件表（一个业务单据可有多个文件）
CREATE TABLE amf_file (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    business_id     BIGINT          NOT NULL,
    file_name       VARCHAR(255)    NOT NULL,
    file_url        VARCHAR(500),
    file_version    INT             DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP
) COMMENT '分析方法文件-文件表';

-- 文件版本记录表
CREATE TABLE amf_file_version (
    id                  BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    file_id             BIGINT          NOT NULL,
    business_id         BIGINT          NOT NULL,
    version_no          INT             NOT NULL,
    file_name           VARCHAR(255)    NOT NULL,
    file_url            VARCHAR(500)    NOT NULL,
    file_size           BIGINT          DEFAULT 0,
    file_type           VARCHAR(20),
    change_description  VARCHAR(500),
    tenant_id           BIGINT          DEFAULT 0,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         DATETIME        DEFAULT CURRENT_TIMESTAMP
) COMMENT '分析方法文件-文件版本记录表';

-- ==================== 2. 索引 ====================

CREATE UNIQUE INDEX uk_amf_business_method_no ON amf_business (method_no);
CREATE INDEX idx_amf_file_business_id ON amf_file (business_id);
CREATE INDEX idx_amf_file_version_file_id ON amf_file_version (file_id);
CREATE INDEX idx_amf_file_version_business_id ON amf_file_version (business_id);
