INSERT INTO system_dingtalk_message_template
    (name, code, message_type, content, params, status, remark, creator, updater, tenant_id)
SELECT '领用登记通知', 'sample_loan_created', 'MARKDOWN',
    $template${"title":"领用登记通知","text":"### 领用登记通知\n\n- **BAS 号**：{basNo}\n- **领用类型**：{materialType}\n- **站点 / 楼层**：{site} / {location}\n- **需求人**：{requester}\n- **提交人**：{submitter}"}$template$,
    '["basNo","materialType","site","location","requester","submitter"]',
    0, '新增领用后发送给所选需求人', '1', '1', t.id
FROM (SELECT id FROM system_tenant WHERE deleted = 0 UNION SELECT 1::bigint) t
WHERE NOT EXISTS (
    SELECT 1 FROM system_dingtalk_message_template m
    WHERE m.tenant_id = t.id AND m.code = 'sample_loan_created' AND m.deleted = 0
);
