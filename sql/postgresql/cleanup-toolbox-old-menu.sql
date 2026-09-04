-- =============================================================
-- 清理 IT 工具箱旧版脚本可能留下的菜单 / 字典
-- 适用：旧版 toolbox-tool.sql（硬编码 7800~7805 / 9000~9006、顶级菜单那版）
--       或想"先清空再跑新版"的干净重来场景
-- 幂等：按 name / permission / type 定位，可重复执行
--
-- ⚠️ 跑完这段后：
--   1) 再执行新版 sql/postgresql/toolbox-tool.sql（会自动按"基础设施 下"重建菜单 + 动态 id + 序列）
--   2) 清 Redis 菜单/权限缓存，或重启后端（裸 SQL 删除不会自动清缓存）
--   3) 重新登录 admin 刷新
-- =============================================================

-- 1. 删按钮权限（先删子级）
DELETE FROM system_menu
WHERE deleted = 0
  AND permission IN (
      'infra:toolbox-tool:query',
      'infra:toolbox-tool:create',
      'infra:toolbox-tool:update',
      'infra:toolbox-tool:delete'
  );

-- 1.2 兼容历史残留：删掉“父级已不存在”的工具按钮（孤儿，如旧版留下的 工具查询/创建/更新）
--     带 NOT EXISTS 父级保护，不会误删仍挂在有效父级下的按钮（如工装夹具台账 下的 5421）
DELETE FROM system_menu m
WHERE m.deleted = 0
  AND m.name IN ('工具查询', '工具创建', '工具更新', '工具删除')
  AND NOT EXISTS (SELECT 1 FROM system_menu p WHERE p.id = m.parent_id AND p.deleted = 0);

-- 2. 删 IT 工具箱目录 及其下的「工具管理」子菜单
--    ⚠️ 不能按 name='工具管理' 删——MES 系统下也有同名的「工具管理」目录(5400)！
--       必须只按 IT 工具箱专属标记（name/component/permission）定位
DELETE FROM system_menu
WHERE deleted = 0
  AND (name = 'IT 工具箱'                        -- 目录（该 name 全局唯一）
       OR component = 'infra/toolboxTool/index'   -- 「工具管理」子菜单（专属 component）
       OR permission = 'infra:toolbox-tool:list');

-- 3. 删字典（工具箱工具分类）
DELETE FROM system_dict_data
WHERE dict_type = 'toolbox_tool_category';

DELETE FROM system_dict_type
WHERE type = 'toolbox_tool_category';
