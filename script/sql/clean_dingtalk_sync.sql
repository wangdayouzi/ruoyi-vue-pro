-- ============================================================
-- 清理钉钉同步的部门和用户（调试用）- PostgreSQL版
-- 
-- 钉钉 social_type = 20
-- 涉及表：system_dept, system_user_role, system_users,
--         system_social_user_bind, system_social_user
--
-- 使用前请确认：有数据库备份
-- ============================================================

BEGIN;

-- Step 1: 删除同步的部门
DELETE FROM system_dept WHERE source_type = 20;

-- Step 2: 删除钉钉用户的角色关联（system_users的外键依赖）
DELETE FROM system_user_role
WHERE user_id IN (
    SELECT user_id FROM system_social_user_bind WHERE social_type = 20
);

-- Step 3: 删除通过钉钉绑定的管理用户
DELETE FROM system_users
WHERE id IN (
    SELECT user_id FROM system_social_user_bind WHERE social_type = 20
);

-- Step 4: 删除钉钉社交用户绑定关系
DELETE FROM system_social_user_bind WHERE social_type = 20;

-- Step 5: 删除钉钉社交用户记录
DELETE FROM system_social_user WHERE type = 20;

-- ============================================================
-- 验证
-- ============================================================
SELECT 'system_dept(钉钉)'         AS tbl, COUNT(*) AS cnt FROM system_dept WHERE source_type = 20
UNION ALL
SELECT 'system_user_role(残留)',   COUNT(*) FROM system_user_role u
       JOIN system_social_user_bind b ON b.user_id = u.user_id AND b.social_type = 20
UNION ALL
SELECT 'system_social_user_bind',  COUNT(*) FROM system_social_user_bind WHERE social_type = 20
UNION ALL
SELECT 'system_social_user',       COUNT(*) FROM system_social_user WHERE type = 20;

COMMIT;
