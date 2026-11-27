-- 上海安全盘年度流水初始化。
-- 执行前，将 next_from_legacy := NULL 改为核对旧台账及已发纸件后的下一个流水号。
-- 例如最后使用 317，填写 318。已有年度流水时保留现值。
BEGIN;

DO $initialize$
DECLARE
    next_from_legacy integer := NULL;
    target_year integer := EXTRACT(YEAR FROM CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Shanghai')::integer;
    next_from_system integer;
BEGIN
    IF EXISTS (SELECT 1 FROM form_secure_counter WHERE tenant_id = 1 AND issue_year = target_year) THEN
        RAISE NOTICE '上海安全盘 % 年流水已初始化，保留现值', target_year;
        RETURN;
    END IF;

    IF next_from_legacy IS NULL OR next_from_legacy NOT BETWEEN 1 AND 999999999 THEN
        RAISE EXCEPTION '请先填写从旧台账核定的下一流水号 next_from_legacy';
    END IF;

    SELECT COALESCE(MAX(sequence_no), 0) + 1 INTO next_from_system
    FROM form_secure_application WHERE tenant_id = 1 AND issue_year = target_year;

    INSERT INTO form_secure_counter(tenant_id, issue_year, next_no, version, initialized_by, source)
    VALUES (1, target_year, GREATEST(next_from_legacy, next_from_system), 2, 0, '上海旧台账核定')
    ON CONFLICT (tenant_id, issue_year) DO NOTHING;
END
$initialize$;

SELECT issue_year, next_no, version, source
FROM form_secure_counter
WHERE tenant_id = 1 AND issue_year = EXTRACT(YEAR FROM CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Shanghai')::integer;

COMMIT;
