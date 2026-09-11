-- 试剂标签打印：一次性初始化脚本（PostgreSQL）
-- 用途：在测试、开发、正式各自的数据库执行一次。
-- 内容：建表、建序列、标准模板、宁波4楼/宁波8楼/上海三台标签打印机。
-- 如果实际业务 schema 不是 mes，请修改下一行。
SET search_path TO mes, public;

BEGIN;

CREATE SEQUENCE IF NOT EXISTS reagent_label_printer_seq START 1;
CREATE SEQUENCE IF NOT EXISTS reagent_label_template_seq START 1;
CREATE SEQUENCE IF NOT EXISTS reagent_label_print_job_seq START 1;

CREATE TABLE IF NOT EXISTS reagent_label_template (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(128) NOT NULL,
    code varchar(128) NOT NULL UNIQUE,
    status smallint NOT NULL DEFAULT 1,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS reagent_label_printer (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(128) NOT NULL,
    agent_code varchar(64) NOT NULL,
    agent_token_hash char(64) NOT NULL,
    system_printer_name varchar(255) NOT NULL,
    model varchar(64) NOT NULL DEFAULT 'PT-P900',
    tape_width_mm integer NULL,
    template_code varchar(128) NOT NULL,
    status smallint NOT NULL DEFAULT 1,
    online_status smallint NOT NULL DEFAULT 0,
    last_heartbeat_time timestamp NULL,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_reagent_label_printer_agent ON reagent_label_printer(agent_code, status, deleted);

CREATE TABLE IF NOT EXISTS reagent_label_print_job (
    id bigint NOT NULL PRIMARY KEY,
    job_no varchar(64) NOT NULL UNIQUE,
    printer_id bigint NOT NULL,
    template_code varchar(128) NOT NULL,
    data_snapshot text NOT NULL,
    copies integer NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    claim_token varchar(64) NULL,
    agent_code varchar(64) NULL,
    printed_count integer NOT NULL DEFAULT 0,
    error_code varchar(64) NULL,
    error_message varchar(1000) NULL,
    claimed_time timestamp NULL,
    completed_time timestamp NULL,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted smallint NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_reagent_label_print_job_claim ON reagent_label_print_job(printer_id, status, create_time, deleted);

INSERT INTO reagent_label_template (id, name, code, status, creator, updater)
VALUES (nextval('reagent_label_template_seq'), '宁波标签模板', 'REAGENT_STANDARD', 1, 'admin', 'admin')
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name, status = 1, updater = 'admin', update_time = CURRENT_TIMESTAMP, deleted = 0;

INSERT INTO reagent_label_template (id, name, code, status, creator, updater)
VALUES (nextval('reagent_label_template_seq'), '上海标签模版', 'SH_REAGENT_STANDARD', 1, 'admin', 'admin')
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name, status = 1, updater = 'admin', update_time = CURRENT_TIMESTAMP, deleted = 0;

-- Windows 队列名称暂统一设为 Brother PT-P900；如现场名称不同，只改对应 system_printer_name。
-- 宁波4楼标签打印机，AgentCode: TEST-PTP900-01
UPDATE reagent_label_printer
SET name = '宁波4楼标签打印机',
    agent_token_hash = '70f8cd18a629448c39324520e7fd46f8a6f65db5d9d77adaa3e742641d17bcba',
    system_printer_name = 'Brother PT-P900', model = 'PT-P900', tape_width_mm = 36,
    template_code = 'REAGENT_STANDARD', status = 1, deleted = 0,
    updater = 'admin', update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'TEST-PTP900-01';

INSERT INTO reagent_label_printer
    (id, name, agent_code, agent_token_hash, system_printer_name, model, tape_width_mm, template_code,
     status, online_status, creator, updater)
SELECT nextval('reagent_label_printer_seq'), '宁波4楼标签打印机', 'TEST-PTP900-01',
       '70f8cd18a629448c39324520e7fd46f8a6f65db5d9d77adaa3e742641d17bcba',
       'Brother PT-P900', 'PT-P900', 36, 'REAGENT_STANDARD', 1, 0, 'admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM reagent_label_printer WHERE agent_code = 'TEST-PTP900-01');

-- 宁波8楼标签打印机，AgentCode: NB-8F-PTP900-01
UPDATE reagent_label_printer
SET name = '宁波8楼标签打印机',
    agent_token_hash = 'd056b0c2926a027800a3b293e3f7bf462826231addf4e93ed7cd8a5f5a7c4a39',
    system_printer_name = 'Brother PT-P900', model = 'PT-P900', tape_width_mm = 36,
    template_code = 'REAGENT_STANDARD', status = 1, deleted = 0,
    updater = 'admin', update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'NB-8F-PTP900-01';

INSERT INTO reagent_label_printer
    (id, name, agent_code, agent_token_hash, system_printer_name, model, tape_width_mm, template_code,
     status, online_status, creator, updater)
SELECT nextval('reagent_label_printer_seq'), '宁波8楼标签打印机', 'NB-8F-PTP900-01',
       'd056b0c2926a027800a3b293e3f7bf462826231addf4e93ed7cd8a5f5a7c4a39',
       'Brother PT-P900', 'PT-P900', 36, 'REAGENT_STANDARD', 1, 0, 'admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM reagent_label_printer WHERE agent_code = 'NB-8F-PTP900-01');

-- 上海标签打印机，AgentCode: SH-PTP900-01
UPDATE reagent_label_printer
SET name = '上海标签打印机',
    agent_token_hash = '8845f231c8fe007ece0d2126eaf89805b45a6912899e5c9abde42432e1dfba4e',
    system_printer_name = 'Brother PT-P900', model = 'PT-P900', tape_width_mm = 36,
    template_code = 'REAGENT_STANDARD', status = 1, deleted = 0,
    updater = 'admin', update_time = CURRENT_TIMESTAMP
WHERE agent_code = 'SH-PTP900-01';

INSERT INTO reagent_label_printer
    (id, name, agent_code, agent_token_hash, system_printer_name, model, tape_width_mm, template_code,
     status, online_status, creator, updater)
SELECT nextval('reagent_label_printer_seq'), '上海标签打印机', 'SH-PTP900-01',
       '8845f231c8fe007ece0d2126eaf89805b45a6912899e5c9abde42432e1dfba4e',
       'Brother PT-P900', 'PT-P900', 36, 'REAGENT_STANDARD', 1, 0, 'admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM reagent_label_printer WHERE agent_code = 'SH-PTP900-01');

COMMIT;
