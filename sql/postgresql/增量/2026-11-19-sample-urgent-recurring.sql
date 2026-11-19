BEGIN;

CREATE SEQUENCE IF NOT EXISTS sample_urgent_plan_seq;
CREATE TABLE IF NOT EXISTS sample_urgent_plan (
    id bigint PRIMARY KEY DEFAULT nextval('sample_urgent_plan_seq'),
    template text NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    daily_time time NOT NULL,
    weekdays text NOT NULL,
    requester_id bigint NOT NULL,
    requester_name varchar(100) NOT NULL,
    status smallint NOT NULL DEFAULT 0 CHECK (status IN (0, 1, 2)),
    revision integer NOT NULL DEFAULT 0,
    next_date date NOT NULL,
    last_error varchar(1000) NOT NULL DEFAULT '',
    creator varchar(64) DEFAULT '', create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '', update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0,
    tenant_id bigint NOT NULL DEFAULT 0,
    CHECK (end_date >= start_date AND end_date - start_date < 366)
);
CREATE INDEX IF NOT EXISTS idx_sample_urgent_plan_due
    ON sample_urgent_plan (status, next_date, id) WHERE deleted = 0;

ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS recurring_plan_id bigint;
ALTER TABLE sample_urgent ADD COLUMN IF NOT EXISTS recurring_date date;
-- 包括已软删除的单据，删除当天单据也不能触发重新生成。
CREATE UNIQUE INDEX IF NOT EXISTS uk_sample_urgent_plan_day
    ON sample_urgent (tenant_id, recurring_plan_id, recurring_date)
    WHERE recurring_plan_id IS NOT NULL;
COMMENT ON TABLE sample_urgent_plan IS '样品加急入库周期预约计划；每天生成独立单据';
COMMENT ON COLUMN sample_urgent.recurring_plan_id IS '来源周期计划，历史单据为空';
COMMENT ON COLUMN sample_urgent.recurring_date IS '周期计划对应的业务日期';

-- 仅登记业务任务配置，不直接写 Quartz 系统表；新版后端启动后补齐实际调度。
-- 首次安装每天 00:05 执行。后续重跑迁移不覆盖管理员修改的时间或暂停状态。
SELECT pg_advisory_xact_lock(7240202611191);
INSERT INTO infra_job (id, name, status, handler_name, handler_param, cron_expression,
                       retry_count, retry_interval, monitor_timeout, creator, updater)
SELECT nextval('infra_job_seq'), '样品加急入库周期预约生成', 0, 'sampleUrgentPlanGenerateJob', '', '0 5 0 * * ?',
       0, 0, 0, '1', '1'
WHERE NOT EXISTS (SELECT 1 FROM infra_job WHERE handler_name = 'sampleUrgentPlanGenerateJob' AND deleted = 0);

COMMIT;
