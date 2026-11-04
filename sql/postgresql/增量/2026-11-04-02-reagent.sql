-- PostgreSQL 发布增量 02：试剂基础数据、加急入库及通知迁移。
-- 先停止后端并执行 2026-11-04-01-dingtalk.sql；部署后执行首次基础数据同步。
-- 前置：系统菜单中已有唯一的「试剂管理」目录。
BEGIN;

-- ============================================================================
-- 试剂管理 → 基础数据：临床方案 / 项目号（老样本系统 MySQL 同步）
--
-- 源库：MySQL  库名 yangben_com  账号 yangben_com（密码在 application-*.yaml 的
--       spring.datasource.dynamic.datasource.yangben.password 中填写）
-- 源表：think_clinical、think_items
-- 落地：本地 PostgreSQL  reagent_clinical、reagent_item
-- 同步：定时任务 Handler = reagentBasicDataSyncJob，cron = 0 0 0 * * ?（每天 00:00）
--       任务在后端启动时自动登记（见 ReagentBasicDataJobInstaller），无需手工新增；
--       也可在页面上点「立即同步」先跑一次。
--
-- 说明：
-- 1. 本脚本可重复执行（表/索引/菜单均做了存在性判断）。
-- 2. 临床方案的 is_auto_receive（是否自动发送寄送通知邮件）由本地维护，
--    同步 upsert 不写该列，因此定时同步不会覆盖页面上手工配置的值。
-- 3. 同步是“全量 + 按源表 id 幂等 upsert”：源表已删除的行标记 status=1（停用），
--    不物理删除，避免历史引用丢失。
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. 临床方案
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reagent_clinical (
    id                bigint       PRIMARY KEY,                 -- 源表 think_clinical.id
    name              varchar(1000) NOT NULL DEFAULT '',        -- 临床方案名称
    number            varchar(255)  NOT NULL DEFAULT '',        -- 临床方案编号
    sponsor_id        bigint,                                   -- 申办方ID
    is_upload         smallint      DEFAULT 0,                  -- 是否上传附件：0否 1是
    effective_date    varchar(50),                              -- 生效日期（源表为 varchar）
    version_number    varchar(50),                              -- 版本号
    is_auto_receive   smallint      NOT NULL DEFAULT 0,         -- 是否自动发送寄送通知邮件：本地维护，同步不覆盖
    src_create_time   timestamp,                                -- 源表添加时间
    src_update_time   timestamp,                                -- 源表编辑时间
    status            smallint      NOT NULL DEFAULT 0,         -- 0正常 1停用（源表已删除）
    sync_batch        bigint,                                   -- 同步批次
    sync_time         timestamp,                                -- 同步时间
    tenant_id         bigint        NOT NULL DEFAULT 0,
    creator           varchar(64)   DEFAULT '',
    create_time       timestamp     NOT NULL DEFAULT now(),
    updater           varchar(64)   DEFAULT '',
    update_time       timestamp     NOT NULL DEFAULT now(),
    deleted           smallint      NOT NULL DEFAULT 0
);
COMMENT ON TABLE reagent_clinical IS '临床方案（老样本系统 think_clinical 同步）';
COMMENT ON COLUMN reagent_clinical.is_auto_receive IS '是否自动发送寄送通知邮件：0否 1是（本地维护，同步不覆盖）';
COMMENT ON COLUMN reagent_clinical.status IS '状态：0正常 1停用（源表已删除）';

CREATE INDEX IF NOT EXISTS idx_reagent_clinical_name   ON reagent_clinical (name);
CREATE INDEX IF NOT EXISTS idx_reagent_clinical_number ON reagent_clinical (number);
CREATE INDEX IF NOT EXISTS idx_reagent_clinical_status ON reagent_clinical (status);

-- ----------------------------------------------------------------------------
-- 2. 项目号
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reagent_item (
    id                bigint       PRIMARY KEY,                 -- 源表 think_items.id
    city_id           smallint,                                 -- 城市：1上海 2宁波
    base_id           varchar(255) NOT NULL DEFAULT '',         -- 项目号（源表 baseid）
    clinical_id       bigint,                                   -- 临床方案ID
    clinical_name     varchar(1000),                            -- 临床方案名称（同步时 join think_clinical）
    clinical_number   varchar(255),                             -- 临床方案编号（同步时 join think_clinical）
    person_id         bigint,                                   -- 项目负责人ID
    type_id           bigint,                                   -- 样本类型ID
    is_aging          smallint      DEFAULT 1,                  -- 是否时效：0否 1是
    review_status     smallint      DEFAULT 0,                  -- 复核状态：0未复核 1已复核
    reviewer          bigint,                                   -- 复核人员ID
    is_report         smallint      DEFAULT 0,                  -- 是否附上报告作为附件：0否 1是
    is_pm             smallint      DEFAULT 0,                  -- 是否涉及病原微生物：0否 1是
    src_create_time   timestamp,                                -- 源表添加时间
    src_update_time   timestamp,                                -- 源表编辑时间
    status            smallint      NOT NULL DEFAULT 0,         -- 0正常 1停用（源表已删除）
    sync_batch        bigint,                                   -- 同步批次
    sync_time         timestamp,                                -- 同步时间
    tenant_id         bigint        NOT NULL DEFAULT 0,
    creator           varchar(64)   DEFAULT '',
    create_time       timestamp     NOT NULL DEFAULT now(),
    updater           varchar(64)   DEFAULT '',
    update_time       timestamp     NOT NULL DEFAULT now(),
    deleted           smallint      NOT NULL DEFAULT 0
);
COMMENT ON TABLE reagent_item IS '项目号（老样本系统 think_items 同步）';
COMMENT ON COLUMN reagent_item.base_id IS '项目号（源表 baseid）';
COMMENT ON COLUMN reagent_item.status IS '状态：0正常 1停用（源表已删除）';

CREATE INDEX IF NOT EXISTS idx_reagent_item_base_id   ON reagent_item (base_id);
CREATE INDEX IF NOT EXISTS idx_reagent_item_clinical  ON reagent_item (clinical_id);
CREATE INDEX IF NOT EXISTS idx_reagent_item_city      ON reagent_item (city_id);
CREATE INDEX IF NOT EXISTS idx_reagent_item_status    ON reagent_item (status);

-- ----------------------------------------------------------------------------
-- 3. 菜单（挂在「试剂管理」目录下）
-- ----------------------------------------------------------------------------
DO $$
DECLARE
    parent      bigint;
    m_clinical  bigint;
    m_item      bigint;
    v_id        bigint;
    child       bigint;
    p           record;
BEGIN
    IF (SELECT count(*) FROM system_menu WHERE deleted = 0 AND type = 1 AND name = '试剂管理') <> 1 THEN
        RAISE EXCEPTION '无法唯一定位「试剂管理」目录，请先核对 system_menu';
    END IF;
    SELECT id INTO parent FROM system_menu WHERE deleted = 0 AND type = 1 AND name = '试剂管理';

    -- 菜单 ID 从当前最大值 +1 开始顺序分配，避免随机 ID 难读、难排查
    SELECT COALESCE(MAX(id), 0) INTO v_id FROM system_menu;

    -- 临床方案菜单
    SELECT id INTO m_clinical FROM system_menu
    WHERE deleted = 0 AND component = 'reagent/clinical/index' LIMIT 1;
    IF m_clinical IS NULL THEN
        v_id := v_id + 1;
        m_clinical := v_id;
        INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component,
                                component_name, status, visible, keep_alive, always_show,
                                creator, updater, deleted)
        VALUES (m_clinical, '临床方案', '', 2, 60, parent, 'clinical', 'ep:document',
                'reagent/clinical/index', 'ReagentClinical', 0, true, true, true, '1', '1', 0);
    END IF;

    -- 项目号菜单
    SELECT id INTO m_item FROM system_menu
    WHERE deleted = 0 AND component = 'reagent/item/index' LIMIT 1;
    IF m_item IS NULL THEN
        v_id := v_id + 1;
        m_item := v_id;
        INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component,
                                component_name, status, visible, keep_alive, always_show,
                                creator, updater, deleted)
        VALUES (m_item, '项目号', '', 2, 70, parent, 'item', 'ep:collection',
                'reagent/item/index', 'ReagentItem', 0, true, true, true, '1', '1', 0);
    END IF;

    -- 按钮权限
    FOR p IN SELECT * FROM (VALUES
        ('查看临床方案',         'reagent:clinical:query',   1, m_clinical),
        ('维护临床方案本地配置', 'reagent:clinical:update',  2, m_clinical),
        ('同步基础数据',         'reagent:basic-data:sync',  3, m_clinical),
        ('查看项目号',           'reagent:item:query',       1, m_item)
    ) AS v(name, permission, sort, parent_id) LOOP
        IF NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted = 0 AND permission = p.permission) THEN
            v_id := v_id + 1;
            child := v_id;
            INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component,
                                    component_name, status, visible, keep_alive, always_show,
                                    creator, updater, deleted)
            VALUES (child, p.name, p.permission, 3, p.sort, p.parent_id, '', '', '', '',
                    0, true, true, true, '1', '1', 0);
        END IF;
    END LOOP;

    -- 同步自增序列，避免之后在页面新增菜单时与手工分配的 ID 冲突（只往前推，不往回退）
    BEGIN
        PERFORM setval('system_menu_seq', GREATEST(
            (SELECT COALESCE(MAX(id), 0) FROM system_menu),
            (SELECT last_value FROM system_menu_seq)));
    EXCEPTION WHEN OTHERS THEN
        -- 序列无权限等异常不影响菜单写入，下次页面新增菜单前可手工校准序列
        RAISE WARNING 'system_menu_seq 同步失败：%', SQLERRM;
    END;
END $$;

-- ----------------------------------------------------------------------------
-- 4. 每天 00:00 定时同步任务
-- ----------------------------------------------------------------------------
-- 任务由后端启动时自动登记（ReagentBasicDataJobInstaller → JobService.createJob，
-- 同时写入 infra_job 与 Quartz），这里不再直接 INSERT infra_job：
-- 直接插 infra_job 不会创建 Quartz 调度，反而会让启动登记逻辑误判“任务已存在”。
-- 如需调整 cron / 暂停，请到「基础设施 → 定时任务」操作。


-- ============================================================================
-- 执行后检查：
--   SELECT * FROM system_menu WHERE component IN ('reagent/clinical/index','reagent/item/index');
--   SELECT * FROM infra_job WHERE handler_name = 'reagentBasicDataSyncJob';
-- 首次使用：
--   1) 在 application-local.yaml（或 prod 环境变量 YANGBEN_DB_PASSWORD）填写源库密码；
--   2) 启动后端后，任务会自动登记为「临床方案/项目号同步 Job」（cron 0 0 0 * * ?）；
--      到「基础设施 → 定时任务」确认存在，并手动执行一次验证；
--   3) 菜单若不显示，请给角色勾选新增的「临床方案 / 项目号」菜单及按钮权限。
-- ============================================================================

-- 样品加急入库：无审批、无钉钉、无库存变更。不导入旧表，不推断历史状态。

CREATE SEQUENCE IF NOT EXISTS sample_urgent_seq;
CREATE TABLE IF NOT EXISTS sample_urgent (
 id bigint PRIMARY KEY DEFAULT nextval('sample_urgent_seq'),
 project_no varchar(200) NOT NULL, new_project boolean NOT NULL DEFAULT false,
 bas_nos varchar(2000) NOT NULL DEFAULT '', sample_scope varchar(2000) NOT NULL,
 deadline timestamp NOT NULL, expected_arrival timestamp, planned_test_time timestamp,
 contacts varchar(300) NOT NULL, reason varchar(2000) NOT NULL,
 logistics_no varchar(1000), remark varchar(4000),
 requester_id bigint NOT NULL, requester_name varchar(100) NOT NULL,
 handler_id bigint, handler_name varchar(100),
 status integer NOT NULL DEFAULT 0 CHECK(status BETWEEN 0 AND 4),
 exception_type varchar(32) NOT NULL DEFAULT 'NONE'
 CHECK(exception_type IN ('NONE','NOT_ARRIVED','WAIT_REPLY','SAMPLE_ISSUE','DELAY','OTHER')),
 exception_note varchar(4000), completed_scope varchar(4000), remaining_scope varchar(4000),
 completed_time timestamp, revision integer NOT NULL DEFAULT 0,
 creator varchar(64) DEFAULT '', create_time timestamp NOT NULL DEFAULT now(),
 updater varchar(64) DEFAULT '', update_time timestamp NOT NULL DEFAULT now(),
 deleted smallint NOT NULL DEFAULT 0, tenant_id bigint NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_sample_urgent_deadline ON sample_urgent(tenant_id,status,deadline) WHERE deleted=0;
CREATE INDEX IF NOT EXISTS idx_sample_urgent_requester ON sample_urgent(tenant_id,requester_id) WHERE deleted=0;
CREATE SEQUENCE IF NOT EXISTS sample_urgent_log_seq;
CREATE TABLE IF NOT EXISTS sample_urgent_log (
 id bigint PRIMARY KEY DEFAULT nextval('sample_urgent_log_seq'), urgent_id bigint NOT NULL REFERENCES sample_urgent(id),
 operator_id bigint NOT NULL, operator_name varchar(100) NOT NULL,
 action varchar(64) NOT NULL, content text NOT NULL,
 creator varchar(64) DEFAULT '', create_time timestamp NOT NULL DEFAULT now(),
 updater varchar(64) DEFAULT '', update_time timestamp NOT NULL DEFAULT now(),
 deleted smallint NOT NULL DEFAULT 0, tenant_id bigint NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_sample_urgent_log_parent ON sample_urgent_log(tenant_id,urgent_id,id);

-- 菜单 ID 使用未占用的随机 bigint，避免依赖既有菜单序列状态。执行时唯一定位试剂目录。
DO $$
DECLARE parent bigint; menu bigint; child bigint; p record;
BEGIN
 IF (SELECT count(*) FROM system_menu WHERE deleted=0 AND type=1 AND name='试剂管理') <> 1 THEN
   RAISE EXCEPTION '无法唯一定位试剂管理目录，请先核对 system_menu';
 END IF;
 SELECT id INTO parent FROM system_menu WHERE deleted=0 AND type=1 AND name='试剂管理';
 SELECT id INTO menu FROM system_menu WHERE deleted=0 AND component='reagent/sample-urgent/index' LIMIT 1;
 IF menu IS NULL THEN
   LOOP
     menu := 1000000000000 + floor(random()*8000000000000)::bigint;
     EXIT WHEN NOT EXISTS(SELECT 1 FROM system_menu WHERE id=menu);
   END LOOP;
   INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
   VALUES(menu,'加急入库','',2,50,parent,'sample-urgent','ep:alarm-clock','reagent/sample-urgent/index','ReagentSampleUrgent',0,true,true,true,'1','1',0);
 END IF;
 FOR p IN SELECT * FROM (VALUES
  ('查看加急入库','reagent:sample-urgent:query',1),
  ('登记加急需求','reagent:sample-urgent:create',2),
  ('修改本人需求','reagent:sample-urgent:update',3),
  ('样品组处理','reagent:sample-urgent:handle',4)
 ) AS v(name,permission,sort) LOOP
   IF NOT EXISTS(SELECT 1 FROM system_menu WHERE deleted=0 AND permission=p.permission) THEN
     LOOP
       child := 1000000000000 + floor(random()*8000000000000)::bigint;
       EXIT WHEN NOT EXISTS(SELECT 1 FROM system_menu WHERE id=child);
     END LOOP;
     INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
     VALUES(child,p.name,p.permission,3,p.sort,menu,'','','','',0,true,true,true,'1','1',0);
   END IF;
 END LOOP;
END $$;
-- 不自动给未知角色授权：项目组分配 query/create/update，样品组分配 query/handle。
-- 同时向对应角色分配目录和“加急入库”菜单；租户套餐需包含新增菜单。

-- 纯数字登记编号，沿用数据库自增ID；可重复执行，兼容已执行旧版JJ编号脚本的环境。

LOCK TABLE sample_urgent IN SHARE ROW EXCLUSIVE MODE;
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS urgent_no varchar(32);
DROP TRIGGER IF EXISTS sample_urgent_no_trigger ON sample_urgent;
UPDATE sample_urgent SET urgent_no=id::text WHERE urgent_no IS DISTINCT FROM id::text;
CREATE UNIQUE INDEX IF NOT EXISTS uk_sample_urgent_no ON sample_urgent(urgent_no);
CREATE OR REPLACE FUNCTION assign_sample_urgent_no() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
 IF TG_OP='UPDATE' THEN
   IF NEW.urgent_no IS DISTINCT FROM OLD.urgent_no THEN RAISE EXCEPTION '登记编号不能修改'; END IF;
 ELSE
   NEW.urgent_no := NEW.id::text;
 END IF;
 RETURN NEW;
END $$;
CREATE TRIGGER sample_urgent_no_trigger BEFORE INSERT OR UPDATE ON sample_urgent FOR EACH ROW EXECUTE FUNCTION assign_sample_urgent_no();
ALTER TABLE sample_urgent ALTER COLUMN urgent_no SET NOT NULL;

-- 加急入库预约改造。执行于业务数据库；保留历史联系人及所有原始字段，不推断人员账号。

ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS appointment_user_ids text NOT NULL DEFAULT '[]';
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS notice_user_ids text NOT NULL DEFAULT '[]';
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS notice_dept_ids text NOT NULL DEFAULT '[]';
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS notice_names text NOT NULL DEFAULT '';
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS legacy_contacts varchar(300);
UPDATE sample_urgent SET legacy_contacts=contacts
WHERE legacy_contacts IS NULL AND appointment_user_ids::jsonb='[]'::jsonb;
CREATE INDEX IF NOT EXISTS idx_sample_urgent_appointment_users ON sample_urgent USING gin ((appointment_user_ids::jsonb));
COMMENT ON COLUMN sample_urgent.project_no IS '临床方案编号，允许多个';
COMMENT ON COLUMN sample_urgent.sample_scope IS '检测项，多选或自定义内容';
COMMENT ON COLUMN sample_urgent.contacts IS '已确认预约人姓名快照；未确认的旧记录保留原值';
COMMENT ON COLUMN sample_urgent.requester_id IS '实际提交人，不替换为预约人';
COMMENT ON COLUMN sample_urgent.appointment_user_ids IS '预约人账号列表，不根据历史姓名自动绑定';
COMMENT ON COLUMN sample_urgent.notice_dept_ids IS '本次通知的样品管理员部门，含启用的子部门';

-- 变更：
--   1) sample_urgent 增加 clinical_id（临床方案ID）一列；
--      历史数据的 project_no（临床方案编号）/ bas_nos（BAS号）原样保留，不迁移、不猜测。
--      说明：BAS 号本身就是项目号（reagent_item.base_id），仍统一存在 bas_nos 多值字段里，
--      不再单独增加 base_id 列。
--   2) 增加「基础数据下拉」按钮权限 reagent:basic-data:select；
--      加急入库、领用台账已有 query 权限的角色不授权也能用下拉（后端做了 hasAnyPermissions）。
--
-- 本脚本可重复执行。
-- ============================================================================

ALTER TABLE sample_urgent
    ADD COLUMN IF NOT EXISTS clinical_id bigint;

COMMENT ON COLUMN sample_urgent.clinical_id IS '临床方案ID（老样本系统 reagent_clinical.id，可为空兼容历史数据）';

-- 旧版本曾增加 base_id 列；如已执行过旧脚本，可手工清理（确认无数据后）：
-- ALTER TABLE sample_urgent DROP COLUMN IF EXISTS base_id;

-- 下拉选项权限（挂在「临床方案」菜单下；找不到菜单说明前置脚本未执行）
DO $$
DECLARE
    parent bigint;
    v_id   bigint;
BEGIN
    SELECT id INTO parent FROM system_menu
    WHERE deleted = 0 AND component = 'reagent/clinical/index' LIMIT 1;
    IF parent IS NULL THEN
        RAISE EXCEPTION '未找到「临床方案」菜单，请先执行 2026-11-04-02-reagent.sql';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM system_menu WHERE deleted = 0 AND permission = 'reagent:basic-data:select') THEN
        SELECT COALESCE(MAX(id), 0) INTO v_id FROM system_menu;
        v_id := v_id + 1;
        INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component,
                                component_name, status, visible, keep_alive, always_show,
                                creator, updater, deleted)
        VALUES (v_id, '基础数据下拉', 'reagent:basic-data:select', 3, 4, parent, '', '', '', '',
                0, true, true, true, '1', '1', 0);
    END IF;

    BEGIN
        PERFORM setval('system_menu_seq', GREATEST(
            (SELECT COALESCE(MAX(id), 0) FROM system_menu),
            (SELECT last_value FROM system_menu_seq)));
    EXCEPTION WHEN OTHERS THEN
        RAISE WARNING 'system_menu_seq 同步失败：%', SQLERRM;
    END;
END $$;


-- ============================================================================
-- 执行后检查：
--   SELECT column_name FROM information_schema.columns
--    WHERE table_name='sample_urgent' AND column_name = 'clinical_id';
--   SELECT * FROM system_menu WHERE permission='reagent:basic-data:select';
-- ============================================================================

-- 复用已有钉钉模板模块。先执行 2026-11-04-01-dingtalk.sql。
-- 不覆盖管理员已有模板或参数，不向员工发送测试消息。

ALTER TABLE system_dingtalk_message_template ADD COLUMN IF NOT EXISTS default_user_ids text NOT NULL DEFAULT '[]';
INSERT INTO system_dingtalk_message_template
 (name,code,message_type,content,params,status,remark,creator,updater,tenant_id)
SELECT '样品加急入库业务提醒','sample_urgent_notice','MARKDOWN',
 '{"title":"加急入库 · {action}","text":"### 加急入库 · {action}\n\n- **登记编号**：{registrationNo}\n- **临床方案编号**：{clinicalProtocolNo}\n- **检测项**：{testItems}\n- **BAS号**：{basNos}\n- **预约入库时间**：{reservationTime}\n- **状态**：{status}\n- **预约人**：{appointmentNames}\n- **提交人**：{submitterName}\n- **处理人**：{handlerName}\n- **预约备注**：{remark}"}',
 '["action","registrationNo","clinicalProtocolNo","testItems","basNos","reservationTime","status","appointmentNames","submitterName","handlerName","remark"]',
 0,'加急入库业务统一模板；默认接收人员请选择样品组；业务单据配置接收范围；固定提示和跳转地址在模板中编辑','1','1',t.id
-- 关闭多租户时通知使用主租户1；即使租户清单未保留该行，也补齐主租户模板。
FROM (SELECT id FROM system_tenant WHERE deleted=0 UNION SELECT 1::bigint) t
WHERE NOT EXISTS(SELECT 1 FROM system_dingtalk_message_template m WHERE m.tenant_id=t.id AND m.code='sample_urgent_notice' AND m.deleted=0);

INSERT INTO infra_config(id,category,name,config_key,value,type,visible,remark,creator,updater)
SELECT nextval('infra_config_seq'),'reagent',v.name,'reagent.sample-urgent.dingtalk.'||t.id||'.'||v.suffix,v.value,2,false,v.remark,'1','1'
FROM (SELECT id FROM system_tenant WHERE deleted=0 UNION SELECT 1::bigint) t CROSS JOIN (VALUES
 ('加急入库钉钉开关','enabled','true','true 开启，false 关闭；租户专属配置')
) v(name,suffix,value,remark)
WHERE NOT EXISTS(SELECT 1 FROM infra_config c WHERE c.config_key='reagent.sample-urgent.dingtalk.'||t.id||'.'||v.suffix AND c.deleted=0);

-- 加急入库消息字段同步。部署顺序：停止后端 → 执行本SQL → 更新前后端 → 启动后端。
-- 只替换旧占位符和可继续发送记录的参数，不覆盖模板固定文案/接收配置，不修改发送状态或计划时间。
-- 已受理/取消/过期的队列快照，以及实际发送日志不改动；无旧变量兼容代码。

DO $$
DECLARE t record; q record; item record; body jsonb; message_payload jsonb; p jsonb; raw_content text;
BEGIN
 FOR t IN SELECT id,content FROM system_dingtalk_message_template WHERE code='sample_urgent_notice' AND deleted=0 FOR UPDATE LOOP
  body:=t.content::jsonb;
  FOR item IN SELECT * FROM (VALUES
        ('urgentNo','registrationNo'),
        ('projectNo','clinicalProtocolNo'),
        ('sampleScope','testItems'),
        ('deadline','reservationTime'),
        ('contacts','appointmentNames'),
        ('requester','submitterName'),
        ('handler','handlerName')
  ) AS aliases(old_key,new_key) LOOP
   SELECT jsonb_object_agg(key,replace(value,'{'||item.old_key||'}','{'||item.new_key||'}')) INTO body FROM jsonb_each_text(body);
  END LOOP;
  SELECT jsonb_object_agg(key,replace(replace(regexp_replace(value,'[[][^]]*[]][(][{]detailUrl[}][)]','','g'),'{detailUrl}',''),'{content}','')) INTO body FROM jsonb_each_text(body);
  UPDATE system_dingtalk_message_template SET content=body::text,
   params=(SELECT COALESCE(jsonb_agg(DISTINCT m.captures[1]),'[]'::jsonb)::text
           FROM jsonb_each_text(body) e CROSS JOIN LATERAL regexp_matches(e.value,'[{]([a-zA-Z][a-zA-Z0-9_]*)[}]','g') AS m(captures)),
   update_time=CURRENT_TIMESTAMP WHERE id=t.id;
 END LOOP;
 FOR q IN SELECT id,payload FROM system_dingtalk_notification_queue
          WHERE status IN(0,1,3,4) AND payload::jsonb->>'templateCode'='sample_urgent_notice' FOR UPDATE LOOP
  message_payload:=q.payload::jsonb; p:=COALESCE(message_payload->'params','{}'::jsonb);
  IF NOT (p ?| ARRAY['urgentNo','projectNo','sampleScope','deadline','contacts','requester','handler','detailUrl','content']) THEN CONTINUE; END IF;
  raw_content:=COALESCE(p->>'content','');
  FOR item IN SELECT * FROM (VALUES
        ('urgentNo','registrationNo'),
        ('projectNo','clinicalProtocolNo'),
        ('sampleScope','testItems'),
        ('deadline','reservationTime'),
        ('contacts','appointmentNames'),
        ('requester','submitterName'),
        ('handler','handlerName')
  ) AS aliases(old_key,new_key) LOOP
   IF p ? item.old_key THEN
    p:=(p-item.old_key)||jsonb_build_object(item.new_key,COALESCE(p->item.new_key,p->item.old_key));
   END IF;
  END LOOP;
  p:='{"action":"—","registrationNo":"—","clinicalProtocolNo":"—","testItems":"—","basNos":"—","reservationTime":"—","appointmentNames":"—","submitterName":"—","noticeRecipients":"—","handlerName":"—","status":"—","remark":"—","completedScope":"—","remainingScope":"—","exceptionTypeName":"—","exceptionNote":"—","actualInboundTime":"—","changeReason":"—"}'::jsonb||p;
  -- 修改原因转存独立字段后，移除旧的汇总说明和链接参数。
  IF p->>'action'='修改需求' THEN
   raw_content:=regexp_replace(split_part(split_part(raw_content,E'\n修改前：',1),E'\n加急原因：',1),'^修改原因：','');
   p:=p||jsonb_build_object('changeReason',COALESCE(NULLIF(raw_content,''),'—'));
  END IF;
  p:=p-'detailUrl'-'content';
  IF p->>'status'='已完成' THEN p:=jsonb_set(p,'{status}',to_jsonb('已入库'::text)); END IF;
  IF p->>'handlerName'='未填写' THEN p:=jsonb_set(p,'{handlerName}',to_jsonb('未接手'::text)); END IF;
  UPDATE system_dingtalk_notification_queue SET payload=jsonb_set(message_payload,'{params}',p)::text WHERE id=q.id;
 END LOOP;
END $$;

-- 新增的可选字段在旧通知快照中没有原始值时显示“—”，不从当前业务记录猜测或回填旧快照。
-- 接口不提供详情URL。需要链接时，由模板填写固定地址并拼接 {registrationNo}。

COMMIT;
