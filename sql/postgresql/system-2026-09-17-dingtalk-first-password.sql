-- 钉钉同步账户首次设置本地登录密码
-- 存量账户默认视为已设置密码，避免上线后影响现有账号登录。
ALTER TABLE system_users
    ADD COLUMN IF NOT EXISTS password_initialized boolean NOT NULL DEFAULT true;

COMMENT ON COLUMN system_users.password_initialized IS '是否已设置本地登录密码（钉钉同步新用户首次设置前为 false）';

-- 如需让【已有】钉钉同步账户也在下次验证身份后设置本地密码，
-- 请在核对受影响账号后，单独执行以下语句；不要删除 system_users 或社交绑定数据。
-- UPDATE system_users u
-- SET password_initialized = false
-- FROM system_social_user_bind b
-- WHERE b.user_id = u.id
--   AND u.deleted = 0
--   AND b.deleted = 0
--   AND b.user_type = 2
--   AND b.social_type = 20;
