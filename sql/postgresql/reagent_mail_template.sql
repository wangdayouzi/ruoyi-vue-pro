-- =============================================
-- 试剂发货通知邮件模板（Reagent）- PostgreSQL
-- 模板编号: reagent-shipment-notify-handler
-- 对应监听器: ReagentShipmentHandlerMailListener
-- 可直接重复执行（id 取当前最大值 + 1，避免冲突）
-- 注意: account_id 需改成你实际配置的"邮件账号"编号（系统管理 → 邮件管理 → 邮箱账号）
-- =============================================

-- @formatter:off
INSERT INTO system_mail_template (id, name, code, account_id, nickname, title, content, params, status, remark, creator, create_time, updater, update_time, deleted)
SELECT COALESCE(MAX(id), 0) + 1,
       '试剂发货任务通知',
       'reagent-shipment-notify-handler',
       1, -- TODO 改成你实际的邮箱账号 id
       '试剂管理系统',
       '【试剂】您的试剂申请单 {applyNo} 有发货任务待处理',
       '<p>您好：</p><p>您的试剂申请单 <b>{applyNo}</b> 有一条发货任务需要处理。</p><p>接收单位：{receiverUnit}</p><p>接收联系人：{receiverName}</p><p>联系电话：{receiverPhone}</p><p>接收地址：{receiverAddress}</p><p>请及时登录系统处理发货。</p>',
       '["applyNo","receiverUnit","receiverName","receiverPhone","receiverAddress"]',
       0,
       '试剂流程：提交后自动通知发货处理人',
       '1',
       CURRENT_TIMESTAMP,
       '1',
       CURRENT_TIMESTAMP,
       '0'
FROM system_mail_template
WHERE NOT EXISTS (SELECT 1 FROM system_mail_template WHERE code = 'reagent-shipment-notify-handler');
-- @formatter:on

-- =============================================
-- 2. 发货完成通知邮件模板（发送给流程发起人）
-- 模板编号: reagent-shipment-notify
-- 对应监听器: ReagentShipmentMailListener（发货节点 complete 事件）
-- 说明: 不携带单号，仅带接收单位等表单字段
-- =============================================

-- @formatter:off
INSERT INTO system_mail_template (id, name, code, account_id, nickname, title, content, params, status, remark, creator, create_time, updater, update_time, deleted)
SELECT COALESCE(MAX(id), 0) + 1,
       '试剂发货完成通知',
       'reagent-shipment-notify',
       1, -- TODO 改成你实际的邮箱账号 id
       '试剂管理系统',
       '【试剂】您的试剂申请单 {applyNo} 已发货',
       '<p>您好：</p><p>您的试剂申请单 <b>{applyNo}</b> 已全部发货完成。</p><p>接收单位：{receiverUnit}</p><p>接收联系人：{receiverName}</p><p>联系电话：{receiverPhone}</p><p>接收地址：{receiverAddress}</p><p>请留意查收。</p>',
       '["applyNo","receiverUnit","receiverName","receiverPhone","receiverAddress"]',
       0,
       '试剂流程：发货完成后通知发起人',
       '1',
       CURRENT_TIMESTAMP,
       '1',
       CURRENT_TIMESTAMP,
       '0'
FROM system_mail_template
WHERE NOT EXISTS (SELECT 1 FROM system_mail_template WHERE code = 'reagent-shipment-notify');
-- @formatter:on
