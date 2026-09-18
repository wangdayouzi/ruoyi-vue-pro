-- 钉钉员工在职状态、工号与通讯录缺失保护
ALTER TABLE system_users
    ADD COLUMN IF NOT EXISTS employee_no varchar(64),
    ADD COLUMN IF NOT EXISTS dingtalk_user_id varchar(128),
    ADD COLUMN IF NOT EXISTS dingtalk_missing_sync_count int2 NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS dingtalk_last_seen_time timestamp;

COMMENT ON COLUMN system_users.employee_no IS '员工工号';
COMMENT ON COLUMN system_users.dingtalk_user_id IS '钉钉用户ID';
COMMENT ON COLUMN system_users.dingtalk_missing_sync_count IS '连续未出现在完整钉钉同步中的次数';
COMMENT ON COLUMN system_users.dingtalk_last_seen_time IS '最近一次出现在钉钉通讯录的时间';

CREATE INDEX IF NOT EXISTS idx_system_users_employee_no ON system_users (employee_no);
CREATE INDEX IF NOT EXISTS idx_system_users_dingtalk_user_id ON system_users (dingtalk_user_id);
