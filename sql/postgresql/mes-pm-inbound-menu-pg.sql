-- =============================================================
-- PM采购入库单明细数据 菜单（MES 仓库管理 5780 下，替代原 出入库流水）
-- master 库 yudao（PostgreSQL）执行
-- 用 PL/pgSQL 动态取 id（MAX(id)+1），避免与库里已有菜单冲突；可重复执行
-- 同时清理原 出入库流水 菜单（mes:wm-transaction）
-- =============================================================

DO $$
DECLARE
    v_menu_id bigint;  -- PM采购入库单明细数据 菜单 id
    v_q_id    bigint;  -- 明细查询 按钮 id
    v_e_id    bigint;  -- 明细导出 按钮 id
    v_rm_max  bigint;  -- system_role_menu 当前最大 id
BEGIN
    -- 0) 清理：旧的出入库流水菜单 + 本次标识（幂等）
    DELETE FROM system_role_menu
    WHERE menu_id IN (SELECT id FROM system_menu
                      WHERE name = '出入库流水' OR permission LIKE 'mes:wm-transaction:%'
                         OR name = 'PM采购入库单明细数据' OR permission LIKE 'mes:pm-inbound:%');
    DELETE FROM system_menu
    WHERE name = '出入库流水' OR permission LIKE 'mes:wm-transaction:%'
       OR name = 'PM采购入库单明细数据' OR permission LIKE 'mes:pm-inbound:%';

    -- 1) 动态分配菜单 id
    SELECT COALESCE(MAX(id), 0) + 1 INTO v_menu_id FROM system_menu;
    v_q_id := v_menu_id + 1;
    v_e_id := v_menu_id + 2;

    -- 2) 菜单：PM采购入库单明细数据（type=2，父=5780 仓库管理，sort=3）
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (v_menu_id, 'PM采购入库单明细数据', '', 2, 3, 5780, 'pm-inbound', 'ep:list',
       'mes/pm/inbound/index', 'MesPmInbound',
       0, true, true, true, '1', now(), '1', now(), 0);

    -- 3) 按钮权限：查询 / 导出（type=3）
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (v_q_id, '采购入库单查询', 'mes:pm-inbound:query', 3, 1, v_menu_id, '', '', '', '',
       0, true, true, true, '1', now(), '1', now(), 0),
      (v_e_id, '采购入库单导出', 'mes:pm-inbound:export', 3, 2, v_menu_id, '', '', '', '',
       0, true, true, true, '1', now(), '1', now(), 0);

    -- 4) 授权给 超管角色(role_id=2, tenant 1)；其他角色请在 系统管理-菜单管理 勾选
    SELECT COALESCE(MAX(id), 0) INTO v_rm_max FROM system_role_menu;
    INSERT INTO system_role_menu (id, role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
    VALUES
      (v_rm_max + 1, 2, v_menu_id, '1', now(), '1', now(), 0, 1),
      (v_rm_max + 2, 2, v_q_id,    '1', now(), '1', now(), 0, 1),
      (v_rm_max + 3, 2, v_e_id,    '1', now(), '1', now(), 0, 1);

    RAISE NOTICE 'PM采购入库单明细数据菜单已创建: 菜单=% 查询=% 导出=%，并已授权超管', v_menu_id, v_q_id, v_e_id;
END $$;
