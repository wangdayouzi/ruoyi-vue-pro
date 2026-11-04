-- PostgreSQL 发布增量 01：钉钉消息、队列及控制台。维护窗口内执行。
BEGIN;

-- 钉钉消息模板和发送记录；PostgreSQL，可重复执行。
-- 在发布维护窗口执行，期间不要并发修改菜单。脚本不读取、不写入应用密钥。

CREATE SEQUENCE IF NOT EXISTS system_dingtalk_message_template_seq;
CREATE TABLE IF NOT EXISTS system_dingtalk_message_template (
    id bigint PRIMARY KEY DEFAULT nextval('system_dingtalk_message_template_seq'),
    name varchar(100) NOT NULL,
    code varchar(100) NOT NULL,
    message_type varchar(40) NOT NULL,
    content text NOT NULL,
    params text NOT NULL DEFAULT '[]',
    default_user_ids text NOT NULL DEFAULT '[]',
    status smallint NOT NULL DEFAULT 0 CHECK (status IN (0, 1)),
    remark varchar(500),
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0,
    tenant_id bigint NOT NULL DEFAULT 0
);
ALTER TABLE system_dingtalk_message_template ADD COLUMN IF NOT EXISTS default_user_ids text NOT NULL DEFAULT '[]';
ALTER TABLE system_dingtalk_message_template ADD COLUMN IF NOT EXISTS default_dept_ids text NOT NULL DEFAULT '[]';
ALTER TABLE system_dingtalk_message_template ADD COLUMN IF NOT EXISTS include_child_depts boolean NOT NULL DEFAULT true;
CREATE UNIQUE INDEX IF NOT EXISTS uk_dingtalk_template_code ON system_dingtalk_message_template(tenant_id, code) WHERE deleted = 0;
COMMENT ON TABLE system_dingtalk_message_template IS '钉钉应用机器人消息模板';
COMMENT ON COLUMN system_dingtalk_message_template.content IS '按消息类型存储字段模板的 JSON 对象';

CREATE SEQUENCE IF NOT EXISTS system_dingtalk_message_log_seq;
CREATE TABLE IF NOT EXISTS system_dingtalk_message_log (
    id bigint PRIMARY KEY DEFAULT nextval('system_dingtalk_message_log_seq'),
    request_id varchar(64) NOT NULL,
    request_hash varchar(64) NOT NULL,
    business_id varchar(128),
    template_id bigint NOT NULL,
    template_code varchar(100) NOT NULL,
    template_name varchar(100) NOT NULL,
    message_type varchar(40) NOT NULL,
    robot_code varchar(128) NOT NULL,
    user_id bigint NOT NULL,
    user_name varchar(100),
    ding_talk_user_id varchar(128) NOT NULL,
    content text NOT NULL,
    send_status smallint NOT NULL DEFAULT 0 CHECK (send_status IN (0, 1, 2, 3)),
    send_time timestamp,
    process_query_key varchar(512),
    error_code varchar(150),
    error_message varchar(500),
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0,
    tenant_id bigint NOT NULL DEFAULT 0,
    CONSTRAINT uk_dingtalk_log_request_user UNIQUE(tenant_id, request_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_dingtalk_log_template ON system_dingtalk_message_log(tenant_id, template_code, id DESC);
CREATE INDEX IF NOT EXISTS idx_dingtalk_log_status ON system_dingtalk_message_log(tenant_id, send_status, id DESC);
COMMENT ON TABLE system_dingtalk_message_log IS '钉钉消息发送快照与幂等记录，不支持直接删除';
COMMENT ON COLUMN system_dingtalk_message_log.send_status IS '0处理中 1钉钉已受理 2失败 3结果未知';

-- 动态分配菜单编号；写锁保护 MAX(id)+1，维护窗口内执行。
LOCK TABLE system_menu IN SHARE ROW EXCLUSIVE MODE;
DO $$
DECLARE
    root_id bigint;
    directory_id bigint;
    template_id bigint;
    log_id bigint;
    entry_id bigint;
    item record;
BEGIN
    SELECT id INTO root_id FROM system_menu WHERE path = '/system' AND deleted = 0 ORDER BY id LIMIT 1;
    IF root_id IS NULL THEN RAISE EXCEPTION '未找到系统管理菜单（/system），请确认目标数据库'; END IF;
    SELECT id INTO directory_id FROM system_menu WHERE parent_id = root_id AND path = 'dingtalk' AND deleted = 0 LIMIT 1;
    IF directory_id IS NULL THEN
        SELECT COALESCE(MAX(id), 0) + 1 INTO directory_id FROM system_menu;
        INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
        VALUES(directory_id, '钉钉消息', '', 1, 25, root_id, 'dingtalk', 'ep:chat-dot-round', '', '', 0, true, true, true, '1', '1', 0);
    END IF;
    FOR item IN SELECT * FROM (VALUES
        ('消息模板', 'template', 'system:dingtalk-template:query', 'system/dingtalk/template/index', 'SystemDingTalkTemplate', 1),
        ('发送记录', 'log', 'system:dingtalk-log:query', 'system/dingtalk/log/index', 'SystemDingTalkLog', 2)
    ) AS pages(name, path, permission, component, component_name, sort)
    LOOP
        SELECT id INTO entry_id FROM system_menu WHERE component = item.component AND deleted = 0 LIMIT 1;
        IF entry_id IS NULL THEN
            SELECT COALESCE(MAX(id), 0) + 1 INTO entry_id FROM system_menu;
            INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
            VALUES(entry_id, item.name, item.permission, 2, item.sort, directory_id, item.path, 'ep:message', item.component, item.component_name, 0, true, true, true, '1', '1', 0);
        END IF;
        IF item.path = 'template' THEN template_id := entry_id; ELSE log_id := entry_id; END IF;
    END LOOP;
    FOR item IN SELECT * FROM (VALUES
        ('新增模板', 'system:dingtalk-template:create', 1),
        ('修改模板', 'system:dingtalk-template:update', 2),
        ('删除模板', 'system:dingtalk-template:delete', 3),
        ('测试发送', 'system:dingtalk-template:send', 4)
    ) AS buttons(name, permission, sort)
    LOOP
        IF NOT EXISTS(SELECT 1 FROM system_menu WHERE permission = item.permission AND deleted = 0) THEN
            SELECT COALESCE(MAX(id), 0) + 1 INTO entry_id FROM system_menu;
            INSERT INTO system_menu(id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, updater, deleted)
            VALUES(entry_id, item.name, item.permission, 3, item.sort, template_id, '', '', '', '', 0, true, true, true, '1', '1', 0);
        END IF;
    END LOOP;
    -- 与框架 @KeySequence 保持一致；只调整菜单序列，不调整业务数据序列。
    PERFORM setval('system_menu_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM system_menu), (SELECT last_value FROM system_menu_seq)), true);
END $$;

-- 超级管理员重新登录可见。其他角色、受租户套餐限制的账号需在角色/套餐中授权新菜单。

-- 本次完整增量：兼容未安装队列及已安装队列的环境；无需再单独执行队列脚本。

CREATE TABLE IF NOT EXISTS system_dingtalk_notification_queue (
 id bigserial PRIMARY KEY,tenant_id bigint NOT NULL,
 request_id varchar(64) NOT NULL,user_id bigint NOT NULL,business_id varchar(128),
 guard_type varchar(100),business_version varchar(100),payload text NOT NULL,
 send_after timestamp NOT NULL,expires_at timestamp,
 status smallint NOT NULL DEFAULT 0 CHECK(status BETWEEN 0 AND 6),error_message varchar(1000),
 create_time timestamp NOT NULL DEFAULT now(),update_time timestamp NOT NULL DEFAULT now(),
 UNIQUE(tenant_id,request_id,user_id)
);
CREATE INDEX IF NOT EXISTS idx_dingtalk_notification_due ON system_dingtalk_notification_queue(send_after,id) WHERE status=0;
COMMENT ON COLUMN system_dingtalk_notification_queue.status IS '0待发送 1处理中 2钉钉已受理 3结果未知 4失败 5取消 6过期';
-- 运维字段（新建与已安装环境均可重复执行）。
ALTER TABLE system_dingtalk_notification_queue ADD COLUMN IF NOT EXISTS retry_of bigint;
ALTER TABLE system_dingtalk_notification_queue ADD COLUMN IF NOT EXISTS retry_no integer NOT NULL DEFAULT 0;
ALTER TABLE system_dingtalk_notification_queue ADD COLUMN IF NOT EXISTS operator_id bigint;
ALTER TABLE system_dingtalk_notification_queue ADD COLUMN IF NOT EXISTS operation_reason varchar(500);
CREATE UNIQUE INDEX IF NOT EXISTS uk_dingtalk_queue_retry ON system_dingtalk_notification_queue(tenant_id,retry_of,retry_no) WHERE retry_of IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_dingtalk_queue_tenant ON system_dingtalk_notification_queue(tenant_id,id DESC);
DO $$
DECLARE parent bigint; menu bigint; button bigint;
BEGIN
 SELECT id INTO parent FROM system_menu WHERE deleted=0 AND type=1 AND path='dingtalk' ORDER BY id LIMIT 1;
 IF parent IS NULL THEN RAISE EXCEPTION '请先执行钉钉消息模块初始化脚本'; END IF;
 SELECT id INTO menu FROM system_menu WHERE deleted=0 AND component='system/dingtalk/notification/index' LIMIT 1;
 IF menu IS NULL THEN
  LOOP
   menu:=1000000000000+floor(random()*8000000000000)::bigint;
   EXIT WHEN NOT EXISTS(SELECT 1 FROM system_menu WHERE id=menu);
  END LOOP;
  INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
  VALUES(menu,'通知管理','system:dingtalk-notification:query',2,3,parent,'notification','ep:bell','system/dingtalk/notification/index','SystemDingTalkNotification',0,true,true,true,'1','1',0);
 END IF;
 IF NOT EXISTS(SELECT 1 FROM system_menu WHERE deleted=0 AND permission='system:dingtalk-notification:manage') THEN
  LOOP
   button:=1000000000000+floor(random()*8000000000000)::bigint;
   EXIT WHEN NOT EXISTS(SELECT 1 FROM system_menu WHERE id=button);
  END LOOP;
  INSERT INTO system_menu(id,name,permission,type,sort,parent_id,path,icon,component,component_name,status,visible,keep_alive,always_show,creator,updater,deleted)
  VALUES(button,'管理通知','system:dingtalk-notification:manage',3,1,menu,'','','','',0,true,true,true,'1','1',0);
 END IF;
END $$;

-- 非超级管理员须给运维角色及租户套餐分配菜单/按钮；业务人员不需要此管理权限。

COMMIT;
