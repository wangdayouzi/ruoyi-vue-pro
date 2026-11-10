ALTER TABLE sample_loan ADD COLUMN IF NOT EXISTS returner varchar(64);
COMMENT ON COLUMN sample_loan.returner IS '实际归还操作人姓名';
