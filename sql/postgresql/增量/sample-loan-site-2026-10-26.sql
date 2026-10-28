-- 旧领用记录全部属于宁波；站点与楼层由系统字典维护。
BEGIN;

ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS site varchar(32);
UPDATE sample_loan SET site = 'NINGBO' WHERE site IS NULL;
-- 旧版本页面在切换期间仍可能写入空站点；新接口会强制校验，数据库暂不加非空约束。
COMMENT ON COLUMN sample_loan.site IS '领用站点，旧记录空值按宁波处理；字典 sample_loan_site';

CREATE INDEX IF NOT EXISTS idx_sample_loan_site_location_status_time
    ON sample_loan (site, location, status, create_time DESC);

INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT nextval('system_dict_type_seq'), v.name, v.type, 0, '领用站点和楼层配置', '1', now(), '1', now(), 0
FROM (VALUES
    ('领用站点', 'sample_loan_site'),
    ('宁波领用楼层', 'sample_loan_floor_ningbo'),
    ('上海领用楼层', 'sample_loan_floor_shanghai')
) AS v(name, type)
WHERE NOT EXISTS (SELECT 1 FROM system_dict_type t WHERE t.type = v.type AND t.deleted = 0);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, creator, create_time, updater, update_time, deleted)
SELECT nextval('system_dict_data_seq'), v.sort, v.label, v.value, v.dict_type, 0, '1', now(), '1', now(), 0
FROM (VALUES
    (1, '宁波', 'NINGBO', 'sample_loan_site'),
    (2, '上海', 'SHANGHAI', 'sample_loan_site'),
    (1, '4楼', '4楼', 'sample_loan_floor_ningbo'),
    (2, '8楼', '8楼', 'sample_loan_floor_ningbo'),
    (1, '5楼', '5楼', 'sample_loan_floor_shanghai'),
    (2, '7楼', '7楼', 'sample_loan_floor_shanghai')
) AS v(sort, label, value, dict_type)
WHERE NOT EXISTS (
    SELECT 1 FROM system_dict_data d
    WHERE d.dict_type = v.dict_type AND d.value = v.value AND d.deleted = 0
);

COMMIT;
