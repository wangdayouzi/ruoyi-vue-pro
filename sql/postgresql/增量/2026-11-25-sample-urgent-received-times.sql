BEGIN;

ALTER TABLE sample_urgent
    ADD COLUMN IF NOT EXISTS sample_received_times text;

-- 把原来的单个时间搬到列表中。已有列表优先，重复执行不会覆盖。
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_attribute
        WHERE attrelid = 'sample_urgent'::regclass
          AND attname = 'sample_received_time' AND NOT attisdropped
    ) THEN
        UPDATE sample_urgent
        SET sample_received_times = json_build_array(
            to_char(sample_received_time, 'YYYY-MM-DD HH24:MI:SS')
        )::text
        WHERE sample_received_time IS NOT NULL AND sample_received_times IS NULL;

        ALTER TABLE sample_urgent DROP COLUMN sample_received_time;
    END IF;
END
$$;

COMMIT;
