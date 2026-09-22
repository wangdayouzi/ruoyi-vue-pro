-- 本地看板演示数据：清理并生成 150 条最近 24 小时、未归还的样品记录。
-- 仅删除本脚本自己生成的 DEMO-BAS-* 数据，不影响真实台账。
BEGIN;

DELETE FROM sample_loan
WHERE bas_no LIKE 'DEMO-BAS-%'
  AND remark = '本地看板演示数据';

INSERT INTO sample_loan
  (id, bas_no, requester_id, requester, submitter_id, submitter, sample_info, remark,
   status, create_time, update_time, creator, updater, deleted, tenant_id)
SELECT
  nextval('sample_loan_seq'),
  'DEMO-BAS-' || lpad(n::text, 3, '0'),
  1,
  (ARRAY[U&'\\738b\\601d\\8fdc', U&'\\674e\\4f73\\5b81', U&'\\9648\\5b50\\6602', U&'\\8d75\\96e8\\6850', U&'\\5218\\535a\\6587', U&'\\5468\\8bd7\\6db5', U&'\\6768\\6668\\66e6', U&'\\5434\\4fca\\6770'])[1 + ((n - 1) % 8)],
  1,
  '本地演示用户',
  '演示样品 ' || lpad(n::text, 3, '0'),
  '本地看板演示数据',
  1,
  CURRENT_TIMESTAMP - ((150 - n) * interval '9 minutes'),
  CURRENT_TIMESTAMP - ((150 - n) * interval '9 minutes'),
  '1',
  '1',
  0,
  1
FROM generate_series(1, 150) AS numbers(n);

COMMIT;
