-- =============================================
-- 试剂存储条件字典（Reagent）- PostgreSQL
-- 供「试剂标签打印」的存储条件下拉使用
-- 可直接重复执行：id 动态取 MAX+1 避免主键冲突；已存在按 type / (dict_type,value) 跳过
-- =============================================

-- 1. 字典类型（type 唯一，已存在则跳过；id 用 MAX+1，不会撞主键）
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
SELECT COALESCE(MAX(id), 0) + 1, '试剂存储条件', 'reagent_storage_condition', 0,
       '试剂标签打印 - 存储条件下拉', '1', NOW(), '1', NOW(), 0, NULL
FROM system_dict_type
WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE type = 'reagent_storage_condition')
LIMIT 1;

-- 2. 字典数据（value 即打印到 Excel 的文本，故 label=value；已存在按 (dict_type,value) 跳过）
WITH opt(sort, label, value) AS (
    VALUES
    (1, '室温（15-30°C）', '室温（15-30°C）'),
    (2, '2-8°C（冷藏）', '2-8°C（冷藏）'),
    (3, '-20°C（冷冻）', '-20°C（冷冻）'),
    (4, '-80°C（超低温）', '-80°C（超低温）'),
    (5, '液氮（-196°C）', '液氮（-196°C）'),
    (6, '避光保存', '避光保存'),
    (7, '干燥保存', '干燥保存')
)
INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT (SELECT COALESCE(MAX(id), 0) FROM system_dict_data) + o.sort,
       o.sort, o.label, o.value, 'reagent_storage_condition', 0, '', '', '', '1', NOW(), '1', NOW(), 0
FROM opt o
WHERE NOT EXISTS (
    SELECT 1 FROM system_dict_data d
    WHERE d.dict_type = 'reagent_storage_condition' AND d.value = o.value
);
