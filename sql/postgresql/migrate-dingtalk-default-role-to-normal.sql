-- 将旧默认角色标识 common 的有效用户角色关联迁移到标识 normal 的角色。
-- 关联只按 system_user_role.role_id 匹配角色，不使用关联表 tenant_id 过滤；
-- 该字段在现有数据中同时出现 0 和 1，迁移时会保留原值。
-- 执行前建议先备份，并先运行下方预览查询确认影响范围。

-- 预览来源角色、目标角色和受影响用户数。
SELECT old_role.id AS old_role_id,
       old_role.name AS old_role_name,
       target_role.id AS target_role_id,
       target_role.name AS target_role_name,
       COUNT(DISTINCT user_role.user_id) AS affected_users
FROM system_user_role user_role
JOIN system_role old_role
  ON old_role.id = user_role.role_id
 AND old_role.code = 'common'
 AND old_role.deleted = 0
CROSS JOIN system_role target_role
WHERE target_role.code = 'normal'
  AND target_role.deleted = 0
  AND user_role.deleted = 0
GROUP BY old_role.id, old_role.name, target_role.id, target_role.name
ORDER BY old_role.id, target_role.id;

BEGIN;

-- 只有旧、新标识各自唯一时才迁移，避免多个租户存在同名标识时选错角色。
DO $$
DECLARE
    old_role_count INTEGER;
    target_role_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO old_role_count
    FROM system_role
    WHERE code = 'common' AND deleted = 0;

    SELECT COUNT(*) INTO target_role_count
    FROM system_role
    WHERE code = 'normal' AND deleted = 0;

    IF old_role_count <> 1 OR target_role_count <> 1 THEN
        RAISE EXCEPTION '迁移中止：有效 common 角色数为 %，有效 normal 角色数为 %；两者都必须各有且仅有一个',
            old_role_count, target_role_count;
    END IF;
END $$;

-- 用户已拥有 normal 时，只软删除其 common 关联，避免重复分配。
UPDATE system_user_role old_assignment
SET deleted = 1,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM system_role old_role,
     system_role target_role
WHERE old_role.id = old_assignment.role_id
  AND old_role.code = 'common'
  AND old_role.deleted = 0
  AND target_role.code = 'normal'
  AND target_role.deleted = 0
  AND old_assignment.deleted = 0
  AND EXISTS (
      SELECT 1
      FROM system_user_role existing_assignment
      WHERE existing_assignment.user_id = old_assignment.user_id
        AND existing_assignment.role_id = target_role.id
        AND existing_assignment.deleted = 0
  );

-- 其余用户每人保留一条有效关联并改指向 normal；重复的 common 关联软删除。
WITH ranked_assignments AS (
    SELECT user_role.id,
           target_role.id AS target_role_id,
           ROW_NUMBER() OVER (
               PARTITION BY user_role.user_id
               ORDER BY user_role.id
           ) AS row_num
    FROM system_user_role user_role
    JOIN system_role old_role
      ON old_role.id = user_role.role_id
     AND old_role.code = 'common'
     AND old_role.deleted = 0
    CROSS JOIN system_role target_role
    WHERE target_role.code = 'normal'
      AND target_role.deleted = 0
      AND user_role.deleted = 0
      AND NOT EXISTS (
          SELECT 1
          FROM system_user_role existing_assignment
          WHERE existing_assignment.user_id = user_role.user_id
            AND existing_assignment.role_id = target_role.id
            AND existing_assignment.deleted = 0
      )
)
UPDATE system_user_role user_role
SET role_id = CASE WHEN ranked_assignments.row_num = 1
                   THEN ranked_assignments.target_role_id ELSE user_role.role_id END,
    deleted = CASE WHEN ranked_assignments.row_num = 1 THEN 0 ELSE 1 END,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM ranked_assignments
WHERE user_role.id = ranked_assignments.id;

COMMIT;

-- 迁移后确认：应无仍使用 common 角色的有效关联。
SELECT old_role.id AS old_role_id,
       old_role.name AS old_role_name,
       COUNT(*) AS remaining_active_assignments
FROM system_user_role user_role
JOIN system_role old_role
  ON old_role.id = user_role.role_id
WHERE old_role.code = 'common'
  AND old_role.deleted = 0
  AND user_role.deleted = 0
GROUP BY old_role.id, old_role.name;
