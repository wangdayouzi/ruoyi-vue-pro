-- =============================================
-- 试剂标签打印 - 菜单（PostgreSQL）
-- 挂在 试剂管理(7000) 目录下，作为第二个二级菜单
-- 可直接重复执行（ON CONFLICT DO NOTHING）
-- =============================================

-- 4.6 二级菜单：试剂标签打印 (type=2 菜单, parent_id=7000)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7003, '试剂标签打印', '', 2, 3, 7000, 'label-print', 'ep:printer', 'reagent/label-print/index', 'ReagentLabelPrint', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 4.7 三级按钮：试剂标签打印-操作权限 (type=3 按钮, parent_id=7003)
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7031, '试剂标签查询', 'reagent:label-print:query', 3, 1, 7003, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (7032, '试剂标签打印', 'reagent:label-print:print', 3, 2, 7003, '', '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (id) DO NOTHING;
