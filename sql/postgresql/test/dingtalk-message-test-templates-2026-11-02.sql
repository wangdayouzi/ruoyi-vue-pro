-- 钉钉消息手动发送测试模板；仅用于测试环境。
-- 前置：已执行 2026-11-04-01-dingtalk.sql，已创建消息模板表。
-- 为所有启用且未删除的租户补充测试模板；不覆盖已存在的同编码模板。
-- 不预设用户、部门或企业编号，不调用钉钉、不触发业务提醒。
-- 执行后进入：系统管理 → 钉钉消息 → 消息模板，搜索 dingtalk_test_。
-- 先用纯文本模板选择自己的账号测试；其余模板按提示填写参数。
-- 多租户仍须使用与当前企业应用相匹配、已关联钉钉的员工。
BEGIN;
SET LOCAL standard_conforming_strings = on;

INSERT INTO system_dingtalk_message_template
 (name,code,message_type,content,params,status,remark,creator,updater,tenant_id)
SELECT '钉钉发送测试 - 纯文本','dingtalk_test_text','TEXT','{"content":"【钉钉消息发送测试】\n这是一条手动触发的测试消息。\n收到此消息说明当前应用机器人已能够向你发送消息。\n此消息不关联任何业务，无需处理。"}','[]',0,'专用测试模板：无参数，选择自己的账号即可测试，不关联业务流程','1','1',t.id
FROM system_tenant t WHERE t.deleted=0 AND t.status=0
ON CONFLICT (tenant_id,code) WHERE deleted=0 DO NOTHING;

INSERT INTO system_dingtalk_message_template
 (name,code,message_type,content,params,status,remark,creator,updater,tenant_id)
SELECT '钉钉发送测试 - Markdown','dingtalk_test_markdown','MARKDOWN','{"title":"钉钉发送测试 · {environment}","text":"### 钉钉消息发送测试\n\n**环境：** {environment}\n\n**测试人：** {tester}\n\n- 参数替换检查\n- Markdown 展示检查\n\n这是一条手动触发的测试消息，**无需处理**。"}','["environment","tester"]',0,'专用测试模板：environment 填正式环境或测试环境，tester 填自己的姓名；选择自己的账号测试','1','1',t.id
FROM system_tenant t WHERE t.deleted=0 AND t.status=0
ON CONFLICT (tenant_id,code) WHERE deleted=0 DO NOTHING;

INSERT INTO system_dingtalk_message_template
 (name,code,message_type,content,params,status,remark,creator,updater,tenant_id)
SELECT '钉钉发送测试 - 按钮卡片','dingtalk_test_action_card','ACTION_CARD','{"title":"钉钉卡片测试 · {environment}","text":"**环境：** {environment}\n\n这是一条手动触发的卡片测试消息，无需处理。\n\n下方按钮将打开钉钉开放平台，用于验证链接跳转。","singleTitle":"打开钉钉开放平台","singleURL":"https://open.dingtalk.com"}','["environment"]',0,'专用测试模板：environment 填正式环境或测试环境；按钮打开公开的钉钉开放平台，不操作业务数据','1','1',t.id
FROM system_tenant t WHERE t.deleted=0 AND t.status=0
ON CONFLICT (tenant_id,code) WHERE deleted=0 DO NOTHING;

COMMIT;

-- 查询本次测试模板（不会发送消息）。
SELECT tenant_id,code,name,message_type,status FROM system_dingtalk_message_template
WHERE code IN ('dingtalk_test_text','dingtalk_test_markdown','dingtalk_test_action_card') AND deleted=0
ORDER BY tenant_id,code;
