BEGIN;

ALTER TABLE sample_urgent
    ADD COLUMN IF NOT EXISTS inbound_site varchar(16)
        CHECK (inbound_site IN ('NINGBO', 'SHANGHAI'));

-- 上海启用前的历史单据均为宁波，保留已经填写地点的单据。
UPDATE sample_urgent
SET inbound_site = 'NINGBO'
WHERE inbound_site IS NULL;

ALTER TABLE sample_urgent
    ADD COLUMN IF NOT EXISTS sample_received_time timestamp;

-- 克隆当前生效的模板正文及参数，接收人由上海管理员单独配置。
INSERT INTO system_dingtalk_message_template
    (name, code, message_type, content, params, status, remark,
     default_user_ids, default_dept_ids, include_child_depts, creator, updater, tenant_id)
SELECT '样品加急入库业务提醒（上海）', 'sample_urgent_notice_shanghai', message_type,
       content, params, 0, '适用于上海入库，请配置上海样品组',
       '[]', '[]', include_child_depts, '1', '1', tenant_id
FROM system_dingtalk_message_template
WHERE code = 'sample_urgent_notice' AND deleted = 0
ON CONFLICT (tenant_id, code) WHERE deleted = 0 DO NOTHING;

COMMIT;
