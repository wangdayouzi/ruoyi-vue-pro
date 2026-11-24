BEGIN;
CREATE TABLE IF NOT EXISTS reagent_matrix_bas_history (
    tenant_id bigint NOT NULL,
    site varchar(16) NOT NULL CHECK (site IN ('NINGBO', 'SHANGHAI')),
    matrix_kind char(1) NOT NULL CHECK (matrix_kind IN ('B', 'P')),
    bas_no varchar(10) NOT NULL CHECK (bas_no ~ '^[0-9]{2}BAS[0-9]{4}[BP]$' AND right(bas_no, 1) = matrix_kind),
    PRIMARY KEY (tenant_id, site, bas_no)
);
CREATE TABLE IF NOT EXISTS reagent_matrix_inbound_counter (
    tenant_id bigint NOT NULL, site varchar(16) NOT NULL, issue_year integer NOT NULL,
    matrix_kind char(1) NOT NULL, last_no integer NOT NULL CHECK (last_no BETWEEN 0 AND 9999),
    PRIMARY KEY (tenant_id, site, issue_year, matrix_kind)
);
CREATE TABLE IF NOT EXISTS reagent_matrix_inbound (
    id bigserial PRIMARY KEY, tenant_id bigint NOT NULL,
    site varchar(16) NOT NULL CHECK (site IN ('NINGBO','SHANGHAI')),
    matrix_kind char(1) NOT NULL CHECK (matrix_kind IN ('B','P')),
    bas_no varchar(10) NOT NULL, applicant_id bigint, applicant_name varchar(200) NOT NULL,
    request_id uuid, created_at timestamp NOT NULL, appointment_date timestamp,
    lims_designed boolean, lims_inbound boolean, exception_remark text,
    registered_by bigint, registered_name varchar(200), registered_at timestamp, version integer NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, site, bas_no), UNIQUE (tenant_id, applicant_id, request_id)
);
CREATE INDEX IF NOT EXISTS idx_matrix_inbound_site ON reagent_matrix_inbound(tenant_id, site, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_matrix_inbound_user ON reagent_matrix_inbound(tenant_id, applicant_id, created_at DESC);
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS deleted boolean NOT NULL DEFAULT false;
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS urgent boolean NOT NULL DEFAULT false;
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS appointment_user_ids jsonb NOT NULL DEFAULT '[]'::jsonb;
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS registered_name varchar(200);
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS updated_by bigint;
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS updated_at timestamp;
ALTER TABLE reagent_matrix_inbound ADD COLUMN IF NOT EXISTS inbound_status smallint CHECK (inbound_status BETWEEN 0 AND 3);
UPDATE reagent_matrix_inbound SET inbound_status=CASE WHEN lims_inbound IS TRUE THEN 3 WHEN lims_inbound IS FALSE THEN 1 ELSE 0 END WHERE inbound_status IS NULL;
ALTER TABLE reagent_matrix_inbound ALTER COLUMN inbound_status SET DEFAULT 0;
ALTER TABLE reagent_matrix_inbound ALTER COLUMN inbound_status SET NOT NULL;
CREATE TABLE IF NOT EXISTS reagent_matrix_inbound_log (
    id bigserial PRIMARY KEY, tenant_id bigint NOT NULL, inbound_id bigint NOT NULL,
    action varchar(16) NOT NULL, from_status smallint NOT NULL, to_status smallint NOT NULL,
    remark varchar(2000), operator_id bigint NOT NULL, operator_name varchar(200) NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_matrix_inbound_log ON reagent_matrix_inbound_log(tenant_id,inbound_id,id);
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_attribute WHERE attrelid='reagent_matrix_inbound'::regclass
               AND attname='appointment_date' AND atttypid='date'::regtype AND NOT attisdropped) THEN
        ALTER TABLE reagent_matrix_inbound ALTER COLUMN appointment_date TYPE timestamp USING appointment_date::timestamp;
    END IF;
END $$;
LOCK TABLE system_menu IN SHARE ROW EXCLUSIVE MODE;
DO $$
DECLARE parent bigint; menu bigint; entry record;
BEGIN
    SELECT id INTO STRICT parent FROM system_menu WHERE deleted=0 AND type=1 AND name IN ('试剂样品管理','试剂管理')
      AND (name='试剂样品管理' OR NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted=0 AND type=1 AND name='试剂样品管理'));
    SELECT id INTO menu FROM system_menu WHERE deleted=0 AND component='reagent/matrix-inbound/index';
    IF menu IS NULL THEN
        IF EXISTS (SELECT 1 FROM system_menu WHERE deleted=0 AND parent_id=parent AND (path='matrix-inbound' OR name='基质入库登记')) THEN
            RAISE EXCEPTION '基质入库菜单已存在但组件不同，请核对';
        END IF;
        SELECT COALESCE(MAX(id),0)+1 INTO menu FROM system_menu;
        INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
        VALUES(menu,'基质入库登记','',2,52,parent,'matrix-inbound','ep:collection','reagent/matrix-inbound/index','ReagentMatrixInbound',0,true,true,true,'1','1',0);
    END IF;
    FOR entry IN SELECT * FROM (VALUES ('查询','query',1),('新建预约','create',2),('登记入库','register',3),('导出 Excel','export',4),('修改预约','update',5),('删除预约','delete',6)) AS p(label, action, sort) LOOP
        IF NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted=0 AND permission='reagent:matrix-inbound:'||entry.action) THEN
            INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
            SELECT COALESCE(MAX(id),0)+1, entry.label,'reagent:matrix-inbound:'||entry.action,3,entry.sort,menu,'','','','',0,true,true,true,'1','1',0 FROM system_menu;
        END IF;
    END LOOP;
    PERFORM setval('system_menu_seq', GREATEST((SELECT MAX(id) FROM system_menu),(SELECT last_value FROM system_menu_seq)),true);
END $$;
INSERT INTO system_dingtalk_message_template
    (name,code,message_type,content,params,status,remark,default_user_ids,default_dept_ids,include_child_depts,creator,updater,tenant_id)
SELECT templates.name,templates.code,'MARKDOWN',
       json_build_object('title','基质入库登记','text',E'### 基质入库登记 · {action}\n\n- 地区：{site}\n- BAS 号：{basNo}\n- 基质类型：{matrixKind}\n- 预约人：{applicantName}\n- 预约完成系统入库日期：{appointmentDate}\n- LIMS/线粒 是否design：{limsDesigned}\n- 是否属于加急入库：{urgent}\n- 入库结果：{status}\n- 登记人：{registeredName}\n- 登记时间：{registeredTime}\n\n{detailLink}')::text,
       '["action","site","basNo","matrixKind","applicantName","appointmentDate","limsDesigned","urgent","status","registeredName","registeredTime","detailLink"]',0,templates.name,'[]','[]',true,'1','1',tenants.tenant_id
FROM (SELECT id AS tenant_id FROM system_tenant WHERE deleted=0 UNION SELECT 1) tenants
CROSS JOIN (VALUES ('基质预约（宁波）','matrix_inbound_ningbo'),('基质预约（上海）','matrix_inbound_shanghai'),('基质已入库','matrix_inbound_completed')) templates(name,code)
ON CONFLICT (tenant_id,code) WHERE deleted=0 DO NOTHING;

-- 新系统从零计数；重复执行不会重置已发流水。
INSERT INTO reagent_matrix_inbound_counter(tenant_id,site,issue_year,matrix_kind,last_no)
SELECT 1,sites.site,EXTRACT(YEAR FROM now() AT TIME ZONE 'Asia/Shanghai')::int,kinds.kind,0
FROM (VALUES ('NINGBO'),('SHANGHAI')) sites(site)
CROSS JOIN (VALUES ('B'),('P')) kinds(kind)
ON CONFLICT(tenant_id,site,issue_year,matrix_kind) DO NOTHING;
COMMIT;
