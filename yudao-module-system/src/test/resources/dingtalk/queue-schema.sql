BEGIN;
CREATE TABLE IF NOT EXISTS system_dingtalk_notification_queue (
 id bigserial PRIMARY KEY, tenant_id bigint NOT NULL,
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

COMMIT;
