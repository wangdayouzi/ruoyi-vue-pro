-- PostgreSQL 专用：领用台账，不关联试剂库存、ERP 或 LIMS。
CREATE SEQUENCE IF NOT EXISTS sample_loan_seq START 1;

CREATE TABLE IF NOT EXISTS sample_loan (
  id bigint NOT NULL PRIMARY KEY,
  bas_no varchar(64) NOT NULL,
  material_type varchar(16) NOT NULL,
  location varchar(32) NOT NULL,
  requester_id bigint,
  requester varchar(64) NOT NULL,
  submitter_id bigint,
  submitter varchar(64),
  sample_info varchar(500),
  remark varchar(500),
  status smallint NOT NULL,
  return_time timestamp,
  creator varchar(64) DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted smallint NOT NULL DEFAULT 0,
  tenant_id bigint NOT NULL DEFAULT 0
);

-- 兼容已经按前一版脚本建出的表。
ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS requester_id bigint;
ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS submitter_id bigint;
ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS submitter varchar(64);
-- 兼容前一版已存在且已有数据的表：先补齐默认值，再收紧为必填。
ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS material_type varchar(16);
ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS location varchar(32);
UPDATE sample_loan SET material_type = 'SAMPLE' WHERE material_type IS NULL;
UPDATE sample_loan SET location = '4楼' WHERE location IS NULL;
ALTER TABLE sample_loan ALTER COLUMN material_type SET NOT NULL;
ALTER TABLE sample_loan ALTER COLUMN location SET NOT NULL;
SELECT setval('sample_loan_seq', COALESCE((SELECT MAX(id) FROM sample_loan), 0) + 1, false);

CREATE INDEX IF NOT EXISTS idx_sample_loan_bas_status ON sample_loan (bas_no, status);
CREATE INDEX IF NOT EXISTS idx_sample_loan_status_create_time ON sample_loan (status, create_time);

COMMENT ON TABLE sample_loan IS '领用台账';
COMMENT ON COLUMN sample_loan.material_type IS '领用类型：SAMPLE-样品，REAGENT-试剂';
COMMENT ON COLUMN sample_loan.location IS '领用地点：4楼、8楼';
COMMENT ON COLUMN sample_loan.sample_info IS '物料信息（选填）';
COMMENT ON COLUMN sample_loan.status IS '状态：1-可领用，2-已归还，3-无需归还';

-- 菜单挂到已有“试剂管理”目录。大屏路由不写入 system_menu。
DO $$
DECLARE
  reagent_parent_id bigint;
  sample_loan_menu_id bigint;
  query_menu_id bigint;
  create_menu_id bigint;
  update_menu_id bigint;
  delete_menu_id bigint;
  no_return_menu_id bigint;
  return_menu_id bigint;
BEGIN
  -- 以当前最大菜单 ID 校准系统菜单序列，避免固定 ID 与现有环境冲突。
  PERFORM setval(
    'system_menu_seq',
    GREATEST(COALESCE((SELECT MAX(id) FROM system_menu), 0), 1),
    true
  );

  -- 角色菜单关联表可能因历史导入导致序列落后，先按当前最大 ID 校准。
  -- 否则修改角色菜单权限时会出现 pk_system_role_menu 主键冲突。
  PERFORM setval(
    'system_role_menu_seq',
    GREATEST(COALESCE((SELECT MAX(id) FROM system_role_menu), 0), 1),
    true
  );

  SELECT id INTO reagent_parent_id
  FROM system_menu
  WHERE name = '试剂管理' AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF reagent_parent_id IS NULL THEN
    RAISE EXCEPTION '未找到“试剂管理”菜单，无法创建“领用列表”菜单';
  END IF;

  SELECT id INTO sample_loan_menu_id
  FROM system_menu
  WHERE parent_id = reagent_parent_id AND path = 'sample-loan' AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF sample_loan_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '领用列表', '', 2, 99, reagent_parent_id, 'sample-loan', 'ep:document',
       'reagent/sample-loan/index', 'ReagentSampleLoan', 0, true, true, true,
       '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO sample_loan_menu_id;
  END IF;

  UPDATE system_menu SET name = '领用列表', update_time = CURRENT_TIMESTAMP, updater = '1'
  WHERE id = sample_loan_menu_id AND deleted = 0;

  SELECT id INTO query_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:query'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF query_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '查询', 'reagent:sample-loan:query', 3, 1, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO query_menu_id;
  END IF;

  SELECT id INTO create_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:create'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF create_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '新增领用', 'reagent:sample-loan:create', 3, 2, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO create_menu_id;
  END IF;

  SELECT id INTO update_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:update'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF update_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '修改', 'reagent:sample-loan:update', 3, 3, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO update_menu_id;
  END IF;

  SELECT id INTO delete_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:delete'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF delete_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '删除', 'reagent:sample-loan:delete', 3, 4, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO delete_menu_id;
  END IF;

  SELECT id INTO no_return_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:no-return'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF no_return_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '无需归还', 'reagent:sample-loan:no-return', 3, 5, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO no_return_menu_id;
  END IF;

  SELECT id INTO return_menu_id
  FROM system_menu
  WHERE parent_id = sample_loan_menu_id
    AND permission = 'reagent:sample-loan:return'
    AND deleted = 0
  ORDER BY id
  LIMIT 1;

  IF return_menu_id IS NULL THEN
    INSERT INTO system_menu
      (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
       status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
    VALUES
      (nextval('system_menu_seq'), '归还', 'reagent:sample-loan:return', 3, 6, sample_loan_menu_id, '', '', '', '',
       0, true, true, true, '1', CURRENT_TIMESTAMP, '1', CURRENT_TIMESTAMP, 0)
    RETURNING id INTO return_menu_id;
  END IF;
END $$;
