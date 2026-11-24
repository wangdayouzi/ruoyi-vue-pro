-- 试剂样品管理 → 基础数据 → 临床方案、项目号、试剂基础数据
BEGIN;
SET LOCAL search_path TO public;
LOCK TABLE system_menu, system_role_menu, system_tenant_package IN SHARE ROW EXCLUSIVE MODE;

DO $$
DECLARE
    root_id bigint;
    folder_id bigint;
    base_id bigint;
    clinical_id bigint;
    item_id bigint;
    base_sort integer;
    moved_ids bigint[];
BEGIN
    SELECT id INTO STRICT root_id FROM system_menu
    WHERE deleted = 0 AND type = 1 AND parent_id = 0 AND path = '/reagent'
      AND name IN ('试剂管理', '试剂样品管理');

    SELECT id INTO folder_id FROM system_menu
    WHERE deleted = 0 AND type = 1 AND parent_id = root_id
      AND name = '基础数据' AND path = 'basic-data';

    IF EXISTS (
        SELECT 1 FROM system_menu
        WHERE deleted = 0 AND parent_id = root_id AND path = 'basic-data'
          AND id IS DISTINCT FROM folder_id
    ) THEN
        RAISE EXCEPTION 'basic-data 路由已被占用';
    END IF;

    SELECT id, sort INTO STRICT base_id, base_sort FROM system_menu
    WHERE deleted = 0 AND type = 2 AND component = 'reagent/base/index'
      AND parent_id IN (root_id, folder_id) AND name IN ('基础数据', '试剂基础数据');
    SELECT id INTO STRICT clinical_id FROM system_menu
    WHERE deleted = 0 AND type = 2 AND component = 'reagent/clinical/index'
      AND parent_id IN (root_id, folder_id);
    SELECT id INTO STRICT item_id FROM system_menu
    WHERE deleted = 0 AND type = 2 AND component = 'reagent/item/index'
      AND parent_id IN (root_id, folder_id);
    moved_ids := ARRAY[clinical_id, item_id, base_id];

    IF folder_id IS NULL THEN
        PERFORM setval('system_menu_seq', GREATEST(
            (SELECT max(id) FROM system_menu), (SELECT last_value FROM system_menu_seq)));
        INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon,
                                 status, visible, keep_alive, always_show, creator, updater)
        VALUES (nextval('system_menu_seq'), '基础数据', 1, base_sort, root_id, 'basic-data',
                'ep:folder', 0, true, true, true, '1', '1')
        RETURNING id INTO folder_id;
    END IF;

    UPDATE system_menu SET name = '试剂样品管理', update_time = CURRENT_TIMESTAMP
    WHERE id = root_id AND name <> '试剂样品管理';

    UPDATE system_menu AS menu
    SET name = target.name, parent_id = folder_id, sort = target.sort,
        update_time = CURRENT_TIMESTAMP
    FROM (VALUES (clinical_id, '临床方案', 1), (item_id, '项目号', 2),
                 (base_id, '试剂基础数据', 3)) AS target(id, name, sort)
    WHERE menu.id = target.id
      AND (menu.name, menu.parent_id, menu.sort) IS DISTINCT FROM
          (target.name, folder_id, target.sort);

    -- 补上原有页面对应的租户套餐目录权限。
    UPDATE system_tenant_package AS package
    SET menu_ids = (package.menu_ids::jsonb || jsonb_build_array(folder_id))::text,
        update_time = CURRENT_TIMESTAMP
    WHERE package.deleted = 0
      AND NOT (package.menu_ids::jsonb @> jsonb_build_array(folder_id))
      AND EXISTS (
          SELECT 1 FROM unnest(moved_ids) AS moved(menu_id)
          WHERE package.menu_ids::jsonb @> jsonb_build_array(moved.menu_id)
      );

    -- 补上原有页面对应的角色目录权限。
    PERFORM setval('system_role_menu_seq', GREATEST(
        (SELECT max(id) FROM system_role_menu), (SELECT last_value FROM system_role_menu_seq)));
    INSERT INTO system_role_menu (id, role_id, menu_id, tenant_id, creator, updater)
    SELECT nextval('system_role_menu_seq'), granted.role_id, folder_id, granted.tenant_id, '1', '1'
    FROM (
        SELECT DISTINCT role_id, tenant_id FROM system_role_menu
        WHERE deleted = 0 AND menu_id = ANY (moved_ids)
    ) AS granted
    WHERE NOT EXISTS (
        SELECT 1 FROM system_role_menu AS existing
        WHERE existing.deleted = 0 AND existing.menu_id = folder_id
          AND existing.role_id = granted.role_id AND existing.tenant_id = granted.tenant_id
    );
END $$;
COMMIT;
