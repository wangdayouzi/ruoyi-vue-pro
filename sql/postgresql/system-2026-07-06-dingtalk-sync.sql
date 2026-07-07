-- ===================================================================
-- 增量 SQL：钉钉(第三方平台)部门/用户同步支持
-- 适用数据库：PostgreSQL
-- 日期：2026-07-06
-- ===================================================================

-- 1. system_dept 新增第三方来源字段
ALTER TABLE system_dept
    ADD COLUMN IF NOT EXISTS source_type int2 NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS source_dept_id varchar(64) NULL DEFAULT NULL;

COMMENT ON COLUMN system_dept.source_type IS '来源类型(0=内部创建, 20=钉钉, 30=企业微信, 40=飞书...)';
COMMENT ON COLUMN system_dept.source_dept_id IS '第三方平台部门ID';

-- 联合唯一索引，防止同一平台的同一部门重复同步
CREATE UNIQUE INDEX IF NOT EXISTS uk_system_dept_source
    ON system_dept(source_type, source_dept_id)
    WHERE source_type > 0 AND source_dept_id IS NOT NULL AND deleted = 0;

