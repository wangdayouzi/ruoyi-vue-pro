BEGIN;
CREATE TABLE IF NOT EXISTS form_secure_rule (
    id bigserial PRIMARY KEY, tenant_id bigint NOT NULL, category varchar(64) NOT NULL,
    instrument varchar(128) NOT NULL, path varchar(512) NOT NULL,
    permissions jsonb NOT NULL, enabled boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS form_secure_template (
    tenant_id bigint PRIMARY KEY, content bytea NOT NULL
);
CREATE TABLE IF NOT EXISTS form_secure_counter (
    tenant_id bigint NOT NULL, issue_year integer NOT NULL,
    next_no integer NOT NULL CHECK (next_no > 0), version integer NOT NULL,
    initialized_by bigint NOT NULL, initialized_at timestamptz NOT NULL DEFAULT now(),
    source varchar(200) NOT NULL, PRIMARY KEY(tenant_id,issue_year)
);
CREATE TABLE IF NOT EXISTS form_secure_application (
    id bigserial PRIMARY KEY, tenant_id bigint NOT NULL, applicant_id bigint NOT NULL,
    request_id uuid NOT NULL, form_no varchar(40) NOT NULL, issue_year integer NOT NULL,
    sequence_no integer NOT NULL, project varchar(40) NOT NULL, accounts varchar(200) NOT NULL,
    category varchar(64) NOT NULL, instrument varchar(128) NOT NULL, path text NOT NULL,
    permissions jsonb NOT NULL, pdf bytea NOT NULL, created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, applicant_id, request_id), UNIQUE (tenant_id,form_no),
    UNIQUE (tenant_id,issue_year,sequence_no)
);
CREATE INDEX IF NOT EXISTS idx_form_secure_my_application ON form_secure_application(tenant_id,applicant_id,id DESC);

ALTER TABLE form_secure_rule ADD COLUMN IF NOT EXISTS deleted boolean NOT NULL DEFAULT false;

CREATE OR REPLACE FUNCTION pg_temp.secure_disk_permissions(legacy text) RETURNS jsonb
LANGUAGE sql IMMUTABLE STRICT AS $fn$
    SELECT COALESCE(jsonb_agg(
        CASE WHEN item ~ '^读取[①-⑤]$' THEN 'R' || strpos('①②③④⑤',right(item,1))
             WHEN item ~ '^写入[①-⑥]$' THEN 'W' || strpos('①②③④⑤⑥',right(item,1))
             ELSE item END ORDER BY ordinal), '[]'::jsonb)
    FROM regexp_split_to_table(legacy, '[,，、]') WITH ORDINALITY AS parts(raw,ordinal)
    CROSS JOIN LATERAL (SELECT btrim(raw) AS item) AS cleaned
$fn$;

DO $migration$
BEGIN
    IF pg_typeof((SELECT permissions FROM form_secure_rule LIMIT 1)) <> 'jsonb'::regtype THEN
        ALTER TABLE form_secure_rule ALTER COLUMN permissions TYPE jsonb
            USING pg_temp.secure_disk_permissions(permissions::text);
    END IF;
    IF pg_typeof((SELECT permissions FROM form_secure_application LIMIT 1)) <> 'jsonb'::regtype THEN
        ALTER TABLE form_secure_application ALTER COLUMN permissions TYPE jsonb
            USING pg_temp.secure_disk_permissions(permissions::text);
    END IF;
END $migration$;
DO $index$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_index WHERE indexrelid=to_regclass('uq_form_secure_rule') AND indpred IS NULL) THEN
        DROP INDEX uq_form_secure_rule;
    END IF;
END $index$;
CREATE UNIQUE INDEX IF NOT EXISTS uq_form_secure_rule ON form_secure_rule(tenant_id,category,instrument,path) WHERE NOT deleted;

ALTER TABLE form_secure_application ALTER COLUMN form_no DROP NOT NULL;
ALTER TABLE form_secure_application ALTER COLUMN issue_year DROP NOT NULL;
ALTER TABLE form_secure_application ALTER COLUMN sequence_no DROP NOT NULL;
ALTER TABLE form_secure_application ALTER COLUMN pdf DROP NOT NULL;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS suggested_sequence integer;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS issued_at timestamptz;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS issued_by bigint;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS voided_at timestamptz;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS voided_by bigint;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS void_reason varchar(200);
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS replaced_by bigint;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS people_ids jsonb NOT NULL DEFAULT '[]'::jsonb;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS rule_ids jsonb NOT NULL DEFAULT '[]'::jsonb;
UPDATE form_secure_application SET issued_at=created_at WHERE form_no IS NOT NULL AND issued_at IS NULL;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS applicant_name text;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS participant_names jsonb NOT NULL DEFAULT '[]'::jsonb;
ALTER TABLE form_secure_application ADD COLUMN IF NOT EXISTS approver_name text;
DO $submit_migration$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_attribute
        WHERE attrelid='form_secure_application'::regclass AND attname='submitted_at' AND NOT attisdropped
    ) THEN
        ALTER TABLE form_secure_application ADD COLUMN submitted_at timestamptz;
        UPDATE form_secure_application SET submitted_at=created_at;

    END IF;
END
$submit_migration$;
CREATE INDEX IF NOT EXISTS idx_form_secure_issued ON form_secure_application(tenant_id,issued_at) WHERE form_no IS NOT NULL;

CREATE TABLE IF NOT EXISTS form_secure_account (
    tenant_id bigint NOT NULL, user_id bigint NOT NULL, nas_account varchar(100) NOT NULL,
    domain_account varchar(100), PRIMARY KEY(tenant_id,user_id)
);

LOCK TABLE system_menu IN SHARE ROW EXCLUSIVE MODE;
DO $$
DECLARE
    root_id bigint;
    disk_id bigint;
    menu_id bigint;
    item record;
BEGIN
    PERFORM setval('system_menu_seq', GREATEST((SELECT COALESCE(MAX(id),1) FROM system_menu),
        (SELECT last_value FROM system_menu_seq)),true);
    SELECT id INTO root_id FROM system_menu WHERE parent_id=0 AND path='/form-management' AND deleted=0;
    IF root_id IS NULL THEN
        root_id := nextval('system_menu_seq');
        INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,status,visible,keep_alive,always_show,creator,updater,deleted)
        VALUES(root_id,'表单管理','',1,55,0,'/form-management','ep:document',0,true,true,true,'1','1',0);
    END IF;
    SELECT id INTO disk_id FROM system_menu WHERE parent_id=root_id AND path='secure-disk' AND deleted=0;
    IF disk_id IS NULL THEN
        disk_id := nextval('system_menu_seq');
        INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,status,visible,keep_alive,always_show,creator,updater,deleted)
        VALUES(disk_id,'安全盘','',1,1,root_id,'secure-disk','ep:folder',0,true,true,true,'1','1',0);
    END IF;
    FOR item IN SELECT * FROM (VALUES ('安全盘申请','apply','SecureDiskApply',1),('规则配置','manage','SecureDiskManage',2))
         AS entries(label,slug,component_name,sort)
    LOOP
        IF NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted=0 AND parent_id=disk_id AND path=item.slug) THEN
            menu_id := nextval('system_menu_seq');
            INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
            VALUES(menu_id,item.label,'form:secure-disk:'||item.slug,2,item.sort,disk_id,item.slug,'',
                'form/secure-disk/'||item.slug,item.component_name,0,true,false,true,'1','1',0);
        END IF;
    END LOOP;
    UPDATE system_menu SET name='安全盘申请' WHERE deleted=0
        AND parent_id=disk_id AND component='form/secure-disk/apply';
    IF NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted=0 AND permission='form:secure-disk:issue') THEN
        INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
        SELECT nextval('system_menu_seq'),'确认表单及导出操作日志','form:secure-disk:issue',3,1,id,'','','','',0,true,false,true,'1','1',0
        FROM system_menu WHERE deleted=0 AND parent_id=disk_id AND path='apply';
    END IF;
    UPDATE system_menu SET name='确认表单及导出操作日志'
    WHERE deleted=0 AND permission='form:secure-disk:issue';
END $$;
COMMIT;
