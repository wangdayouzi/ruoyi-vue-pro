-- ============================================================
-- IT 工具箱（infra_toolbox_tool）建表 + 菜单
-- 数据库：PostgreSQL（与 wms-menu-pg.sql 风格一致，幂等）
-- ============================================================

-- ----------------------------
-- 1. 工具表
-- ----------------------------
CREATE TABLE IF NOT EXISTS infra_toolbox_tool (
    id             int8          NOT NULL,
    name           varchar(100)  NOT NULL,
    category       varchar(50)   NOT NULL DEFAULT '',
    icon           varchar(255)  NOT NULL DEFAULT '',
    description    varchar(500)  NOT NULL DEFAULT '',
    version        varchar(30)   NOT NULL DEFAULT '',
    file_url       varchar(512)  NOT NULL DEFAULT '',
    file_size      int8          NOT NULL DEFAULT 0,
    platform       varchar(30)   NOT NULL DEFAULT '',
    sort           int4          NOT NULL DEFAULT 0,
    status         int2          NOT NULL DEFAULT 0,
    download_count int4          NOT NULL DEFAULT 0,
    creator        varchar(64)   NULL     DEFAULT '',
    create_time    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater        varchar(64)   NULL     DEFAULT '',
    update_time    timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        int2          NOT NULL DEFAULT 0,
    tenant_id      int8          NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 主键序列：框架在 PostgreSQL 下按 @KeySequence("infra_toolbox_tool_seq") 从这里取 id
CREATE SEQUENCE IF NOT EXISTS infra_toolbox_tool_seq START 1;
-- 幂等保护：把序列推进到不低于当前表最大 id，避免重跑/已有数据时主键冲突
SELECT setval('infra_toolbox_tool_seq',
              GREATEST((SELECT COALESCE(MAX(id), 1) FROM infra_toolbox_tool),
                       (SELECT last_value FROM infra_toolbox_tool_seq)));

COMMENT ON TABLE infra_toolbox_tool IS 'IT 工具箱工具表';

-- 兼容：表已存在时补充分类字段（幂等）
ALTER TABLE infra_toolbox_tool ADD COLUMN IF NOT EXISTS category varchar(50) NOT NULL DEFAULT '';

COMMENT ON COLUMN infra_toolbox_tool.id IS '主键';
COMMENT ON COLUMN infra_toolbox_tool.name IS '工具名称';
COMMENT ON COLUMN infra_toolbox_tool.category IS '工具分类（字典：toolbox_tool_category）';
COMMENT ON COLUMN infra_toolbox_tool.icon IS '图标（Element Plus 图标名或图标 URL）';
COMMENT ON COLUMN infra_toolbox_tool.description IS '工具说明';
COMMENT ON COLUMN infra_toolbox_tool.version IS '版本号';
COMMENT ON COLUMN infra_toolbox_tool.file_url IS '下载地址（exe 文件 URL）';
COMMENT ON COLUMN infra_toolbox_tool.file_size IS '文件大小（字节）';
COMMENT ON COLUMN infra_toolbox_tool.platform IS '支持平台';
COMMENT ON COLUMN infra_toolbox_tool.sort IS '排序';
COMMENT ON COLUMN infra_toolbox_tool.status IS '状态（0 开启 1 关闭）';
COMMENT ON COLUMN infra_toolbox_tool.download_count IS '下载次数';
COMMENT ON COLUMN infra_toolbox_tool.creator IS '创建者';
COMMENT ON COLUMN infra_toolbox_tool.create_time IS '创建时间';
COMMENT ON COLUMN infra_toolbox_tool.updater IS '更新者';
COMMENT ON COLUMN infra_toolbox_tool.update_time IS '更新时间';
COMMENT ON COLUMN infra_toolbox_tool.deleted IS '是否删除';
COMMENT ON COLUMN infra_toolbox_tool.tenant_id IS '租户编号';

-- ----------------------------
-- 2. 菜单（挂在“基础设施”下：一级目录 + 工具管理 + 按钮权限）
-- 注：id 用 MAX(id)+偏移 动态生成，避免与已有数据冲突；NOT EXISTS 保证幂等
-- ----------------------------
-- 目录：IT 工具箱（基础设施 的子目录，path 用相对路径）
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 1, 'IT 工具箱', '', 1, 99,
       (SELECT id FROM system_menu WHERE name = '基础设施' AND deleted = 0),
       'toolbox', 'ep:tools', '', '', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE name = 'IT 工具箱' AND deleted = 0);

-- 兼容：若旧版脚本已把 IT 工具箱建成了顶级菜单，这里修正为“基础设施”的子菜单
UPDATE system_menu
SET parent_id = (SELECT id FROM system_menu WHERE name = '基础设施' AND deleted = 0),
    path = 'toolbox', sort = 99
WHERE name = 'IT 工具箱' AND deleted = 0
  AND parent_id <> (SELECT id FROM system_menu WHERE name = '基础设施' AND deleted = 0);

-- 二级菜单：工具管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 2, '工具管理', 'infra:toolbox-tool:list', 2, 1,
       (SELECT id FROM system_menu WHERE name = 'IT 工具箱' AND deleted = 0),
       'tool', 'ep:box', 'infra/toolboxTool/index', 'InfraToolboxTool', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE name = '工具管理' AND deleted = 0);

-- 按钮：工具查询
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 3, '工具查询', 'infra:toolbox-tool:query', 3, 1,
       (SELECT id FROM system_menu WHERE name = '工具管理' AND deleted = 0),
       '', '', '', '', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE permission = 'infra:toolbox-tool:query' AND deleted = 0);

-- 按钮：工具新增
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 4, '工具新增', 'infra:toolbox-tool:create', 3, 2,
       (SELECT id FROM system_menu WHERE name = '工具管理' AND deleted = 0),
       '', '', '', '', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE permission = 'infra:toolbox-tool:create' AND deleted = 0);

-- 按钮：工具修改
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 5, '工具修改', 'infra:toolbox-tool:update', 3, 3,
       (SELECT id FROM system_menu WHERE name = '工具管理' AND deleted = 0),
       '', '', '', '', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE permission = 'infra:toolbox-tool:update' AND deleted = 0);

-- 按钮：工具删除
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_menu) + 6, '工具删除', 'infra:toolbox-tool:delete', 3, 4,
       (SELECT id FROM system_menu WHERE name = '工具管理' AND deleted = 0),
       '', '', '', '', 0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE permission = 'infra:toolbox-tool:delete' AND deleted = 0);

-- ----------------------------
-- 3. 字典：工具箱工具分类（幂等，id 动态分配）
-- ----------------------------
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_dict_type) + 1, '工具箱工具分类', 'toolbox_tool_category', 0, 'IT 工具箱工具分类', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE type = 'toolbox_tool_category');

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_dict_data) + v.sort, v.sort, v.label, v.label, 'toolbox_tool_category', 0, v.color_type, '', '', '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0
FROM (VALUES
    (1, '网络工具', 'primary'),
    (2, '系统工具', 'success'),
    (3, '办公效率', 'warning'),
    (4, '数据库工具', 'danger'),
    (5, '开发工具', 'info'),
    (6, '其他', '')
) AS v(sort, label, color_type)
WHERE NOT EXISTS (SELECT 1 FROM system_dict_data d WHERE d.dict_type = 'toolbox_tool_category' AND d.value = v.label);
