/*
 Yudao Database Transfer Tool

 Source Server Type    : MySQL

 Target Server Type    : PostgreSQL

 Date: 2026-08-11 10:33:39
*/


-- ----------------------------
-- Table structure for dual
-- ----------------------------
DROP TABLE IF EXISTS dual;
CREATE TABLE dual
(
    id int2
);

COMMENT ON TABLE dual IS '数据库连接的表';

-- ----------------------------
-- Records of dual
-- ----------------------------
-- @formatter:off
INSERT INTO dual VALUES (1);
-- @formatter:on

-- ----------------------------
-- Table structure for mes_cal_holiday
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_holiday;
CREATE TABLE mes_cal_holiday (
    id int8 NOT NULL,
  day timestamp NOT NULL,
  type int2 NOT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_holiday ADD CONSTRAINT pk_mes_cal_holiday PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_holiday.id IS '编号';
COMMENT ON COLUMN mes_cal_holiday.day IS '日期';
COMMENT ON COLUMN mes_cal_holiday.type IS '日期类型';
COMMENT ON COLUMN mes_cal_holiday.remark IS '备注';
COMMENT ON COLUMN mes_cal_holiday.creator IS '创建者';
COMMENT ON COLUMN mes_cal_holiday.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_holiday.updater IS '更新者';
COMMENT ON COLUMN mes_cal_holiday.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_holiday.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_holiday.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_holiday IS 'MES 假期设置';

-- ----------------------------
-- Records of mes_cal_holiday
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_holiday (id, day, type, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, '2026-02-01 00:00:00', 2, NULL, '1', '2026-02-16 19:23:55', '1', '2026-02-16 11:32:14', '0', 1), (2, '1970-01-01 08:00:00', 2, NULL, '1', '2026-02-16 19:37:14', '1', '2026-02-16 19:37:24', '0', 1), (3, '2026-01-31 00:00:00', 2, NULL, '1', '2026-02-16 19:46:27', '1', '2026-02-16 19:46:27', '0', 1), (4, '2026-01-30 00:00:00', 2, NULL, '1', '2026-02-16 19:49:12', '1', '2026-02-16 19:49:12', '0', 1), (5, '2026-04-15 00:00:00', 2, '', '1', '2026-04-16 19:48:13', '1', '2026-04-16 19:48:15', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_holiday_seq;
CREATE SEQUENCE mes_cal_holiday_seq
    START 6;

-- ----------------------------
-- Table structure for mes_cal_plan
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_plan;
CREATE TABLE mes_cal_plan (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  calendar_type int2 NULL DEFAULT NULL,
  start_date timestamp NOT NULL,
  end_date timestamp NOT NULL,
  shift_type int2 NULL DEFAULT NULL,
  shift_method int2 NULL DEFAULT NULL,
  shift_count int4 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_plan ADD CONSTRAINT pk_mes_cal_plan PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_plan.id IS '计划编号';
COMMENT ON COLUMN mes_cal_plan.code IS '计划编码';
COMMENT ON COLUMN mes_cal_plan.name IS '计划名称';
COMMENT ON COLUMN mes_cal_plan.calendar_type IS '班组类型';
COMMENT ON COLUMN mes_cal_plan.start_date IS '开始日期';
COMMENT ON COLUMN mes_cal_plan.end_date IS '结束日期';
COMMENT ON COLUMN mes_cal_plan.shift_type IS '轮班方式';
COMMENT ON COLUMN mes_cal_plan.shift_method IS '倒班方式';
COMMENT ON COLUMN mes_cal_plan.shift_count IS '倒班天数';
COMMENT ON COLUMN mes_cal_plan.status IS '状态';
COMMENT ON COLUMN mes_cal_plan.remark IS '备注';
COMMENT ON COLUMN mes_cal_plan.creator IS '创建者';
COMMENT ON COLUMN mes_cal_plan.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_plan.updater IS '更新者';
COMMENT ON COLUMN mes_cal_plan.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_plan.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_plan.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_plan IS 'MES 排班计划表';

-- ----------------------------
-- Records of mes_cal_plan
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_plan (id, code, name, calendar_type, start_date, end_date, shift_type, shift_method, shift_count, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PLAN001', '注塑8月三班倒', 1, '2022-08-01 00:00:00', '2022-08-31 00:00:00', 3, 3, 1, 1, '', '1', '2026-02-17 06:55:00', '1', '2026-04-01 16:28:08', '0', 1), (2, 'PLAN002', '注塑11月两班倒', 2, '2022-11-01 00:00:00', '2022-11-30 00:00:00', 2, 3, 1, 1, '', '1', '2026-02-17 06:55:00', '1', '2026-04-01 16:28:09', '0', 1), (3, 'PLAN003', '仓库单白班', 1, '2022-07-01 00:00:00', '2022-07-31 00:00:00', 1, 4, 1, 1, '', '1', '2026-02-17 06:55:00', '1', '2026-04-01 16:28:10', '0', 1), (4, 'PLAN004', '组装三班倒', 2, '2022-09-01 00:00:00', '2022-10-31 00:00:00', 3, 3, 1, 1, '', '1', '2026-02-17 06:55:00', '1', '2026-04-01 16:28:11', '0', 1), (5, 'PLAN005', '测试计划', 1, '2022-08-19 00:00:00', '2022-08-26 00:00:00', 2, 2, 1, 0, '', '1', '2026-02-17 06:55:00', '1', '2026-04-01 16:28:12', '0', 1), (10, 'PLAN-2602-ZS', '注塑2026年2月三班倒', 2, '2026-02-01 00:00:00', '2026-02-28 23:59:59', 3, 3, 1, 1, '', '1', '2026-02-19 12:39:57', '1', '2026-04-01 16:28:13', '0', 1), (11, 'PLAN-2602-ZZ', '组装2026年2月两班倒', 1, '2026-02-01 00:00:00', '2026-02-28 23:59:59', 2, 3, 1, 1, '', '1', '2026-02-19 12:39:57', '1', '2026-04-01 16:28:13', '0', 1), (12, 'PLAN-2602-CK', '仓库2026年2月单白班', 2, '2026-02-01 00:00:00', '2026-02-28 23:59:59', 1, 4, 1, 1, '', '1', '2026-02-19 12:39:57', '1', '2026-04-01 16:28:14', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_plan_seq;
CREATE SEQUENCE mes_cal_plan_seq
    START 13;

-- ----------------------------
-- Table structure for mes_cal_plan_shift
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_plan_shift;
CREATE TABLE mes_cal_plan_shift (
    id int8 NOT NULL,
  plan_id int8 NOT NULL,
  sort int4 NOT NULL,
  name varchar(64) NOT NULL,
  start_time varchar(10) NOT NULL,
  end_time varchar(10) NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_plan_shift ADD CONSTRAINT pk_mes_cal_plan_shift PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_plan_shift.id IS '班次编号';
COMMENT ON COLUMN mes_cal_plan_shift.plan_id IS '排班计划编号';
COMMENT ON COLUMN mes_cal_plan_shift.sort IS '显示顺序';
COMMENT ON COLUMN mes_cal_plan_shift.name IS '班次名称';
COMMENT ON COLUMN mes_cal_plan_shift.start_time IS '开始时间';
COMMENT ON COLUMN mes_cal_plan_shift.end_time IS '结束时间';
COMMENT ON COLUMN mes_cal_plan_shift.remark IS '备注';
COMMENT ON COLUMN mes_cal_plan_shift.creator IS '创建者';
COMMENT ON COLUMN mes_cal_plan_shift.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_plan_shift.updater IS '更新者';
COMMENT ON COLUMN mes_cal_plan_shift.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_plan_shift.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_plan_shift.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_plan_shift IS 'MES 计划班次表';

-- ----------------------------
-- Records of mes_cal_plan_shift
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_plan_shift (id, plan_id, sort, name, start_time, end_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '白班', '08:00', '16:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (2, 1, 2, '中班', '16:00', '00:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (3, 1, 3, '夜班', '00:00', '08:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (4, 2, 1, '白班', '08:00', '20:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (5, 2, 2, '夜班', '20:00', '08:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (6, 3, 1, '白班', '08:00', '18:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (7, 4, 1, '白班', '08:00', '16:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (8, 4, 2, '中班', '16:00', '00:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (9, 4, 3, '夜班', '00:00', '08:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (10, 5, 1, '白班', '08:00', '20:00', '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:47', '0', 1), (11, 5, 2, '夜班', '20:00', '08:01', '', '1', '2026-02-17 06:55:00', '1', '2026-04-02 00:31:34', '0', 1), (101, 10, 1, '白班', '08:00', '16:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (102, 10, 2, '中班', '16:00', '00:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (103, 10, 3, '夜班', '00:00', '08:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (104, 11, 1, '白班', '08:00', '20:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (105, 11, 2, '夜班', '20:00', '08:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (106, 12, 1, '白班', '08:00', '17:00', '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_plan_shift_seq;
CREATE SEQUENCE mes_cal_plan_shift_seq
    START 107;

-- ----------------------------
-- Table structure for mes_cal_plan_team
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_plan_team;
CREATE TABLE mes_cal_plan_team (
    id int8 NOT NULL,
  plan_id int8 NOT NULL,
  team_id int8 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_plan_team ADD CONSTRAINT pk_mes_cal_plan_team PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_plan_team.id IS '编号';
COMMENT ON COLUMN mes_cal_plan_team.plan_id IS '排班计划编号';
COMMENT ON COLUMN mes_cal_plan_team.team_id IS '班组编号';
COMMENT ON COLUMN mes_cal_plan_team.remark IS '备注';
COMMENT ON COLUMN mes_cal_plan_team.creator IS '创建者';
COMMENT ON COLUMN mes_cal_plan_team.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_plan_team.updater IS '更新者';
COMMENT ON COLUMN mes_cal_plan_team.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_plan_team.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_plan_team.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_plan_team IS 'MES 计划班组关联表';

-- ----------------------------
-- Records of mes_cal_plan_team
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_plan_team (id, plan_id, team_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 201, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (2, 1, 202, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (3, 1, 203, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (4, 2, 201, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (5, 2, 202, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (6, 3, 211, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (7, 4, 208, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (8, 4, 209, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (9, 4, 210, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (10, 5, 201, '', '1', '2026-02-17 06:55:00', '1', '2026-02-17 06:55:33', '0', 1), (101, 10, 201, '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (102, 10, 202, '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (103, 10, 203, '', '1', '2026-02-19 12:39:57', '1', '2026-02-19 12:39:57', '0', 1), (104, 11, 208, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (105, 11, 209, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (106, 12, 211, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_plan_team_seq;
CREATE SEQUENCE mes_cal_plan_team_seq
    START 107;

-- ----------------------------
-- Table structure for mes_cal_team
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_team;
CREATE TABLE mes_cal_team (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  calendar_type int2 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_team ADD CONSTRAINT pk_mes_cal_team PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_team.id IS '班组编号';
COMMENT ON COLUMN mes_cal_team.code IS '班组编码';
COMMENT ON COLUMN mes_cal_team.name IS '班组名称';
COMMENT ON COLUMN mes_cal_team.calendar_type IS '班组类型';
COMMENT ON COLUMN mes_cal_team.remark IS '备注';
COMMENT ON COLUMN mes_cal_team.creator IS '创建者';
COMMENT ON COLUMN mes_cal_team.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_team.updater IS '更新者';
COMMENT ON COLUMN mes_cal_team.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_team.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_team.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_team IS 'MES 班组表';

-- ----------------------------
-- Records of mes_cal_team
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_team (id, code, name, calendar_type, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (201, 'TEAM-A', '注塑A组', 1, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:24', '0', 1), (202, 'TEAM-B', '注塑B组', 1, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:25', '0', 1), (203, 'TEAM-C', '注塑C组', 2, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:26', '0', 1), (208, 'TEAM-D', '组装A组', 2, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:27', '0', 1), (209, 'TEAM-E', '组装B组', 2, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:28', '0', 1), (210, 'TEAM-F', '组装C组', 2, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:29', '0', 1), (211, 'TEAM-G', '仓库组', 1, '', '1', '2026-02-18 01:16:10', '1', '2026-04-01 16:06:30', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_team_seq;
CREATE SEQUENCE mes_cal_team_seq
    START 212;

-- ----------------------------
-- Table structure for mes_cal_team_member
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_team_member;
CREATE TABLE mes_cal_team_member (
    id int8 NOT NULL,
  team_id int8 NOT NULL,
  user_id int8 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_team_member ADD CONSTRAINT pk_mes_cal_team_member PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_team_member.id IS '班组成员编号';
COMMENT ON COLUMN mes_cal_team_member.team_id IS '班组编号';
COMMENT ON COLUMN mes_cal_team_member.user_id IS '用户编号';
COMMENT ON COLUMN mes_cal_team_member.remark IS '备注';
COMMENT ON COLUMN mes_cal_team_member.creator IS '创建者';
COMMENT ON COLUMN mes_cal_team_member.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_team_member.updater IS '更新者';
COMMENT ON COLUMN mes_cal_team_member.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_team_member.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_team_member.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_team_member IS 'MES 班组成员表';

-- ----------------------------
-- Records of mes_cal_team_member
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_team_member (id, team_id, user_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 201, 1, '', '1', '2026-02-18 01:16:10', '1', '2026-02-18 01:17:01', '0', 1), (2, 201, 104, '', '1', '2026-02-18 01:16:10', '1', '2026-02-18 01:17:01', '0', 1), (3, 202, 105, '', '1', '2026-02-18 01:16:10', '1', '2026-02-18 01:17:01', '0', 1), (4, 211, 2, '', '1', '2026-02-18 11:16:21', '1', '2026-04-01 19:06:10', '1', 1), (5, 211, 100, 'XXXX', '1', '2026-04-01 19:06:18', '1', '2026-04-02 00:26:58', '1', 1), (6, 211, 115, '', '1', '2026-04-02 00:12:07', '1', '2026-04-02 00:12:07', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_team_member_seq;
CREATE SEQUENCE mes_cal_team_member_seq
    START 7;

-- ----------------------------
-- Table structure for mes_cal_team_shift
-- ----------------------------
DROP TABLE IF EXISTS mes_cal_team_shift;
CREATE TABLE mes_cal_team_shift (
    id int8 NOT NULL,
  plan_id int8 NULL DEFAULT NULL,
  team_id int8 NOT NULL,
  shift_id int8 NOT NULL,
  day timestamp NOT NULL,
  sort int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_cal_team_shift ADD CONSTRAINT pk_mes_cal_team_shift PRIMARY KEY (id);

COMMENT ON COLUMN mes_cal_team_shift.id IS '编号';
COMMENT ON COLUMN mes_cal_team_shift.plan_id IS '排班计划编号';
COMMENT ON COLUMN mes_cal_team_shift.team_id IS '班组编号';
COMMENT ON COLUMN mes_cal_team_shift.shift_id IS '班次编号';
COMMENT ON COLUMN mes_cal_team_shift.day IS '日期';
COMMENT ON COLUMN mes_cal_team_shift.sort IS '排序';
COMMENT ON COLUMN mes_cal_team_shift.remark IS '备注';
COMMENT ON COLUMN mes_cal_team_shift.creator IS '创建者';
COMMENT ON COLUMN mes_cal_team_shift.create_time IS '创建时间';
COMMENT ON COLUMN mes_cal_team_shift.updater IS '更新者';
COMMENT ON COLUMN mes_cal_team_shift.update_time IS '更新时间';
COMMENT ON COLUMN mes_cal_team_shift.deleted IS '是否删除';
COMMENT ON COLUMN mes_cal_team_shift.tenant_id IS '租户编号';
COMMENT ON TABLE mes_cal_team_shift IS 'MES 班组排班表';

-- ----------------------------
-- Records of mes_cal_team_shift
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_cal_team_shift (id, plan_id, team_id, shift_id, day, sort, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 10, 201, 101, '2026-02-02 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (2, 10, 202, 102, '2026-02-02 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (3, 10, 203, 103, '2026-02-02 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (4, 10, 201, 101, '2026-02-03 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (5, 10, 202, 102, '2026-02-03 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (6, 10, 203, 103, '2026-02-03 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (7, 10, 201, 101, '2026-02-04 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (8, 10, 202, 102, '2026-02-04 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (9, 10, 203, 103, '2026-02-04 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (10, 10, 201, 101, '2026-02-05 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (11, 10, 202, 102, '2026-02-05 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (12, 10, 203, 103, '2026-02-05 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (13, 10, 201, 101, '2026-02-06 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (14, 10, 202, 102, '2026-02-06 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (15, 10, 203, 103, '2026-02-06 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (16, 10, 202, 101, '2026-02-09 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (17, 10, 203, 102, '2026-02-09 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (18, 10, 201, 103, '2026-02-09 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (19, 10, 202, 101, '2026-02-10 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (20, 10, 203, 102, '2026-02-10 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (21, 10, 201, 103, '2026-02-10 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (22, 10, 202, 101, '2026-02-11 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (23, 10, 203, 102, '2026-02-11 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (24, 10, 201, 103, '2026-02-11 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (25, 10, 202, 101, '2026-02-12 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (26, 10, 203, 102, '2026-02-12 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (27, 10, 201, 103, '2026-02-12 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (28, 10, 202, 101, '2026-02-13 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (29, 10, 203, 102, '2026-02-13 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (30, 10, 201, 103, '2026-02-13 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (31, 10, 203, 101, '2026-02-16 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (32, 10, 201, 102, '2026-02-16 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (33, 10, 202, 103, '2026-02-16 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (34, 10, 203, 101, '2026-02-17 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (35, 10, 201, 102, '2026-02-17 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (36, 10, 202, 103, '2026-02-17 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (37, 10, 203, 101, '2026-02-18 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (38, 10, 201, 102, '2026-02-18 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (39, 10, 202, 103, '2026-02-18 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (40, 10, 203, 101, '2026-02-19 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (41, 10, 201, 102, '2026-02-19 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (42, 10, 202, 103, '2026-02-19 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (43, 10, 203, 101, '2026-02-20 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (44, 10, 201, 102, '2026-02-20 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (45, 10, 202, 103, '2026-02-20 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (46, 10, 201, 101, '2026-02-23 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (47, 10, 202, 102, '2026-02-23 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (48, 10, 203, 103, '2026-02-23 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (49, 10, 201, 101, '2026-02-24 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (50, 10, 202, 102, '2026-02-24 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (51, 10, 203, 103, '2026-02-24 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (52, 10, 201, 101, '2026-02-25 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (53, 10, 202, 102, '2026-02-25 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (54, 10, 203, 103, '2026-02-25 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (55, 10, 201, 101, '2026-02-26 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (56, 10, 202, 102, '2026-02-26 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (57, 10, 203, 103, '2026-02-26 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (58, 10, 201, 101, '2026-02-27 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (59, 10, 202, 102, '2026-02-27 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (60, 10, 203, 103, '2026-02-27 00:00:00', 3, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (61, 11, 208, 104, '2026-02-02 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (62, 11, 209, 105, '2026-02-02 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (63, 11, 208, 104, '2026-02-03 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (64, 11, 209, 105, '2026-02-03 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (65, 11, 208, 104, '2026-02-04 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (66, 11, 209, 105, '2026-02-04 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (67, 11, 208, 104, '2026-02-05 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (68, 11, 209, 105, '2026-02-05 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (69, 11, 208, 104, '2026-02-06 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (70, 11, 209, 105, '2026-02-06 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (71, 11, 209, 104, '2026-02-09 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (72, 11, 208, 105, '2026-02-09 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (73, 11, 209, 104, '2026-02-10 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (74, 11, 208, 105, '2026-02-10 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (75, 11, 209, 104, '2026-02-11 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (76, 11, 208, 105, '2026-02-11 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (77, 11, 209, 104, '2026-02-12 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (78, 11, 208, 105, '2026-02-12 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (79, 11, 209, 104, '2026-02-13 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (80, 11, 208, 105, '2026-02-13 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (81, 11, 208, 104, '2026-02-16 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (82, 11, 209, 105, '2026-02-16 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (83, 11, 208, 104, '2026-02-17 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (84, 11, 209, 105, '2026-02-17 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (85, 11, 208, 104, '2026-02-18 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (86, 11, 209, 105, '2026-02-18 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (87, 11, 208, 104, '2026-02-19 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (88, 11, 209, 105, '2026-02-19 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (89, 11, 208, 104, '2026-02-20 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (90, 11, 209, 105, '2026-02-20 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (91, 11, 209, 104, '2026-02-23 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (92, 11, 208, 105, '2026-02-23 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (93, 11, 209, 104, '2026-02-24 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (94, 11, 208, 105, '2026-02-24 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (95, 11, 209, 104, '2026-02-25 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (96, 11, 208, 105, '2026-02-25 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (97, 11, 209, 104, '2026-02-26 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (98, 11, 208, 105, '2026-02-26 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (99, 11, 209, 104, '2026-02-27 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (100, 11, 208, 105, '2026-02-27 00:00:00', 2, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (101, 12, 211, 106, '2026-02-02 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (102, 12, 211, 106, '2026-02-03 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (103, 12, 211, 106, '2026-02-04 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (104, 12, 211, 106, '2026-02-05 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (105, 12, 211, 106, '2026-02-06 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (106, 12, 211, 106, '2026-02-09 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (107, 12, 211, 106, '2026-02-10 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (108, 12, 211, 106, '2026-02-11 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (109, 12, 211, 106, '2026-02-12 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (110, 12, 211, 106, '2026-02-13 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (111, 12, 211, 106, '2026-02-16 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (112, 12, 211, 106, '2026-02-17 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (113, 12, 211, 106, '2026-02-18 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (114, 12, 211, 106, '2026-02-19 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (115, 12, 211, 106, '2026-02-20 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (116, 12, 211, 106, '2026-02-23 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (117, 12, 211, 106, '2026-02-24 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (118, 12, 211, 106, '2026-02-25 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (119, 12, 211, 106, '2026-02-26 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1), (120, 12, 211, 106, '2026-02-27 00:00:00', 1, '', '1', '2026-02-19 12:39:58', '1', '2026-02-19 12:39:58', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_cal_team_shift_seq;
CREATE SEQUENCE mes_cal_team_shift_seq
    START 121;

-- ----------------------------
-- Table structure for mes_dv_check_plan
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_check_plan;
CREATE TABLE mes_dv_check_plan (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  type int2 NOT NULL,
  start_date timestamp NULL DEFAULT NULL,
  end_date timestamp NULL DEFAULT NULL,
  cycle_type int2 NOT NULL,
  cycle_count int4 NOT NULL DEFAULT 1,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_check_plan ADD CONSTRAINT pk_mes_dv_check_plan PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_check_plan.id IS '编号';
COMMENT ON COLUMN mes_dv_check_plan.code IS '方案编码';
COMMENT ON COLUMN mes_dv_check_plan.name IS '方案名称';
COMMENT ON COLUMN mes_dv_check_plan.type IS '巡检类型';
COMMENT ON COLUMN mes_dv_check_plan.start_date IS '开始日期';
COMMENT ON COLUMN mes_dv_check_plan.end_date IS '结束日期';
COMMENT ON COLUMN mes_dv_check_plan.cycle_type IS '循环类型';
COMMENT ON COLUMN mes_dv_check_plan.cycle_count IS '周期数量';
COMMENT ON COLUMN mes_dv_check_plan.status IS '状态';
COMMENT ON COLUMN mes_dv_check_plan.remark IS '备注';
COMMENT ON COLUMN mes_dv_check_plan.creator IS '创建者';
COMMENT ON COLUMN mes_dv_check_plan.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_check_plan.updater IS '更新者';
COMMENT ON COLUMN mes_dv_check_plan.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_check_plan.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_check_plan.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_check_plan IS 'MES 点检保养方案';

-- ----------------------------
-- Records of mes_dv_check_plan
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_check_plan (id, code, name, type, start_date, end_date, cycle_type, cycle_count, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'CHP001', '注塑机日检方案', 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1, 1, 1, '每日检查注塑机关键部位', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:41', '0', 1), (2, 'MTP001', '注塑机月度保养方案', 2, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 3, 1, 0, '每月对注塑机进行全面保养', '1', '2026-02-20 07:11:52', '1', '2026-02-20 15:14:50', '0', 1), (3, 'CHP002', '测试草稿方案', 1, '2024-06-01 00:00:00', '2024-12-31 23:59:59', 2, 1, 0, '草稿状态的方案', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:41', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_check_plan_seq;
CREATE SEQUENCE mes_dv_check_plan_seq
    START 4;

-- ----------------------------
-- Table structure for mes_dv_check_plan_machinery
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_check_plan_machinery;
CREATE TABLE mes_dv_check_plan_machinery (
    id int8 NOT NULL,
  plan_id int8 NOT NULL,
  machinery_id int8 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_check_plan_machinery ADD CONSTRAINT pk_mes_dv_check_plan_machinery PRIMARY KEY (id);

CREATE INDEX idx_mes_dv_check_plan_machinery_01 ON mes_dv_check_plan_machinery (plan_id);

COMMENT ON COLUMN mes_dv_check_plan_machinery.id IS '编号';
COMMENT ON COLUMN mes_dv_check_plan_machinery.plan_id IS '方案编号';
COMMENT ON COLUMN mes_dv_check_plan_machinery.machinery_id IS '设备编号';
COMMENT ON COLUMN mes_dv_check_plan_machinery.remark IS '备注';
COMMENT ON COLUMN mes_dv_check_plan_machinery.creator IS '创建者';
COMMENT ON COLUMN mes_dv_check_plan_machinery.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_check_plan_machinery.updater IS '更新者';
COMMENT ON COLUMN mes_dv_check_plan_machinery.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_check_plan_machinery.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_check_plan_machinery.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_check_plan_machinery IS 'MES 点检保养方案设备';

-- ----------------------------
-- Records of mes_dv_check_plan_machinery
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_check_plan_machinery (id, plan_id, machinery_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:49', '0', 1), (2, 1, 2, '', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:49', '0', 1), (3, 3, 10, 'EEE', '1', '2026-04-03 00:48:35', '1', '2026-04-03 00:48:35', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_check_plan_machinery_seq;
CREATE SEQUENCE mes_dv_check_plan_machinery_seq
    START 4;

-- ----------------------------
-- Table structure for mes_dv_check_plan_subject
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_check_plan_subject;
CREATE TABLE mes_dv_check_plan_subject (
    id int8 NOT NULL,
  plan_id int8 NOT NULL,
  subject_id int8 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_check_plan_subject ADD CONSTRAINT pk_mes_dv_check_plan_subject PRIMARY KEY (id);

CREATE INDEX idx_mes_dv_check_plan_subject_01 ON mes_dv_check_plan_subject (plan_id);

COMMENT ON COLUMN mes_dv_check_plan_subject.id IS '编号';
COMMENT ON COLUMN mes_dv_check_plan_subject.plan_id IS '方案编号';
COMMENT ON COLUMN mes_dv_check_plan_subject.subject_id IS '项目编号';
COMMENT ON COLUMN mes_dv_check_plan_subject.remark IS '备注';
COMMENT ON COLUMN mes_dv_check_plan_subject.creator IS '创建者';
COMMENT ON COLUMN mes_dv_check_plan_subject.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_check_plan_subject.updater IS '更新者';
COMMENT ON COLUMN mes_dv_check_plan_subject.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_check_plan_subject.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_check_plan_subject.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_check_plan_subject IS 'MES 点检保养方案项目';

-- ----------------------------
-- Records of mes_dv_check_plan_subject
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_check_plan_subject (id, plan_id, subject_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:55', '0', 1), (2, 1, 2, '', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:55', '0', 1), (3, 1, 3, '', '1', '2026-02-20 07:11:52', '1', '2026-02-20 07:13:55', '0', 1), (4, 3, 6, '', '1', '2026-02-20 15:23:03', '1', '2026-02-20 15:23:03', '0', 1), (5, 3, 7, 'QQQ', '1', '2026-04-03 00:48:43', '1', '2026-04-03 00:48:43', '0', 1), (6, 2, 7, '', '1', '2026-04-16 01:36:50', '1', '2026-04-16 01:36:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_check_plan_subject_seq;
CREATE SEQUENCE mes_dv_check_plan_subject_seq
    START 7;

-- ----------------------------
-- Table structure for mes_dv_check_record
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_check_record;
CREATE TABLE mes_dv_check_record (
    id int8 NOT NULL,
  plan_id int8 NULL DEFAULT NULL,
  machinery_id int8 NOT NULL,
  check_time timestamp NOT NULL,
  user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 10,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_check_record ADD CONSTRAINT pk_mes_dv_check_record PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_check_record.id IS '主键';
COMMENT ON COLUMN mes_dv_check_record.plan_id IS '点检计划 ID';
COMMENT ON COLUMN mes_dv_check_record.machinery_id IS '设备 ID';
COMMENT ON COLUMN mes_dv_check_record.check_time IS '点检时间';
COMMENT ON COLUMN mes_dv_check_record.user_id IS '点检人 ID';
COMMENT ON COLUMN mes_dv_check_record.status IS '状态';
COMMENT ON COLUMN mes_dv_check_record.remark IS '备注';
COMMENT ON COLUMN mes_dv_check_record.creator IS '创建者';
COMMENT ON COLUMN mes_dv_check_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_check_record.updater IS '更新者';
COMMENT ON COLUMN mes_dv_check_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_check_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_check_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_check_record IS '设备点检记录表';

-- ----------------------------
-- Records of mes_dv_check_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_check_record (id, plan_id, machinery_id, check_time, user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '2025-03-10 08:30:00', 1, 20, '注塑机 M0001 日常点检，全部正常', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:16', '0', 1), (2, 1, 2, '2025-03-11 09:00:00', 1, 20, '数控机床 M0002 日常点检，电气检查异常', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:16', '0', 1), (3, 1, 1, '2025-03-12 08:00:00', 1, 20, '待执行的点检任务', '1', '2026-02-20 09:55:49', '1', '2026-04-03 18:46:20', '0', 1), (4, NULL, 3, '2025-03-13 14:00:00', 1, 20, '冲压机临时点检', '1', '2026-02-20 09:55:49', '1', '2026-04-03 09:58:04', '0', 1), (5, NULL, 2, '2025-03-09 16:00:00', 1, 20, '数控机床临时巡检，润滑系统正常', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:16', '0', 1), (6, 3, 10, '2026-04-16 00:00:00', 1, 20, '', '1', '2026-04-03 08:57:19', '1', '2026-04-03 08:57:42', '0', 1), (7, 2, 10, '2026-04-24 00:00:00', NULL, 10, '', '1', '2026-04-06 19:44:06', '1', '2026-04-06 19:44:06', '0', 1), (8, NULL, 1, '2026-04-11 18:28:00', NULL, 10, '', '', '2026-04-11 18:28:00', '', '2026-04-11 18:28:00', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_check_record_seq;
CREATE SEQUENCE mes_dv_check_record_seq
    START 9;

-- ----------------------------
-- Table structure for mes_dv_check_record_line
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_check_record_line;
CREATE TABLE mes_dv_check_record_line (
    id int8 NOT NULL,
  record_id int8 NOT NULL,
  subject_id int8 NOT NULL,
  check_status int2 NOT NULL DEFAULT 1,
  check_result varchar(500) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_check_record_line ADD CONSTRAINT pk_mes_dv_check_record_line PRIMARY KEY (id);

CREATE INDEX idx_mes_dv_check_record_line_01 ON mes_dv_check_record_line (record_id);

COMMENT ON COLUMN mes_dv_check_record_line.id IS '主键';
COMMENT ON COLUMN mes_dv_check_record_line.record_id IS '点检记录 ID';
COMMENT ON COLUMN mes_dv_check_record_line.subject_id IS '点检项目 ID';
COMMENT ON COLUMN mes_dv_check_record_line.check_status IS '点检结果';
COMMENT ON COLUMN mes_dv_check_record_line.check_result IS '异常描述';
COMMENT ON COLUMN mes_dv_check_record_line.remark IS '备注';
COMMENT ON COLUMN mes_dv_check_record_line.creator IS '创建者';
COMMENT ON COLUMN mes_dv_check_record_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_check_record_line.updater IS '更新者';
COMMENT ON COLUMN mes_dv_check_record_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_check_record_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_check_record_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_check_record_line IS '设备点检记录明细表';

-- ----------------------------
-- Records of mes_dv_check_record_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_check_record_line (id, record_id, subject_id, check_status, check_result, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (2, 1, 2, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (3, 1, 3, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (4, 2, 1, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (5, 2, 2, 2, '电气接线端子有松动，需紧固处理', '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (6, 2, 3, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (7, 3, 1, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (8, 3, 2, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (9, 3, 3, 1, NULL, '', '1', '2026-02-20 09:55:49', '1', '2026-02-20 09:56:36', '0', 1), (10, 4, 8, 1, '', '123', '1', '2026-02-20 17:57:28', '1', '2026-02-20 17:57:28', '0', 1), (11, 6, 6, 1, NULL, '', '1', '2026-04-03 08:57:20', '1', '2026-04-03 08:57:20', '0', 1), (12, 6, 7, 1, NULL, '', '1', '2026-04-03 08:57:20', '1', '2026-04-03 08:57:20', '0', 1), (13, 7, 7, 1, '', '', '1', '2026-04-06 19:51:13', '1', '2026-04-06 19:51:13', '0', 1), (14, 7, 4, 2, 'abc', 'EFC', '1', '2026-04-16 13:27:13', '1', '2026-04-16 13:27:13', '0', 1), (15, 8, 1, 1, 'EEE', '', '1', '2026-05-24 20:25:07', '1', '2026-05-24 20:25:07', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_check_record_line_seq;
CREATE SEQUENCE mes_dv_check_record_line_seq
    START 16;

-- ----------------------------
-- Table structure for mes_dv_machinery
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_machinery;
CREATE TABLE mes_dv_machinery (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  brand varchar(255) NULL DEFAULT NULL,
  specification varchar(255) NULL DEFAULT NULL,
  machinery_type_id int8 NOT NULL,
  workshop_id int8 NOT NULL,
  status int2 NOT NULL DEFAULT 1,
  last_mainten_time timestamp NULL DEFAULT NULL,
  last_check_time timestamp NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_machinery ADD CONSTRAINT pk_mes_dv_machinery PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_machinery.id IS '编号';
COMMENT ON COLUMN mes_dv_machinery.code IS '设备编码';
COMMENT ON COLUMN mes_dv_machinery.name IS '设备名称';
COMMENT ON COLUMN mes_dv_machinery.brand IS '品牌';
COMMENT ON COLUMN mes_dv_machinery.specification IS '规格型号';
COMMENT ON COLUMN mes_dv_machinery.machinery_type_id IS '设备类型编号';
COMMENT ON COLUMN mes_dv_machinery.workshop_id IS '所属车间编号';
COMMENT ON COLUMN mes_dv_machinery.status IS '设备状态';
COMMENT ON COLUMN mes_dv_machinery.last_mainten_time IS '最近保养时间';
COMMENT ON COLUMN mes_dv_machinery.last_check_time IS '最近点检时间';
COMMENT ON COLUMN mes_dv_machinery.remark IS '备注';
COMMENT ON COLUMN mes_dv_machinery.creator IS '创建者';
COMMENT ON COLUMN mes_dv_machinery.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_machinery.updater IS '更新者';
COMMENT ON COLUMN mes_dv_machinery.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_machinery.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_machinery.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_machinery IS 'MES 设备台账';

-- ----------------------------
-- Records of mes_dv_machinery
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_machinery (id, code, name, brand, specification, machinery_type_id, workshop_id, status, last_mainten_time, last_check_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'M0001', '海天注塑机A', '海天', 'MA1600', 2, 1, 1, '2025-01-15 08:00:00', '2025-02-01 09:00:00', '', '1', '2022-08-18 11:01:42', '1', '2026-02-17 02:51:10', '0', 1), (2, 'M0002', '海天注塑机B', '海天', 'MA1600', 2, 1, 1, '2025-01-15 08:00:00', '2025-02-01 09:00:00', '', '1', '2022-08-18 11:02:00', '1', '2026-02-17 02:51:10', '0', 1), (3, 'M0003', '钢筋裁切机', '虎王', 'HW-300', 8, 3, 1, NULL, NULL, '', '1', '2022-08-21 19:42:53', '1', '2026-02-17 02:51:10', '0', 1), (4, 'M0004', '冲压机', '扬锻', 'YD-80T', 9, 3, 3, NULL, NULL, '', '1', '2022-08-22 09:48:52', '1', '2026-02-17 02:51:10', '0', 1), (5, 'M0005', '物料干燥机', '信易', 'SHD-50', 3, 1, 2, NULL, NULL, '', '1', '2022-08-19 14:36:15', '1', '2026-02-17 02:51:10', '0', 1), (6, 'M0006', '自动拧螺丝机A', '快克', 'QK-S200', 5, 2, 1, '2025-01-20 10:00:00', '2025-02-05 14:00:00', '', '1', '2022-08-24 10:00:00', '1', '2026-02-17 02:51:10', '0', 1), (7, 'M0007', '压装机', '先帝达', 'XD-500', 6, 2, 1, NULL, NULL, '', '1', '2022-08-24 10:00:00', '1', '2026-02-17 02:51:10', '0', 1), (8, 'M0008', 'CCD视觉检测台', '基恩士', 'CV-X480F', 11, 2, 1, NULL, '2025-02-05 10:55:58', '', '1', '2025-02-05 10:55:58', '1', '2026-02-17 11:30:16', '0', 1), (9, 'M00001', 'ABCED', NULL, 'EEE', 1, 1, 1, NULL, NULL, 'EEE', '1', '2026-04-02 23:20:15', '1', '2026-04-02 23:20:15', '0', 1), (10, 'M00002', 'AAA', 'EEE', '123213', 2, 1, 1, NULL, NULL, NULL, '1', '2026-04-02 23:37:18', '1', '2026-04-02 23:37:18', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_machinery_seq;
CREATE SEQUENCE mes_dv_machinery_seq
    START 11;

-- ----------------------------
-- Table structure for mes_dv_machinery_type
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_machinery_type;
CREATE TABLE mes_dv_machinery_type (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  parent_id int8 NOT NULL DEFAULT 0,
  status int2 NOT NULL DEFAULT 0,
  sort int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_machinery_type ADD CONSTRAINT pk_mes_dv_machinery_type PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_machinery_type.id IS '编号';
COMMENT ON COLUMN mes_dv_machinery_type.code IS '类型编码';
COMMENT ON COLUMN mes_dv_machinery_type.name IS '类型名称';
COMMENT ON COLUMN mes_dv_machinery_type.parent_id IS '父类型编号';
COMMENT ON COLUMN mes_dv_machinery_type.status IS '状态';
COMMENT ON COLUMN mes_dv_machinery_type.sort IS '显示排序';
COMMENT ON COLUMN mes_dv_machinery_type.remark IS '备注';
COMMENT ON COLUMN mes_dv_machinery_type.creator IS '创建者';
COMMENT ON COLUMN mes_dv_machinery_type.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_machinery_type.updater IS '更新者';
COMMENT ON COLUMN mes_dv_machinery_type.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_machinery_type.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_machinery_type.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_machinery_type IS 'MES 设备类型';

-- ----------------------------
-- Records of mes_dv_machinery_type
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_machinery_type (id, code, name, parent_id, status, sort, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'M_TYPE_001', '注塑设备', 0, 0, 0, '', '1', '2022-05-08 19:26:57', '1', '2026-02-17 02:51:24', '0', 1), (2, 'M_TYPE_002', '注塑机', 1, 0, 0, '', '1', '2022-05-08 19:50:41', '1', '2026-02-17 02:51:24', '0', 1), (3, 'M_TYPE_003', '干燥机', 1, 0, 1, '', '1', '2022-05-08 19:50:57', '1', '2026-02-17 02:51:24', '0', 1), (4, 'M_TYPE_004', '组装设备', 0, 0, 1, '', '1', '2022-05-08 19:51:10', '1', '2026-02-17 02:51:24', '0', 1), (5, 'M_TYPE_005', '自动拧螺丝机', 4, 0, 0, '', '1', '2022-05-08 19:51:25', '1', '2026-02-17 02:51:24', '0', 1), (6, 'M_TYPE_006', '压装机', 4, 0, 1, '', '1', '2022-05-14 13:40:03', '1', '2026-02-17 02:51:24', '0', 1), (7, 'M_TYPE_007', '五金加工设备', 0, 0, 2, 'aaa', '1', '2022-05-14 13:43:59', '1', '2026-02-17 10:52:04', '0', 1), (8, 'M_TYPE_008', '裁切机', 7, 0, 0, '', '1', '2022-05-14 13:44:23', '1', '2026-02-17 02:51:24', '0', 1), (9, 'M_TYPE_009', '冲压机', 7, 0, 1, '', '1', '2022-05-14 13:44:33', '1', '2026-02-17 02:51:24', '0', 1), (10, 'M_TYPE_010', '检测设备', 0, 0, 3, '', '1', '2022-05-14 13:49:13', '1', '2026-02-17 02:51:24', '0', 1), (11, 'M_TYPE_011', 'CCD检测台', 10, 0, 0, '', '1', '2022-05-14 13:49:25', '1', '2026-02-17 02:51:24', '0', 1), (12, 'MT008', 'xxx', 1, 0, 0, '', '1', '2026-05-24 20:24:11', '1', '2026-05-24 20:24:11', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_machinery_type_seq;
CREATE SEQUENCE mes_dv_machinery_type_seq
    START 13;

-- ----------------------------
-- Table structure for mes_dv_mainten_record
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_mainten_record;
CREATE TABLE mes_dv_mainten_record (
    id int8 NOT NULL,
  plan_id int8 NULL DEFAULT NULL,
  machinery_id int8 NOT NULL,
  mainten_time timestamp NOT NULL,
  user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_mainten_record ADD CONSTRAINT pk_mes_dv_mainten_record PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_mainten_record.id IS '主键';
COMMENT ON COLUMN mes_dv_mainten_record.plan_id IS '计划ID';
COMMENT ON COLUMN mes_dv_mainten_record.machinery_id IS '设备ID';
COMMENT ON COLUMN mes_dv_mainten_record.mainten_time IS '保养时间';
COMMENT ON COLUMN mes_dv_mainten_record.user_id IS '用户ID';
COMMENT ON COLUMN mes_dv_mainten_record.status IS '状态';
COMMENT ON COLUMN mes_dv_mainten_record.remark IS '备注';
COMMENT ON COLUMN mes_dv_mainten_record.creator IS '创建者';
COMMENT ON COLUMN mes_dv_mainten_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_mainten_record.updater IS '更新者';
COMMENT ON COLUMN mes_dv_mainten_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_mainten_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_mainten_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_mainten_record IS '设备保养记录表';

-- ----------------------------
-- Records of mes_dv_mainten_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_mainten_record (id, plan_id, machinery_id, mainten_time, user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '2026-01-15 09:00:00', 1, 0, 'CNC加工中心A 1月份保养', '1', '2026-01-15 09:30:00', '1', '2026-04-16 05:32:37', '0', 1), (2, 1, 2, '2026-01-15 14:00:00', 1, 0, 'CNC加工中心B 1月份保养', '1', '2026-01-15 14:30:00', '1', '2026-04-16 05:32:37', '0', 1), (3, 2, 4, '2026-01-20 08:30:00', 1, 0, '注塑机D Q1保养', '1', '2026-01-20 09:00:00', '1', '2026-04-16 05:32:37', '0', 1), (4, 1, 1, '2026-02-15 09:00:00', 1, 4, 'CNC加工中心A 2月份保养（草稿）', '1', '2026-02-15 09:30:00', '1', '2026-04-16 05:32:37', '0', 1), (5, 1, 3, '2026-02-16 10:00:00', 1, 4, '数控车床C 2月份保养（草稿）', '1', '2026-02-16 10:30:00', '1', '2026-04-16 05:32:37', '0', 1), (6, NULL, 5, '2026-02-18 13:00:00', 1, 4, '冲压机E 临时保养（无计划）', '1', '2026-02-18 13:30:00', '1', '2026-04-16 05:32:37', '0', 1), (7, 3, 10, '2026-04-09 00:00:00', 1, 0, '', '1', '2026-04-03 21:45:05', '1', '2026-04-03 22:50:16', '1', 1), (8, 3, 9, '2026-04-11 00:00:00', 1, 0, '', '1', '2026-04-03 22:50:26', '1', '2026-04-03 22:50:26', '0', 1), (9, NULL, 1, '2026-04-11 18:28:00', NULL, 0, '', '', '2026-04-11 18:28:00', '', '2026-04-16 05:32:37', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_mainten_record_seq;
CREATE SEQUENCE mes_dv_mainten_record_seq
    START 10;

-- ----------------------------
-- Table structure for mes_dv_mainten_record_line
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_mainten_record_line;
CREATE TABLE mes_dv_mainten_record_line (
    id int8 NOT NULL,
  record_id int8 NOT NULL,
  subject_id int8 NOT NULL,
  status int2 NOT NULL,
  result varchar(500) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_mainten_record_line ADD CONSTRAINT pk_mes_dv_mainten_record_line PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_mainten_record_line.id IS '主键';
COMMENT ON COLUMN mes_dv_mainten_record_line.record_id IS '保养记录ID';
COMMENT ON COLUMN mes_dv_mainten_record_line.subject_id IS '项目ID';
COMMENT ON COLUMN mes_dv_mainten_record_line.status IS '保养结果';
COMMENT ON COLUMN mes_dv_mainten_record_line.result IS '异常描述';
COMMENT ON COLUMN mes_dv_mainten_record_line.remark IS '备注';
COMMENT ON COLUMN mes_dv_mainten_record_line.creator IS '创建者';
COMMENT ON COLUMN mes_dv_mainten_record_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_mainten_record_line.updater IS '更新者';
COMMENT ON COLUMN mes_dv_mainten_record_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_mainten_record_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_mainten_record_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_mainten_record_line IS '设备保养记录明细表';

-- ----------------------------
-- Records of mes_dv_mainten_record_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_mainten_record_line (id, record_id, subject_id, status, result, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, NULL, NULL, '1', '2026-01-15 09:10:00', '1', '2026-02-20 07:45:46', '0', 1), (2, 1, 2, 1, NULL, NULL, '1', '2026-01-15 09:15:00', '1', '2026-02-20 07:45:46', '0', 1), (3, 1, 3, 1, NULL, NULL, '1', '2026-01-15 09:20:00', '1', '2026-02-20 07:45:46', '0', 1), (4, 1, 4, 2, '主轴跳动0.008mm，超标', '需安排维修', '1', '2026-01-15 09:25:00', '1', '2026-02-20 07:45:46', '0', 1), (5, 2, 1, 1, NULL, NULL, '1', '2026-01-15 14:10:00', '1', '2026-02-20 07:45:46', '0', 1), (6, 2, 2, 1, NULL, NULL, '1', '2026-01-15 14:15:00', '1', '2026-02-20 07:45:46', '0', 1), (7, 2, 3, 1, NULL, NULL, '1', '2026-01-15 14:20:00', '1', '2026-02-20 07:45:46', '0', 1), (8, 2, 4, 1, NULL, NULL, '1', '2026-01-15 14:25:00', '1', '2026-02-20 07:45:46', '0', 1), (9, 3, 5, 1, NULL, NULL, '1', '2026-01-20 08:40:00', '1', '2026-02-20 07:45:46', '0', 1), (10, 3, 6, 2, '液压管路轻微渗油', '已临时处理，下次更换密封圈', '1', '2026-01-20 08:50:00', '1', '2026-02-20 07:45:46', '0', 1), (11, 4, 1, 1, NULL, NULL, '1', '2026-02-15 09:10:00', '1', '2026-02-20 07:45:46', '0', 1), (12, 4, 2, 1, NULL, NULL, '1', '2026-02-15 09:15:00', '1', '2026-02-20 07:45:46', '0', 1), (13, 7, 8, 1, 'EEEE', 'abc', '1', '2026-04-03 21:47:46', '1', '2026-04-03 14:50:16', '1', 1), (14, 9, 7, 1, '', '', '1', '2026-04-16 17:14:38', '1', '2026-04-16 17:14:38', '0', 1), (15, 9, 4, 0, 'abc', 'efs', '1', '2026-04-16 17:14:47', '1', '2026-04-16 17:14:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_mainten_record_line_seq;
CREATE SEQUENCE mes_dv_mainten_record_line_seq
    START 16;

-- ----------------------------
-- Table structure for mes_dv_repair
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_repair;
CREATE TABLE mes_dv_repair (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  machinery_id int8 NOT NULL,
  require_date timestamp NULL DEFAULT NULL,
  finish_date timestamp NULL DEFAULT NULL,
  confirm_date timestamp NULL DEFAULT NULL,
  result int2 NULL DEFAULT NULL,
  accepted_user_id int8 NULL DEFAULT NULL,
  confirm_user_id int8 NULL DEFAULT NULL,
  source_doc_type int2 NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_repair ADD CONSTRAINT pk_mes_dv_repair PRIMARY KEY (id);

CREATE INDEX idx_mes_dv_repair_01 ON mes_dv_repair (machinery_id);
CREATE INDEX idx_mes_dv_repair_02 ON mes_dv_repair (status);

COMMENT ON COLUMN mes_dv_repair.id IS '编号';
COMMENT ON COLUMN mes_dv_repair.code IS '维修工单编码';
COMMENT ON COLUMN mes_dv_repair.name IS '维修工单名称';
COMMENT ON COLUMN mes_dv_repair.machinery_id IS '设备编号（关联 mes_dv_machinery.id）';
COMMENT ON COLUMN mes_dv_repair.require_date IS '报修日期';
COMMENT ON COLUMN mes_dv_repair.finish_date IS '维修完成日期';
COMMENT ON COLUMN mes_dv_repair.confirm_date IS '验收日期';
COMMENT ON COLUMN mes_dv_repair.result IS '维修结果';
COMMENT ON COLUMN mes_dv_repair.accepted_user_id IS '维修人用户编号（关联 system_users.id）';
COMMENT ON COLUMN mes_dv_repair.confirm_user_id IS '验收人用户编号（关联 system_users.id）';
COMMENT ON COLUMN mes_dv_repair.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_dv_repair.source_doc_id IS '来源单据编号';
COMMENT ON COLUMN mes_dv_repair.source_doc_code IS '来源单据编码';
COMMENT ON COLUMN mes_dv_repair.status IS '状态';
COMMENT ON COLUMN mes_dv_repair.remark IS '备注';
COMMENT ON COLUMN mes_dv_repair.creator IS '创建者';
COMMENT ON COLUMN mes_dv_repair.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_repair.updater IS '更新者';
COMMENT ON COLUMN mes_dv_repair.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_repair.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_repair.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_repair IS 'MES 维修工单';

-- ----------------------------
-- Records of mes_dv_repair
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_repair (id, code, name, machinery_id, require_date, finish_date, confirm_date, result, accepted_user_id, confirm_user_id, source_doc_type, source_doc_id, source_doc_code, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'REP2024001', '注塑机液压系统漏油维修', 1, '2024-03-10 18:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '液压系统发现漏油，需紧急处理', '1', '2026-02-20 10:56:40', '1', '2026-04-16 05:00:14', '0', 1), (2, 'REP2024002', '数控机床主轴异常噪音排查', 2, '2024-03-12 08:00:00', '2024-03-14 16:30:00', NULL, NULL, 1, NULL, NULL, NULL, NULL, 2, '主轴轴承磨损，已更换轴承', '1', '2026-02-20 10:56:40', '1', '2026-04-16 05:00:14', '0', 1), (3, 'REP2024003', '注塑机控制面板故障维修', 1, '2024-02-20 08:00:00', '2024-02-22 14:00:00', '2024-02-23 10:00:00', 1, 1, 1, NULL, NULL, NULL, 4, '控制面板主板已更换，验收通过', '1', '2026-02-20 10:56:40', '1', '2026-04-16 05:00:14', '0', 1), (4, 'BX20260404000002', 'AAB', 10, '2026-04-03 00:00:00', '2026-04-04 00:45:04', '2026-04-04 00:45:09', 1, 1, 1, NULL, NULL, NULL, 4, '', '1', '2026-04-04 00:43:47', '1', '2026-04-04 00:45:09', '0', 1), (5, 'BX20260404000003', 'EEAA', 7, '2026-04-29 00:00:00', '2026-04-06 00:00:00', NULL, NULL, 1, NULL, NULL, NULL, NULL, 2, '', '1', '2026-04-04 00:51:04', '1', '2026-04-04 01:06:37', '0', 1), (6, 'BX20260404000004', 'abc', 10, '2026-04-22 00:00:00', '2026-04-07 00:00:00', '2026-04-04 01:06:31', 1, 1, 1, NULL, NULL, NULL, 4, '', '1', '2026-04-04 00:58:17', '1', '2026-04-04 01:06:31', '0', 1), (7, 'BX20260404000005', 'AAAA', 10, '2026-04-08 00:00:00', NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 1, '', '1', '2026-04-04 01:09:14', '1', '2026-04-04 01:09:18', '0', 1), (8, 'BX20260416000004', 'EEE', 5, '2026-04-15 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '', '1', '2026-04-16 09:27:12', '1', '2026-04-16 09:27:12', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_repair_seq;
CREATE SEQUENCE mes_dv_repair_seq
    START 9;

-- ----------------------------
-- Table structure for mes_dv_repair_line
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_repair_line;
CREATE TABLE mes_dv_repair_line (
    id int8 NOT NULL,
  repair_id int8 NOT NULL,
  subject_id int8 NULL DEFAULT NULL,
  malfunction varchar(500) NOT NULL,
  malfunction_url varchar(1000) NULL DEFAULT NULL,
  description varchar(500) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_repair_line ADD CONSTRAINT pk_mes_dv_repair_line PRIMARY KEY (id);

CREATE INDEX idx_mes_dv_repair_line_01 ON mes_dv_repair_line (repair_id);

COMMENT ON COLUMN mes_dv_repair_line.id IS '编号';
COMMENT ON COLUMN mes_dv_repair_line.repair_id IS '维修工单编号（关联 mes_dv_repair.id）';
COMMENT ON COLUMN mes_dv_repair_line.subject_id IS '点检保养项目编号（关联 mes_dv_subject.id）';
COMMENT ON COLUMN mes_dv_repair_line.malfunction IS '故障描述';
COMMENT ON COLUMN mes_dv_repair_line.malfunction_url IS '故障图片 URL';
COMMENT ON COLUMN mes_dv_repair_line.description IS '维修描述';
COMMENT ON COLUMN mes_dv_repair_line.remark IS '备注';
COMMENT ON COLUMN mes_dv_repair_line.creator IS '创建者';
COMMENT ON COLUMN mes_dv_repair_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_repair_line.updater IS '更新者';
COMMENT ON COLUMN mes_dv_repair_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_repair_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_repair_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_repair_line IS 'MES 维修工单行';

-- ----------------------------
-- Records of mes_dv_repair_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_repair_line (id, repair_id, subject_id, malfunction, malfunction_url, description, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, '液压缸密封圈老化，出现渗漏油现象', NULL, NULL, '', '1', '2026-02-20 10:56:40', '1', '2026-02-20 11:17:46', '0', 1), (2, 2, NULL, '主轴运行时有明显异响，振动值超标', NULL, '更换 6208 型号轴承一套，润滑脂重新填充', '', '1', '2026-02-20 10:56:40', '1', '2026-02-20 11:17:46', '0', 1), (3, 2, NULL, '主轴端面跳动量超出公差范围', NULL, '重新校准主轴，调整安装精度至合格', '', '1', '2026-02-20 10:56:40', '1', '2026-02-20 11:17:46', '0', 1), (4, 3, 2, '操作面板触摸屏无响应，控制板指示灯异常', NULL, '更换主控板 PCB，重新烧录固件并测试通过', '', '1', '2026-02-20 10:56:40', '1', '2026-02-20 11:17:46', '0', 1), (5, 8, 7, 'EEE', '', '', '', '1', '2026-04-16 17:18:38', '1', '2026-04-16 17:18:38', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_repair_line_seq;
CREATE SEQUENCE mes_dv_repair_line_seq
    START 6;

-- ----------------------------
-- Table structure for mes_dv_subject
-- ----------------------------
DROP TABLE IF EXISTS mes_dv_subject;
CREATE TABLE mes_dv_subject (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  type int2 NOT NULL,
  content varchar(500) NOT NULL,
  standard varchar(255) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_dv_subject ADD CONSTRAINT pk_mes_dv_subject PRIMARY KEY (id);

COMMENT ON COLUMN mes_dv_subject.id IS '编号';
COMMENT ON COLUMN mes_dv_subject.code IS '项目编码';
COMMENT ON COLUMN mes_dv_subject.name IS '项目名称';
COMMENT ON COLUMN mes_dv_subject.type IS '项目类型';
COMMENT ON COLUMN mes_dv_subject.content IS '项目内容';
COMMENT ON COLUMN mes_dv_subject.standard IS '标准';
COMMENT ON COLUMN mes_dv_subject.status IS '状态';
COMMENT ON COLUMN mes_dv_subject.remark IS '备注';
COMMENT ON COLUMN mes_dv_subject.creator IS '创建者';
COMMENT ON COLUMN mes_dv_subject.create_time IS '创建时间';
COMMENT ON COLUMN mes_dv_subject.updater IS '更新者';
COMMENT ON COLUMN mes_dv_subject.update_time IS '更新时间';
COMMENT ON COLUMN mes_dv_subject.deleted IS '是否删除';
COMMENT ON COLUMN mes_dv_subject.tenant_id IS '租户编号';
COMMENT ON TABLE mes_dv_subject IS 'MES 点检保养项目';

-- ----------------------------
-- Records of mes_dv_subject
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_dv_subject (id, code, name, type, content, standard, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'CHK001', '注塑机外观检查', 1, '检查注塑机外壳是否有裂纹、变形、漏油等异常', '外观完好，无明显损伤', 0, '', '1', '2022-06-16 20:30:00', '1', '2026-02-20 01:49:32', '0', 1), (2, 'CHK002', '电气线路点检', 1, '检查设备电气线路连接是否牢固，有无破损老化', '线路连接正常，无裸露铜线', 0, '', '1', '2022-06-16 20:31:00', '1', '2026-02-20 01:49:32', '0', 1), (3, 'CHK003', '安全防护装置检查', 1, '检查设备安全门、急停按钮、防护罩等安全装置是否正常', '安全装置功能正常，无失效', 0, '', '1', '2022-06-16 20:32:00', '1', '2026-02-20 01:49:32', '0', 1), (4, 'CHK004', '润滑系统点检', 1, '检查润滑油位、油质、油路是否通畅', '油位在标准范围内，油质清澈', 0, '', '1', '2022-06-16 20:33:00', '1', '2026-02-20 01:49:32', '0', 1), (5, 'MNT001', '注塑机润滑保养', 2, '对注塑机各润滑点进行加油保养，更换润滑油', '按照设备手册规定的润滑周期和油品进行保养', 0, '', '1', '2022-06-16 20:34:00', '1', '2026-02-20 01:49:32', '0', 1), (6, 'MNT002', '液压系统保养', 2, '清洗液压过滤器，检查并补充液压油，检测系统压力', '液压油清洁度达标，系统压力在正常范围', 0, '', '1', '2022-06-16 20:35:00', '1', '2026-02-20 01:49:32', '0', 1), (7, 'MNT003', '电气系统保养', 2, '清洁电控柜，紧固接线端子，检查散热风扇', '电控柜内无灰尘积累，接线端子紧固', 0, '', '1', '2022-06-16 20:36:00', '1', '2026-02-20 01:49:32', '0', 1), (8, 'CHK005', '温度检测点检', 1, '检测设备关键部位温度是否在正常范围', '温度不超过设备规定的最高值', 1, '已停用', '1', '2022-06-16 20:37:00', '1', '2026-02-20 01:49:32', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_dv_subject_seq;
CREATE SEQUENCE mes_dv_subject_seq
    START 9;

-- ----------------------------
-- Table structure for mes_md_auto_code_part
-- ----------------------------
DROP TABLE IF EXISTS mes_md_auto_code_part;
CREATE TABLE mes_md_auto_code_part (
    id int8 NOT NULL,
  rule_id int8 NOT NULL,
  sort int4 NOT NULL,
  type int2 NOT NULL,
  length int4 NOT NULL,
  date_format varchar(20) NULL DEFAULT NULL,
  fix_character varchar(64) NULL DEFAULT NULL,
  serial_start_no int4 NULL DEFAULT NULL,
  serial_step int4 NULL DEFAULT NULL,
  cycle_flag bool NULL DEFAULT '0',
  cycle_method int2 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_auto_code_part ADD CONSTRAINT pk_mes_md_auto_code_part PRIMARY KEY (id);

CREATE INDEX idx_mes_md_auto_code_part_01 ON mes_md_auto_code_part (rule_id);

COMMENT ON COLUMN mes_md_auto_code_part.id IS '分段 ID';
COMMENT ON COLUMN mes_md_auto_code_part.rule_id IS '规则 ID';
COMMENT ON COLUMN mes_md_auto_code_part.sort IS '分段序号';
COMMENT ON COLUMN mes_md_auto_code_part.type IS '分段类型';
COMMENT ON COLUMN mes_md_auto_code_part.length IS '分段长度';
COMMENT ON COLUMN mes_md_auto_code_part.date_format IS '日期格式（当 type=2 时使用）';
COMMENT ON COLUMN mes_md_auto_code_part.fix_character IS '固定字符（当 type=3 时使用）';
COMMENT ON COLUMN mes_md_auto_code_part.serial_start_no IS '流水号起始值（当 type=4 时使用）';
COMMENT ON COLUMN mes_md_auto_code_part.serial_step IS '流水号步长（当 type=4 时使用）';
COMMENT ON COLUMN mes_md_auto_code_part.cycle_flag IS '流水号是否循环（当 type=4 时使用）';
COMMENT ON COLUMN mes_md_auto_code_part.cycle_method IS '循环方式';
COMMENT ON COLUMN mes_md_auto_code_part.remark IS '备注';
COMMENT ON COLUMN mes_md_auto_code_part.creator IS '创建者';
COMMENT ON COLUMN mes_md_auto_code_part.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_auto_code_part.updater IS '更新者';
COMMENT ON COLUMN mes_md_auto_code_part.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_auto_code_part.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_auto_code_part.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_auto_code_part IS 'MES 编码规则组成表';

-- ----------------------------
-- Records of mes_md_auto_code_part
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_auto_code_part (id, rule_id, sort, type, length, date_format, fix_character, serial_start_no, serial_step, cycle_flag, cycle_method, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 2, 2, 1, 1, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-03-05 01:06:00', '1', '2026-03-05 01:06:00', '0', 1), (2, 2, 4, 2, 2, 'yyyy', NULL, 1, 1, '0', NULL, '231', '1', '2026-03-05 01:06:10', '1', '2026-03-05 01:06:10', '0', 1), (3, 2, 5, 3, 50, NULL, '1232', 1, 1, '0', NULL, NULL, '1', '2026-03-05 01:06:21', '1', '2026-03-05 01:06:21', '0', 1), (4, 2, 5, 4, 2, NULL, NULL, 3, 4, '1', 1, '1000', '1', '2026-03-05 01:06:34', '1', '2026-03-05 01:06:34', '0', 1), (5, 2, 10, 3, 3, NULL, '22', 1, 1, '0', NULL, '3', '1', '2026-03-05 01:15:16', '1', '2026-03-05 01:15:16', '0', 1), (6, 2, 1, 3, 10, 'yyyy', '32', NULL, NULL, '0', NULL, '3222', '1', '2026-03-05 01:17:23', '1', '2026-03-05 01:23:09', '1', 1), (7, 3, 1, 3, 2, NULL, 'SN', NULL, NULL, '0', NULL, NULL, '1', '2026-03-05 01:29:24', '1', '2026-03-05 01:29:24', '0', 1), (8, 3, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-05 01:29:24', '1', '2026-03-05 01:29:24', '0', 1), (9, 3, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-05 01:29:24', '1', '2026-03-05 01:29:24', '0', 1), (10, 4, 1, 3, 3, NULL, 'PKG', NULL, NULL, '0', NULL, NULL, '1', '2026-03-08 04:44:00', '1', '2026-03-08 04:44:00', '0', 1), (11, 4, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-08 04:44:00', '1', '2026-03-08 04:44:00', '0', 1), (12, 4, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-08 04:44:00', '1', '2026-03-08 04:44:00', '0', 1), (15, 12, 1, 3, 2, NULL, 'PC', NULL, NULL, '0', NULL, '固定字符PC', '1', '2026-03-13 15:36:41', '1', '2026-03-13 15:36:41', '0', 1), (16, 12, 2, 1, 6, NULL, NULL, 1, 1, '1', NULL, '6位流水号', '1', '2026-03-13 15:36:41', '1', '2026-03-13 23:54:38', '1', 1), (17, 12, 2, 2, 5, 'yyyyMMddHHmm', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-13 23:54:46', '1', '2026-03-13 23:54:46', '0', 1), (18, 13, 1, 3, 4, NULL, 'TASK', NULL, NULL, '0', NULL, NULL, '1', '2026-03-15 14:42:59', '1', '2026-03-15 14:42:59', '0', 1), (19, 13, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-15 14:42:59', '1', '2026-03-15 14:42:59', '0', 1), (20, 13, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-15 14:42:59', '1', '2026-03-15 14:42:59', '0', 1), (21, 12, 3, 4, 4, NULL, NULL, 1, 1, '1', 1, NULL, '1', '2026-03-21 07:07:29', '1', '2026-03-21 07:07:29', '0', 1), (22, 14, 1, 3, 3, NULL, 'IQC', NULL, NULL, '0', NULL, NULL, '', '2026-03-23 14:52:48', '', '2026-03-23 14:52:48', '0', 1), (23, 14, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '', '2026-03-23 14:52:48', '', '2026-03-23 14:52:48', '0', 1), (24, 14, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '', '2026-03-23 14:52:48', '', '2026-03-23 15:03:11', '0', 1), (25, 15, 1, 3, 4, NULL, 'IPQC', NULL, NULL, '0', NULL, NULL, '1', '2026-03-24 13:30:06', '1', '2026-03-24 13:30:06', '0', 1), (26, 15, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-24 13:30:06', '1', '2026-03-24 13:30:06', '0', 1), (27, 15, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-24 13:30:06', '1', '2026-03-24 13:30:06', '0', 1), (28, 16, 1, 3, 3, NULL, 'RQC', NULL, NULL, '0', NULL, NULL, '1', '2026-03-26 05:09:58', '1', '2026-03-26 05:09:58', '0', 1), (29, 16, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-26 05:09:58', '1', '2026-03-26 05:09:58', '0', 1), (30, 16, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-26 05:09:58', '1', '2026-03-26 05:09:58', '0', 1), (31, 17, 1, 3, 3, NULL, 'OQC', NULL, NULL, '0', NULL, NULL, '1', '2026-03-27 09:00:03', '1', '2026-03-27 09:00:03', '0', 1), (32, 17, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-27 09:00:03', '1', '2026-03-27 09:00:03', '0', 1), (33, 17, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-27 09:00:03', '1', '2026-03-27 09:00:03', '0', 1), (34, 18, 1, 3, 4, NULL, 'ITEM', NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 00:56:57', '1', '2026-03-28 00:56:57', '0', 1), (35, 18, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 00:56:57', '1', '2026-03-28 00:56:57', '0', 1), (36, 18, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-28 00:56:57', '1', '2026-03-28 00:56:57', '0', 1), (37, 19, 1, 3, 1, NULL, 'C', NULL, NULL, '0', NULL, NULL, 'admin', '2026-03-28 03:41:30', 'admin', '2026-03-28 03:41:30', '0', 1), (38, 19, 2, 4, 5, NULL, NULL, 1, 1, '0', NULL, NULL, 'admin', '2026-03-28 03:41:30', 'admin', '2026-03-28 03:41:30', '0', 1), (39, 20, 1, 3, 2, NULL, 'WS', NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 09:41:24', '1', '2026-03-28 09:41:24', '0', 1), (40, 20, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 09:41:24', '1', '2026-03-28 09:41:24', '0', 1), (41, 20, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-28 09:41:24', '1', '2026-03-28 09:41:24', '0', 1), (42, 21, 1, 3, 2, NULL, 'WH', NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (43, 21, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (44, 21, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (45, 22, 1, 3, 2, NULL, 'LC', NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (46, 22, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (47, 22, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (48, 23, 1, 3, 2, NULL, 'AR', NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (49, 23, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (50, 23, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (51, 24, 1, 3, 2, NULL, 'TT', NULL, NULL, '0', NULL, NULL, 'admin', '2026-03-29 01:27:14', 'admin', '2026-03-29 01:27:14', '0', 1), (52, 24, 2, 4, 3, NULL, NULL, 1, 1, '0', NULL, NULL, 'admin', '2026-03-29 01:27:14', 'admin', '2026-03-29 01:27:14', '0', 1), (53, 25, 1, 3, 2, NULL, 'TL', NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 01:56:00', '1', '2026-03-29 01:56:00', '0', 1), (54, 25, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 01:56:00', '1', '2026-03-29 01:56:00', '0', 1), (55, 25, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-29 01:56:00', '1', '2026-03-29 01:56:00', '0', 1), (56, 26, 1, 3, 2, NULL, 'IR', NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 02:48:40', '1', '2026-03-29 02:48:40', '0', 1), (57, 26, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 02:48:40', '1', '2026-03-29 02:48:40', '0', 1), (58, 26, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-29 02:48:40', '1', '2026-03-29 02:48:40', '0', 1), (59, 27, 1, 3, 2, NULL, 'AN', NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 11:10:49', '1', '2026-03-29 11:10:49', '0', 1), (60, 27, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 11:10:49', '1', '2026-03-29 11:10:49', '0', 1), (61, 27, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-29 11:10:49', '1', '2026-03-29 11:10:49', '0', 1), (62, 28, 1, 3, 2, NULL, 'RV', NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 13:01:34', '1', '2026-03-29 13:01:34', '0', 1), (63, 28, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-29 13:01:34', '1', '2026-03-29 13:01:34', '0', 1), (64, 28, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-29 13:01:34', '1', '2026-03-29 13:01:34', '0', 1), (65, 29, 1, 3, 2, NULL, 'PI', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 01:44:21', '1', '2026-03-30 01:44:21', '0', 1), (66, 29, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 01:44:21', '1', '2026-03-30 01:44:21', '0', 1), (67, 29, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 01:44:21', '1', '2026-03-30 01:44:21', '0', 1), (68, 30, 1, 3, 2, NULL, 'RI', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 03:49:52', '1', '2026-03-30 03:49:52', '0', 1), (69, 30, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 03:49:52', '1', '2026-03-30 03:49:52', '0', 1), (70, 30, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 03:49:52', '1', '2026-03-30 03:49:52', '0', 1), (71, 31, 1, 3, 2, NULL, 'PR', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 04:35:05', '1', '2026-03-30 04:35:05', '0', 1), (72, 31, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 04:35:05', '1', '2026-03-30 04:35:05', '0', 1), (73, 31, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 04:35:05', '1', '2026-03-30 04:35:05', '0', 1), (74, 32, 1, 3, 2, NULL, 'SN', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 05:05:32', '1', '2026-03-30 05:05:32', '0', 1), (75, 32, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 05:05:32', '1', '2026-03-30 05:05:32', '0', 1), (76, 32, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 05:05:32', '1', '2026-03-30 05:05:32', '0', 1), (77, 34, 1, 3, 2, NULL, 'RS', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 11:11:15', '1', '2026-03-30 11:11:15', '0', 1), (78, 34, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 11:11:15', '1', '2026-03-30 11:11:15', '0', 1), (79, 34, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 11:11:15', '1', '2026-03-30 11:11:15', '0', 1), (80, 35, 1, 3, 2, NULL, 'PS', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 12:04:29', '1', '2026-03-30 12:04:29', '0', 1), (81, 35, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 12:04:29', '1', '2026-03-30 12:04:29', '0', 1), (82, 35, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 12:04:29', '1', '2026-03-30 12:04:29', '0', 1), (83, 36, 1, 3, 5, NULL, 'MISCI', NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 15:19:37', '1', '2026-03-30 15:19:37', '0', 1), (84, 36, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-30 15:19:37', '1', '2026-03-30 15:19:37', '0', 1), (85, 36, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-30 15:19:37', '1', '2026-03-30 15:19:37', '0', 1), (86, 37, 1, 3, 5, NULL, 'MISCR', NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 01:49:05', '1', '2026-03-31 01:49:05', '0', 1), (87, 37, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 01:49:13', '1', '2026-03-31 01:49:13', '0', 1), (88, 37, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-31 01:49:21', '1', '2026-03-31 01:49:21', '0', 1), (89, 38, 1, 3, 2, NULL, 'TR', NULL, NULL, '0', NULL, NULL, '', '2026-03-31 07:57:32', '', '2026-03-31 07:57:32', '0', 1), (90, 38, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '', '2026-03-31 07:57:32', '', '2026-03-31 07:57:32', '0', 1), (91, 38, 3, 4, 3, NULL, NULL, 1, 1, '1', 3, NULL, '', '2026-03-31 07:57:32', '', '2026-03-31 07:57:32', '0', 1), (92, 43, 1, 3, 3, NULL, 'PDP', NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (93, 43, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (94, 43, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (95, 44, 1, 3, 3, NULL, 'PDT', NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (96, 44, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (97, 44, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (98, 45, 1, 3, 3, NULL, 'OSI', NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 14:39:35', '1', '2026-03-31 14:39:35', '0', 1), (99, 45, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-03-31 14:39:35', '1', '2026-03-31 14:39:35', '0', 1), (100, 45, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-03-31 14:39:35', '1', '2026-03-31 14:39:35', '0', 1), (101, 46, 1, 3, 2, NULL, 'PP', NULL, NULL, '0', NULL, NULL, '1', '2026-04-01 17:25:54', '1', '2026-04-01 17:25:54', '0', 1), (102, 46, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-01 17:25:54', '1', '2026-04-01 17:25:54', '0', 1), (103, 46, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-01 17:25:54', '1', '2026-04-01 17:25:54', '0', 1), (106, 48, 1, 3, 1, NULL, 'M', NULL, NULL, '0', NULL, NULL, '1', '2026-04-02 14:31:05', '1', '2026-04-02 14:31:05', '0', 1), (107, 48, 2, 4, 5, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-04-02 14:31:05', '1', '2026-04-02 14:31:05', '0', 1), (108, 49, 1, 3, 3, NULL, 'CHP', NULL, NULL, '0', NULL, NULL, '1', '2026-04-02 16:54:57', '1', '2026-04-02 16:54:57', '0', 1), (109, 49, 2, 4, 5, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-04-02 16:54:57', '1', '2026-04-02 16:54:57', '0', 1), (110, 50, 1, 3, 2, NULL, 'BX', NULL, NULL, '0', NULL, NULL, '1', '2026-04-03 16:42:51', '1', '2026-04-03 16:42:51', '0', 1), (111, 50, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-03 16:42:51', '1', '2026-04-03 16:42:51', '0', 1), (112, 50, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-03 16:42:51', '1', '2026-04-03 16:42:51', '0', 1), (113, 54, 1, 3, 2, NULL, 'WO', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 02:34:56', '', '2026-04-04 02:34:56', '0', 1), (114, 54, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 02:34:56', '', '2026-04-04 02:34:56', '0', 1), (115, 54, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-04 02:34:56', '', '2026-04-04 02:34:56', '0', 1), (116, 55, 1, 3, 4, NULL, 'PROC', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 03:32:16', '1', '2026-04-04 03:32:16', '0', 1), (117, 55, 2, 4, 6, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-04-04 03:32:16', '1', '2026-04-04 03:32:16', '0', 1), (118, 56, 1, 3, 2, NULL, 'PR', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 08:37:12', '1', '2026-04-04 08:37:12', '0', 1), (119, 56, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 08:37:12', '1', '2026-04-04 08:37:12', '0', 1), (120, 56, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-04 08:37:12', '1', '2026-04-04 08:37:12', '0', 1), (121, 57, 1, 3, 2, NULL, 'FB', NULL, NULL, '0', NULL, '固定前缀FB', '1', '2026-04-04 09:47:29', '1', '2026-04-04 09:47:29', '0', 1), (122, 57, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, '日期部分', '1', '2026-04-04 09:47:29', '1', '2026-04-04 09:47:29', '0', 1), (123, 57, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, '流水号，按天循环', '1', '2026-04-04 09:47:29', '1', '2026-04-04 09:47:29', '0', 1), (124, 58, 1, 3, 4, NULL, 'CARD', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 12:18:15', '1', '2026-04-04 12:18:15', '0', 1), (125, 58, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 12:18:15', '1', '2026-04-04 12:18:15', '0', 1), (126, 58, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-04 12:18:15', '1', '2026-04-04 12:18:15', '0', 1), (127, 59, 1, 3, 6, NULL, 'DEFECT', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 12:47:23', '1', '2026-04-04 12:47:23', '0', 1), (128, 59, 2, 4, 8, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-04-04 12:47:23', '1', '2026-04-04 12:47:23', '0', 1), (129, 60, 1, 3, 3, NULL, 'QCT', NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 14:36:43', '1', '2026-04-04 14:36:43', '0', 1), (130, 60, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, NULL, '1', '2026-04-04 14:36:43', '1', '2026-04-04 14:36:43', '0', 1), (131, 60, 3, 4, 6, NULL, NULL, 1, 1, '1', 3, NULL, '1', '2026-04-04 14:36:43', '1', '2026-04-04 14:36:43', '0', 1), (132, 61, 1, 3, 2, NULL, 'QR', NULL, NULL, '0', NULL, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (133, 61, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (134, 61, 3, 4, 5, NULL, NULL, 1, 1, '1', 3, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (135, 62, 1, 3, 2, NULL, 'OR', NULL, NULL, '0', NULL, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (136, 62, 2, 2, 8, 'yyyyMMdd', NULL, NULL, NULL, '0', NULL, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (137, 62, 3, 4, 5, NULL, NULL, 1, 1, '1', 3, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (138, 63, 1, 3, 4, NULL, 'ITYP', 0, 0, '0', 0, NULL, '', '2026-04-09 06:38:49', '', '2026-04-09 06:38:49', '0', 1), (139, 63, 2, 2, 8, 'yyyyMMdd', NULL, 0, 0, '0', 0, NULL, '', '2026-04-09 06:38:49', '', '2026-04-09 06:38:49', '0', 1), (140, 63, 3, 4, 4, NULL, NULL, 1, 1, '1', 3, NULL, '', '2026-04-09 06:38:49', '', '2026-04-09 06:38:49', '0', 1), (141, 47, 1, 3, 2, NULL, 'MT', NULL, NULL, '0', NULL, NULL, '1', '2026-04-09 07:29:15', '1', '2026-04-09 07:29:15', '0', 1), (142, 47, 2, 4, 3, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-04-09 07:29:15', '1', '2026-04-09 07:29:15', '0', 1), (143, 64, 1, 3, 1, NULL, 'V', NULL, NULL, '0', NULL, NULL, '', '2026-04-09 11:36:40', '', '2026-04-09 11:36:40', '0', 1), (144, 64, 2, 4, 5, NULL, NULL, 1, 1, '0', NULL, NULL, '', '2026-04-09 11:36:40', '', '2026-04-09 11:36:40', '0', 1), (145, 65, 1, 3, 1, NULL, 'W', NULL, NULL, '0', NULL, NULL, '', '2026-04-10 01:27:53', '', '2026-04-13 09:25:58', '1', 1), (146, 65, 2, 4, 4, NULL, NULL, 1, 1, '0', NULL, NULL, '', '2026-04-10 01:27:53', '', '2026-04-13 09:25:58', '1', 1), (147, 66, 1, 3, 5, NULL, 'INDIC', NULL, NULL, '0', NULL, 'EEE', '1', '2026-05-01 16:47:19', '1', '2026-05-21 14:46:32', '0', 1), (148, 66, 2, 4, 8, NULL, NULL, 1, 1, '0', NULL, NULL, '1', '2026-05-01 16:47:19', '1', '2026-05-01 16:47:19', '0', 1), (149, 67, 1, 3, 4, NULL, 'TEAM', NULL, NULL, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (150, 67, 2, 4, 4, NULL, NULL, 1, 1, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (151, 68, 1, 3, 3, NULL, 'CHK', NULL, NULL, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (152, 68, 2, 4, 3, NULL, NULL, 1, 1, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (153, 69, 1, 3, 2, NULL, 'WS', NULL, NULL, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (154, 69, 2, 4, 3, NULL, NULL, 1, 1, '0', NULL, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_auto_code_part_seq;
CREATE SEQUENCE mes_md_auto_code_part_seq
    START 155;

-- ----------------------------
-- Table structure for mes_md_auto_code_record
-- ----------------------------
DROP TABLE IF EXISTS mes_md_auto_code_record;
CREATE TABLE mes_md_auto_code_record (
    id int8 NOT NULL,
  rule_id int8 NOT NULL,
  result varchar(64) NULL DEFAULT NULL,
  serial_no int8 NULL DEFAULT NULL,
  input_char varchar(64) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_auto_code_record ADD CONSTRAINT pk_mes_md_auto_code_record PRIMARY KEY (id);

CREATE INDEX idx_mes_md_auto_code_record_01 ON mes_md_auto_code_record (rule_id);

COMMENT ON COLUMN mes_md_auto_code_record.id IS '记录 ID';
COMMENT ON COLUMN mes_md_auto_code_record.rule_id IS '规则 ID';
COMMENT ON COLUMN mes_md_auto_code_record.result IS '生成的编码';
COMMENT ON COLUMN mes_md_auto_code_record.serial_no IS '生成的流水号（当规则组成中包含流水号分段时记录）';
COMMENT ON COLUMN mes_md_auto_code_record.input_char IS '传入的参数';
COMMENT ON COLUMN mes_md_auto_code_record.creator IS '创建者';
COMMENT ON COLUMN mes_md_auto_code_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_auto_code_record.updater IS '更新者';
COMMENT ON COLUMN mes_md_auto_code_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_auto_code_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_auto_code_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_auto_code_record IS 'MES 编码生成记录表';

-- ----------------------------
-- Records of mes_md_auto_code_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_auto_code_record (id, rule_id, result, serial_no, input_char, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 'SN20260305000001', 1, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (2, 3, 'SN20260305000002', 2, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (3, 3, 'SN20260305000003', 3, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (4, 3, 'SN20260305000004', 4, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (5, 3, 'SN20260305000005', 5, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (6, 3, 'SN20260305000006', 6, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (7, 3, 'SN20260305000007', 7, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (8, 3, 'SN20260305000008', 8, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (9, 3, 'SN20260305000009', 9, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (10, 3, 'SN20260305000010', 10, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (11, 3, 'SN20260305000011', 11, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (12, 3, 'SN20260305000012', 12, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (13, 3, 'SN20260305000013', 13, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (14, 3, 'SN20260305000014', 14, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (15, 3, 'SN20260305000015', 15, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (16, 3, 'SN20260305000016', 16, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (17, 3, 'SN20260305000017', 17, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (18, 3, 'SN20260305000018', 18, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (19, 3, 'SN20260305000019', 19, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (20, 3, 'SN20260305000020', 20, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (21, 3, 'SN20260305000021', 21, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (22, 3, 'SN20260305000022', 22, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (23, 3, 'SN20260305000023', 23, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (24, 3, 'SN20260305000024', 24, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (25, 3, 'SN20260305000025', 25, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (26, 3, 'SN20260305000026', 26, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (27, 3, 'SN20260305000027', 27, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (28, 3, 'SN20260305000028', 28, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (29, 3, 'SN20260305000029', 29, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (30, 3, 'SN20260305000030', 30, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (31, 3, 'SN20260305000031', 31, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (32, 3, 'SN20260305000032', 32, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (33, 3, 'SN20260305000033', 33, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (34, 3, 'SN20260305000034', 34, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (35, 3, 'SN20260305000035', 35, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (36, 3, 'SN20260305000036', 36, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (37, 3, 'SN20260305000037', 37, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (38, 3, 'SN20260305000038', 38, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (39, 3, 'SN20260305000039', 39, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (40, 3, 'SN20260305000040', 40, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (41, 3, 'SN20260305000041', 41, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (42, 3, 'SN20260305000042', 42, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (43, 3, 'SN20260305000043', 43, NULL, '1', '2026-03-05 09:51:00', '1', '2026-03-05 09:51:00', '0', 1), (44, 3, 'SN20260305000044', 44, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (45, 3, 'SN20260305000045', 45, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (46, 3, 'SN20260305000046', 46, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (47, 3, 'SN20260305000047', 47, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (48, 3, 'SN20260305000048', 48, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (49, 3, 'SN20260305000049', 49, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (50, 3, 'SN20260305000050', 50, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (51, 3, 'SN20260305000051', 51, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (52, 3, 'SN20260305000052', 52, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (53, 3, 'SN20260305000053', 53, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (54, 3, 'SN20260305000054', 54, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (55, 3, 'SN20260305000055', 55, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (56, 3, 'SN20260305000056', 56, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (57, 3, 'SN20260305000057', 57, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (58, 3, 'SN20260305000058', 58, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (59, 3, 'SN20260305000059', 59, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (60, 3, 'SN20260305000060', 60, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (61, 3, 'SN20260305000061', 61, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (62, 3, 'SN20260305000062', 62, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (63, 3, 'SN20260305000063', 63, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (64, 3, 'SN20260305000064', 64, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (65, 3, 'SN20260305000065', 65, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (66, 3, 'SN20260305000066', 66, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (67, 3, 'SN20260305000067', 67, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (68, 3, 'SN20260305000068', 68, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (69, 3, 'SN20260305000069', 69, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (70, 3, 'SN20260305000070', 70, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (71, 3, 'SN20260305000071', 71, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (72, 3, 'SN20260305000072', 72, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (73, 3, 'SN20260305000073', 73, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (74, 3, 'SN20260305000074', 74, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (75, 3, 'SN20260305000075', 75, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (76, 3, 'SN20260305000076', 76, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (77, 3, 'SN20260305000077', 77, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (78, 3, 'SN20260305000078', 78, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (79, 3, 'SN20260305000079', 79, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (80, 3, 'SN20260305000080', 80, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (81, 3, 'SN20260305000081', 81, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (82, 3, 'SN20260305000082', 82, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (83, 3, 'SN20260305000083', 83, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (84, 3, 'SN20260305000084', 84, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (85, 3, 'SN20260305000085', 85, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (86, 3, 'SN20260305000086', 86, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (87, 3, 'SN20260305000087', 87, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (88, 3, 'SN20260305000088', 88, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (89, 3, 'SN20260305000089', 89, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (90, 3, 'SN20260305000090', 90, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (91, 3, 'SN20260305000091', 91, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (92, 3, 'SN20260305000092', 92, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (93, 3, 'SN20260305000093', 93, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (94, 3, 'SN20260305000094', 94, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (95, 3, 'SN20260305000095', 95, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (96, 3, 'SN20260305000096', 96, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (97, 3, 'SN20260305000097', 97, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (98, 3, 'SN20260305000098', 98, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (99, 3, 'SN20260305000099', 99, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (100, 3, 'SN20260305000100', 100, NULL, '1', '2026-03-05 09:51:01', '1', '2026-03-05 09:51:01', '0', 1), (101, 3, 'SN20260305000101', 101, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (102, 3, 'SN20260305000102', 102, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (103, 3, 'SN20260305000103', 103, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (104, 3, 'SN20260305000104', 104, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (105, 3, 'SN20260305000105', 105, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (106, 3, 'SN20260305000106', 106, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (107, 3, 'SN20260305000107', 107, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (108, 3, 'SN20260305000108', 108, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (109, 3, 'SN20260305000109', 109, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (110, 3, 'SN20260305000110', 110, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (111, 3, 'SN20260305000111', 111, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (112, 3, 'SN20260305000112', 112, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (113, 3, 'SN20260305000113', 113, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (114, 3, 'SN20260305000114', 114, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (115, 3, 'SN20260305000115', 115, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (116, 3, 'SN20260305000116', 116, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (117, 3, 'SN20260305000117', 117, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (118, 3, 'SN20260305000118', 118, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (119, 3, 'SN20260305000119', 119, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (120, 3, 'SN20260305000120', 120, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (121, 3, 'SN20260305000121', 121, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (122, 3, 'SN20260305000122', 122, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (123, 3, 'SN20260305000123', 123, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (124, 3, 'SN20260305000124', 124, NULL, '1', '2026-03-05 13:24:55', '1', '2026-03-05 13:24:55', '0', 1), (125, 3, 'SN20260305000125', 125, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (126, 3, 'SN20260305000126', 126, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (127, 3, 'SN20260305000127', 127, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (128, 3, 'SN20260305000128', 128, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (129, 3, 'SN20260305000129', 129, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (130, 3, 'SN20260305000130', 130, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (131, 3, 'SN20260305000131', 131, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (132, 3, 'SN20260305000132', 132, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (133, 3, 'SN20260305000133', 133, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (134, 3, 'SN20260305000134', 134, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (135, 3, 'SN20260305000135', 135, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (136, 3, 'SN20260305000136', 136, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (137, 3, 'SN20260305000137', 137, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (138, 3, 'SN20260305000138', 138, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (139, 3, 'SN20260305000139', 139, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (140, 3, 'SN20260305000140', 140, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (141, 3, 'SN20260305000141', 141, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (142, 3, 'SN20260305000142', 142, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (143, 3, 'SN20260305000143', 143, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (144, 3, 'SN20260305000144', 144, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (145, 3, 'SN20260305000145', 145, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (146, 3, 'SN20260305000146', 146, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (147, 3, 'SN20260305000147', 147, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (148, 3, 'SN20260305000148', 148, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (149, 3, 'SN20260305000149', 149, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (150, 3, 'SN20260305000150', 150, NULL, '1', '2026-03-05 13:24:56', '1', '2026-03-05 13:24:56', '0', 1), (151, 3, 'SN20260305000151', 151, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (152, 3, 'SN20260305000152', 152, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (153, 3, 'SN20260305000153', 153, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (154, 3, 'SN20260305000154', 154, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (155, 3, 'SN20260305000155', 155, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (156, 3, 'SN20260305000156', 156, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (157, 3, 'SN20260305000157', 157, NULL, '1', '2026-03-05 13:29:19', '1', '2026-03-05 13:29:19', '0', 1), (158, 3, 'SN20260305000158', 158, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (159, 3, 'SN20260305000159', 159, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (160, 3, 'SN20260305000160', 160, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (161, 3, 'SN20260305000161', 161, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (162, 3, 'SN20260305000162', 162, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (163, 3, 'SN20260305000163', 163, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (164, 3, 'SN20260305000164', 164, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (165, 3, 'SN20260305000165', 165, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (166, 3, 'SN20260305000166', 166, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (167, 3, 'SN20260305000167', 167, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (168, 3, 'SN20260305000168', 168, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (169, 3, 'SN20260305000169', 169, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (170, 3, 'SN20260305000170', 170, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (171, 3, 'SN20260305000171', 171, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (172, 3, 'SN20260305000172', 172, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (173, 3, 'SN20260305000173', 173, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (174, 3, 'SN20260305000174', 174, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (175, 3, 'SN20260305000175', 175, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (176, 3, 'SN20260305000176', 176, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (177, 3, 'SN20260305000177', 177, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (178, 3, 'SN20260305000178', 178, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (179, 3, 'SN20260305000179', 179, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (180, 3, 'SN20260305000180', 180, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (181, 3, 'SN20260305000181', 181, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (182, 3, 'SN20260305000182', 182, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (183, 3, 'SN20260305000183', 183, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (184, 3, 'SN20260305000184', 184, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (185, 3, 'SN20260305000185', 185, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (186, 3, 'SN20260305000186', 186, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (187, 3, 'SN20260305000187', 187, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (188, 3, 'SN20260305000188', 188, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (189, 3, 'SN20260305000189', 189, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (190, 3, 'SN20260305000190', 190, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (191, 3, 'SN20260305000191', 191, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (192, 3, 'SN20260305000192', 192, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (193, 3, 'SN20260305000193', 193, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (194, 3, 'SN20260305000194', 194, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (195, 3, 'SN20260305000195', 195, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (196, 3, 'SN20260305000196', 196, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (197, 3, 'SN20260305000197', 197, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (198, 3, 'SN20260305000198', 198, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (199, 3, 'SN20260305000199', 199, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (200, 3, 'SN20260305000200', 200, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (201, 3, 'SN20260305000201', 201, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (202, 3, 'SN20260305000202', 202, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (203, 3, 'SN20260305000203', 203, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (204, 3, 'SN20260305000204', 204, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (205, 3, 'SN20260305000205', 205, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (206, 3, 'SN20260305000206', 206, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (207, 3, 'SN20260305000207', 207, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (208, 3, 'SN20260305000208', 208, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (209, 3, 'SN20260305000209', 209, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (210, 3, 'SN20260305000210', 210, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (211, 3, 'SN20260305000211', 211, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (212, 3, 'SN20260305000212', 212, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (213, 3, 'SN20260305000213', 213, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (214, 3, 'SN20260305000214', 214, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (215, 3, 'SN20260305000215', 215, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (216, 3, 'SN20260305000216', 216, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (217, 3, 'SN20260305000217', 217, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (218, 3, 'SN20260305000218', 218, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (219, 3, 'SN20260305000219', 219, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (220, 3, 'SN20260305000220', 220, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (221, 3, 'SN20260305000221', 221, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (222, 3, 'SN20260305000222', 222, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (223, 3, 'SN20260305000223', 223, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (224, 3, 'SN20260305000224', 224, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (225, 3, 'SN20260305000225', 225, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (226, 3, 'SN20260305000226', 226, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (227, 3, 'SN20260305000227', 227, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (228, 3, 'SN20260305000228', 228, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (229, 3, 'SN20260305000229', 229, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (230, 3, 'SN20260305000230', 230, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (231, 3, 'SN20260305000231', 231, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (232, 3, 'SN20260305000232', 232, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (233, 3, 'SN20260305000233', 233, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (234, 3, 'SN20260305000234', 234, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (235, 3, 'SN20260305000235', 235, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (236, 3, 'SN20260305000236', 236, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (237, 3, 'SN20260305000237', 237, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (238, 3, 'SN20260305000238', 238, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (239, 3, 'SN20260305000239', 239, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (240, 3, 'SN20260305000240', 240, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (241, 3, 'SN20260305000241', 241, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (242, 3, 'SN20260305000242', 242, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (243, 3, 'SN20260305000243', 243, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (244, 3, 'SN20260305000244', 244, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (245, 3, 'SN20260305000245', 245, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (246, 3, 'SN20260305000246', 246, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (247, 3, 'SN20260305000247', 247, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (248, 3, 'SN20260305000248', 248, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (249, 3, 'SN20260305000249', 249, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (250, 3, 'SN20260305000250', 250, NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (251, 4, 'PKG202603080001', 1, NULL, '1', '2026-03-08 13:00:21', '1', '2026-03-08 13:00:21', '0', 1), (252, 4, 'PKG202603080002', 2, NULL, '1', '2026-03-08 13:04:24', '1', '2026-03-08 13:04:24', '0', 1), (253, 4, 'PKG202603080003', 3, NULL, '1', '2026-03-08 13:04:25', '1', '2026-03-08 13:04:25', '0', 1), (254, 4, 'PKG202603080004', 4, NULL, '1', '2026-03-08 13:04:29', '1', '2026-03-08 13:04:29', '0', 1), (255, 4, 'PKG202603080005', 5, NULL, '1', '2026-03-08 13:04:30', '1', '2026-03-08 13:04:30', '0', 1), (256, 4, 'PKG202603080006', 6, NULL, '1', '2026-03-08 13:04:30', '1', '2026-03-08 13:04:30', '0', 1), (257, 4, 'PKG202603080007', 7, NULL, '1', '2026-03-08 13:04:30', '1', '2026-03-08 13:04:30', '0', 1), (258, 12, 'PC', NULL, NULL, '1', '2026-03-13 23:49:42', '1', '2026-03-13 23:49:42', '0', 1), (259, 12, 'PC20260', NULL, NULL, '1', '2026-03-13 23:54:53', '1', '2026-03-13 23:54:53', '0', 1), (260, 13, 'TASK20260315000001', 1, NULL, '1', '2026-03-15 22:56:12', '1', '2026-03-15 22:56:12', '0', 1), (261, 12, 'PC202600001', 1, NULL, '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (262, 14, '00000IQC202603230001', 1, NULL, '1', '2026-03-23 23:00:59', '1', '2026-03-23 23:00:59', '0', 1), (263, 14, '00000IQC202603230002', 2, NULL, '1', '2026-03-23 23:00:59', '1', '2026-03-23 23:00:59', '0', 1), (264, 14, '00000IQC202603230003', 3, NULL, '1', '2026-03-23 23:01:00', '1', '2026-03-23 23:01:00', '0', 1), (265, 14, '00000IQC202603230004', 4, NULL, '1', '2026-03-23 23:01:00', '1', '2026-03-23 23:01:00', '0', 1), (266, 14, '00000IQC202603230005', 5, NULL, '1', '2026-03-23 23:01:00', '1', '2026-03-23 23:01:00', '0', 1), (267, 14, '00000IQC202603230006', 6, NULL, '1', '2026-03-23 23:01:03', '1', '2026-03-23 23:01:03', '0', 1), (268, 14, '00000IQC202603230007', 7, NULL, '1', '2026-03-23 23:01:04', '1', '2026-03-23 23:01:04', '0', 1), (269, 14, '00000IQC202603230008', 8, NULL, '1', '2026-03-23 23:01:04', '1', '2026-03-23 23:01:04', '0', 1), (270, 14, '00000IQC202603230009', 9, NULL, '1', '2026-03-23 23:01:15', '1', '2026-03-23 23:01:15', '0', 1), (271, 14, 'IQC20260323010', 10, NULL, '1', '2026-03-23 23:03:34', '1', '2026-03-23 23:03:34', '0', 1), (272, 14, 'IQC20260323011', 11, NULL, '1', '2026-03-23 23:03:35', '1', '2026-03-23 23:03:35', '0', 1), (273, 14, 'IQC20260323012', 12, NULL, '1', '2026-03-23 23:03:35', '1', '2026-03-23 23:03:35', '0', 1), (274, 14, 'IQC20260323013', 13, NULL, '1', '2026-03-23 23:03:35', '1', '2026-03-23 23:03:35', '0', 1), (275, 12, 'PC202600002', 2, NULL, '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (276, 15, 'IPQC20260325000001', 1, NULL, '1', '2026-03-25 17:16:41', '1', '2026-03-25 17:16:41', '0', 1), (277, 15, 'IPQC20260325000002', 2, NULL, '1', '2026-03-25 17:28:19', '1', '2026-03-25 17:28:19', '0', 1), (278, 14, 'IQC20260325001', 1, NULL, '1', '2026-03-25 17:36:11', '1', '2026-03-25 17:36:11', '0', 1), (279, 16, 'RQC20260326000001', 1, NULL, '1', '2026-03-26 13:10:39', '1', '2026-03-26 13:10:39', '0', 1), (280, 16, 'RQC20260326000002', 2, NULL, '1', '2026-03-26 13:20:46', '1', '2026-03-26 13:20:46', '0', 1), (281, 16, 'RQC20260326000003', 3, NULL, '1', '2026-03-26 21:09:41', '1', '2026-03-26 21:09:41', '0', 1), (282, 16, 'RQC20260326000004', 4, NULL, '1', '2026-03-26 22:37:56', '1', '2026-03-26 22:37:56', '0', 1), (283, 17, 'OQC20260327001', 1, NULL, '1', '2026-03-27 17:01:56', '1', '2026-03-27 17:01:56', '0', 1), (284, 18, 'ITEM20260328000001', 1, NULL, '1', '2026-03-28 08:57:14', '1', '2026-03-28 08:57:14', '0', 1), (285, 18, 'ITEM20260328000002', 2, NULL, '1', '2026-03-28 08:57:15', '1', '2026-03-28 08:57:15', '0', 1), (286, 18, 'ITEM20260328000003', 3, NULL, '1', '2026-03-28 08:57:15', '1', '2026-03-28 08:57:15', '0', 1), (287, 18, 'ITEM20260328000004', 4, NULL, '1', '2026-03-28 08:57:18', '1', '2026-03-28 08:57:18', '0', 1), (288, 18, 'ITEM20260328000005', 5, NULL, '1', '2026-03-28 08:57:18', '1', '2026-03-28 08:57:18', '0', 1), (289, 18, 'ITEM20260328000006', 6, NULL, '1', '2026-03-28 08:57:18', '1', '2026-03-28 08:57:18', '0', 1), (290, 18, 'ITEM20260328000007', 7, NULL, '1', '2026-03-28 09:52:50', '1', '2026-03-28 09:52:50', '0', 1), (291, 18, 'ITEM20260328000008', 8, NULL, '1', '2026-03-28 10:34:27', '1', '2026-03-28 10:34:27', '0', 1), (292, 18, 'ITEM20260328000009', 9, NULL, '1', '2026-03-28 10:37:29', '1', '2026-03-28 10:37:29', '0', 1), (293, 19, 'C00001', 1, NULL, '1', '2026-03-28 17:09:31', '1', '2026-03-28 17:09:31', '0', 1), (294, 19, 'C00002', 2, NULL, '1', '2026-03-28 17:09:31', '1', '2026-03-28 17:09:31', '0', 1), (295, 19, 'C00003', 3, NULL, '1', '2026-03-28 17:09:32', '1', '2026-03-28 17:09:32', '0', 1), (296, 19, 'C00004', 4, NULL, '1', '2026-03-28 17:09:32', '1', '2026-03-28 17:09:32', '0', 1), (297, 19, 'C00005', 5, NULL, '1', '2026-03-28 17:09:32', '1', '2026-03-28 17:09:32', '0', 1), (298, 19, 'C00006', 6, NULL, '1', '2026-03-28 17:15:02', '1', '2026-03-28 17:15:02', '0', 1), (299, 19, 'C00007', 7, NULL, '1', '2026-03-28 17:15:03', '1', '2026-03-28 17:15:03', '0', 1), (300, 19, 'C00008', 8, NULL, '1', '2026-03-28 17:15:03', '1', '2026-03-28 17:15:03', '0', 1), (301, 19, 'C00009', 9, NULL, '1', '2026-03-28 17:15:03', '1', '2026-03-28 17:15:03', '0', 1), (302, 19, 'C00010', 10, NULL, '1', '2026-03-28 17:15:03', '1', '2026-03-28 17:15:03', '0', 1), (303, 21, 'WH202603280001', 1, NULL, '1', '2026-03-28 20:49:03', '1', '2026-03-28 20:49:03', '0', 1), (304, 21, 'WH202603280002', 2, NULL, '1', '2026-03-28 20:49:04', '1', '2026-03-28 20:49:04', '0', 1), (305, 21, 'WH202603280003', 3, NULL, '1', '2026-03-28 20:49:04', '1', '2026-03-28 20:49:04', '0', 1), (306, 21, 'WH202603280004', 4, NULL, '1', '2026-03-28 20:49:04', '1', '2026-03-28 20:49:04', '0', 1), (307, 21, 'WH202603280005', 5, NULL, '1', '2026-03-28 20:49:05', '1', '2026-03-28 20:49:05', '0', 1), (308, 21, 'WH202603280006', 6, NULL, '1', '2026-03-28 20:49:06', '1', '2026-03-28 20:49:06', '0', 1), (309, 23, 'AR202603280001', 1, NULL, '1', '2026-03-28 23:30:15', '1', '2026-03-28 23:30:15', '0', 1), (310, 23, 'AR202603280002', 2, NULL, '1', '2026-03-28 23:30:15', '1', '2026-03-28 23:30:15', '0', 1), (311, 23, 'AR202603280003', 3, NULL, '1', '2026-03-28 23:30:17', '1', '2026-03-28 23:30:17', '0', 1), (312, 23, 'AR202603280004', 4, NULL, '1', '2026-03-28 23:30:17', '1', '2026-03-28 23:30:17', '0', 1), (313, 23, 'AR202603280005', 5, NULL, '1', '2026-03-28 23:30:17', '1', '2026-03-28 23:30:17', '0', 1), (314, 23, 'AR202603280006', 6, NULL, '1', '2026-03-28 23:30:20', '1', '2026-03-28 23:30:20', '0', 1), (315, 24, 'TT001', 1, NULL, '1', '2026-03-29 09:30:11', '1', '2026-03-29 09:30:11', '0', 1), (316, 24, 'TT002', 2, NULL, '1', '2026-03-29 09:30:11', '1', '2026-03-29 09:30:11', '0', 1), (317, 24, 'TT003', 3, NULL, '1', '2026-03-29 09:30:12', '1', '2026-03-29 09:30:12', '0', 1), (318, 24, 'TT004', 4, NULL, '1', '2026-03-29 09:30:12', '1', '2026-03-29 09:30:12', '0', 1), (319, 24, 'TT005', 5, NULL, '1', '2026-03-29 09:30:13', '1', '2026-03-29 09:30:13', '0', 1), (320, 24, 'TT006', 6, NULL, '1', '2026-03-29 09:43:34', '1', '2026-03-29 09:43:34', '0', 1), (321, 25, 'TL202603290001', 1, NULL, '1', '2026-03-29 09:56:46', '1', '2026-03-29 09:56:46', '0', 1), (322, 25, 'TL202603290002', 2, NULL, '1', '2026-03-29 09:56:46', '1', '2026-03-29 09:56:46', '0', 1), (323, 26, 'IR20260329000001', 1, NULL, '1', '2026-03-29 16:18:47', '1', '2026-03-29 16:18:47', '0', 1), (324, 27, 'AN20260329000001', 1, NULL, '1', '2026-03-29 19:35:11', '1', '2026-03-29 19:35:11', '0', 1), (325, 28, 'RV20260329000001', 1, NULL, '1', '2026-03-29 21:29:30', '1', '2026-03-29 21:29:30', '0', 1), (326, 28, 'RV20260329000002', 2, NULL, '1', '2026-03-29 21:37:10', '1', '2026-03-29 21:37:10', '0', 1), (327, 28, 'RV20260329000003', 3, NULL, '1', '2026-03-29 22:09:03', '1', '2026-03-29 22:09:03', '0', 1), (328, 28, 'RV20260329000004', 4, NULL, '1', '2026-03-29 22:09:04', '1', '2026-03-29 22:09:04', '0', 1), (329, 28, 'RV20260329000005', 5, NULL, '1', '2026-03-29 22:09:04', '1', '2026-03-29 22:09:04', '0', 1), (330, 28, 'RV20260329000006', 6, NULL, '1', '2026-03-29 22:09:19', '1', '2026-03-29 22:09:19', '0', 1), (331, 28, 'RV20260329000007', 7, NULL, '1', '2026-03-29 22:09:19', '1', '2026-03-29 22:09:19', '0', 1), (332, 28, 'RV20260329000008', 8, NULL, '1', '2026-03-29 22:09:20', '1', '2026-03-29 22:09:20', '0', 1), (333, 28, 'RV20260329000009', 9, NULL, '1', '2026-03-29 22:09:22', '1', '2026-03-29 22:09:22', '0', 1), (334, 28, 'RV20260329000010', 10, NULL, '1', '2026-03-29 22:37:13', '1', '2026-03-29 22:37:13', '0', 1), (335, 28, 'RV20260329000011', 11, NULL, '1', '2026-03-29 22:44:53', '1', '2026-03-29 22:44:53', '0', 1), (336, 29, 'PI20260330000001', 1, NULL, '1', '2026-03-30 11:02:01', '1', '2026-03-30 11:02:01', '0', 1), (337, 29, 'PI20260330000002', 2, NULL, '1', '2026-03-30 11:02:08', '1', '2026-03-30 11:02:08', '0', 1), (338, 30, 'RI20260330000001', 1, NULL, '1', '2026-03-30 11:52:10', '1', '2026-03-30 11:52:10', '0', 1), (339, 30, 'RI20260330000002', 2, NULL, '1', '2026-03-30 12:07:33', '1', '2026-03-30 12:07:33', '0', 1), (340, 31, 'PR20260330000001', 1, NULL, '1', '2026-03-30 15:17:16', '1', '2026-03-30 15:17:16', '0', 1), (341, 32, 'SN20260330000001', 1, NULL, '1', '2026-03-30 16:58:23', '1', '2026-03-30 16:58:23', '0', 1), (342, 32, 'SN20260330000002', 2, NULL, '1', '2026-03-30 17:01:47', '1', '2026-03-30 17:01:47', '0', 1), (343, 32, 'SN20260330000003', 3, NULL, '1', '2026-03-30 17:01:47', '1', '2026-03-30 17:01:47', '0', 1), (344, 32, 'SN20260330000004', 4, NULL, '1', '2026-03-30 18:02:56', '1', '2026-03-30 18:02:56', '0', 1), (345, 34, 'RS20260330001', 1, NULL, '1', '2026-03-30 19:13:01', '1', '2026-03-30 19:13:01', '0', 1), (346, 35, 'PS20260330000001', 1, NULL, '1', '2026-03-30 20:31:53', '1', '2026-03-30 20:31:53', '0', 1), (347, 35, 'PS20260330000002', 2, NULL, '1', '2026-03-30 20:32:39', '1', '2026-03-30 20:32:39', '0', 1), (348, 35, 'PS20260330000003', 3, NULL, '1', '2026-03-30 20:58:23', '1', '2026-03-30 20:58:23', '0', 1), (349, 35, 'PS20260330000004', 4, NULL, '1', '2026-03-30 21:05:23', '1', '2026-03-30 21:05:23', '0', 1), (350, 35, 'PS20260330000005', 5, NULL, '1', '2026-03-30 21:08:56', '1', '2026-03-30 21:08:56', '0', 1), (351, 36, 'MISCI20260330001', 1, NULL, '1', '2026-03-30 23:40:16', '1', '2026-03-30 23:40:16', '0', 1), (352, 36, 'MISCI20260330002', 2, NULL, '1', '2026-03-30 23:40:17', '1', '2026-03-30 23:40:17', '0', 1), (353, 36, 'MISCI20260330003', 3, NULL, '1', '2026-03-30 23:40:17', '1', '2026-03-30 23:40:17', '0', 1), (354, 36, 'MISCI20260330004', 4, NULL, '1', '2026-03-30 23:40:17', '1', '2026-03-30 23:40:17', '0', 1), (355, 36, 'MISCI20260330005', 5, NULL, '1', '2026-03-30 23:40:18', '1', '2026-03-30 23:40:18', '0', 1), (356, 37, 'MISCR20260331001', 1, NULL, '1', '2026-03-31 09:55:00', '1', '2026-03-31 09:55:00', '0', 1), (357, 37, 'MISCR20260331002', 2, NULL, '1', '2026-03-31 10:03:12', '1', '2026-03-31 10:03:12', '0', 1), (358, 37, 'MISCR20260331003', 3, NULL, '1', '2026-03-31 10:03:13', '1', '2026-03-31 10:03:13', '0', 1), (359, 37, 'MISCR20260331004', 4, NULL, '1', '2026-03-31 10:03:13', '1', '2026-03-31 10:03:13', '0', 1), (360, 37, 'MISCR20260331005', 5, NULL, '1', '2026-03-31 10:03:18', '1', '2026-03-31 10:03:18', '0', 1), (361, 37, 'MISCR20260331006', 6, NULL, '1', '2026-03-31 10:03:18', '1', '2026-03-31 10:03:18', '0', 1), (362, 37, 'MISCR20260331007', 7, NULL, '1', '2026-03-31 10:03:18', '1', '2026-03-31 10:03:18', '0', 1), (363, 37, 'MISCR20260331008', 8, NULL, '1', '2026-03-31 10:03:18', '1', '2026-03-31 10:03:18', '0', 1), (364, 37, 'MISCR20260331009', 9, NULL, '1', '2026-03-31 10:03:19', '1', '2026-03-31 10:03:19', '0', 1), (365, 37, 'MISCR20260331010', 10, NULL, '1', '2026-03-31 10:03:20', '1', '2026-03-31 10:03:20', '0', 1), (366, 37, 'MISCR20260331011', 11, NULL, '1', '2026-03-31 10:03:20', '1', '2026-03-31 10:03:20', '0', 1), (367, 37, 'MISCR20260331012', 12, NULL, '1', '2026-03-31 10:04:29', '1', '2026-03-31 10:04:29', '0', 1), (368, 37, 'MISCR20260331013', 13, NULL, '1', '2026-03-31 10:06:46', '1', '2026-03-31 10:06:46', '0', 1), (369, 37, 'MISCR20260331014', 14, NULL, '1', '2026-03-31 10:06:46', '1', '2026-03-31 10:06:46', '0', 1), (370, 37, 'MISCR20260331015', 15, NULL, '1', '2026-03-31 10:06:46', '1', '2026-03-31 10:06:46', '0', 1), (371, 37, 'MISCR20260331016', 16, NULL, '1', '2026-03-31 10:06:46', '1', '2026-03-31 10:06:46', '0', 1), (372, 37, 'MISCR20260331017', 17, NULL, '1', '2026-03-31 10:06:46', '1', '2026-03-31 10:06:46', '0', 1), (373, 37, 'MISCR20260331018', 18, NULL, '1', '2026-03-31 10:06:48', '1', '2026-03-31 10:06:48', '0', 1), (374, 37, 'MISCR20260331019', 19, NULL, '1', '2026-03-31 10:06:49', '1', '2026-03-31 10:06:49', '0', 1), (375, 37, 'MISCR20260331020', 20, NULL, '1', '2026-03-31 10:06:51', '1', '2026-03-31 10:06:51', '0', 1), (376, 38, 'TR20260331001', 1, NULL, '1', '2026-03-31 16:02:49', '1', '2026-03-31 16:02:49', '0', 1), (377, 38, 'TR20260331002', 2, NULL, '1', '2026-03-31 16:02:50', '1', '2026-03-31 16:02:50', '0', 1), (378, 43, 'PDP202603310001', 1, NULL, '1', '2026-03-31 18:22:57', '1', '2026-03-31 18:22:57', '0', 1), (379, 4, 'PKG202603310001', 1, NULL, '1', '2026-03-31 20:06:21', '1', '2026-03-31 20:06:21', '0', 1), (380, 45, 'OSI202603310001', 1, NULL, '1', '2026-03-31 22:42:06', '1', '2026-03-31 22:42:06', '0', 1), (381, 45, 'OSI202603310002', 2, NULL, '1', '2026-03-31 22:42:28', '1', '2026-03-31 22:42:28', '0', 1), (382, 45, 'OSI202603310003', 3, NULL, '1', '2026-03-31 23:16:01', '1', '2026-03-31 23:16:01', '0', 1), (383, 45, 'OSI202603310004', 4, NULL, '1', '2026-03-31 23:17:18', '1', '2026-03-31 23:17:18', '0', 1), (384, 46, 'PP20260402000001', 1, NULL, '1', '2026-04-02 01:27:42', '1', '2026-04-02 01:27:42', '0', 1), (385, 46, 'PP20260402000002', 2, NULL, '1', '2026-04-02 01:27:43', '1', '2026-04-02 01:27:43', '0', 1), (386, 46, 'PP20260402000003', 3, NULL, '1', '2026-04-02 01:28:03', '1', '2026-04-02 01:28:03', '0', 1), (387, 47, 'MT001', 1, NULL, '1', '2026-04-02 21:54:33', '1', '2026-04-02 21:54:33', '0', 1), (388, 47, 'MT002', 2, NULL, '1', '2026-04-02 21:54:33', '1', '2026-04-02 21:54:33', '0', 1), (389, 47, 'MT003', 3, NULL, '1', '2026-04-02 21:54:34', '1', '2026-04-02 21:54:34', '0', 1), (390, 47, 'MT004', 4, NULL, '1', '2026-04-02 21:54:34', '1', '2026-04-02 21:54:34', '0', 1), (391, 48, 'M00001', 1, NULL, '1', '2026-04-02 23:19:23', '1', '2026-04-02 23:19:23', '0', 1), (392, 48, 'M00002', 2, NULL, '1', '2026-04-02 23:37:03', '1', '2026-04-02 23:37:03', '0', 1), (393, 50, 'BX20260404000001', 1, NULL, '1', '2026-04-04 00:43:29', '1', '2026-04-04 00:43:29', '0', 1), (394, 50, 'BX20260404000002', 2, NULL, '1', '2026-04-04 00:43:30', '1', '2026-04-04 00:43:30', '0', 1), (395, 50, 'BX20260404000003', 3, NULL, '1', '2026-04-04 00:50:44', '1', '2026-04-04 00:50:44', '0', 1), (396, 50, 'BX20260404000004', 4, NULL, '1', '2026-04-04 00:58:09', '1', '2026-04-04 00:58:09', '0', 1), (397, 50, 'BX20260404000005', 5, NULL, '1', '2026-04-04 01:09:09', '1', '2026-04-04 01:09:09', '0', 1), (398, 54, 'WO202604040001', 1, NULL, '1', '2026-04-04 10:37:55', '1', '2026-04-04 10:37:55', '0', 1), (399, 54, 'WO202604040002', 2, NULL, '1', '2026-04-04 10:37:56', '1', '2026-04-04 10:37:56', '0', 1), (400, 54, 'WO202604040003', 3, NULL, '1', '2026-04-04 10:37:56', '1', '2026-04-04 10:37:56', '0', 1), (401, 54, 'WO202604040004', 4, NULL, '1', '2026-04-04 10:37:56', '1', '2026-04-04 10:37:56', '0', 1), (402, 54, 'WO202604040005', 5, NULL, '1', '2026-04-04 10:37:57', '1', '2026-04-04 10:37:57', '0', 1), (403, 54, 'WO202604040006', 6, NULL, '1', '2026-04-04 10:37:57', '1', '2026-04-04 10:37:57', '0', 1), (404, 55, 'PROC000001', 1, NULL, '1', '2026-04-04 11:32:18', '1', '2026-04-04 11:32:18', '0', 1), (405, 55, 'PROC000002', 2, NULL, '1', '2026-04-04 11:32:18', '1', '2026-04-04 11:32:18', '0', 1), (406, 55, 'PROC000003', 3, NULL, '1', '2026-04-04 11:32:19', '1', '2026-04-04 11:32:19', '0', 1), (407, 55, 'PROC000004', 4, NULL, '1', '2026-04-04 11:32:19', '1', '2026-04-04 11:32:19', '0', 1), (408, 56, 'PR202604040001', 1, NULL, '1', '2026-04-04 16:37:52', '1', '2026-04-04 16:37:52', '0', 1), (409, 56, 'PR202604040002', 2, NULL, '1', '2026-04-04 16:37:52', '1', '2026-04-04 16:37:52', '0', 1), (410, 56, 'PR202604040003', 3, NULL, '1', '2026-04-04 16:37:53', '1', '2026-04-04 16:37:53', '0', 1), (411, 56, 'PR202604040004', 4, NULL, '1', '2026-04-04 16:37:56', '1', '2026-04-04 16:37:56', '0', 1), (412, 57, 'FB20260404000001', 1, NULL, '1', '2026-04-04 19:44:34', '1', '2026-04-04 19:44:34', '0', 1), (413, 57, 'FB20260404000002', 2, NULL, '1', '2026-04-04 19:44:37', '1', '2026-04-04 19:44:37', '0', 1), (414, 57, 'FB20260404000003', 3, NULL, '1', '2026-04-04 19:46:59', '1', '2026-04-04 19:46:59', '0', 1), (415, 57, 'FB20260404000004', 4, NULL, '1', '2026-04-04 19:49:03', '1', '2026-04-04 19:49:03', '0', 1), (416, 58, 'CARD20260404000001', 1, NULL, '1', '2026-04-04 20:30:27', '1', '2026-04-04 20:30:27', '0', 1), (417, 59, 'DEFECT00000001', 1, NULL, '1', '2026-04-04 20:48:09', '1', '2026-04-04 20:48:09', '0', 1), (418, 59, 'DEFECT00000002', 2, NULL, '1', '2026-04-04 20:48:10', '1', '2026-04-04 20:48:10', '0', 1), (419, 59, 'DEFECT00000003', 3, NULL, '1', '2026-04-04 20:48:10', '1', '2026-04-04 20:48:10', '0', 1), (420, 59, 'DEFECT00000004', 4, NULL, '1', '2026-04-04 20:48:10', '1', '2026-04-04 20:48:10', '0', 1), (421, 60, 'QCT20260404000002', 2, NULL, '1', '2026-04-04 22:36:59', '1', '2026-04-04 22:36:59', '0', 1), (422, 60, 'QCT20260404000001', 1, NULL, '1', '2026-04-04 22:36:59', '1', '2026-04-04 22:36:59', '0', 1), (423, 60, 'QCT20260404000003', 3, NULL, '1', '2026-04-04 22:37:00', '1', '2026-04-04 22:37:00', '0', 1), (424, 60, 'QCT20260404000004', 4, NULL, '1', '2026-04-04 22:37:34', '1', '2026-04-04 22:37:34', '0', 1), (425, 61, 'QR2026040500001', 1, NULL, '1', '2026-04-05 00:48:00', '1', '2026-04-05 00:48:00', '0', 1), (426, 61, 'QR2026040500002', 2, NULL, '1', '2026-04-05 00:48:00', '1', '2026-04-05 00:48:00', '0', 1), (427, 61, 'QR2026040500003', 3, NULL, '1', '2026-04-05 00:48:00', '1', '2026-04-05 00:48:00', '0', 1), (428, 61, 'QR2026040500004', 4, NULL, '1', '2026-04-05 00:48:01', '1', '2026-04-05 00:48:01', '0', 1), (429, 4, 'PKG202604060001', 1, NULL, '1', '2026-04-06 00:52:28', '1', '2026-04-06 00:52:28', '0', 1), (430, 4, 'PKG202604060002', 2, NULL, '1', '2026-04-06 01:07:49', '1', '2026-04-06 01:07:49', '0', 1), (431, 62, 'OR2026040600001', 1, NULL, '1', '2026-04-06 09:50:19', '1', '2026-04-06 09:50:19', '0', 1), (432, 28, 'RV20260406000001', 1, NULL, '1', '2026-04-06 10:40:54', '1', '2026-04-06 10:40:54', '0', 1), (433, 44, 'PDT202604060001', 1, NULL, '1', '2026-04-06 12:05:00', '1', '2026-04-06 12:05:00', '0', 1), (434, 28, 'RV20260406000002', 2, NULL, '1', '2026-04-06 16:58:23', '1', '2026-04-06 16:58:23', '0', 1), (435, 57, 'FB20260406000001', 1, NULL, '1', '2026-04-06 23:41:30', '1', '2026-04-06 23:41:30', '0', 1), (436, 57, 'FB20260406000002', 2, NULL, '1', '2026-04-06 23:45:19', '1', '2026-04-06 23:45:19', '0', 1), (437, 57, 'FB20260407000001', 1, NULL, '1', '2026-04-07 23:34:29', '1', '2026-04-07 23:34:29', '0', 1), (438, 57, 'FB20260407000002', 2, NULL, '1', '2026-04-07 23:34:36', '1', '2026-04-07 23:34:36', '0', 1), (439, 50, 'BX20260407000001', 1, NULL, '1', '2026-04-07 23:42:45', '1', '2026-04-07 23:42:45', '0', 1), (440, 63, 'ITYP202604090001', 1, NULL, '1', '2026-04-09 14:39:24', '1', '2026-04-09 14:39:24', '0', 1), (441, 63, 'ITYP202604090002', 2, NULL, '1', '2026-04-09 14:39:24', '1', '2026-04-09 14:39:24', '0', 1), (442, 63, 'ITYP202604090003', 3, NULL, '1', '2026-04-09 14:39:25', '1', '2026-04-09 14:39:25', '0', 1), (443, 63, 'ITYP202604090004', 4, NULL, '1', '2026-04-09 14:39:25', '1', '2026-04-09 14:39:25', '0', 1), (444, 63, 'ITYP202604090005', 5, NULL, '1', '2026-04-09 14:47:34', '1', '2026-04-09 14:47:34', '0', 1), (445, 64, 'V00001', 1, NULL, '1', '2026-04-09 19:38:12', '1', '2026-04-09 19:38:12', '0', 1), (446, 64, 'V00002', 2, NULL, '1', '2026-04-09 19:38:13', '1', '2026-04-09 19:38:13', '0', 1), (447, 57, 'FB20260411000001', 1, NULL, '1', '2026-04-11 20:36:42', '1', '2026-04-11 20:36:42', '0', 1), (448, 57, 'FB20260413000001', 1, NULL, '1', '2026-04-13 20:19:49', '1', '2026-04-13 20:19:49', '0', 1), (449, 57, 'FB20260413000002', 2, NULL, '1', '2026-04-13 20:23:44', '1', '2026-04-13 20:23:44', '0', 1), (450, 57, 'FB20260414000001', 1, NULL, '1', '2026-04-14 10:13:50', '1', '2026-04-14 10:13:50', '0', 1), (451, 57, 'FB20260414000002', 2, NULL, '1', '2026-04-14 13:54:58', '1', '2026-04-14 13:54:58', '0', 1), (452, 57, 'FB20260414000003', 3, NULL, '1', '2026-04-14 14:03:09', '1', '2026-04-14 14:03:09', '0', 1), (453, 57, 'FB20260414000004', 4, NULL, '1', '2026-04-14 14:57:52', '1', '2026-04-14 14:57:52', '0', 1), (454, 57, 'FB20260415000001', 1, NULL, '1', '2026-04-15 16:46:52', '1', '2026-04-15 16:46:52', '0', 1), (455, 57, 'FB20260415000002', 2, NULL, '1', '2026-04-15 16:48:13', '1', '2026-04-15 16:48:13', '0', 1), (456, 3, 'SN20260415000001', 1, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (457, 3, 'SN20260415000002', 2, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (458, 3, 'SN20260415000003', 3, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (459, 3, 'SN20260415000004', 4, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (460, 3, 'SN20260415000005', 5, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (461, 3, 'SN20260415000006', 6, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (462, 3, 'SN20260415000007', 7, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (463, 3, 'SN20260415000008', 8, NULL, '1', '2026-04-15 19:31:39', '1', '2026-04-15 19:31:39', '0', 1), (464, 3, 'SN20260415000009', 9, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (465, 3, 'SN20260415000010', 10, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (466, 3, 'SN20260415000011', 11, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (467, 3, 'SN20260415000012', 12, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (468, 3, 'SN20260415000013', 13, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (469, 3, 'SN20260415000014', 14, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (470, 3, 'SN20260415000015', 15, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (471, 3, 'SN20260415000016', 16, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (472, 3, 'SN20260415000017', 17, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (473, 3, 'SN20260415000018', 18, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (474, 3, 'SN20260415000019', 19, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (475, 3, 'SN20260415000020', 20, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (476, 3, 'SN20260415000021', 21, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (477, 3, 'SN20260415000022', 22, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (478, 3, 'SN20260415000023', 23, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (479, 3, 'SN20260415000024', 24, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (480, 3, 'SN20260415000025', 25, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (481, 3, 'SN20260415000026', 26, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (482, 3, 'SN20260415000027', 27, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (483, 3, 'SN20260415000028', 28, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (484, 3, 'SN20260415000029', 29, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (485, 3, 'SN20260415000030', 30, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (486, 3, 'SN20260415000031', 31, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (487, 3, 'SN20260415000032', 32, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (488, 3, 'SN20260415000033', 33, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (489, 3, 'SN20260415000034', 34, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (490, 3, 'SN20260415000035', 35, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (491, 3, 'SN20260415000036', 36, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (492, 3, 'SN20260415000037', 37, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (493, 3, 'SN20260415000038', 38, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (494, 3, 'SN20260415000039', 39, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (495, 3, 'SN20260415000040', 40, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (496, 3, 'SN20260415000041', 41, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (497, 3, 'SN20260415000042', 42, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (498, 3, 'SN20260415000043', 43, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (499, 3, 'SN20260415000044', 44, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (500, 3, 'SN20260415000045', 45, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (501, 3, 'SN20260415000046', 46, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (502, 3, 'SN20260415000047', 47, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (503, 3, 'SN20260415000048', 48, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (504, 3, 'SN20260415000049', 49, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (505, 3, 'SN20260415000050', 50, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (506, 3, 'SN20260415000051', 51, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (507, 3, 'SN20260415000052', 52, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (508, 3, 'SN20260415000053', 53, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (509, 3, 'SN20260415000054', 54, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (510, 3, 'SN20260415000055', 55, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (511, 3, 'SN20260415000056', 56, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (512, 3, 'SN20260415000057', 57, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (513, 3, 'SN20260415000058', 58, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (514, 3, 'SN20260415000059', 59, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (515, 3, 'SN20260415000060', 60, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (516, 3, 'SN20260415000061', 61, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (517, 3, 'SN20260415000062', 62, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (518, 3, 'SN20260415000063', 63, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (519, 3, 'SN20260415000064', 64, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (520, 3, 'SN20260415000065', 65, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (521, 3, 'SN20260415000066', 66, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (522, 3, 'SN20260415000067', 67, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (523, 3, 'SN20260415000068', 68, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (524, 3, 'SN20260415000069', 69, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (525, 3, 'SN20260415000070', 70, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (526, 3, 'SN20260415000071', 71, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (527, 3, 'SN20260415000072', 72, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (528, 3, 'SN20260415000073', 73, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (529, 3, 'SN20260415000074', 74, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (530, 3, 'SN20260415000075', 75, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (531, 3, 'SN20260415000076', 76, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (532, 3, 'SN20260415000077', 77, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (533, 3, 'SN20260415000078', 78, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (534, 3, 'SN20260415000079', 79, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (535, 3, 'SN20260415000080', 80, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (536, 3, 'SN20260415000081', 81, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (537, 3, 'SN20260415000082', 82, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (538, 3, 'SN20260415000083', 83, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (539, 3, 'SN20260415000084', 84, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (540, 3, 'SN20260415000085', 85, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (541, 3, 'SN20260415000086', 86, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (542, 3, 'SN20260415000087', 87, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (543, 3, 'SN20260415000088', 88, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (544, 3, 'SN20260415000089', 89, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (545, 3, 'SN20260415000090', 90, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (546, 3, 'SN20260415000091', 91, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (547, 3, 'SN20260415000092', 92, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (548, 3, 'SN20260415000093', 93, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (549, 3, 'SN20260415000094', 94, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (550, 3, 'SN20260415000095', 95, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (551, 3, 'SN20260415000096', 96, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (552, 3, 'SN20260415000097', 97, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (553, 3, 'SN20260415000098', 98, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (554, 3, 'SN20260415000099', 99, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (555, 3, 'SN20260415000100', 100, NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (556, 60, 'QCT20260415000001', 1, NULL, '1', '2026-04-15 20:36:17', '1', '2026-04-15 20:36:17', '0', 1), (557, 29, 'PI20260415000001', 1, NULL, '1', '2026-04-15 23:51:38', '1', '2026-04-15 23:51:38', '0', 1), (558, 29, 'PI20260415000002', 2, NULL, '1', '2026-04-15 23:51:38', '1', '2026-04-15 23:51:38', '0', 1), (559, 29, 'PI20260415000003', 3, NULL, '1', '2026-04-15 23:51:38', '1', '2026-04-15 23:51:38', '0', 1), (560, 61, 'QR2026041500001', 1, NULL, '1', '2026-04-15 23:54:49', '1', '2026-04-15 23:54:49', '0', 1), (561, 29, 'PI20260415000004', 4, NULL, '1', '2026-04-15 23:55:25', '1', '2026-04-15 23:55:25', '0', 1), (562, 61, 'QR2026041500002', 2, NULL, '1', '2026-04-15 23:56:36', '1', '2026-04-15 23:56:36', '0', 1), (563, 29, 'PI20260416000001', 1, NULL, '1', '2026-04-16 01:48:48', '1', '2026-04-16 01:48:48', '0', 1), (564, 29, 'PI20260416000002', 2, NULL, '1', '2026-04-16 01:48:48', '1', '2026-04-16 01:48:48', '0', 1), (565, 50, 'BX20260416000001', 1, NULL, '1', '2026-04-16 08:20:55', '1', '2026-04-16 08:20:55', '0', 1), (566, 50, 'BX20260416000002', 2, NULL, '1', '2026-04-16 08:23:35', '1', '2026-04-16 08:23:35', '0', 1), (567, 50, 'BX20260416000003', 3, NULL, '1', '2026-04-16 08:25:47', '1', '2026-04-16 08:25:47', '0', 1), (568, 50, 'BX20260416000004', 4, NULL, '1', '2026-04-16 09:15:45', '1', '2026-04-16 09:15:45', '0', 1), (569, 61, 'QR2026041700001', 1, NULL, '1', '2026-04-17 07:33:07', '1', '2026-04-17 07:33:07', '0', 1), (570, 36, 'MISCI20260417001', 1, NULL, '1', '2026-04-17 08:41:35', '1', '2026-04-17 08:41:35', '0', 1), (571, 37, 'MISCR20260417001', 1, NULL, '1', '2026-04-17 08:43:31', '1', '2026-04-17 08:43:31', '0', 1), (572, 45, 'OSI202604170001', 1, NULL, '1', '2026-04-17 09:35:10', '1', '2026-04-17 09:35:10', '0', 1), (573, 45, 'OSI202604170002', 2, NULL, '1', '2026-04-17 09:35:15', '1', '2026-04-17 09:35:15', '0', 1), (574, 12, 'PC202600003', 3, NULL, '1', '2026-04-17 09:38:09', '1', '2026-04-17 09:38:09', '0', 1), (575, 32, 'SN20260417000001', 1, NULL, '1', '2026-04-17 09:44:54', '1', '2026-04-17 09:44:54', '0', 1), (576, 29, 'PI20260417000001', 1, NULL, '1', '2026-04-17 15:27:31', '1', '2026-04-17 15:27:31', '0', 1), (577, 66, 'INDIC00000001', 1, NULL, '1', '2026-05-02 00:53:54', '1', '2026-05-02 00:53:54', '0', 1), (578, 66, 'INDIC00000002', 2, NULL, '1', '2026-05-02 00:53:56', '1', '2026-05-02 00:53:56', '0', 1), (579, 63, 'ITYP202605190001', 1, NULL, '1', '2026-05-19 18:06:21', '1', '2026-05-19 18:06:21', '0', 1), (580, 63, 'ITYP202605190002', 2, NULL, '1', '2026-05-19 18:06:21', '1', '2026-05-19 18:06:21', '0', 1), (581, 63, 'ITYP202605190003', 3, NULL, '1', '2026-05-19 18:06:22', '1', '2026-05-19 18:06:22', '0', 1), (582, 63, 'ITYP202605190004', 4, NULL, '1', '2026-05-19 18:06:23', '1', '2026-05-19 18:06:23', '0', 1), (583, 63, 'ITYP202605190005', 5, NULL, '1', '2026-05-19 22:13:13', '1', '2026-05-19 22:13:13', '0', 1), (584, 63, 'ITYP202605190006', 6, NULL, '1', '2026-05-19 22:13:13', '1', '2026-05-19 22:13:13', '0', 1), (585, 63, 'ITYP202605190007', 7, NULL, '1', '2026-05-19 22:13:37', '1', '2026-05-19 22:13:37', '0', 1), (586, 63, 'ITYP202605190008', 8, NULL, '1', '2026-05-19 22:13:37', '1', '2026-05-19 22:13:37', '0', 1), (587, 18, 'ITEM20260521000001', 1, NULL, '1', '2026-05-21 18:47:57', '1', '2026-05-21 18:47:57', '0', 1), (588, 18, 'ITEM20260521000002', 2, NULL, '1', '2026-05-21 18:47:58', '1', '2026-05-21 18:47:58', '0', 1), (589, 18, 'ITEM20260521000003', 3, NULL, '1', '2026-05-21 18:47:58', '1', '2026-05-21 18:47:58', '0', 1), (590, 18, 'ITEM20260521000004', 4, NULL, '1', '2026-05-21 18:47:58', '1', '2026-05-21 18:47:58', '0', 1), (591, 24, 'TT007', 7, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (592, 24, 'TT008', 8, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (593, 24, 'TT009', 9, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (594, 24, 'TT010', 10, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (595, 24, 'TT011', 11, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (596, 24, 'TT012', 12, NULL, '1', '2026-05-22 08:46:48', '1', '2026-05-22 08:46:48', '0', 1), (597, 24, 'TT013', 13, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (598, 24, 'TT014', 14, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (599, 24, 'TT015', 15, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (600, 24, 'TT016', 16, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (601, 24, 'TT017', 17, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (602, 24, 'TT018', 18, NULL, '1', '2026-05-22 08:46:49', '1', '2026-05-22 08:46:49', '0', 1), (603, 24, 'TT019', 19, NULL, '1', '2026-05-22 08:46:50', '1', '2026-05-22 08:46:50', '0', 1), (604, 24, 'TT020', 20, NULL, '1', '2026-05-22 08:46:50', '1', '2026-05-22 08:46:50', '0', 1), (605, 24, 'TT021', 21, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (606, 24, 'TT022', 22, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (607, 24, 'TT023', 23, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (608, 24, 'TT024', 24, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (609, 24, 'TT025', 25, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (610, 24, 'TT026', 26, NULL, '1', '2026-05-22 08:46:52', '1', '2026-05-22 08:46:52', '0', 1), (611, 24, 'TT027', 27, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (612, 24, 'TT028', 28, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (613, 24, 'TT029', 29, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (614, 24, 'TT030', 30, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (615, 24, 'TT031', 31, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (616, 24, 'TT032', 32, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (617, 24, 'TT033', 33, NULL, '1', '2026-05-22 08:46:53', '1', '2026-05-22 08:46:53', '0', 1), (618, 24, 'TT034', 34, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (619, 24, 'TT035', 35, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (620, 24, 'TT036', 36, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (621, 24, 'TT037', 37, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (622, 24, 'TT038', 38, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (623, 24, 'TT039', 39, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (624, 24, 'TT040', 40, NULL, '1', '2026-05-22 08:46:54', '1', '2026-05-22 08:46:54', '0', 1), (625, 24, 'TT041', 41, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (626, 24, 'TT042', 42, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (627, 24, 'TT043', 43, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (628, 24, 'TT044', 44, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (629, 24, 'TT045', 45, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (630, 24, 'TT046', 46, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (631, 24, 'TT047', 47, NULL, '1', '2026-05-22 08:46:55', '1', '2026-05-22 08:46:55', '0', 1), (632, 24, 'TT048', 48, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (633, 24, 'TT049', 49, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (634, 24, 'TT050', 50, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (635, 24, 'TT051', 51, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (636, 24, 'TT052', 52, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (637, 24, 'TT053', 53, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (638, 24, 'TT054', 54, NULL, '1', '2026-05-22 08:46:56', '1', '2026-05-22 08:46:56', '0', 1), (639, 24, 'TT055', 55, NULL, '1', '2026-05-22 08:46:57', '1', '2026-05-22 08:46:57', '0', 1), (640, 25, 'TL202605220001', 1, NULL, '1', '2026-05-22 08:47:09', '1', '2026-05-22 08:47:09', '0', 1), (641, 25, 'TL202605220002', 2, NULL, '1', '2026-05-22 08:47:12', '1', '2026-05-22 08:47:12', '0', 1), (642, 25, 'TL202605220003', 3, NULL, '1', '2026-05-22 08:47:12', '1', '2026-05-22 08:47:12', '0', 1), (643, 25, 'TL202605220004', 4, NULL, '1', '2026-05-22 08:47:13', '1', '2026-05-22 08:47:13', '0', 1), (644, 25, 'TL202605220005', 5, NULL, '1', '2026-05-22 08:47:13', '1', '2026-05-22 08:47:13', '0', 1), (645, 25, 'TL202605220006', 6, NULL, '1', '2026-05-22 08:47:13', '1', '2026-05-22 08:47:13', '0', 1), (646, 25, 'TL202605220007', 7, NULL, '1', '2026-05-22 08:47:13', '1', '2026-05-22 08:47:13', '0', 1), (647, 25, 'TL202605220008', 8, NULL, '1', '2026-05-22 08:47:14', '1', '2026-05-22 08:47:14', '0', 1), (648, 25, 'TL202605220009', 9, NULL, '1', '2026-05-22 08:47:21', '1', '2026-05-22 08:47:21', '0', 1), (649, 25, 'TL202605220010', 10, NULL, '1', '2026-05-22 08:47:21', '1', '2026-05-22 08:47:21', '0', 1), (650, 25, 'TL202605220011', 11, NULL, '1', '2026-05-22 08:47:21', '1', '2026-05-22 08:47:21', '0', 1), (651, 25, 'TL202605220012', 12, NULL, '1', '2026-05-22 08:47:22', '1', '2026-05-22 08:47:22', '0', 1), (652, 25, 'TL202605220013', 13, NULL, '1', '2026-05-22 08:47:22', '1', '2026-05-22 08:47:22', '0', 1), (653, 25, 'TL202605220014', 14, NULL, '1', '2026-05-22 08:47:22', '1', '2026-05-22 08:47:22', '0', 1), (654, 18, 'ITEM20260522000001', 1, NULL, '1', '2026-05-22 08:47:38', '1', '2026-05-22 08:47:38', '0', 1), (655, 18, 'ITEM20260522000002', 2, NULL, '1', '2026-05-22 08:47:40', '1', '2026-05-22 08:47:40', '0', 1), (656, 18, 'ITEM20260522000003', 3, NULL, '1', '2026-05-22 08:47:40', '1', '2026-05-22 08:47:40', '0', 1), (657, 18, 'ITEM20260522000004', 4, NULL, '1', '2026-05-22 08:47:41', '1', '2026-05-22 08:47:41', '0', 1), (658, 18, 'ITEM20260522000005', 5, NULL, '1', '2026-05-22 08:47:41', '1', '2026-05-22 08:47:41', '0', 1), (659, 18, 'ITEM20260522000006', 6, NULL, '1', '2026-05-22 08:47:41', '1', '2026-05-22 08:47:41', '0', 1), (660, 18, 'ITEM20260522000007', 7, NULL, '1', '2026-05-22 08:47:41', '1', '2026-05-22 08:47:41', '0', 1), (661, 18, 'ITEM20260522000008', 8, NULL, '1', '2026-05-22 08:47:41', '1', '2026-05-22 08:47:41', '0', 1), (662, 18, 'ITEM20260522000009', 9, NULL, '1', '2026-05-22 08:47:42', '1', '2026-05-22 08:47:42', '0', 1), (663, 18, 'ITEM20260522000010', 10, NULL, '1', '2026-05-22 08:47:45', '1', '2026-05-22 08:47:45', '0', 1), (664, 18, 'ITEM20260522000011', 11, NULL, '1', '2026-05-22 08:47:45', '1', '2026-05-22 08:47:45', '0', 1), (665, 18, 'ITEM20260522000012', 12, NULL, '1', '2026-05-22 08:47:46', '1', '2026-05-22 08:47:46', '0', 1), (666, 18, 'ITEM20260522000013', 13, NULL, '1', '2026-05-22 08:47:48', '1', '2026-05-22 08:47:48', '0', 1), (667, 26, 'IR20260522000001', 1, NULL, '1', '2026-05-22 08:47:58', '1', '2026-05-22 08:47:58', '0', 1), (668, 26, 'IR20260522000002', 2, NULL, '1', '2026-05-22 08:47:59', '1', '2026-05-22 08:47:59', '0', 1), (669, 24, 'TT056', 56, NULL, '1', '2026-05-22 08:50:15', '1', '2026-05-22 08:50:15', '0', 1), (670, 24, 'TT057', 57, NULL, '1', '2026-05-22 08:50:16', '1', '2026-05-22 08:50:16', '0', 1), (671, 24, 'TT058', 58, NULL, '1', '2026-05-22 08:50:16', '1', '2026-05-22 08:50:16', '0', 1), (672, 24, 'TT059', 59, NULL, '1', '2026-05-22 08:50:16', '1', '2026-05-22 08:50:16', '0', 1), (673, 24, 'TT060', 60, NULL, '1', '2026-05-22 08:50:16', '1', '2026-05-22 08:50:16', '0', 1), (674, 24, 'TT061', 61, NULL, '1', '2026-05-24 18:33:01', '1', '2026-05-24 18:33:01', '0', 1), (675, 24, 'TT062', 62, NULL, '1', '2026-05-24 18:33:01', '1', '2026-05-24 18:33:01', '0', 1), (676, 47, 'MT005', 5, NULL, '1', '2026-05-24 20:24:07', '1', '2026-05-24 20:24:07', '0', 1), (677, 47, 'MT006', 6, NULL, '1', '2026-05-24 20:24:08', '1', '2026-05-24 20:24:08', '0', 1), (678, 47, 'MT007', 7, NULL, '1', '2026-05-24 20:24:08', '1', '2026-05-24 20:24:08', '0', 1), (679, 47, 'MT008', 8, NULL, '1', '2026-05-24 20:24:08', '1', '2026-05-24 20:24:08', '0', 1), (680, 49, 'CHP00001', 1, NULL, '1', '2026-05-24 20:24:53', '1', '2026-05-24 20:24:53', '0', 1), (681, 49, 'CHP00002', 2, NULL, '1', '2026-05-24 20:24:53', '1', '2026-05-24 20:24:53', '0', 1), (682, 49, 'CHP00003', 3, NULL, '1', '2026-05-24 20:24:54', '1', '2026-05-24 20:24:54', '0', 1), (683, 49, 'CHP00004', 4, NULL, '1', '2026-05-24 20:24:54', '1', '2026-05-24 20:24:54', '0', 1), (684, 49, 'CHP00005', 5, NULL, '1', '2026-05-24 20:24:54', '1', '2026-05-24 20:24:54', '0', 1), (685, 49, 'CHP00006', 6, NULL, '1', '2026-05-24 20:24:54', '1', '2026-05-24 20:24:54', '0', 1), (686, 49, 'CHP00007', 7, NULL, '1', '2026-05-24 20:24:54', '1', '2026-05-24 20:24:54', '0', 1), (687, 49, 'CHP00008', 8, NULL, '1', '2026-05-24 20:24:55', '1', '2026-05-24 20:24:55', '0', 1), (688, 49, 'CHP00009', 9, NULL, '1', '2026-05-24 20:24:55', '1', '2026-05-24 20:24:55', '0', 1), (689, 49, 'CHP00010', 10, NULL, '1', '2026-05-24 20:24:55', '1', '2026-05-24 20:24:55', '0', 1), (690, 55, 'PROC000005', 5, NULL, '1', '2026-05-25 09:02:51', '1', '2026-05-25 09:02:51', '0', 1), (691, 55, 'PROC000006', 6, NULL, '1', '2026-05-25 09:02:52', '1', '2026-05-25 09:02:52', '0', 1), (692, 55, 'PROC000007', 7, NULL, '1', '2026-05-25 09:02:52', '1', '2026-05-25 09:02:52', '0', 1), (693, 55, 'PROC000008', 8, NULL, '1', '2026-05-25 09:02:52', '1', '2026-05-25 09:02:52', '0', 1), (694, 55, 'PROC000009', 9, NULL, '1', '2026-05-25 09:02:52', '1', '2026-05-25 09:02:52', '0', 1), (695, 55, 'PROC000010', 10, NULL, '1', '2026-05-25 09:02:53', '1', '2026-05-25 09:02:53', '0', 1), (696, 57, 'FB20260526000001', 1, NULL, '1', '2026-05-26 00:37:42', '1', '2026-05-26 00:37:42', '0', 1), (697, 57, 'FB20260526000002', 2, NULL, '1', '2026-05-26 08:35:32', '1', '2026-05-26 08:35:32', '0', 1), (698, 57, 'FB20260526000003', 3, NULL, '1', '2026-05-26 21:32:08', '1', '2026-05-26 21:32:08', '0', 1), (699, 57, 'FB20260526000004', 4, NULL, '1', '2026-05-26 21:32:09', '1', '2026-05-26 21:32:09', '0', 1), (700, 59, 'DEFECT00000005', 5, NULL, '1', '2026-05-27 06:38:25', '1', '2026-05-27 06:38:25', '0', 1), (701, 59, 'DEFECT00000006', 6, NULL, '1', '2026-05-27 06:38:25', '1', '2026-05-27 06:38:25', '0', 1), (702, 59, 'DEFECT00000007', 7, NULL, '1', '2026-05-27 06:38:25', '1', '2026-05-27 06:38:25', '0', 1), (703, 59, 'DEFECT00000008', 8, NULL, '1', '2026-05-27 06:38:25', '1', '2026-05-27 06:38:25', '0', 1), (704, 59, 'DEFECT00000009', 9, NULL, '1', '2026-05-27 06:38:25', '1', '2026-05-27 06:38:25', '0', 1), (705, 59, 'DEFECT00000010', 10, NULL, '1', '2026-05-27 06:38:26', '1', '2026-05-27 06:38:26', '0', 1), (706, 59, 'DEFECT00000011', 11, NULL, '1', '2026-05-27 06:38:26', '1', '2026-05-27 06:38:26', '0', 1), (707, 59, 'DEFECT00000012', 12, NULL, '1', '2026-05-27 06:38:26', '1', '2026-05-27 06:38:26', '0', 1), (708, 59, 'DEFECT00000013', 13, NULL, '1', '2026-05-27 06:38:26', '1', '2026-05-27 06:38:26', '0', 1), (709, 21, 'WH202605270001', 1, NULL, '1', '2026-05-27 06:41:53', '1', '2026-05-27 06:41:53', '0', 1), (710, 21, 'WH202605270002', 2, NULL, '1', '2026-05-27 06:41:53', '1', '2026-05-27 06:41:53', '0', 1), (711, 21, 'WH202605270003', 3, NULL, '1', '2026-05-27 06:42:10', '1', '2026-05-27 06:42:10', '0', 1), (712, 60, 'QCT20260528000001', 1, NULL, '1', '2026-05-28 23:56:39', '1', '2026-05-28 23:56:39', '0', 1), (713, 14, 'IQC20260529001', 1, NULL, '1', '2026-05-29 09:56:16', '1', '2026-05-29 09:56:16', '0', 1), (714, 4, 'PKG202605290001', 1, NULL, '1', '2026-05-29 23:55:50', '1', '2026-05-29 23:55:50', '0', 1), (715, 4, 'PKG202605290002', 2, NULL, '1', '2026-05-29 23:55:51', '1', '2026-05-29 23:55:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_auto_code_record_seq;
CREATE SEQUENCE mes_md_auto_code_record_seq
    START 716;

-- ----------------------------
-- Table structure for mes_md_auto_code_rule
-- ----------------------------
DROP TABLE IF EXISTS mes_md_auto_code_rule;
CREATE TABLE mes_md_auto_code_rule (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  description varchar(500) NULL DEFAULT NULL,
  max_length int4 NULL DEFAULT NULL,
  padded bool NOT NULL DEFAULT '0',
  padded_char varchar(20) NULL DEFAULT NULL,
  padded_method int2 NULL DEFAULT 1,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_auto_code_rule ADD CONSTRAINT pk_mes_md_auto_code_rule PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_md_auto_code_rule_01 ON mes_md_auto_code_rule (code, deleted, tenant_id);

COMMENT ON COLUMN mes_md_auto_code_rule.id IS '规则 ID';
COMMENT ON COLUMN mes_md_auto_code_rule.code IS '规则编码';
COMMENT ON COLUMN mes_md_auto_code_rule.name IS '规则名称';
COMMENT ON COLUMN mes_md_auto_code_rule.description IS '描述';
COMMENT ON COLUMN mes_md_auto_code_rule.max_length IS '最大长度';
COMMENT ON COLUMN mes_md_auto_code_rule.padded IS '是否补齐';
COMMENT ON COLUMN mes_md_auto_code_rule.padded_char IS '补齐字符';
COMMENT ON COLUMN mes_md_auto_code_rule.padded_method IS '补齐方式';
COMMENT ON COLUMN mes_md_auto_code_rule.status IS '状态';
COMMENT ON COLUMN mes_md_auto_code_rule.remark IS '备注';
COMMENT ON COLUMN mes_md_auto_code_rule.creator IS '创建者';
COMMENT ON COLUMN mes_md_auto_code_rule.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_auto_code_rule.updater IS '更新者';
COMMENT ON COLUMN mes_md_auto_code_rule.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_auto_code_rule.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_auto_code_rule.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_auto_code_rule IS 'MES 编码规则表';

-- ----------------------------
-- Records of mes_md_auto_code_rule
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_auto_code_rule (id, code, name, description, max_length, padded, padded_char, padded_method, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, '132', '323', 'QQQ', 20, '0', '0', 1, 0, 'XXX', '1', '2026-03-04 23:47:19', '1', '2026-03-05 00:52:50', '0', 1), (2, '312', '333', '444', 5, '0', NULL, 1, 0, NULL, '1', '2026-03-05 01:05:45', '1', '2026-03-05 01:05:45', '0', 1), (3, 'WM_SN_CODE', 'SN 码', 'SN 码自动生成规则', NULL, '0', NULL, 1, 0, NULL, '1', '2026-03-05 01:29:24', '1', '2026-03-23 14:58:01', '0', 1), (4, 'WM_PACKAGE_CODE', '装箱单编码', NULL, NULL, '0', NULL, 1, 0, NULL, '1', '2026-03-08 04:44:00', '1', '2026-03-23 14:58:01', '0', 1), (12, 'WM_BATCH_CODE', '批次编码', '批次编码自动生成规则', 11, '0', NULL, 1, 0, NULL, '1', '2026-03-13 15:36:41', '1', '2026-03-23 14:58:01', '0', 1), (13, 'PRO_TASK_CODE', '生产任务编码', '生产任务编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-15 14:42:59', '1', '2026-03-23 14:58:01', '0', 1), (14, 'QC_IQC_CODE', '来料检验单编码', '来料检验单（IQC）自动编码规则', 14, '0', '0', 1, 0, NULL, '', '2026-03-23 14:52:16', '', '2026-03-23 15:03:11', '0', 1), (15, 'QC_IPQC_CODE', '过程检验单编码', '过程检验单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-24 13:30:06', '1', '2026-03-24 13:30:06', '0', 1), (16, 'QC_RQC_CODE', '退货检验单编码', '退货检验单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-26 05:09:58', '1', '2026-03-26 05:09:58', '0', 1), (17, 'QC_OQC_CODE', '出货检验单编码', 'OQC + 日期 + 流水号', 14, '0', '0', 1, 0, NULL, '1', '2026-03-27 09:00:03', '1', '2026-03-27 09:00:03', '0', 1), (18, 'MD_ITEM_CODE', '物料编码', '物料编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-28 00:56:57', '1', '2026-03-28 00:56:57', '0', 1), (19, 'MD_CLIENT_CODE', '客户编码', '客户编码规则：前缀C + 5位流水号', 6, '0', NULL, NULL, 0, NULL, 'admin', '2026-03-28 03:41:15', 'admin', '2026-03-28 09:12:15', '0', 1), (20, 'MD_WORKSTATION_CODE', '工作站编码', '工作站编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-28 09:41:24', '1', '2026-03-28 09:41:24', '0', 1), (21, 'WM_WAREHOUSE_CODE', '仓库编码', '仓库编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (22, 'WM_LOCATION_CODE', '库区编码', '库区编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (23, 'WM_AREA_CODE', '库位编码', '库位编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-28 12:48:18', '1', '2026-03-28 12:48:18', '0', 1), (24, 'TM_TOOL_TYPE_CODE', '工具类型编码', '工具类型自动编码规则', 5, '0', NULL, NULL, 0, NULL, 'admin', '2026-03-29 01:27:07', 'admin', '2026-03-29 01:27:07', '0', 1), (25, 'TM_TOOL_CODE', '工具编码', '工具编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-29 01:56:00', '1', '2026-03-29 01:56:00', '0', 1), (26, 'WM_ITEM_RECEIPT_CODE', '采购入库单编码', '采购入库单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-29 02:48:40', '1', '2026-03-29 02:48:40', '0', 1), (27, 'WM_ARRIVAL_NOTICE_CODE', '到货通知单编码', '到货通知单自动编码规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-29 11:10:40', '1', '2026-03-29 11:10:40', '0', 1), (28, 'WM_RETURN_VENDOR_CODE', '采购退货单编码', '采购退货单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-29 13:01:34', '1', '2026-03-29 13:01:34', '0', 1), (29, 'WM_PRODUCT_ISSUE_CODE', '生产领料出库单编码', '生产领料出库单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-30 01:44:21', '1', '2026-03-30 01:44:21', '0', 1), (30, 'WM_RETURN_ISSUE_CODE', '生产退料单编码', '生产退料单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-30 03:49:52', '1', '2026-03-30 03:49:52', '0', 1), (31, 'PRODUCTRECPT_CODE', '产品入库单编码', '产品入库单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-30 04:35:05', '1', '2026-03-30 04:35:05', '0', 1), (32, 'WM_SALES_NOTICE_CODE', '发货通知单编码', '发货通知单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-30 05:05:32', '1', '2026-03-30 05:05:32', '0', 1), (34, 'WM_RETURN_SALES_CODE', '销售退货单编码规则', '格式: RS + yyyyMMdd + 3位流水号', 13, '0', NULL, 1, 0, NULL, '1', '2026-03-30 11:11:15', '1', '2026-03-30 11:11:15', '0', 1), (35, 'WM_PRODUCT_SALES_CODE', '销售出库单编码', '销售出库单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-03-30 12:04:29', '1', '2026-03-30 12:04:29', '0', 1), (36, 'WM_MISC_ISSUE_CODE', '杂项出库单编码', NULL, 16, '0', NULL, 1, 0, NULL, '1', '2026-03-30 15:19:37', '1', '2026-03-30 15:19:37', '0', 1), (37, 'WM_MISC_RECEIPT_CODE', '杂项入库单编码', NULL, 16, '0', NULL, 1, 0, NULL, '1', '2026-03-31 01:48:51', '1', '2026-03-31 01:48:51', '0', 1), (38, 'TRANSFER_CODE', '转移单编码规则', NULL, 13, '0', NULL, 1, 0, NULL, '', '2026-03-31 07:57:32', '', '2026-03-31 08:01:53', '0', 1), (43, 'WM_STOCK_TAKING_PLAN_CODE', '盘点方案编码', '盘点方案编码规则', 20, '0', NULL, 1, 0, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (44, 'WM_STOCK_TAKING_CODE', '盘点任务编码', '盘点任务编码规则', 20, '0', NULL, 1, 0, NULL, '1', '2026-03-31 10:17:02', '1', '2026-03-31 10:17:02', '0', 1), (45, 'WM_OUTSOURCE_ISSUE_CODE', '外协发料单编码', '外协发料单编码规则', 20, '0', NULL, 1, 0, NULL, '1', '2026-03-31 14:39:35', '1', '2026-03-31 14:39:35', '0', 1), (46, 'CAL_PLAN_CODE', '排班计划编码', '排班计划编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-01 17:25:54', '1', '2026-04-01 17:25:54', '0', 1), (47, 'DV_MACHINERY_TYPE_CODE', '设备类型编码', '设备类型编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-02 07:02:16', '1', '2026-04-09 07:29:15', '0', 1), (48, 'DV_MACHINERY_CODE', '设备编码', '设备编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-02 14:31:05', '1', '2026-04-02 14:31:05', '0', 1), (49, 'DV_CHECK_PLAN_CODE', '点检保养方案编码', '点检保养方案编码自动生成规则', NULL, '0', NULL, 1, 0, NULL, '1', '2026-04-02 16:54:57', '1', '2026-04-02 16:54:57', '0', 1), (50, 'DV_REPAIR_CODE', '维修单编码', '维修单编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-03 16:42:51', '1', '2026-04-03 16:42:51', '0', 1), (54, 'PRO_WORK_ORDER_CODE', '生产工单编码', NULL, 20, '0', NULL, 1, 0, NULL, '1', '2026-04-04 02:34:56', '', '2026-04-04 02:34:56', '0', 1), (55, 'PRO_PROCESS_CODE', '工序编码', '工序编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-04 03:32:16', '1', '2026-04-04 03:32:16', '0', 1), (56, 'PRO_ROUTE_CODE', '工艺路线编码', '工艺路线单的自动编码规则', 50, '0', '0', 1, 0, NULL, '1', '2026-04-04 08:37:12', '1', '2026-04-04 08:37:12', '0', 1), (57, 'PRO_FEEDBACK_CODE', '生产报工单编码', '生产报工单自动编码规则', NULL, '0', NULL, 1, 0, NULL, '1', '2026-04-04 09:47:29', '1', '2026-04-04 09:47:29', '0', 1), (58, 'PRO_CARD_CODE', '流转卡编码', '生产流转卡自动编码规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-04 12:18:15', '1', '2026-04-04 12:18:15', '0', 1), (59, 'QC_DEFECT_CODE', '缺陷类型编码', '缺陷类型编码自动生成规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-04 12:47:23', '1', '2026-04-04 12:47:23', '0', 1), (60, 'QC_TEMPLATE_CODE', '质检方案编码', '质检方案自动编码规则', NULL, '0', NULL, NULL, 0, NULL, '1', '2026-04-04 14:36:43', '1', '2026-04-04 14:36:43', '0', 1), (61, 'QC_INDICATOR_RESULT_CODE', '样品检验结果编码', '自动生成', 20, '0', NULL, 1, 0, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (62, 'WM_OUTSOURCE_RECEIPT_CODE', '外协入库单编码', '自动生成', 20, '0', NULL, 1, 0, '', '1', '2026-04-04 16:31:07', '1', '2026-04-04 16:34:26', '0', 1), (63, 'MD_ITEM_TYPE_CODE', '物料分类编码', '物料/产品分类编码', 0, '0', NULL, 1, 0, NULL, '', '2026-04-09 06:38:49', '', '2026-04-09 06:38:49', '0', 1), (64, 'MD_VENDOR_CODE', '车间编码规则', '车间编码自动生成规则', 6, '0', NULL, NULL, 0, NULL, '', '2026-04-09 11:36:40', '1', '2026-04-13 17:27:26', '0', 1), (65, 'MD_WORKSHOP_CODE', 'è½¦é—´ç¼–ç ', 'è½¦é—´æµæ°´å·: W + 4ä½åºåˆ—å·', 5, '0', NULL, 1, 0, NULL, '', '2026-04-10 01:27:53', '1', '2026-04-13 17:25:58', '1', 1), (66, 'QC_INDICATOR_CODE', '检测项编码', NULL, NULL, '0', NULL, 1, 0, NULL, '1', '2026-05-01 16:47:19', '1', '2026-05-01 16:47:19', '0', 1), (67, 'CAL_TEAM_CODE', '班组编码', '班组设置编码规则', 8, '0', NULL, NULL, 0, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (68, 'DV_SUBJECT_CODE', '点检保养项目编码', '点检保养项目编码规则', 6, '0', NULL, NULL, 0, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1), (69, 'MD_WORKSHOP_CODE', '车间编码', '车间设置编码规则', 5, '0', NULL, NULL, 0, '补充 MES 初始化编码规则', '1', '2026-06-12 14:06:49', '1', '2026-06-12 14:06:49', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_auto_code_rule_seq;
CREATE SEQUENCE mes_md_auto_code_rule_seq
    START 70;

-- ----------------------------
-- Table structure for mes_md_client
-- ----------------------------
DROP TABLE IF EXISTS mes_md_client;
CREATE TABLE mes_md_client (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  nickname varchar(255) NULL DEFAULT NULL,
  english_name varchar(255) NULL DEFAULT NULL,
  description varchar(500) NULL DEFAULT NULL,
  logo varchar(255) NULL DEFAULT NULL,
  type int2 NOT NULL,
  address varchar(500) NULL DEFAULT NULL,
  website varchar(255) NULL DEFAULT NULL,
  email varchar(255) NULL DEFAULT NULL,
  telephone varchar(64) NULL DEFAULT NULL,
  contact1_name varchar(64) NULL DEFAULT NULL,
  contact1_telephone varchar(64) NULL DEFAULT NULL,
  contact1_email varchar(255) NULL DEFAULT NULL,
  contact2_name varchar(64) NULL DEFAULT NULL,
  contact2_telephone varchar(64) NULL DEFAULT NULL,
  contact2_email varchar(255) NULL DEFAULT NULL,
  credit_code varchar(64) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_client ADD CONSTRAINT pk_mes_md_client PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_client.id IS '客户编号';
COMMENT ON COLUMN mes_md_client.code IS '客户编码';
COMMENT ON COLUMN mes_md_client.name IS '客户名称';
COMMENT ON COLUMN mes_md_client.nickname IS '客户简称';
COMMENT ON COLUMN mes_md_client.english_name IS '客户英文名称';
COMMENT ON COLUMN mes_md_client.description IS '客户简介';
COMMENT ON COLUMN mes_md_client.logo IS '客户LOGO地址';
COMMENT ON COLUMN mes_md_client.type IS '客户类型';
COMMENT ON COLUMN mes_md_client.address IS '客户地址';
COMMENT ON COLUMN mes_md_client.website IS '客户官网地址';
COMMENT ON COLUMN mes_md_client.email IS '客户邮箱地址';
COMMENT ON COLUMN mes_md_client.telephone IS '客户电话';
COMMENT ON COLUMN mes_md_client.contact1_name IS '联系人1';
COMMENT ON COLUMN mes_md_client.contact1_telephone IS '联系人1-电话';
COMMENT ON COLUMN mes_md_client.contact1_email IS '联系人1-邮箱';
COMMENT ON COLUMN mes_md_client.contact2_name IS '联系人2';
COMMENT ON COLUMN mes_md_client.contact2_telephone IS '联系人2-电话';
COMMENT ON COLUMN mes_md_client.contact2_email IS '联系人2-邮箱';
COMMENT ON COLUMN mes_md_client.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN mes_md_client.status IS '状态';
COMMENT ON COLUMN mes_md_client.remark IS '备注';
COMMENT ON COLUMN mes_md_client.creator IS '创建者';
COMMENT ON COLUMN mes_md_client.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_client.updater IS '更新者';
COMMENT ON COLUMN mes_md_client.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_client.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_client.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_client IS 'MES 客户表';

-- ----------------------------
-- Records of mes_md_client
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_client (id, code, name, nickname, english_name, description, logo, type, address, website, email, telephone, contact1_name, contact1_telephone, contact1_email, contact2_name, contact2_telephone, contact2_email, credit_code, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'C00184', '比亚迪股份有限公司', '比亚迪', 'BYD', '比亚迪品牌诞生于深圳', '', 1, '深圳南山区无名路12号', 'https://www.bydglobal.com', 'salse@bydglobal.com', '123432222', '张三', '122212312', 's1@bydglobal.com', '李四', '1132323232', 's2@bydglobal.com', '11212121', 0, '', '1', '2026-02-15 15:06:50', '1', '2026-02-15 15:06:50', '0', 1), (207, 'C00197', '博世', '博世', 'BOSCH', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-02-15 15:06:50', '1', '2026-02-15 15:06:50', '0', 1), (208, 'C00198', '德力西电气', '德力西', 'DELIXI', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-02-15 15:06:50', '1', '2026-02-15 15:06:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_client_seq;
CREATE SEQUENCE mes_md_client_seq
    START 209;

-- ----------------------------
-- Table structure for mes_md_item
-- ----------------------------
DROP TABLE IF EXISTS mes_md_item;
CREATE TABLE mes_md_item (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  specification varchar(500) NULL DEFAULT NULL,
  unit_measure_id int8 NOT NULL DEFAULT 0,
  item_type_id int8 NOT NULL DEFAULT 0,
  status int2 NOT NULL DEFAULT 0,
  safe_stock_flag bool NOT NULL DEFAULT '0',
  min_stock numeric(12,4) NOT NULL DEFAULT 0.0000,
  max_stock numeric(12,4) NOT NULL DEFAULT 0.0000,
  high_value bool NOT NULL DEFAULT '0',
  batch_flag bool NOT NULL DEFAULT '1',
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_item ADD CONSTRAINT pk_mes_md_item PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_item.id IS '物料编号';
COMMENT ON COLUMN mes_md_item.code IS '物料编码';
COMMENT ON COLUMN mes_md_item.name IS '物料名称';
COMMENT ON COLUMN mes_md_item.specification IS '规格型号';
COMMENT ON COLUMN mes_md_item.unit_measure_id IS '计量单位编号';
COMMENT ON COLUMN mes_md_item.item_type_id IS '物料分类编号';
COMMENT ON COLUMN mes_md_item.status IS '状态';
COMMENT ON COLUMN mes_md_item.safe_stock_flag IS '是否启用安全库存';
COMMENT ON COLUMN mes_md_item.min_stock IS '最低库存量';
COMMENT ON COLUMN mes_md_item.max_stock IS '最高库存量';
COMMENT ON COLUMN mes_md_item.high_value IS '是否高值物料';
COMMENT ON COLUMN mes_md_item.batch_flag IS '是否启用批次管理';
COMMENT ON COLUMN mes_md_item.remark IS '备注';
COMMENT ON COLUMN mes_md_item.creator IS '创建者';
COMMENT ON COLUMN mes_md_item.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_item.updater IS '更新者';
COMMENT ON COLUMN mes_md_item.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_item.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_item.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_item IS 'MES 物料产品表';

-- ----------------------------
-- Records of mes_md_item
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_item (id, code, name, specification, unit_measure_id, item_type_id, status, safe_stock_flag, min_stock, max_stock, high_value, batch_flag, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (69, 'IF2022082437', '色粉【黑色】', '黑色2', 201, 275, 0, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:44:21', 'admin', '2026-02-15 14:07:59', '0', 1), (70, 'IF2022082432', 'PVC颗粒', '透明', 200, 275, 0, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:44:59', 'admin', '2026-02-15 14:07:59', '0', 1), (71, 'IF2022082403', '色粉【蓝色】', '蓝色', 201, 275, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:45:24', 'admin', '2026-02-15 14:07:59', '0', 1), (72, 'IF2022082404', '钢筋', '100mm X  5mm', 204, 274, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:46:06', 'testuser', '2026-02-15 14:07:59', '0', 1), (73, 'IF2022082428', '螺丝刀刀柄【蓝色】', '10CM', 202, 276, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:52:09', 'admin', '2026-02-15 14:07:59', '0', 1), (74, 'IF2022082416', '螺丝刀刀头', '15CM', 202, 276, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:52:35', 'admin', '2026-02-15 14:07:59', '0', 1), (75, 'IF2022082439', '螺丝刀【蓝色，一字型】', '蓝色，一字型，3.2x75mm', 202, 277, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2022-08-24 21:52:46', 'admin', '2026-02-15 14:07:59', '0', 1), (94, 'IF20250312003', '小包装盒', NULL, 202, 282, 0, '1', 1000.0000, 50000.0000, '0', '0', '', 'admin', '2025-03-12 10:36:41', 'admin', '2026-02-15 14:07:59', '0', 1), (95, 'IF20250312004', '大包装箱', NULL, 202, 282, 0, '1', 500.0000, 1000.0000, '0', '0', '', 'admin', '2025-03-12 10:37:08', '', '2026-02-15 14:07:59', '0', 1), (96, 'IF20250312005', '螺丝刀刀柄【黑色】', NULL, 202, 276, 1, '0', 0.0000, 0.0000, '0', '1', '', 'admin', '2025-03-12 10:39:32', '1', '2026-02-16 17:39:22', '0', 1), (100, 'SF-BLACK-001', 'ABC', 'EFG', 200, 272, 0, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-10 11:14:37', '1', '2026-03-15 00:36:38', '0', 1), (101, 'ABC-A', 'ABC-A', NULL, 200, 275, 0, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-15 00:35:28', '1', '2026-03-15 00:35:28', '0', 1), (102, 'ABC-B', 'ABC-B', NULL, 200, 274, 0, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-15 00:35:46', '1', '2026-03-15 00:36:25', '0', 1), (103, 'ABC-CCC', 'ABC-CCC', NULL, 200, 273, 0, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-15 10:12:30', '1', '2026-03-15 10:15:59', '0', 1), (104, 'ITEM20260328000007', 'AAA', NULL, 200, 200, 1, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-28 09:52:57', '1', '2026-03-28 10:29:14', '0', 1), (105, 'ITEM20260328000008', '奥特曼', NULL, 200, 274, 1, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-28 10:34:37', '1', '2026-03-28 10:34:37', '0', 1), (106, 'ITEM20260328000009', '剑来', '呃呃呃', 200, 274, 1, '0', 0.0000, 0.0000, '0', '1', '', '1', '2026-03-28 10:37:51', '1', '2026-05-28 09:35:45', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_item_seq;
CREATE SEQUENCE mes_md_item_seq
    START 107;

-- ----------------------------
-- Table structure for mes_md_item_batch_config
-- ----------------------------
DROP TABLE IF EXISTS mes_md_item_batch_config;
CREATE TABLE mes_md_item_batch_config (
    id int8 NOT NULL,
  item_id int8 NOT NULL,
  produce_date_flag bool NOT NULL DEFAULT '0',
  expire_date_flag bool NOT NULL DEFAULT '0',
  receipt_date_flag bool NOT NULL DEFAULT '0',
  vendor_flag bool NOT NULL DEFAULT '0',
  client_flag bool NOT NULL DEFAULT '0',
  sales_order_code_flag bool NOT NULL DEFAULT '0',
  purchase_order_code_flag bool NOT NULL DEFAULT '0',
  work_order_flag bool NOT NULL DEFAULT '0',
  task_flag bool NOT NULL DEFAULT '0',
  workstation_flag bool NOT NULL DEFAULT '0',
  tool_flag bool NOT NULL DEFAULT '0',
  mold_flag bool NOT NULL DEFAULT '0',
  lot_number_flag bool NOT NULL DEFAULT '0',
  quality_status_flag bool NOT NULL DEFAULT '0',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_item_batch_config ADD CONSTRAINT pk_mes_md_item_batch_config PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_md_item_batch_config_01 ON mes_md_item_batch_config (item_id);

COMMENT ON COLUMN mes_md_item_batch_config.id IS '编号';
COMMENT ON COLUMN mes_md_item_batch_config.item_id IS '物料编号';
COMMENT ON COLUMN mes_md_item_batch_config.produce_date_flag IS '批次属性-生产日期';
COMMENT ON COLUMN mes_md_item_batch_config.expire_date_flag IS '批次属性-有效期';
COMMENT ON COLUMN mes_md_item_batch_config.receipt_date_flag IS '批次属性-入库日期';
COMMENT ON COLUMN mes_md_item_batch_config.vendor_flag IS '批次属性-供应商';
COMMENT ON COLUMN mes_md_item_batch_config.client_flag IS '批次属性-客户';
COMMENT ON COLUMN mes_md_item_batch_config.sales_order_code_flag IS '批次属性-销售订单编号';
COMMENT ON COLUMN mes_md_item_batch_config.purchase_order_code_flag IS '批次属性-采购订单编号';
COMMENT ON COLUMN mes_md_item_batch_config.work_order_flag IS '批次属性-生产工单';
COMMENT ON COLUMN mes_md_item_batch_config.task_flag IS '批次属性-生产任务';
COMMENT ON COLUMN mes_md_item_batch_config.workstation_flag IS '批次属性-工作站';
COMMENT ON COLUMN mes_md_item_batch_config.tool_flag IS '批次属性-工具';
COMMENT ON COLUMN mes_md_item_batch_config.mold_flag IS '批次属性-模具';
COMMENT ON COLUMN mes_md_item_batch_config.lot_number_flag IS '批次属性-生产批号';
COMMENT ON COLUMN mes_md_item_batch_config.quality_status_flag IS '批次属性-质量状态';
COMMENT ON COLUMN mes_md_item_batch_config.creator IS '创建者';
COMMENT ON COLUMN mes_md_item_batch_config.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_item_batch_config.updater IS '更新者';
COMMENT ON COLUMN mes_md_item_batch_config.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_item_batch_config.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_item_batch_config.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_item_batch_config IS 'MES 物料批次属性配置表';

-- ----------------------------
-- Records of mes_md_item_batch_config
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_item_batch_config (id, item_id, produce_date_flag, expire_date_flag, receipt_date_flag, vendor_flag, client_flag, sales_order_code_flag, purchase_order_code_flag, work_order_flag, task_flag, workstation_flag, tool_flag, mold_flag, lot_number_flag, quality_status_flag, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 96, '1', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '0', '1', '1', '2026-02-16 00:47:44', '1', '2026-02-16 01:21:35', '0', 1), (2, 95, '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2026-02-16 00:47:59', '1', '2026-02-16 00:47:59', '0', 1), (3, 100, '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2026-03-13 23:48:32', '1', '2026-03-13 23:48:47', '0', 1), (4, 102, '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2026-03-15 00:36:24', '1', '2026-03-15 00:36:24', '0', 1), (5, 103, '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2026-03-15 10:15:58', '1', '2026-03-15 10:15:58', '0', 1), (6, 75, '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2026-03-21 06:49:58', '1', '2026-03-21 06:49:58', '0', 1), (7, 104, '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2026-03-28 10:02:57', '1', '2026-03-28 10:02:57', '0', 1), (8, 106, '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2026-05-21 00:42:36', '1', '2026-05-21 00:42:36', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_item_batch_config_seq;
CREATE SEQUENCE mes_md_item_batch_config_seq
    START 9;

-- ----------------------------
-- Table structure for mes_md_item_type
-- ----------------------------
DROP TABLE IF EXISTS mes_md_item_type;
CREATE TABLE mes_md_item_type (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  parent_id int8 NOT NULL DEFAULT 0,
  item_or_product varchar(20) NOT NULL,
  sort int4 NOT NULL DEFAULT 0,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_item_type ADD CONSTRAINT pk_mes_md_item_type PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_item_type.id IS '分类编号';
COMMENT ON COLUMN mes_md_item_type.code IS '分类编码';
COMMENT ON COLUMN mes_md_item_type.name IS '分类名称';
COMMENT ON COLUMN mes_md_item_type.parent_id IS '父分类编号';
COMMENT ON COLUMN mes_md_item_type.item_or_product IS '物料/产品标识';
COMMENT ON COLUMN mes_md_item_type.sort IS '显示排序';
COMMENT ON COLUMN mes_md_item_type.status IS '状态';
COMMENT ON COLUMN mes_md_item_type.remark IS '备注';
COMMENT ON COLUMN mes_md_item_type.creator IS '创建者';
COMMENT ON COLUMN mes_md_item_type.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_item_type.updater IS '更新者';
COMMENT ON COLUMN mes_md_item_type.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_item_type.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_item_type.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_item_type IS 'MES 物料产品分类表';

-- ----------------------------
-- Records of mes_md_item_type
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_item_type (id, code, name, parent_id, item_or_product, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'ITEM_TYPE_0000', '物料产品分类', 0, 'PRODUCT', 1, 0, '', 'admin', '2022-04-27 16:32:09', 'admin', '2026-02-15 00:48:58', '0', 1), (272, 'ITYP202604090005', '原材料', 200, 'ITEM', 1, 0, '', 'admin', '2022-08-24 21:33:18', '1', '2026-04-09 14:47:36', '0', 1), (273, 'ITEM_TYPE_0088', '产品', 200, 'PRODUCT', 2, 0, '', 'admin', '2022-08-24 21:33:36', '', '2026-02-15 00:48:58', '0', 1), (274, 'ITEM_TYPE_0089', '五金类', 272, 'ITEM', 1, 0, '', 'admin', '2022-08-24 21:42:41', '', '2026-02-15 00:48:58', '0', 1), (275, 'ITEM_TYPE_0090', '注塑类', 272, 'ITEM', 2, 0, '', 'admin', '2022-08-24 21:42:52', '', '2026-02-15 00:48:58', '0', 1), (276, 'ITEM_TYPE_0091', '半成品', 273, 'PRODUCT', 1, 0, '', 'admin', '2022-08-24 21:43:06', '', '2026-02-15 00:48:58', '0', 1), (277, 'ITEM_TYPE_0092', '产成品', 273, 'PRODUCT', 2, 0, '', 'admin', '2022-08-24 21:43:16', '', '2026-02-15 00:48:58', '0', 1), (278, 'ITEM_TYPE_0093', '包装类', 272, 'ITEM', 3, 0, '', 'admin', '2022-09-27 10:01:36', '', '2026-02-15 00:48:58', '0', 1), (282, 'ITEM_TYPE_0097', '辅材', 272, 'ITEM', 3, 0, 'QQQ', 'admin', '2025-03-12 10:34:43', '1', '2026-02-15 14:02:55', '0', 1), (283, 'xx', 'yy', 0, 'ITEM', 0, 0, '', '1', '2026-02-15 14:02:45', '1', '2026-02-15 14:02:48', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_item_type_seq;
CREATE SEQUENCE mes_md_item_type_seq
    START 284;

-- ----------------------------
-- Table structure for mes_md_product_bom
-- ----------------------------
DROP TABLE IF EXISTS mes_md_product_bom;
CREATE TABLE mes_md_product_bom (
    id int8 NOT NULL,
  item_id int8 NOT NULL,
  bom_item_id int8 NOT NULL,
  quantity numeric(12,4) NOT NULL DEFAULT 0.0000,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_product_bom ADD CONSTRAINT pk_mes_md_product_bom PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_product_bom.id IS 'BOM 编号';
COMMENT ON COLUMN mes_md_product_bom.item_id IS '物料产品 ID（父产品）';
COMMENT ON COLUMN mes_md_product_bom.bom_item_id IS 'BOM 物料 ID（子物料）';
COMMENT ON COLUMN mes_md_product_bom.quantity IS '物料使用比例';
COMMENT ON COLUMN mes_md_product_bom.status IS '是否启用';
COMMENT ON COLUMN mes_md_product_bom.remark IS '备注';
COMMENT ON COLUMN mes_md_product_bom.creator IS '创建者';
COMMENT ON COLUMN mes_md_product_bom.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_product_bom.updater IS '更新者';
COMMENT ON COLUMN mes_md_product_bom.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_product_bom.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_product_bom.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_product_bom IS 'MES 产品BOM表（物料清单）';

-- ----------------------------
-- Records of mes_md_product_bom
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_product_bom (id, item_id, bom_item_id, quantity, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 96, 95, 1.0000, 0, NULL, '1', '2026-02-16 17:28:31', '1', '2026-02-16 17:28:31', '0', 1), (2, 96, 95, 1.0000, 0, NULL, '1', '2026-02-16 17:31:07', '1', '2026-02-16 17:31:07', '0', 1), (3, 96, 94, 1.0000, 0, NULL, '1', '2026-02-16 17:31:07', '1', '2026-02-16 17:31:07', '0', 1), (4, 96, 75, 1.0000, 0, NULL, '1', '2026-02-16 17:39:20', '1', '2026-02-16 17:39:20', '0', 1), (5, 96, 95, 1.0000, 0, NULL, '1', '2026-02-18 09:14:19', '1', '2026-02-18 09:14:19', '0', 1), (6, 100, 101, 1.0000, 0, NULL, '1', '2026-03-15 00:35:33', '1', '2026-03-15 00:35:33', '0', 1), (7, 100, 102, 1.0000, 0, NULL, '1', '2026-03-15 00:36:36', '1', '2026-03-15 00:36:36', '0', 1), (8, 100, 103, 1.0000, 0, NULL, '1', '2026-03-15 10:12:36', '1', '2026-03-15 10:12:36', '0', 1), (9, 103, 73, 1.0000, 0, NULL, '1', '2026-03-28 08:41:35', '1', '2026-03-28 08:41:35', '0', 1), (10, 104, 102, 1.0000, 0, 'AAA', '1', '2026-03-28 10:24:21', '1', '2026-03-28 10:32:18', '0', 1), (11, 104, 101, 1.0000, 0, NULL, '1', '2026-03-28 10:26:56', '1', '2026-03-28 10:26:56', '0', 1), (12, 104, 100, 1.0000, 0, NULL, '1', '2026-03-28 10:26:56', '1', '2026-03-28 10:26:56', '0', 1), (13, 104, 95, 1.0000, 0, NULL, '1', '2026-03-28 10:28:11', '1', '2026-03-28 10:28:11', '0', 1), (14, 106, 103, 1.0000, 0, NULL, '1', '2026-03-28 10:38:01', '1', '2026-03-28 10:38:01', '0', 1), (15, 106, 103, 1.0000, 0, NULL, '1', '2026-04-13 10:22:26', '1', '2026-04-13 10:22:26', '0', 1), (16, 106, 95, 1.0000, 0, NULL, '1', '2026-05-21 00:56:51', '1', '2026-05-21 00:56:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_product_bom_seq;
CREATE SEQUENCE mes_md_product_bom_seq
    START 17;

-- ----------------------------
-- Table structure for mes_md_product_sip
-- ----------------------------
DROP TABLE IF EXISTS mes_md_product_sip;
CREATE TABLE mes_md_product_sip (
    id int8 NOT NULL,
  item_id int8 NOT NULL,
  sort int4 NULL DEFAULT NULL,
  process_id int8 NULL DEFAULT NULL,
  title varchar(255) NULL DEFAULT NULL,
  description varchar(500) NULL DEFAULT NULL,
  url varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_product_sip ADD CONSTRAINT pk_mes_md_product_sip PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_product_sip.id IS 'SIP 编号';
COMMENT ON COLUMN mes_md_product_sip.item_id IS '物料产品 ID';
COMMENT ON COLUMN mes_md_product_sip.sort IS '排列顺序';
COMMENT ON COLUMN mes_md_product_sip.process_id IS '工序 ID';
COMMENT ON COLUMN mes_md_product_sip.title IS '标题';
COMMENT ON COLUMN mes_md_product_sip.description IS '详细描述';
COMMENT ON COLUMN mes_md_product_sip.url IS '图片地址';
COMMENT ON COLUMN mes_md_product_sip.remark IS '备注';
COMMENT ON COLUMN mes_md_product_sip.creator IS '创建者';
COMMENT ON COLUMN mes_md_product_sip.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_product_sip.updater IS '更新者';
COMMENT ON COLUMN mes_md_product_sip.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_product_sip.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_product_sip.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_product_sip IS 'MES 产品标准检验程序表（SIP）';

-- ----------------------------
-- Records of mes_md_product_sip
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_product_sip (id, item_id, sort, process_id, title, description, url, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 96, 2, NULL, '11', '333', NULL, NULL, '1', '2026-02-16 12:30:26', '1', '2026-02-16 12:30:26', '0', 1), (2, 106, 0, 1, 'ABC', NULL, 'http://test.yudao.iocoder.cn/20260328/108260013-1770021196430-gettyimages-2258948071-TFSPI_31012026-4160_1774665919537.jpeg', NULL, '1', '2026-03-28 10:42:23', '1', '2026-03-28 10:45:38', '1', 1), (3, 106, 0, NULL, 'SIP-Standard-01', NULL, 'http://test.yudao.iocoder.cn/20260521/iShot_2026-05-11_19.33.09.png', NULL, '1', '2026-04-13 10:14:34', '1', '2026-05-21 00:46:30', '0', 1), (4, 103, 0, NULL, 'SIP-Standard-01', NULL, NULL, NULL, '1', '2026-04-13 10:43:20', '1', '2026-04-13 10:43:20', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_product_sip_seq;
CREATE SEQUENCE mes_md_product_sip_seq
    START 5;

-- ----------------------------
-- Table structure for mes_md_product_sop
-- ----------------------------
DROP TABLE IF EXISTS mes_md_product_sop;
CREATE TABLE mes_md_product_sop (
    id int8 NOT NULL,
  item_id int8 NOT NULL,
  sort int4 NULL DEFAULT NULL,
  process_id int8 NULL DEFAULT NULL,
  title varchar(255) NULL DEFAULT NULL,
  description varchar(500) NULL DEFAULT NULL,
  url varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_product_sop ADD CONSTRAINT pk_mes_md_product_sop PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_product_sop.id IS 'SOP 编号';
COMMENT ON COLUMN mes_md_product_sop.item_id IS '物料产品 ID';
COMMENT ON COLUMN mes_md_product_sop.sort IS '排列顺序';
COMMENT ON COLUMN mes_md_product_sop.process_id IS '工序 ID';
COMMENT ON COLUMN mes_md_product_sop.title IS '标题';
COMMENT ON COLUMN mes_md_product_sop.description IS '详细描述';
COMMENT ON COLUMN mes_md_product_sop.url IS '图片地址';
COMMENT ON COLUMN mes_md_product_sop.remark IS '备注';
COMMENT ON COLUMN mes_md_product_sop.creator IS '创建者';
COMMENT ON COLUMN mes_md_product_sop.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_product_sop.updater IS '更新者';
COMMENT ON COLUMN mes_md_product_sop.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_product_sop.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_product_sop.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_product_sop IS 'MES 产品标准作业程序表（SOP）';

-- ----------------------------
-- Records of mes_md_product_sop
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_product_sop (id, item_id, sort, process_id, title, description, url, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 96, 2, NULL, '11', '33', NULL, '222', '1', '2026-02-16 12:30:06', '1', '2026-02-16 12:30:06', '0', 1), (2, 106, 0, NULL, 'SOP-Work-01', NULL, 'http://test.yudao.iocoder.cn/20260521/iShot_2026-05-10_14.00.36.png', NULL, '1', '2026-04-13 10:16:57', '1', '2026-05-21 00:57:35', '0', 1), (3, 103, 0, NULL, 'SOP-Work-01', NULL, NULL, NULL, '1', '2026-04-13 10:48:56', '1', '2026-04-13 10:48:56', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_product_sop_seq;
CREATE SEQUENCE mes_md_product_sop_seq
    START 4;

-- ----------------------------
-- Table structure for mes_md_unit_measure
-- ----------------------------
DROP TABLE IF EXISTS mes_md_unit_measure;
CREATE TABLE mes_md_unit_measure (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  primary_flag bool NOT NULL DEFAULT '1',
  primary_id int8 NULL DEFAULT NULL,
  change_rate numeric(12,4) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_unit_measure ADD CONSTRAINT pk_mes_md_unit_measure PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_unit_measure.id IS '单位编号';
COMMENT ON COLUMN mes_md_unit_measure.code IS '单位编码';
COMMENT ON COLUMN mes_md_unit_measure.name IS '单位名称';
COMMENT ON COLUMN mes_md_unit_measure.primary_flag IS '是否主单位';
COMMENT ON COLUMN mes_md_unit_measure.primary_id IS '主单位编号';
COMMENT ON COLUMN mes_md_unit_measure.change_rate IS '与主单位换算比例';
COMMENT ON COLUMN mes_md_unit_measure.status IS '状态';
COMMENT ON COLUMN mes_md_unit_measure.remark IS '备注';
COMMENT ON COLUMN mes_md_unit_measure.creator IS '创建者';
COMMENT ON COLUMN mes_md_unit_measure.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_unit_measure.updater IS '更新者';
COMMENT ON COLUMN mes_md_unit_measure.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_unit_measure.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_unit_measure.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_unit_measure IS 'MES 计量单位表';

-- ----------------------------
-- Records of mes_md_unit_measure
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_unit_measure (id, code, name, primary_flag, primary_id, change_rate, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'KG', '公斤', '1', NULL, NULL, 0, '', 'admin', '2022-04-27 21:52:19', 'admin', '2026-02-15 14:08:56', '0', 1), (201, 'g', '克', '0', 200, 0.1000, 0, '', 'admin', '2022-04-27 21:53:29', 'admin', '2026-02-15 14:08:56', '0', 1), (202, 'PCS', '个', '1', NULL, NULL, 0, '', 'admin', '2022-04-27 21:54:13', '', '2026-02-15 14:08:56', '0', 1), (203, 'CASE', '箱', '1', NULL, NULL, 0, '', 'admin', '2022-04-27 21:55:14', '', '2026-02-15 14:08:56', '0', 1), (204, 'm', '米', '1', NULL, NULL, 0, '', 'admin', '2022-05-18 15:03:21', '', '2026-02-15 14:08:56', '0', 1), (205, 'cm', '厘米', '0', 204, 100.0000, 0, '', 'admin', '2022-05-18 15:07:23', '', '2026-02-15 14:08:56', '0', 1), (206, 'mm', '毫米', '0', 204, 1000.0000, 0, '', 'admin', '2022-05-18 15:07:42', '', '2026-02-15 14:08:56', '0', 1), (214, 'T', '吨', '1', NULL, NULL, 0, '', 'admin', '2022-08-17 11:16:18', '', '2026-02-15 14:08:56', '0', 1), (216, 'p', '瓶', '0', 203, 10.0000, 0, '', 'admin', '2022-08-18 14:11:57', 'admin', '2026-02-15 14:08:56', '0', 1), (218, 'pm', '测试人员', '1', NULL, NULL, 0, '', 'admin', '2022-08-19 14:24:41', '', '2026-02-15 14:08:56', '0', 1), (219, 'Nm', '公支', '1', NULL, NULL, 0, '', 'admin', '2022-08-21 18:49:28', '', '2026-02-15 14:08:56', '0', 1), (220, 'Ne', '英支', '1', NULL, NULL, 0, '', 'admin', '2022-08-21 18:49:55', '', '2026-02-15 14:08:56', '0', 1), (221, '匹', '匹', '1', NULL, NULL, 0, '', 'admin', '2022-08-21 18:59:57', '', '2026-02-15 14:08:56', '0', 1), (222, '捆', '捆', '1', NULL, NULL, 0, '', 'admin', '2022-08-21 19:05:50', '', '2026-02-15 14:08:56', '0', 1), (223, 'mg', '毫克', '0', 200, 0.0010, 0, '', 'admin', '2022-09-27 10:17:16', '', '2026-02-15 14:08:56', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_unit_measure_seq;
CREATE SEQUENCE mes_md_unit_measure_seq
    START 224;

-- ----------------------------
-- Table structure for mes_md_vendor
-- ----------------------------
DROP TABLE IF EXISTS mes_md_vendor;
CREATE TABLE mes_md_vendor (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  nickname varchar(255) NULL DEFAULT NULL,
  english_name varchar(255) NULL DEFAULT NULL,
  description varchar(500) NULL DEFAULT NULL,
  logo varchar(255) NULL DEFAULT NULL,
  level varchar(64) NULL DEFAULT NULL,
  score int4 NULL DEFAULT NULL,
  address varchar(500) NULL DEFAULT NULL,
  website varchar(255) NULL DEFAULT NULL,
  email varchar(255) NULL DEFAULT NULL,
  telephone varchar(64) NULL DEFAULT NULL,
  contact1_name varchar(64) NULL DEFAULT NULL,
  contact1_telephone varchar(64) NULL DEFAULT NULL,
  contact1_email varchar(255) NULL DEFAULT NULL,
  contact2_name varchar(64) NULL DEFAULT NULL,
  contact2_telephone varchar(64) NULL DEFAULT NULL,
  contact2_email varchar(255) NULL DEFAULT NULL,
  credit_code varchar(64) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_vendor ADD CONSTRAINT pk_mes_md_vendor PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_vendor.id IS '供应商编号';
COMMENT ON COLUMN mes_md_vendor.code IS '供应商编码';
COMMENT ON COLUMN mes_md_vendor.name IS '供应商名称';
COMMENT ON COLUMN mes_md_vendor.nickname IS '供应商简称';
COMMENT ON COLUMN mes_md_vendor.english_name IS '供应商英文名称';
COMMENT ON COLUMN mes_md_vendor.description IS '供应商简介';
COMMENT ON COLUMN mes_md_vendor.logo IS '供应商LOGO地址';
COMMENT ON COLUMN mes_md_vendor.level IS '供应商等级';
COMMENT ON COLUMN mes_md_vendor.score IS '供应商评分';
COMMENT ON COLUMN mes_md_vendor.address IS '供应商地址';
COMMENT ON COLUMN mes_md_vendor.website IS '供应商官网地址';
COMMENT ON COLUMN mes_md_vendor.email IS '供应商邮箱地址';
COMMENT ON COLUMN mes_md_vendor.telephone IS '供应商电话';
COMMENT ON COLUMN mes_md_vendor.contact1_name IS '联系人1';
COMMENT ON COLUMN mes_md_vendor.contact1_telephone IS '联系人1-电话';
COMMENT ON COLUMN mes_md_vendor.contact1_email IS '联系人1-邮箱';
COMMENT ON COLUMN mes_md_vendor.contact2_name IS '联系人2';
COMMENT ON COLUMN mes_md_vendor.contact2_telephone IS '联系人2-电话';
COMMENT ON COLUMN mes_md_vendor.contact2_email IS '联系人2-邮箱';
COMMENT ON COLUMN mes_md_vendor.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN mes_md_vendor.status IS '状态';
COMMENT ON COLUMN mes_md_vendor.remark IS '备注';
COMMENT ON COLUMN mes_md_vendor.creator IS '创建者';
COMMENT ON COLUMN mes_md_vendor.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_vendor.updater IS '更新者';
COMMENT ON COLUMN mes_md_vendor.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_vendor.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_vendor.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_vendor IS 'MES 供应商表';

-- ----------------------------
-- Records of mes_md_vendor
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_vendor (id, code, name, nickname, english_name, description, logo, level, score, address, website, email, telephone, contact1_name, contact1_telephone, contact1_email, contact2_name, contact2_telephone, contact2_email, credit_code, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'V00101', '深圳市海力德电子有限公司', '海力德', 'HLD Electronics', '专业从事电子元器件生产与销售', '', 'A', 95, '深圳市宝安区福永街道白石厦工业区', 'https://www.hld-elec.com', 'info@hld-elec.com', '0755-12345678', '王经理', '13800138001', 'wang@hld-elec.com', '赵助理', '13800138002', 'zhao@hld-elec.com', '91440300MA5EXAMPLE', 0, '', '1', '2026-02-15 16:00:07', '1', '2026-02-15 16:00:07', '0', 1), (201, 'V00102', '东莞市精密五金制品有限公司', '精密五金', 'JM Hardware', NULL, NULL, 'B', 80, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-02-15 16:00:07', '1', '2026-02-15 16:00:07', '0', 1), (202, 'V00103', '苏州工业材料科技有限公司', '苏州工材', 'SZ Material Tech', NULL, NULL, 'A', 92, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-02-15 16:00:07', '1', '2026-02-15 16:00:07', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_vendor_seq;
CREATE SEQUENCE mes_md_vendor_seq
    START 203;

-- ----------------------------
-- Table structure for mes_md_workshop
-- ----------------------------
DROP TABLE IF EXISTS mes_md_workshop;
CREATE TABLE mes_md_workshop (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(100) NOT NULL,
  area numeric(12,2) NULL DEFAULT NULL,
  charge_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_workshop ADD CONSTRAINT pk_mes_md_workshop PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_md_workshop_01 ON mes_md_workshop (code);

COMMENT ON COLUMN mes_md_workshop.id IS '车间编号';
COMMENT ON COLUMN mes_md_workshop.code IS '车间编码';
COMMENT ON COLUMN mes_md_workshop.name IS '车间名称';
COMMENT ON COLUMN mes_md_workshop.area IS '面积';
COMMENT ON COLUMN mes_md_workshop.charge_user_id IS '负责人用户 ID';
COMMENT ON COLUMN mes_md_workshop.status IS '状态（0开启 1关闭）';
COMMENT ON COLUMN mes_md_workshop.remark IS '备注';
COMMENT ON COLUMN mes_md_workshop.creator IS '创建者';
COMMENT ON COLUMN mes_md_workshop.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_workshop.updater IS '更新者';
COMMENT ON COLUMN mes_md_workshop.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_workshop.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_workshop.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_workshop IS 'MES 车间表';

-- ----------------------------
-- Records of mes_md_workshop
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_workshop (id, code, name, area, charge_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'WS001', '注塑车间', 1200.00, 1, 0, '负责PVC注塑成型，生产螺丝刀刀柄', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:11', '0', 1), (2, 'WS002', '组装车间', 800.00, 1, 0, '负责螺丝刀半成品组装和成品包装', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:11', '0', 1), (3, 'WS003', '五金车间', 600.00, 1, 0, '负责钢筋裁切和刀头加工', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:11', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_workshop_seq;
CREATE SEQUENCE mes_md_workshop_seq
    START 4;

-- ----------------------------
-- Table structure for mes_md_workstation
-- ----------------------------
DROP TABLE IF EXISTS mes_md_workstation;
CREATE TABLE mes_md_workstation (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(100) NOT NULL,
  address varchar(255) NULL DEFAULT NULL,
  workshop_id int8 NOT NULL,
  process_id int8 NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_workstation ADD CONSTRAINT pk_mes_md_workstation PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_md_workstation_01 ON mes_md_workstation (code);

COMMENT ON COLUMN mes_md_workstation.id IS '工作站编号';
COMMENT ON COLUMN mes_md_workstation.code IS '工作站编码';
COMMENT ON COLUMN mes_md_workstation.name IS '工作站名称';
COMMENT ON COLUMN mes_md_workstation.address IS '工作站地点';
COMMENT ON COLUMN mes_md_workstation.workshop_id IS '所在车间 ID';
COMMENT ON COLUMN mes_md_workstation.process_id IS '工序 ID';
COMMENT ON COLUMN mes_md_workstation.warehouse_id IS '线边库 ID';
COMMENT ON COLUMN mes_md_workstation.location_id IS '库区 ID';
COMMENT ON COLUMN mes_md_workstation.area_id IS '库位 ID';
COMMENT ON COLUMN mes_md_workstation.status IS '状态（0开启 1关闭）';
COMMENT ON COLUMN mes_md_workstation.remark IS '备注';
COMMENT ON COLUMN mes_md_workstation.creator IS '创建者';
COMMENT ON COLUMN mes_md_workstation.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_workstation.updater IS '更新者';
COMMENT ON COLUMN mes_md_workstation.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_workstation.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_workstation.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_workstation IS 'MES 工作站表';

-- ----------------------------
-- Records of mes_md_workstation
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_workstation (id, code, name, address, workshop_id, process_id, warehouse_id, location_id, area_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'ST001', '注塑工位A', '注塑车间A区', 1, NULL, NULL, NULL, NULL, 0, '蓝色刀柄注塑', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:42', '0', 1), (2, 'ST002', '注塑工位B', '注塑车间B区', 1, NULL, NULL, NULL, NULL, 0, '黑色刀柄注塑', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:42', '0', 1), (3, 'ST003', '组装工位A', '组装车间A区', 2, NULL, NULL, NULL, NULL, 0, '刀头+刀柄组装', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:42', '0', 1), (4, 'ST004', '包装工位', '组装车间B区', 2, NULL, NULL, NULL, NULL, 0, '成品包装', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:42', '0', 1), (5, 'ST005', '裁切工位', '五金车间A区', 3, NULL, NULL, NULL, NULL, 0, '钢筋裁切', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:42', '0', 1), (6, 'ST006', '刀头加工工位', '五金车间B区', 3, 1, 703, 714, 725, 0, '刀头成型加工', 'admin', '2022-08-24 10:00:00', '1', '2026-03-28 17:36:44', '0', 1), (7, 'ST_ACCEPT_FB_P1', '验收报工-下料工位', '验收造数', 3, 1, NULL, NULL, NULL, 0, '验收造数：生产报工非质检工序', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (8, 'ST_ACCEPT_FB_P4', '验收报工-质检工位', '验收造数', 2, 4, NULL, NULL, NULL, 0, '验收造数：生产报工质检工序', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_workstation_seq;
CREATE SEQUENCE mes_md_workstation_seq
    START 9;

-- ----------------------------
-- Table structure for mes_md_workstation_machine
-- ----------------------------
DROP TABLE IF EXISTS mes_md_workstation_machine;
CREATE TABLE mes_md_workstation_machine (
    id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  machinery_id int8 NOT NULL,
  quantity int4 NOT NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_workstation_machine ADD CONSTRAINT pk_mes_md_workstation_machine PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_workstation_machine.id IS '编号';
COMMENT ON COLUMN mes_md_workstation_machine.workstation_id IS '工作站 ID';
COMMENT ON COLUMN mes_md_workstation_machine.machinery_id IS '设备 ID';
COMMENT ON COLUMN mes_md_workstation_machine.quantity IS '数量';
COMMENT ON COLUMN mes_md_workstation_machine.remark IS '备注';
COMMENT ON COLUMN mes_md_workstation_machine.creator IS '创建者';
COMMENT ON COLUMN mes_md_workstation_machine.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_workstation_machine.updater IS '更新者';
COMMENT ON COLUMN mes_md_workstation_machine.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_workstation_machine.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_workstation_machine.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_workstation_machine IS 'MES 工作站-设备资源表';

-- ----------------------------
-- Records of mes_md_workstation_machine
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_workstation_machine (id, workstation_id, machinery_id, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 2, '注塑机2台', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:19', '0', 1), (2, 2, 2, 1, '注塑机1台', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:19', '0', 1), (3, 5, 3, 1, '裁切机1台', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:19', '0', 1), (4, 6, 4, 1, '冲压机1台', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:19', '0', 1), (5, 6, 8, 1, NULL, '1', '2026-03-28 18:06:28', '1', '2026-03-28 18:06:30', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_workstation_machine_seq;
CREATE SEQUENCE mes_md_workstation_machine_seq
    START 6;

-- ----------------------------
-- Table structure for mes_md_workstation_tool
-- ----------------------------
DROP TABLE IF EXISTS mes_md_workstation_tool;
CREATE TABLE mes_md_workstation_tool (
    id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  tool_type_id int8 NOT NULL,
  quantity int4 NOT NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_workstation_tool ADD CONSTRAINT pk_mes_md_workstation_tool PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_workstation_tool.id IS '编号';
COMMENT ON COLUMN mes_md_workstation_tool.workstation_id IS '工作站 ID';
COMMENT ON COLUMN mes_md_workstation_tool.tool_type_id IS '工具类型 ID';
COMMENT ON COLUMN mes_md_workstation_tool.quantity IS '数量';
COMMENT ON COLUMN mes_md_workstation_tool.remark IS '备注';
COMMENT ON COLUMN mes_md_workstation_tool.creator IS '创建者';
COMMENT ON COLUMN mes_md_workstation_tool.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_workstation_tool.updater IS '更新者';
COMMENT ON COLUMN mes_md_workstation_tool.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_workstation_tool.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_workstation_tool.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_workstation_tool IS 'MES 工作站-工装夹具资源表';

-- ----------------------------
-- Records of mes_md_workstation_tool
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_workstation_tool (id, workstation_id, tool_type_id, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 1, 5, '螺丝刀专用组装夹具5套', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:24', '0', 1), (2, 3, 2, 3, '电动扭力工具3把', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:24', '0', 1), (3, 5, 3, 10, '裁切刀片10片', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:24', '0', 1), (4, 6, 1, 2, '3', '1', '2026-02-16 15:21:21', '1', '2026-02-16 15:22:09', '0', 1), (5, 6, 205, 1, NULL, '1', '2026-03-28 18:06:35', '1', '2026-03-28 18:06:35', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_workstation_tool_seq;
CREATE SEQUENCE mes_md_workstation_tool_seq
    START 6;

-- ----------------------------
-- Table structure for mes_md_workstation_worker
-- ----------------------------
DROP TABLE IF EXISTS mes_md_workstation_worker;
CREATE TABLE mes_md_workstation_worker (
    id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  post_id int8 NOT NULL,
  quantity int4 NOT NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_md_workstation_worker ADD CONSTRAINT pk_mes_md_workstation_worker PRIMARY KEY (id);

COMMENT ON COLUMN mes_md_workstation_worker.id IS '编号';
COMMENT ON COLUMN mes_md_workstation_worker.workstation_id IS '工作站 ID';
COMMENT ON COLUMN mes_md_workstation_worker.post_id IS '岗位 ID';
COMMENT ON COLUMN mes_md_workstation_worker.quantity IS '数量';
COMMENT ON COLUMN mes_md_workstation_worker.remark IS '备注';
COMMENT ON COLUMN mes_md_workstation_worker.creator IS '创建者';
COMMENT ON COLUMN mes_md_workstation_worker.create_time IS '创建时间';
COMMENT ON COLUMN mes_md_workstation_worker.updater IS '更新者';
COMMENT ON COLUMN mes_md_workstation_worker.update_time IS '更新时间';
COMMENT ON COLUMN mes_md_workstation_worker.deleted IS '是否删除';
COMMENT ON COLUMN mes_md_workstation_worker.tenant_id IS '租户编号';
COMMENT ON TABLE mes_md_workstation_worker IS 'MES 工作站-人力资源表';

-- ----------------------------
-- Records of mes_md_workstation_worker
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_md_workstation_worker (id, workstation_id, post_id, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 2, '注塑操作员2人', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:30', '0', 1), (2, 3, 1, 3, '组装操作员3人', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:30', '0', 1), (3, 3, 2, 1, '质检员1人', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:30', '0', 1), (4, 4, 1, 2, '包装操作员2人', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:30', '0', 1), (5, 5, 1, 1, '裁切操作员1人', 'admin', '2022-08-24 10:00:00', 'admin', '2026-02-15 23:51:30', '0', 1), (6, 6, 2, 1, '3', '1', '2026-02-16 15:28:01', '1', '2026-02-16 15:28:01', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_md_workstation_worker_seq;
CREATE SEQUENCE mes_md_workstation_worker_seq
    START 7;

-- ----------------------------
-- Table structure for mes_pro_andon_config
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_andon_config;
CREATE TABLE mes_pro_andon_config (
    id int8 NOT NULL,
  reason varchar(500) NOT NULL,
  level int2 NOT NULL DEFAULT 3,
  handler_role_id int8 NULL DEFAULT NULL,
  handler_user_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_andon_config ADD CONSTRAINT pk_mes_pro_andon_config PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_andon_config.id IS '编号';
COMMENT ON COLUMN mes_pro_andon_config.reason IS '呼叫原因';
COMMENT ON COLUMN mes_pro_andon_config.level IS '级别';
COMMENT ON COLUMN mes_pro_andon_config.handler_role_id IS '处置人角色编号';
COMMENT ON COLUMN mes_pro_andon_config.handler_user_id IS '处置人编号';
COMMENT ON COLUMN mes_pro_andon_config.remark IS '备注';
COMMENT ON COLUMN mes_pro_andon_config.creator IS '创建者';
COMMENT ON COLUMN mes_pro_andon_config.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_andon_config.updater IS '更新者';
COMMENT ON COLUMN mes_pro_andon_config.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_andon_config.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_andon_config.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_andon_config IS 'MES 安灯呼叫配置';

-- ----------------------------
-- Records of mes_pro_andon_config
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_andon_config (id, reason, level, handler_role_id, handler_user_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, '设备故障，需要维修', 1, 3, 103, '', '1', '2026-02-21 00:08:46', '1', '2026-05-25 22:58:46', '0', 1), (2, '物料不足，需要补料', 2, NULL, 1, '', '1', '2026-02-21 00:08:46', '1', '2026-05-25 23:18:47', '0', 1), (3, '质量异常，需要确认', 2, 2, 100, '', '1', '2026-02-21 00:08:46', '1', '2026-05-25 23:18:51', '0', 1), (4, '人员不足，需要支援', 3, NULL, 107, '', '1', '2026-02-21 00:08:46', '1', '2026-02-21 00:08:46', '0', 1), (5, '工艺问题，需要技术支持', 1, 2, NULL, '', '1', '2026-02-21 00:08:46', '1', '2026-02-21 00:08:46', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_andon_config_seq;
CREATE SEQUENCE mes_pro_andon_config_seq
    START 6;

-- ----------------------------
-- Table structure for mes_pro_andon_record
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_andon_record;
CREATE TABLE mes_pro_andon_record (
    id int8 NOT NULL,
  config_id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  user_id int8 NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  process_id int8 NULL DEFAULT NULL,
  reason varchar(500) NOT NULL,
  level int2 NOT NULL DEFAULT 3,
  status int2 NOT NULL DEFAULT 0,
  handle_time timestamp NULL DEFAULT NULL,
  handler_user_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_andon_record ADD CONSTRAINT pk_mes_pro_andon_record PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_andon_record.id IS '编号';
COMMENT ON COLUMN mes_pro_andon_record.config_id IS '安灯配置编号';
COMMENT ON COLUMN mes_pro_andon_record.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_andon_record.user_id IS '发起用户编号';
COMMENT ON COLUMN mes_pro_andon_record.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_andon_record.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_andon_record.reason IS '呼叫原因';
COMMENT ON COLUMN mes_pro_andon_record.level IS '级别';
COMMENT ON COLUMN mes_pro_andon_record.status IS '处置状态';
COMMENT ON COLUMN mes_pro_andon_record.handle_time IS '处置时间';
COMMENT ON COLUMN mes_pro_andon_record.handler_user_id IS '处置人编号';
COMMENT ON COLUMN mes_pro_andon_record.remark IS '备注';
COMMENT ON COLUMN mes_pro_andon_record.creator IS '创建者';
COMMENT ON COLUMN mes_pro_andon_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_andon_record.updater IS '更新者';
COMMENT ON COLUMN mes_pro_andon_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_andon_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_andon_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_andon_record IS 'MES 安灯呼叫记录';

-- ----------------------------
-- Records of mes_pro_andon_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_andon_record (id, config_id, workstation_id, user_id, work_order_id, process_id, reason, level, status, handle_time, handler_user_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 0, 1, 100, 1, 1, '设备故障，需要维修', 1, 1, '2025-03-15 10:30:00', 107, '已更换电机', '1', '2026-02-21 00:08:46', '1', '2026-02-21 00:08:46', '0', 1), (2, 0, 2, 101, 1, 2, '物料不足，需要补料', 2, 1, '1970-01-01 08:00:00', 1, '', '1', '2026-02-21 00:08:46', '1', '2026-03-17 20:12:53', '0', 1), (3, 0, 1, 100, 2, 1, '质量异常，需要确认', 2, 1, '2025-03-16 14:00:00', 107, '已调整工艺参数', '1', '2026-02-21 00:08:46', '1', '2026-02-21 00:08:46', '0', 1), (4, 4, 3, 1, NULL, NULL, '人员不足，需要支援', 3, 1, '1970-01-01 08:00:00', 1, '333', '1', '2026-02-21 09:23:41', '1', '2026-02-21 09:25:55', '0', 1), (5, 3, 1, 1, NULL, NULL, '质量异常，需要确认', 2, 1, '1970-01-01 08:00:00', 115, '', '1', '2026-03-17 21:03:05', '1', '2026-03-17 21:03:27', '0', 1), (6, 2, 1, 1, 2, 2, '物料不足，需要补料', 2, 1, '1970-01-01 08:00:00', 1, '', '1', '2026-03-17 21:03:39', '1', '2026-04-04 10:29:58', '0', 1), (7, 2, 1, 1, 16, 2, '物料不足，需要补料', 2, 0, '2026-05-25 22:40:45', 1, '', '1', '2026-04-04 10:30:17', '1', '2026-05-25 22:41:03', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_andon_record_seq;
CREATE SEQUENCE mes_pro_andon_record_seq
    START 8;

-- ----------------------------
-- Table structure for mes_pro_card
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_card;
CREATE TABLE mes_pro_card (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  work_order_id int8 NOT NULL,
  item_id int8 NOT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  transfered_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_card ADD CONSTRAINT pk_mes_pro_card PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_card.id IS '编号';
COMMENT ON COLUMN mes_pro_card.code IS '流转卡编码';
COMMENT ON COLUMN mes_pro_card.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_card.item_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_card.batch_code IS '批次号';
COMMENT ON COLUMN mes_pro_card.transfered_quantity IS '流转数量';
COMMENT ON COLUMN mes_pro_card.status IS '状态';
COMMENT ON COLUMN mes_pro_card.remark IS '备注';
COMMENT ON COLUMN mes_pro_card.creator IS '创建者';
COMMENT ON COLUMN mes_pro_card.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_card.updater IS '更新者';
COMMENT ON COLUMN mes_pro_card.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_card.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_card.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_card IS 'MES 生产流转卡';

-- ----------------------------
-- Records of mes_pro_card
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_card (id, code, work_order_id, item_id, batch_code, transfered_quantity, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'CARD202503150001', 1, 75, 'B20250315001', 5000.00, 1, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (2, 'CARD202503160001', 1, 75, 'B20250316001', 3000.00, 0, '第二批次流转', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (3, 'CARD202503200001', 4, 75, NULL, 10000.00, 1, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (4, 'CARD20260404000001', 16, 69, 'AABB', 1.00, 2, '', '1', '2026-04-04 20:30:36', '1', '2026-04-04 20:35:29', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_card_seq;
CREATE SEQUENCE mes_pro_card_seq
    START 5;

-- ----------------------------
-- Table structure for mes_pro_card_process
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_card_process;
CREATE TABLE mes_pro_card_process (
    id int8 NOT NULL,
  card_id int8 NOT NULL,
  sort int4 NULL DEFAULT NULL,
  process_id int8 NULL DEFAULT NULL,
  input_time timestamp NULL DEFAULT NULL,
  output_time timestamp NULL DEFAULT NULL,
  input_quantity numeric(14,2) NULL DEFAULT NULL,
  output_quantity numeric(14,2) NULL DEFAULT NULL,
  unqualified_quantity numeric(14,2) NULL DEFAULT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  user_id int8 NULL DEFAULT NULL,
  ipqc_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_card_process ADD CONSTRAINT pk_mes_pro_card_process PRIMARY KEY (id);

CREATE INDEX idx_mes_pro_card_process_01 ON mes_pro_card_process (card_id);

COMMENT ON COLUMN mes_pro_card_process.id IS '编号';
COMMENT ON COLUMN mes_pro_card_process.card_id IS '流转卡编号';
COMMENT ON COLUMN mes_pro_card_process.sort IS '序号';
COMMENT ON COLUMN mes_pro_card_process.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_card_process.input_time IS '进入工序时间';
COMMENT ON COLUMN mes_pro_card_process.output_time IS '出工序时间';
COMMENT ON COLUMN mes_pro_card_process.input_quantity IS '投入数量';
COMMENT ON COLUMN mes_pro_card_process.output_quantity IS '产出数量';
COMMENT ON COLUMN mes_pro_card_process.unqualified_quantity IS '不合格品数量';
COMMENT ON COLUMN mes_pro_card_process.workstation_id IS '工位编号';
COMMENT ON COLUMN mes_pro_card_process.user_id IS '操作人编号';
COMMENT ON COLUMN mes_pro_card_process.ipqc_id IS '过程检验单编号';
COMMENT ON COLUMN mes_pro_card_process.remark IS '备注';
COMMENT ON COLUMN mes_pro_card_process.creator IS '创建者';
COMMENT ON COLUMN mes_pro_card_process.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_card_process.updater IS '更新者';
COMMENT ON COLUMN mes_pro_card_process.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_card_process.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_card_process.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_card_process IS 'MES 流转卡工序记录';

-- ----------------------------
-- Records of mes_pro_card_process
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_card_process (id, card_id, sort, process_id, input_time, output_time, input_quantity, output_quantity, unqualified_quantity, workstation_id, user_id, ipqc_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, '2025-03-15 08:00:00', '2025-03-15 16:00:00', 5000.00, 4980.00, 20.00, 1, 1, NULL, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (2, 1, 2, 2, '2025-03-16 08:00:00', '2025-03-16 16:00:00', 4980.00, 4960.00, 20.00, 2, 1, NULL, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (3, 1, 3, 3, '2025-03-17 08:00:00', NULL, 4960.00, NULL, NULL, 3, 1, NULL, '组装进行中', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (4, 3, 1, 1, '2025-03-20 08:00:00', '2025-03-20 18:00:00', 10000.00, 9950.00, 50.00, 1, 1, NULL, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (5, 3, 2, 2, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, '', '1', '2026-02-21 03:16:43', '1', '2026-02-21 03:16:43', '0', 1), (6, 4, 1, 1, '2026-04-06 00:00:00', '2026-04-15 00:00:00', 2.00, 1.00, 1.00, 1, 100, NULL, 'aaab', '1', '2026-04-04 20:30:59', '1', '2026-04-04 20:30:59', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_card_process_seq;
CREATE SEQUENCE mes_pro_card_process_seq
    START 7;

-- ----------------------------
-- Table structure for mes_pro_feedback
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_feedback;
CREATE TABLE mes_pro_feedback (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  type int2 NOT NULL,
  channel varchar(64) NULL DEFAULT NULL,
  feedback_time timestamp NULL DEFAULT NULL,
  workstation_id int8 NOT NULL,
  route_id int8 NOT NULL,
  process_id int8 NOT NULL,
  work_order_id int8 NOT NULL,
  task_id int8 NOT NULL,
  item_id int8 NOT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  lot_number varchar(64) NULL DEFAULT NULL,
  scheduled_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  feedback_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  qualified_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  unqualified_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  uncheck_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  labor_scrap_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  material_scrap_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  other_scrap_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  feedback_user_id int8 NULL DEFAULT NULL,
  approve_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_feedback ADD CONSTRAINT pk_mes_pro_feedback PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_feedback.id IS '编号';
COMMENT ON COLUMN mes_pro_feedback.code IS '报工单编号';
COMMENT ON COLUMN mes_pro_feedback.type IS '报工类型';
COMMENT ON COLUMN mes_pro_feedback.channel IS '报工途径';
COMMENT ON COLUMN mes_pro_feedback.feedback_time IS '报工时间';
COMMENT ON COLUMN mes_pro_feedback.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_feedback.route_id IS '工艺路线编号';
COMMENT ON COLUMN mes_pro_feedback.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_feedback.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_feedback.task_id IS '生产任务编号';
COMMENT ON COLUMN mes_pro_feedback.item_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_feedback.expire_date IS '过期日期';
COMMENT ON COLUMN mes_pro_feedback.lot_number IS '生产批号';
COMMENT ON COLUMN mes_pro_feedback.scheduled_quantity IS '排产数量';
COMMENT ON COLUMN mes_pro_feedback.feedback_quantity IS '本次报工数量';
COMMENT ON COLUMN mes_pro_feedback.qualified_quantity IS '合格品数量';
COMMENT ON COLUMN mes_pro_feedback.unqualified_quantity IS '不良品数量';
COMMENT ON COLUMN mes_pro_feedback.uncheck_quantity IS '待检测数量';
COMMENT ON COLUMN mes_pro_feedback.labor_scrap_quantity IS '工废数量';
COMMENT ON COLUMN mes_pro_feedback.material_scrap_quantity IS '料废数量';
COMMENT ON COLUMN mes_pro_feedback.other_scrap_quantity IS '其他废品数量';
COMMENT ON COLUMN mes_pro_feedback.feedback_user_id IS '报工用户编号';
COMMENT ON COLUMN mes_pro_feedback.approve_user_id IS '审核用户编号';
COMMENT ON COLUMN mes_pro_feedback.status IS '状态';
COMMENT ON COLUMN mes_pro_feedback.remark IS '备注';
COMMENT ON COLUMN mes_pro_feedback.creator IS '创建者';
COMMENT ON COLUMN mes_pro_feedback.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_feedback.updater IS '更新者';
COMMENT ON COLUMN mes_pro_feedback.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_feedback.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_feedback.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_feedback IS 'MES 生产报工';

-- ----------------------------
-- Records of mes_pro_feedback
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_feedback (id, code, type, channel, feedback_time, workstation_id, route_id, process_id, work_order_id, task_id, item_id, expire_date, lot_number, scheduled_quantity, feedback_quantity, qualified_quantity, unqualified_quantity, uncheck_quantity, labor_scrap_quantity, material_scrap_quantity, other_scrap_quantity, feedback_user_id, approve_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'FB202503160001', 1, 'PC', '2025-03-16 10:30:00', 1, 1, 1, 1, 1, 75, NULL, NULL, 5000.00, 510.00, 500.00, 10.00, 0.00, 6.00, 4.00, 0.00, 1, 1, 4, '', '1', '2026-02-21 00:50:39', '1', '2026-03-19 00:48:09', '0', 1), (2, 'FB202503170001', 1, 'PC', '2026-02-21 12:29:27', 1, 1, 1, 1, 1, 75, NULL, NULL, 5000.00, 820.00, 800.00, 20.00, 0.00, 12.00, 5.00, 3.00, 1, 1, 4, '第二批次报工', '1', '2026-02-21 00:50:39', '1', '2026-03-19 00:48:09', '0', 1), (3, 'FB202503180001', 2, 'APP', '2025-03-18 09:00:00', 1, 1, 1, 1, 1, 75, NULL, NULL, 5000.00, 200.00, 0.00, 0.00, 200.00, 0.00, 0.00, 0.00, 1, 1, 3, '统一报工，待质检', '1', '2026-02-21 00:50:39', '1', '2026-03-19 00:48:09', '0', 1), (4, 'FB20260318233037414', 1, NULL, '2026-03-19 08:35:25', 3, 1, 3, 1, 4, 75, NULL, NULL, 0.00, 1.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1, NULL, 2, '', '1', '2026-03-18 23:30:59', '1', '2026-03-19 00:48:09', '0', 1), (5, 'FB20260319083538497', 1, NULL, '2026-03-19 08:39:03', 3, 1, 3, 1, 4, 75, NULL, NULL, 0.00, 1.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1, 1, 4, '', '1', '2026-03-19 08:36:03', '1', '2026-03-21 15:07:46', '0', 1), (6, 'FB20260319083926305', 1, NULL, '2026-03-19 13:01:06', 3, 1, 3, 1, 4, 75, NULL, NULL, 0.00, 1.00, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00, 1, 1, 4, '', '1', '2026-03-19 08:39:40', '1', '2026-03-21 15:09:48', '0', 1), (7, 'FB20260319085214000', 1, NULL, '2026-03-19 08:52:59', 3, 1, 3, 1, 4, 75, NULL, NULL, 0.00, 1.00, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00, 1, 1, 4, '', '1', '2026-03-19 08:52:28', '1', '2026-03-19 23:02:57', '0', 1), (8, 'FB20260324231635515', 2, NULL, '2026-03-24 23:17:21', 3, 1, 3, 1, 4, 75, NULL, NULL, 0.00, 2.00, 1.00, 1.00, 0.00, 1.00, 0.00, 0.00, 1, 1, 4, '', '1', '2026-03-24 23:17:15', '1', '2026-03-24 23:17:24', '0', 1), (9, 'FB20260324231843582', 2, NULL, '2026-03-24 23:19:14', 1, 1, 1, 1, 1, 75, NULL, NULL, 0.00, 3.00, 2.00, 1.00, 0.00, 0.00, 0.00, 0.00, 1, 1, 2, '321', '1', '2026-03-24 23:19:09', '1', '2026-04-15 09:03:44', '0', 1), (10, 'FB_ACCEPT_PREPARE', 1, 'PC', '2026-05-26 00:33:47', 7, 1, 1, 17, 7, 75, '2026-11-22 00:00:00', 'LOT-FB-PREPARE', 100.00, 10.00, 8.00, 2.00, 0.00, 1.00, 1.00, 0.00, 100, 1, 0, '验收造数：草稿，可测试编辑/提交', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (11, 'FB_ACCEPT_APPROVING', 1, 'PC', '2026-05-25 23:33:47', 7, 1, 1, 17, 7, 75, '2026-11-22 00:00:00', 'LOT-FB-APPROVING', 100.00, 12.00, 11.00, 1.00, 0.00, 1.00, 0.00, 0.00, 100, 1, 2, '验收造数：审批中，可测试审批/驳回', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (12, 'FB_ACCEPT_UNCHECK', 2, 'PC', '2026-05-25 22:33:47', 8, 1, 4, 17, 8, 75, '2026-11-22 00:00:00', 'LOT-FB-UNCHECK', 100.00, 15.00, 0.00, 0.00, 15.00, 0.00, 0.00, 0.00, 100, 1, 3, '验收造数：待检验，可测试只读和明细 Tab', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (13, 'FB_ACCEPT_FINISHED', 2, 'PC', '2026-05-25 21:33:47', 7, 1, 1, 17, 7, 75, '2026-11-22 00:00:00', 'LOT-FB-FINISHED', 100.00, 20.00, 18.00, 2.00, 0.00, 1.00, 1.00, 0.00, 100, 1, 4, '验收造数：已完成，可测试消耗/产出明细', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_feedback_seq;
CREATE SEQUENCE mes_pro_feedback_seq
    START 14;

-- ----------------------------
-- Table structure for mes_pro_process
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_process;
CREATE TABLE mes_pro_process (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  attention varchar(1000) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_process ADD CONSTRAINT pk_mes_pro_process PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_process.id IS '编号';
COMMENT ON COLUMN mes_pro_process.code IS '工序编码';
COMMENT ON COLUMN mes_pro_process.name IS '工序名称';
COMMENT ON COLUMN mes_pro_process.attention IS '工艺要求';
COMMENT ON COLUMN mes_pro_process.status IS '状态';
COMMENT ON COLUMN mes_pro_process.remark IS '备注';
COMMENT ON COLUMN mes_pro_process.creator IS '创建者';
COMMENT ON COLUMN mes_pro_process.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_process.updater IS '更新者';
COMMENT ON COLUMN mes_pro_process.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_process.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_process.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_process IS 'MES 生产工序';

-- ----------------------------
-- Records of mes_pro_process
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_process (id, code, name, attention, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PROCESS001', '下料工序', '按照图纸尺寸进行切割，注意材料方向', 0, '金属板材下料', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (2, 'PROCESS002', '折弯工序', '折弯角度需精确到 ±1°，避免回弹', 0, '钣金折弯成型', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (3, 'PROCESS003', '焊接工序', '焊接电流 180A，氩气流量 12L/min', 0, '氩弧焊接', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (4, 'PROCESS004', '打磨工序', '打磨至 Ra3.2，无毛刺', 0, '表面处理', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (5, 'PROCESS005', '喷涂工序', '底漆厚度 30μm，面漆厚度 50μm', 0, '表面喷涂', '1', '2026-02-17 11:39:58', '1', '2026-02-18 15:54:14', '0', 1), (6, 'PROC000010', '测试 123', '测试 123', 0, '', '1', '2026-03-15 19:44:01', '1', '2026-05-25 09:02:54', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_process_seq;
CREATE SEQUENCE mes_pro_process_seq
    START 7;

-- ----------------------------
-- Table structure for mes_pro_process_content
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_process_content;
CREATE TABLE mes_pro_process_content (
    id int8 NOT NULL,
  process_id int8 NOT NULL,
  sort int4 NOT NULL DEFAULT 0,
  content varchar(500) NULL DEFAULT NULL,
  device varchar(255) NULL DEFAULT NULL,
  material varchar(255) NULL DEFAULT NULL,
  doc_url varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_process_content ADD CONSTRAINT pk_mes_pro_process_content PRIMARY KEY (id);

CREATE INDEX idx_mes_pro_process_content_01 ON mes_pro_process_content (process_id);

COMMENT ON COLUMN mes_pro_process_content.id IS '编号';
COMMENT ON COLUMN mes_pro_process_content.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_process_content.sort IS '顺序编号';
COMMENT ON COLUMN mes_pro_process_content.content IS '步骤说明';
COMMENT ON COLUMN mes_pro_process_content.device IS '辅助设备';
COMMENT ON COLUMN mes_pro_process_content.material IS '辅助材料';
COMMENT ON COLUMN mes_pro_process_content.doc_url IS '材料文档 URL';
COMMENT ON COLUMN mes_pro_process_content.remark IS '备注';
COMMENT ON COLUMN mes_pro_process_content.creator IS '创建者';
COMMENT ON COLUMN mes_pro_process_content.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_process_content.updater IS '更新者';
COMMENT ON COLUMN mes_pro_process_content.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_process_content.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_process_content.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_process_content IS 'MES 生产工序内容';

-- ----------------------------
-- Records of mes_pro_process_content
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_process_content (id, process_id, sort, content, device, material, doc_url, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 1, '检查焊接设备是否正常', '氩弧焊机', '无', NULL, '', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (2, 3, 2, '清洁焊接部位，去除油污', '角磨机', '丙酮', NULL, '', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (3, 3, 3, '点焊定位', '氩弧焊机', '焊丝 ER308', NULL, '', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (4, 3, 4, '正式焊接，控制焊接速度', '氩弧焊机', '焊丝 ER308', NULL, '', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (5, 3, 5, '焊后检查，清除焊渣', '锤子', '钢丝刷', NULL, '', '1', '2026-02-17 11:39:58', '1', '2026-02-17 11:39:58', '0', 1), (6, 5, 1, '你好', '1', '2', '3', '4', '1', '2026-02-18 15:54:08', '1', '2026-02-18 15:54:08', '0', 1), (7, 6, 1, '组装', '', '', '', '', '1', '2026-03-15 19:44:14', '1', '2026-03-15 19:44:14', '0', 1), (8, 6, 2, '质检', '', '', '', '', '1', '2026-03-15 19:44:23', '1', '2026-03-15 19:44:23', '0', 1), (9, 6, 3, '包装', '', '', '', '', '1', '2026-03-15 19:44:28', '1', '2026-03-15 19:44:28', '0', 1), (10, 6, 4, '怎么说？？？', NULL, NULL, NULL, '呃呃呃', '1', '2026-05-25 09:02:37', '1', '2026-05-25 09:02:45', '0', 1), (11, 6, 5, '呃呃呃', NULL, NULL, NULL, '', '1', '2026-05-25 15:43:46', '1', '2026-05-25 15:43:46', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_process_content_seq;
CREATE SEQUENCE mes_pro_process_content_seq
    START 12;

-- ----------------------------
-- Table structure for mes_pro_route
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_route;
CREATE TABLE mes_pro_route (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  description varchar(500) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_route ADD CONSTRAINT pk_mes_pro_route PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_route.id IS '编号';
COMMENT ON COLUMN mes_pro_route.code IS '工艺路线编码';
COMMENT ON COLUMN mes_pro_route.name IS '工艺路线名称';
COMMENT ON COLUMN mes_pro_route.description IS '工艺路线说明';
COMMENT ON COLUMN mes_pro_route.status IS '状态';
COMMENT ON COLUMN mes_pro_route.remark IS '备注';
COMMENT ON COLUMN mes_pro_route.creator IS '创建者';
COMMENT ON COLUMN mes_pro_route.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_route.updater IS '更新者';
COMMENT ON COLUMN mes_pro_route.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_route.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_route.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_route IS 'MES 工艺路线表';

-- ----------------------------
-- Records of mes_pro_route
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_route (id, code, name, description, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'ROUTE20250301001', '螺丝刀生产工艺', '一字型蓝色螺丝刀的完整生产工艺路线，包含下料、折弯、焊接、打磨、喷涂五道工序', 1, '', '1', '2026-02-19 09:23:15', '1', '2026-02-19 18:27:09', '0', 1), (2, 'ROUTE20250301002', '刀柄注塑工艺', 'PVC注塑成型刀柄的生产工艺路线', 0, '', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (3, 'ROUTE20250301003', '刀头加工工艺', '螺丝刀刀头的钢筋加工工艺路线', 0, '', '1', '2026-02-19 09:23:15', '1', '2026-02-19 18:26:04', '0', 1), (4, 'ROUTE20250301004', '新产品试制工艺', '新产品试制阶段的临时工艺路线，待完善后启用', 1, '待补充工序和产品', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (5, 'ROUTE20250301005', '成品包装工艺', '螺丝刀成品包装工艺路线，单道工序完成质检和包装', 0, '', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (6, 'ROUTEyyXnJu4W', '测试流程', '', 0, '', '1', '2026-03-15 19:44:39', '1', '2026-03-15 20:14:41', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_route_seq;
CREATE SEQUENCE mes_pro_route_seq
    START 7;

-- ----------------------------
-- Table structure for mes_pro_route_process
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_route_process;
CREATE TABLE mes_pro_route_process (
    id int8 NOT NULL,
  route_id int8 NOT NULL,
  process_id int8 NOT NULL,
  sort int4 NOT NULL DEFAULT 1,
  next_process_id int8 NULL DEFAULT NULL,
  link_type int2 NOT NULL DEFAULT 0,
  prepare_time int4 NULL DEFAULT 0,
  wait_time int4 NULL DEFAULT 0,
  color_code char(7) NULL DEFAULT '#00AEF3',
  key_flag bool NOT NULL DEFAULT '0',
  check_flag bool NOT NULL DEFAULT '0',
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_route_process ADD CONSTRAINT pk_mes_pro_route_process PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_route_process.id IS '编号';
COMMENT ON COLUMN mes_pro_route_process.route_id IS '工艺路线编号';
COMMENT ON COLUMN mes_pro_route_process.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_route_process.sort IS '序号';
COMMENT ON COLUMN mes_pro_route_process.next_process_id IS '下一道工序编号';
COMMENT ON COLUMN mes_pro_route_process.link_type IS '与下一道工序关系';
COMMENT ON COLUMN mes_pro_route_process.prepare_time IS '准备时间（分钟）';
COMMENT ON COLUMN mes_pro_route_process.wait_time IS '等待时间（分钟）';
COMMENT ON COLUMN mes_pro_route_process.color_code IS '甘特图显示颜色';
COMMENT ON COLUMN mes_pro_route_process.key_flag IS '是否关键工序';
COMMENT ON COLUMN mes_pro_route_process.check_flag IS '是否质检工序';
COMMENT ON COLUMN mes_pro_route_process.remark IS '备注';
COMMENT ON COLUMN mes_pro_route_process.creator IS '创建者';
COMMENT ON COLUMN mes_pro_route_process.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_route_process.updater IS '更新者';
COMMENT ON COLUMN mes_pro_route_process.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_route_process.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_route_process.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_route_process IS 'MES 工艺路线工序表';

-- ----------------------------
-- Records of mes_pro_route_process
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_route_process (id, route_id, process_id, sort, next_process_id, link_type, prepare_time, wait_time, color_code, key_flag, check_flag, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, 2, 3, 10, 5, '#409EFF', '0', '0', '按图纸切割钢筋', '1', '2026-02-19 09:23:15', '1', '2026-02-19 18:27:55', '0', 1), (2, 1, 2, 2, 3, 3, 5, 5, '#67C23A', '0', '0', '折弯成刀头形状', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (3, 1, 3, 3, 4, 3, 15, 30, '#E6A23C', '1', '0', '焊接刀头与刀柄', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (4, 1, 4, 4, 5, 3, 5, 10, '#F56C6C', '0', '1', '打磨焊接处和毛刺', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (5, 1, 5, 5, NULL, 3, 10, 60, '#909399', '0', '0', '表面喷涂保护漆', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (6, 2, 1, 1, 3, 3, 5, 5, '#409EFF', '0', '0', '称量 PVC 颗粒和色粉', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (7, 2, 3, 2, 5, 3, 20, 15, '#E6A23C', '1', '0', '注塑加热成型', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (8, 2, 5, 3, NULL, 3, 5, 30, '#909399', '0', '1', '表面光泽检查及喷涂', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (9, 3, 1, 1, 2, 3, 10, 5, '#409EFF', '0', '0', '按尺寸切割钢筋', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (10, 3, 2, 2, 4, 3, 5, 5, '#67C23A', '1', '0', '折弯成一字型刀头', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (11, 3, 4, 3, NULL, 3, 5, 10, '#F56C6C', '0', '1', '去毛刺精整', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (12, 5, 4, 1, NULL, 3, 5, 5, '#F56C6C', '1', '1', '成品外观检查及包装', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (13, 6, 6, 1, 5, 0, 0, 0, '#00AEF3', '1', '0', '', '1', '2026-03-15 19:45:21', '1', '2026-03-15 19:45:45', '0', 1), (14, 6, 2, 2, NULL, 2, 0, 0, '#00AEF3', '0', '0', '', '1', '2026-03-15 19:45:30', '1', '2026-03-15 19:48:06', '1', 1), (15, 6, 5, 2, 3, 3, 0, 0, '#00AEF3', '0', '0', '', '1', '2026-03-15 20:14:07', '1', '2026-03-15 20:14:07', '0', 1), (16, 6, 3, 3, NULL, 3, 0, 0, '#00AEF3', '0', '0', '', '1', '2026-03-15 20:14:37', '1', '2026-03-15 20:14:37', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_route_process_seq;
CREATE SEQUENCE mes_pro_route_process_seq
    START 17;

-- ----------------------------
-- Table structure for mes_pro_route_product
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_route_product;
CREATE TABLE mes_pro_route_product (
    id int8 NOT NULL,
  route_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity int4 NULL DEFAULT 1,
  production_time numeric(12,2) NULL DEFAULT 1.00,
  time_unit_type varchar(64) NULL DEFAULT 'MINUTE',
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_route_product ADD CONSTRAINT pk_mes_pro_route_product PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_route_product.id IS '编号';
COMMENT ON COLUMN mes_pro_route_product.route_id IS '工艺路线编号';
COMMENT ON COLUMN mes_pro_route_product.item_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_route_product.quantity IS '生产数量';
COMMENT ON COLUMN mes_pro_route_product.production_time IS '生产用时';
COMMENT ON COLUMN mes_pro_route_product.time_unit_type IS '时间单位';
COMMENT ON COLUMN mes_pro_route_product.remark IS '备注';
COMMENT ON COLUMN mes_pro_route_product.creator IS '创建者';
COMMENT ON COLUMN mes_pro_route_product.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_route_product.updater IS '更新者';
COMMENT ON COLUMN mes_pro_route_product.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_route_product.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_route_product.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_route_product IS 'MES 工艺路线产品表';

-- ----------------------------
-- Records of mes_pro_route_product
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_route_product (id, route_id, item_id, quantity, production_time, time_unit_type, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 75, 100, 480.00, 'MINUTE', '每批 100 个螺丝刀', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (2, 2, 73, 500, 2.00, 'HOUR', '每批 500 个蓝色刀柄', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (3, 2, 96, 500, 2.00, 'HOUR', '每批 500 个黑色刀柄', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (4, 3, 74, 1000, 3.00, 'HOUR', '每批 1000 个刀头', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (5, 6, 100, 1, 1.00, 'MINUTE', '', '1', '2026-03-15 19:45:08', '1', '2026-03-15 19:55:00', '0', 1), (6, 6, 101, 1, 1.00, 'MINUTE', '', '1', '2026-03-15 19:46:19', '1', '2026-03-15 19:54:33', '1', 1), (7, 6, 102, 1, 1.00, 'MINUTE', '', '1', '2026-03-15 19:46:25', '1', '2026-03-15 19:54:35', '1', 1), (8, 6, 103, 1, 1.00, 'MINUTE', '', '1', '2026-03-15 19:46:29', '1', '2026-03-15 19:54:37', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_route_product_seq;
CREATE SEQUENCE mes_pro_route_product_seq
    START 9;

-- ----------------------------
-- Table structure for mes_pro_route_product_bom
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_route_product_bom;
CREATE TABLE mes_pro_route_product_bom (
    id int8 NOT NULL,
  route_id int8 NOT NULL,
  process_id int8 NOT NULL,
  product_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NULL DEFAULT 1.00,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_route_product_bom ADD CONSTRAINT pk_mes_pro_route_product_bom PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_route_product_bom.id IS '编号';
COMMENT ON COLUMN mes_pro_route_product_bom.route_id IS '工艺路线编号';
COMMENT ON COLUMN mes_pro_route_product_bom.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_route_product_bom.product_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_route_product_bom.item_id IS 'BOM 物料编号';
COMMENT ON COLUMN mes_pro_route_product_bom.quantity IS '用料比例';
COMMENT ON COLUMN mes_pro_route_product_bom.remark IS '备注';
COMMENT ON COLUMN mes_pro_route_product_bom.creator IS '创建者';
COMMENT ON COLUMN mes_pro_route_product_bom.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_route_product_bom.updater IS '更新者';
COMMENT ON COLUMN mes_pro_route_product_bom.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_route_product_bom.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_route_product_bom.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_route_product_bom IS 'MES 工艺路线产品 BOM 表';

-- ----------------------------
-- Records of mes_pro_route_product_bom
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_route_product_bom (id, route_id, process_id, product_id, item_id, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 75, 72, 15.00, '每个螺丝刀需钢筋 15cm', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (2, 1, 3, 75, 72, 0.50, '焊接补料', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (3, 1, 5, 75, 71, 5.00, '蓝色面漆色粉 5g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (4, 1, 5, 75, 70, 2.00, 'PVC底漆 2g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (5, 2, 1, 73, 70, 20.00, 'PVC颗粒 20g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (6, 2, 1, 73, 71, 2.00, '蓝色色粉 2g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (7, 2, 5, 73, 71, 1.00, '表面喷涂蓝色色粉 1g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (8, 2, 1, 96, 70, 20.00, 'PVC颗粒 20g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (9, 2, 1, 96, 69, 2.00, '黑色色粉 2g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (10, 2, 5, 96, 69, 1.00, '表面喷涂黑色色粉 1g/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (11, 3, 1, 74, 72, 8.00, '钢筋 8cm/个', '1', '2026-02-19 09:23:15', '1', '2026-02-19 09:23:15', '0', 1), (12, 6, 6, 100, 101, 1.00, '', '1', '2026-03-15 19:54:43', '1', '2026-03-15 19:54:43', '0', 1), (13, 6, 6, 100, 102, 1.00, '', '1', '2026-03-15 19:54:49', '1', '2026-03-15 19:54:49', '0', 1), (14, 6, 6, 100, 103, 2.00, '', '1', '2026-03-15 19:54:59', '1', '2026-03-15 19:54:59', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_route_product_bom_seq;
CREATE SEQUENCE mes_pro_route_product_bom_seq
    START 15;

-- ----------------------------
-- Table structure for mes_pro_task
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_task;
CREATE TABLE mes_pro_task (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  work_order_id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  route_id int8 NOT NULL,
  process_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  produced_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  qualify_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  unqualify_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  changed_quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  client_id int8 NULL DEFAULT NULL,
  start_time timestamp NULL DEFAULT NULL,
  duration int4 NOT NULL DEFAULT 1,
  end_time timestamp NULL DEFAULT NULL,
  color_code char(7) NULL DEFAULT '#00AEF3',
  finish_date timestamp NULL DEFAULT NULL,
  cancel_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_task ADD CONSTRAINT pk_mes_pro_task PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_task.id IS '编号';
COMMENT ON COLUMN mes_pro_task.code IS '任务编码';
COMMENT ON COLUMN mes_pro_task.name IS '任务名称';
COMMENT ON COLUMN mes_pro_task.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_task.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_task.route_id IS '工艺路线编号';
COMMENT ON COLUMN mes_pro_task.process_id IS '工序编号';
COMMENT ON COLUMN mes_pro_task.item_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_task.quantity IS '排产数量';
COMMENT ON COLUMN mes_pro_task.produced_quantity IS '已生产数量';
COMMENT ON COLUMN mes_pro_task.qualify_quantity IS '合格品数量';
COMMENT ON COLUMN mes_pro_task.unqualify_quantity IS '不良品数量';
COMMENT ON COLUMN mes_pro_task.changed_quantity IS '调整数量';
COMMENT ON COLUMN mes_pro_task.client_id IS '客户编号';
COMMENT ON COLUMN mes_pro_task.start_time IS '开始生产时间';
COMMENT ON COLUMN mes_pro_task.duration IS '生产时长（工作日，1=8小时）';
COMMENT ON COLUMN mes_pro_task.end_time IS '结束生产时间';
COMMENT ON COLUMN mes_pro_task.color_code IS '甘特图显示颜色';
COMMENT ON COLUMN mes_pro_task.finish_date IS '完成日期';
COMMENT ON COLUMN mes_pro_task.cancel_date IS '取消日期';
COMMENT ON COLUMN mes_pro_task.status IS '任务状态';
COMMENT ON COLUMN mes_pro_task.remark IS '备注';
COMMENT ON COLUMN mes_pro_task.creator IS '创建者';
COMMENT ON COLUMN mes_pro_task.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_task.updater IS '更新者';
COMMENT ON COLUMN mes_pro_task.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_task.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_task.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_task IS 'MES 生产任务';

-- ----------------------------
-- Records of mes_pro_task
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_task (id, code, name, work_order_id, workstation_id, route_id, process_id, item_id, quantity, produced_quantity, qualify_quantity, unqualify_quantity, changed_quantity, client_id, start_time, duration, end_time, color_code, finish_date, cancel_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PT202503150001', '博世螺丝刀【5000】PCS 注塑', 1, 1, 1, 1, 75, 5000.00, 1500.00, 1480.00, 20.00, 0.00, 207, '2025-03-15 08:00:00', 3, '2025-03-18 08:00:00', '#00AEF3', NULL, NULL, 0, '', '1', '2026-02-19 15:25:39', '1', '2026-04-16 09:47:00', '0', 1), (2, 'PT202503150002', '博世螺丝刀【5000】PCS 注塑', 1, 1, 1, 1, 75, 5000.00, 0.00, 0.00, 0.00, 0.00, 207, '2025-03-18 08:00:00', 3, '2025-03-21 08:00:00', '#FF6B6B', NULL, NULL, 0, '', '1', '2026-02-19 15:25:39', '1', '2026-02-19 15:25:39', '0', 1), (3, 'PT202503150003', '博世螺丝刀【10000】PCS 冲压', 1, 2, 1, 2, 75, 10000.00, 0.00, 0.00, 0.00, 0.00, 207, '2025-03-20 08:00:00', 5, '2025-03-27 08:00:00', '#4ECDC4', NULL, NULL, 0, '', '1', '2026-02-19 15:25:39', '1', '2026-02-19 15:25:39', '0', 1), (4, 'PT202503150004', '博世螺丝刀【10000】PCS 组装', 1, 3, 1, 3, 75, 10000.00, 4.00, 2.00, 2.00, 0.00, 207, '2025-03-27 08:00:00', 4, '2025-04-02 08:00:00', '#45B7D1', NULL, NULL, 0, '', '1', '2026-02-19 15:25:39', '1', '2026-03-24 15:17:23', '0', 1), (5, 'PT202503200001', '德力西螺丝刀【10000】PCS 注塑', 4, 1, 1, 1, 75, 10000.00, 0.00, 0.00, 0.00, 0.00, 208, '2025-03-25 08:00:00', 5, '2025-04-01 08:00:00', '#96CEB4', NULL, NULL, 0, '', '1', '2026-02-19 15:25:39', '1', '2026-02-19 15:25:39', '0', 1), (6, 'TASK20260315000001', 'ABC【null】', 15, 1, 6, 6, 100, 1.00, 0.00, 0.00, 0.00, 0.00, NULL, '1970-01-01 08:00:00', 5, '1970-01-01 08:00:00', '#00AEF3', '2026-04-04 10:41:24', NULL, 5, '', '1', '2026-03-15 22:56:12', '1', '2026-04-16 09:47:00', '0', 1), (7, 'TASK_ACCEPT_FB_NORMAL', '验收报工任务-非质检', 17, 7, 1, 1, 75, 100.00, 20.00, 18.00, 2.00, 0.00, 207, '2026-05-26 00:33:47', 1, '2026-05-27 00:33:47', '#00AEF3', NULL, NULL, 0, '验收造数：非质检任务', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (8, 'TASK_ACCEPT_FB_CHECK', '验收报工任务-待检验', 17, 8, 1, 4, 75, 100.00, 20.00, 18.00, 2.00, 0.00, 207, '2026-05-26 00:33:47', 1, '2026-05-27 00:33:47', '#F59E0B', NULL, NULL, 0, '验收造数：质检任务', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_task_seq;
CREATE SEQUENCE mes_pro_task_seq
    START 9;

-- ----------------------------
-- Table structure for mes_pro_task_issue
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_task_issue;
CREATE TABLE mes_pro_task_issue (
    id int8 NOT NULL,
  task_id int8 NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  source_doc_type varchar(64) NULL DEFAULT NULL,
  source_doc_id int8 NOT NULL,
  source_line_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  item_id int8 NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  issued_quantity numeric(12,2) NULL DEFAULT 0.00,
  available_quantity numeric(12,2) NULL DEFAULT 0.00,
  used_quantity numeric(12,2) NULL DEFAULT 0.00,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_task_issue ADD CONSTRAINT pk_mes_pro_task_issue PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_task_issue.id IS '编号';
COMMENT ON COLUMN mes_pro_task_issue.task_id IS '生产任务编号';
COMMENT ON COLUMN mes_pro_task_issue.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_task_issue.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_task_issue.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_pro_task_issue.source_doc_id IS '来源单据编号';
COMMENT ON COLUMN mes_pro_task_issue.source_line_id IS '来源单据行编号';
COMMENT ON COLUMN mes_pro_task_issue.source_doc_code IS '来源单据编码';
COMMENT ON COLUMN mes_pro_task_issue.batch_code IS '投料批次';
COMMENT ON COLUMN mes_pro_task_issue.item_id IS '产品物料编号';
COMMENT ON COLUMN mes_pro_task_issue.unit_measure_id IS '单位编号';
COMMENT ON COLUMN mes_pro_task_issue.issued_quantity IS '总投料数量';
COMMENT ON COLUMN mes_pro_task_issue.available_quantity IS '当前可用数量';
COMMENT ON COLUMN mes_pro_task_issue.used_quantity IS '当前使用数量';
COMMENT ON COLUMN mes_pro_task_issue.remark IS '备注';
COMMENT ON COLUMN mes_pro_task_issue.creator IS '创建者';
COMMENT ON COLUMN mes_pro_task_issue.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_task_issue.updater IS '更新者';
COMMENT ON COLUMN mes_pro_task_issue.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_task_issue.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_task_issue.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_task_issue IS 'MES 生产任务投料';

-- ----------------------------
-- Records of mes_pro_task_issue
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_task_issue (id, task_id, work_order_id, workstation_id, source_doc_type, source_doc_id, source_line_id, source_doc_code, batch_code, item_id, unit_measure_id, issued_quantity, available_quantity, used_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, 'ISSUE', 1001, 1, 'ISS202503150001', 'B20250315001', 70, 200, 100.00, 50.00, 50.00, '', '1', '2026-02-19 15:25:39', '1', '2026-02-19 15:25:39', '0', 1), (2, 1, 1, 1, 'ISSUE', 1001, 2, 'ISS202503150001', 'B20250315001', 71, 201, 25000.00, 10000.00, 15000.00, '', '1', '2026-02-19 15:25:39', '1', '2026-02-19 15:25:39', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_task_issue_seq;
CREATE SEQUENCE mes_pro_task_issue_seq
    START 3;

-- ----------------------------
-- Table structure for mes_pro_work_order
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_work_order;
CREATE TABLE mes_pro_work_order (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  type int2 NOT NULL DEFAULT 1,
  order_source_type int2 NOT NULL,
  order_source_code varchar(64) NULL DEFAULT NULL,
  product_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  quantity_produced numeric(14,2) NOT NULL DEFAULT 0.00,
  quantity_changed numeric(14,2) NOT NULL DEFAULT 0.00,
  quantity_scheduled numeric(14,2) NOT NULL DEFAULT 0.00,
  client_id int8 NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  request_date timestamp NOT NULL,
  parent_id int8 NOT NULL DEFAULT 0,
  finish_date timestamp NULL DEFAULT NULL,
  cancel_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_work_order ADD CONSTRAINT pk_mes_pro_work_order PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_work_order.id IS '编号';
COMMENT ON COLUMN mes_pro_work_order.code IS '工单编码';
COMMENT ON COLUMN mes_pro_work_order.name IS '工单名称';
COMMENT ON COLUMN mes_pro_work_order.type IS '工单类型';
COMMENT ON COLUMN mes_pro_work_order.order_source_type IS '来源类型';
COMMENT ON COLUMN mes_pro_work_order.order_source_code IS '来源单据编号';
COMMENT ON COLUMN mes_pro_work_order.product_id IS '产品编号';
COMMENT ON COLUMN mes_pro_work_order.quantity IS '生产数量';
COMMENT ON COLUMN mes_pro_work_order.quantity_produced IS '已生产数量';
COMMENT ON COLUMN mes_pro_work_order.quantity_changed IS '调整数量';
COMMENT ON COLUMN mes_pro_work_order.quantity_scheduled IS '已排产数量';
COMMENT ON COLUMN mes_pro_work_order.client_id IS '客户编号';
COMMENT ON COLUMN mes_pro_work_order.vendor_id IS '供应商编号';
COMMENT ON COLUMN mes_pro_work_order.batch_code IS '批次号';
COMMENT ON COLUMN mes_pro_work_order.request_date IS '需求日期';
COMMENT ON COLUMN mes_pro_work_order.parent_id IS '父工单编号';
COMMENT ON COLUMN mes_pro_work_order.finish_date IS '完成时间';
COMMENT ON COLUMN mes_pro_work_order.cancel_date IS '取消时间';
COMMENT ON COLUMN mes_pro_work_order.status IS '工单状态';
COMMENT ON COLUMN mes_pro_work_order.remark IS '备注';
COMMENT ON COLUMN mes_pro_work_order.creator IS '创建者';
COMMENT ON COLUMN mes_pro_work_order.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_work_order.updater IS '更新者';
COMMENT ON COLUMN mes_pro_work_order.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_work_order.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_work_order.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_work_order IS 'MES 生产工单';

-- ----------------------------
-- Records of mes_pro_work_order
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_work_order (id, code, name, type, order_source_type, order_source_code, product_id, quantity, quantity_produced, quantity_changed, quantity_scheduled, client_id, vendor_id, batch_code, request_date, parent_id, finish_date, cancel_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'MO202503120008', '博世螺丝刀【一字型，蓝色刀柄】', 1, 1, 'PO202503011001', 75, 10000.00, 19.00, 0.00, 0.00, 207, NULL, NULL, '2025-03-31 00:00:00', 0, NULL, NULL, 1, '', '1', '2026-02-17 12:05:23', '1', '2026-03-24 15:17:23', '0', 1), (2, 'MO202503120009', '螺丝刀刀柄【蓝色】【10000】PCS', 1, 1, 'PO202503011001', 73, 10000.00, 20.00, 0.00, 0.00, 207, NULL, NULL, '2025-03-31 00:00:00', 1, '2025-05-07 15:48:12', '2025-05-07 16:07:45', 3, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (3, 'MO202503120010', '螺丝刀刀头【10000】PCS', 1, 1, 'PO202503011001', 74, 10000.00, 100.00, 0.00, 0.00, 207, NULL, NULL, '2025-03-31 00:00:00', 1, '2025-05-07 16:07:31', NULL, 2, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (4, 'MO202503180007', '德力西螺丝刀【蓝色、一字型】', 1, 1, '202503181001', 75, 10000.00, 0.00, 0.00, 0.00, 208, NULL, NULL, '2025-03-31 00:00:00', 0, NULL, NULL, 1, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (5, 'MO202503180013', '螺丝刀刀柄【蓝色】【10000】PCS', 1, 1, '202503181001', 73, 10000.00, 0.00, 0.00, 0.00, 208, NULL, NULL, '2025-03-31 00:00:00', 4, NULL, '2025-05-07 16:07:24', 3, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (6, 'MO202503180014', '螺丝刀刀头【10000】PCS', 1, 1, '202503181001', 74, 10000.00, 0.00, 0.00, 0.00, 208, NULL, NULL, '2025-03-31 00:00:00', 4, NULL, NULL, 1, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (7, 'MO202504120001', '外协工单-螺丝刀刀柄【蓝色】【1000】个', 2, 1, 'PO202503011001', 73, 1000.00, 112.00, 0.00, 0.00, 207, 201, NULL, '2025-03-31 00:00:00', 1, NULL, '2025-05-07 16:07:28', 3, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (8, 'MOPFAsX7VPpn', '222', 1, 1, '1', 69, 3.00, 0.00, 0.00, 0.00, 200, NULL, '5', '2026-02-05 00:00:00', 0, NULL, NULL, 1, '2312312', '1', '2026-02-19 19:22:42', '1', '2026-02-19 23:56:48', '0', 1), (9, 'MO2FkNe4IlHv', 'EEE', 1, 1, NULL, 69, 10.00, 0.00, 0.00, 0.00, NULL, NULL, NULL, '2026-03-27 00:00:00', 0, '2026-04-04 10:41:42', NULL, 2, '', '1', '2026-03-15 00:14:30', '1', '2026-04-04 10:41:42', '0', 1), (10, 'MOp6T6IA7yJS', '123', 1, 1, NULL, 104, 10.00, 0.00, 0.00, 0.00, NULL, NULL, NULL, '2026-03-27 00:00:00', 0, NULL, NULL, 0, '', '1', '2026-03-15 00:32:18', '1', '2026-04-05 16:19:56', '0', 1), (11, 'MOfIPF0aMKBM', 'EEE', 1, 1, NULL, 70, 10.00, 0.00, 0.00, 0.00, NULL, NULL, NULL, '2026-03-19 00:00:00', 0, NULL, NULL, 0, '', '1', '2026-03-15 00:33:54', '1', '2026-03-15 00:33:54', '0', 1), (12, 'MOnzySr0PP6s', '123', 1, 1, NULL, 100, 1.00, 0.00, 0.00, 0.00, NULL, NULL, NULL, '2026-04-02 00:00:00', 0, '2026-04-04 17:00:09', NULL, 2, '', '1', '2026-03-15 00:36:59', '1', '2026-04-04 17:00:09', '0', 1), (13, 'MOFkVXhpDqwa', 'eee', 1, 1, NULL, 69, 1.00, 0.00, 0.00, 0.00, 207, NULL, NULL, '2026-03-14 00:00:00', 0, NULL, '2026-03-15 09:24:32', 3, '', '1', '2026-03-15 09:24:20', '1', '2026-03-15 09:24:32', '0', 1), (14, 'MOjs2jM1ETv6', '11', 1, 1, NULL, 100, 10.00, 0.00, 0.00, 0.00, 207, NULL, NULL, '2026-03-12 00:00:00', 0, NULL, NULL, 0, '1321', '1', '2026-03-15 09:28:06', '1', '2026-03-15 09:28:23', '0', 1), (15, 'MOgf77qA1G0y', 'EEE', 1, 2, NULL, 100, 10.00, 0.00, 0.00, 0.00, 200, NULL, NULL, '2026-03-25 00:00:00', 0, '2026-04-04 10:41:24', NULL, 2, '', '1', '2026-03-15 10:13:10', '1', '2026-04-04 10:41:24', '0', 1), (16, 'MO1InqI3Jbfa', 'ABC-CCC【10】公斤', 1, 2, NULL, 102, 10.00, 0.00, 0.00, 0.00, 200, NULL, NULL, '2026-03-25 00:00:00', 15, NULL, NULL, 0, '', '1', '2026-03-15 10:38:53', '1', '2026-04-05 16:19:41', '0', 1), (17, 'MO_ACCEPT_FB_20260526', '验收生产报工工单-螺丝刀', 1, 1, 'ACCEPT-FEEDBACK', 75, 200.00, 40.00, 0.00, 0.00, 207, NULL, 'BATCH-ACCEPT-FB', '2026-06-02 00:00:00', 0, NULL, NULL, 1, '验收造数：生产报工依赖工单', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_work_order_seq;
CREATE SEQUENCE mes_pro_work_order_seq
    START 18;

-- ----------------------------
-- Table structure for mes_pro_work_order_bom
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_work_order_bom;
CREATE TABLE mes_pro_work_order_bom (
    id int8 NOT NULL,
  work_order_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL DEFAULT 0.00,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_work_order_bom ADD CONSTRAINT pk_mes_pro_work_order_bom PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_work_order_bom.id IS '编号';
COMMENT ON COLUMN mes_pro_work_order_bom.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_pro_work_order_bom.item_id IS 'BOM 物料编号';
COMMENT ON COLUMN mes_pro_work_order_bom.quantity IS '预计使用量';
COMMENT ON COLUMN mes_pro_work_order_bom.remark IS '备注';
COMMENT ON COLUMN mes_pro_work_order_bom.creator IS '创建者';
COMMENT ON COLUMN mes_pro_work_order_bom.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_work_order_bom.updater IS '更新者';
COMMENT ON COLUMN mes_pro_work_order_bom.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_work_order_bom.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_work_order_bom.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_work_order_bom IS 'MES 生产工单 BOM';

-- ----------------------------
-- Records of mes_pro_work_order_bom
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_work_order_bom (id, work_order_id, item_id, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 73, 10000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (2, 1, 74, 10000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (3, 1, 94, 1000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (4, 1, 95, 100.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (5, 2, 71, 50000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (6, 2, 70, 200.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (7, 3, 72, 2000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (8, 4, 73, 10000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (9, 4, 74, 10000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (10, 4, 94, 1000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (11, 4, 95, 100.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (12, 5, 71, 50000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (13, 5, 70, 200.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (14, 6, 72, 2001.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (15, 7, 71, 5000.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (16, 7, 70, 20.00, '', '1', '2026-02-17 12:05:23', '1', '2026-02-17 12:05:23', '0', 1), (17, 12, 101, 1.00, '', '1', '2026-03-15 00:37:12', '1', '2026-03-15 00:37:12', '0', 1), (18, 12, 102, 1.00, '', '1', '2026-03-15 00:37:12', '1', '2026-03-15 00:37:12', '0', 1), (19, 14, 101, 10.00, '', '1', '2026-03-15 09:28:06', '1', '2026-03-15 09:28:06', '0', 1), (20, 14, 102, 200.00, '', '1', '2026-03-15 09:28:06', '1', '2026-03-15 09:28:20', '0', 1), (21, 15, 101, 10.00, '', '1', '2026-03-15 10:13:10', '1', '2026-03-15 10:13:10', '0', 1), (22, 15, 102, 10.00, '', '1', '2026-03-15 10:13:10', '1', '2026-03-15 10:13:10', '0', 1), (23, 15, 103, 10.00, '', '1', '2026-03-15 10:13:10', '1', '2026-03-15 10:13:10', '0', 1), (24, 10, 102, 10.00, '', '1', '2026-04-05 16:19:56', '1', '2026-04-05 16:19:56', '0', 1), (25, 10, 101, 10.00, '', '1', '2026-04-05 16:19:56', '1', '2026-04-05 16:19:56', '0', 1), (26, 10, 100, 10.00, '', '1', '2026-04-05 16:19:56', '1', '2026-04-05 16:19:56', '0', 1), (27, 10, 95, 10.00, '', '1', '2026-04-05 16:19:56', '1', '2026-04-05 16:19:56', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_work_order_bom_seq;
CREATE SEQUENCE mes_pro_work_order_bom_seq
    START 28;

-- ----------------------------
-- Table structure for mes_pro_work_record
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_work_record;
CREATE TABLE mes_pro_work_record (
    id int8 NOT NULL,
  user_id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  type int2 NOT NULL,
  clock_in_time timestamp NULL DEFAULT NULL,
  clock_out_time timestamp NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_work_record ADD CONSTRAINT pk_mes_pro_work_record PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_pro_work_record_01 ON mes_pro_work_record (user_id);

COMMENT ON COLUMN mes_pro_work_record.id IS '编号';
COMMENT ON COLUMN mes_pro_work_record.user_id IS '用户编号';
COMMENT ON COLUMN mes_pro_work_record.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_work_record.type IS '当前状态';
COMMENT ON COLUMN mes_pro_work_record.clock_in_time IS '上工时间';
COMMENT ON COLUMN mes_pro_work_record.clock_out_time IS '下工时间';
COMMENT ON COLUMN mes_pro_work_record.remark IS '备注';
COMMENT ON COLUMN mes_pro_work_record.creator IS '创建者';
COMMENT ON COLUMN mes_pro_work_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_work_record.updater IS '更新者';
COMMENT ON COLUMN mes_pro_work_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_work_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_work_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_work_record IS 'MES 当前绑定状态快照';

-- ----------------------------
-- Records of mes_pro_work_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_work_record (id, user_id, workstation_id, type, clock_in_time, clock_out_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 7, 1, '2026-05-28 00:01:50', '2026-05-28 00:01:42', '', '1', '2026-04-05 09:00:00', '1', '2026-05-28 00:01:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_work_record_seq;
CREATE SEQUENCE mes_pro_work_record_seq
    START 2;

-- ----------------------------
-- Table structure for mes_pro_work_record_log
-- ----------------------------
DROP TABLE IF EXISTS mes_pro_work_record_log;
CREATE TABLE mes_pro_work_record_log (
    id int8 NOT NULL,
  user_id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  type int2 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_pro_work_record_log ADD CONSTRAINT pk_mes_pro_work_record_log PRIMARY KEY (id);

COMMENT ON COLUMN mes_pro_work_record_log.id IS '编号';
COMMENT ON COLUMN mes_pro_work_record_log.user_id IS '用户编号';
COMMENT ON COLUMN mes_pro_work_record_log.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_pro_work_record_log.type IS '操作类型';
COMMENT ON COLUMN mes_pro_work_record_log.remark IS '备注';
COMMENT ON COLUMN mes_pro_work_record_log.creator IS '创建者';
COMMENT ON COLUMN mes_pro_work_record_log.create_time IS '创建时间';
COMMENT ON COLUMN mes_pro_work_record_log.updater IS '更新者';
COMMENT ON COLUMN mes_pro_work_record_log.update_time IS '更新时间';
COMMENT ON COLUMN mes_pro_work_record_log.deleted IS '是否删除';
COMMENT ON COLUMN mes_pro_work_record_log.tenant_id IS '租户编号';
COMMENT ON TABLE mes_pro_work_record_log IS 'MES 上下工记录流水';

-- ----------------------------
-- Records of mes_pro_work_record_log
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_pro_work_record_log (id, user_id, workstation_id, type, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, '', '1', '2026-04-05 09:00:00', '1', '2026-04-05 09:00:00', '0', 1), (2, 1, 1, 2, '', '1', '2026-04-05 12:00:00', '1', '2026-04-05 12:00:00', '0', 1), (3, 1, 3, 1, '', '1', '2026-04-05 13:30:00', '1', '2026-04-05 13:30:00', '0', 1), (4, 1, 3, 2, '', '1', '2026-04-05 17:30:00', '1', '2026-04-05 17:30:00', '0', 1), (5, 1, 1, 1, '', '1', '2026-04-06 09:00:00', '1', '2026-04-06 09:00:00', '0', 1), (6, 1, 1, 2, '', '1', '2026-05-27 06:55:24', '1', '2026-05-27 06:55:24', '0', 1), (7, 1, 4, 1, '', '1', '2026-05-27 06:55:40', '1', '2026-05-27 06:55:40', '0', 1), (8, 1, 4, 2, '', '1', '2026-05-27 06:55:45', '1', '2026-05-27 06:55:45', '0', 1), (9, 1, 6, 1, '', '1', '2026-05-27 06:56:04', '1', '2026-05-27 06:56:04', '0', 1), (10, 1, 6, 2, '', '1', '2026-05-27 06:56:07', '1', '2026-05-27 06:56:07', '0', 1), (11, 1, 6, 1, '', '1', '2026-05-27 23:55:22', '1', '2026-05-27 23:55:22', '0', 1), (12, 1, 6, 2, '', '1', '2026-05-27 23:55:26', '1', '2026-05-27 23:55:26', '0', 1), (13, 1, 5, 1, '', '1', '2026-05-27 23:55:43', '1', '2026-05-27 23:55:43', '0', 1), (14, 1, 5, 2, '', '1', '2026-05-28 00:01:34', '1', '2026-05-28 00:01:34', '0', 1), (15, 1, 6, 1, '', '1', '2026-05-28 00:01:40', '1', '2026-05-28 00:01:40', '0', 1), (16, 1, 6, 2, '', '1', '2026-05-28 00:01:42', '1', '2026-05-28 00:01:42', '0', 1), (17, 1, 7, 1, '', '1', '2026-05-28 00:01:50', '1', '2026-05-28 00:01:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_pro_work_record_log_seq;
CREATE SEQUENCE mes_pro_work_record_log_seq
    START 18;

-- ----------------------------
-- Table structure for mes_qc_defect
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_defect;
CREATE TABLE mes_qc_defect (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(500) NOT NULL,
  type int2 NOT NULL,
  level int4 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_defect ADD CONSTRAINT pk_mes_qc_defect PRIMARY KEY (id);

COMMENT ON COLUMN mes_qc_defect.id IS '编号';
COMMENT ON COLUMN mes_qc_defect.code IS '缺陷编码';
COMMENT ON COLUMN mes_qc_defect.name IS '缺陷描述';
COMMENT ON COLUMN mes_qc_defect.type IS '检测项类型（枚举：MesQcDefectTypeEnum）';
COMMENT ON COLUMN mes_qc_defect.level IS '缺陷等级';
COMMENT ON COLUMN mes_qc_defect.remark IS '备注';
COMMENT ON COLUMN mes_qc_defect.creator IS '创建者';
COMMENT ON COLUMN mes_qc_defect.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_defect.updater IS '更新者';
COMMENT ON COLUMN mes_qc_defect.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_defect.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_defect.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_defect IS 'MES 缺陷类型表';

-- ----------------------------
-- Records of mes_qc_defect
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_defect (id, code, name, type, level, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'D0001', '表面划痕', 2, 3, '轻微划痕，不影响功能', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (201, 'D0002', '颜色偏差', 2, 3, '色差在可接受范围边缘', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (202, 'D0003', '尺寸超差', 1, 2, '尺寸超出公差范围', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (203, 'D0004', '表面凹陷', 2, 2, '表面有明显凹陷', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (204, 'D0005', '材料裂纹', 2, 1, '材料出现裂纹，存在安全隐患', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (205, 'D0006', '毛刺未清理', 2, 3, '边缘存在毛刺', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (206, 'D0007', '焊接不良', 2, 2, '焊点不饱满或虚焊', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1), (207, 'D0008', '功能失效', 2, 1, '功能测试不通过', '1', '2026-02-21 13:35:58', '1', '2026-04-09 15:03:20', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_defect_seq;
CREATE SEQUENCE mes_qc_defect_seq
    START 208;

-- ----------------------------
-- Table structure for mes_qc_defect_record
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_defect_record;
CREATE TABLE mes_qc_defect_record (
    id int8 NOT NULL,
  qc_type int4 NOT NULL DEFAULT 1,
  qc_id int8 NOT NULL,
  line_id int8 NOT NULL,
  name varchar(500) NOT NULL,
  level int4 NOT NULL,
  quantity int4 NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_defect_record ADD CONSTRAINT pk_mes_qc_defect_record PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_defect_record_01 ON mes_qc_defect_record (qc_type, qc_id);
CREATE INDEX idx_mes_qc_defect_record_02 ON mes_qc_defect_record (qc_type, qc_id, line_id);

COMMENT ON COLUMN mes_qc_defect_record.id IS '编号';
COMMENT ON COLUMN mes_qc_defect_record.qc_type IS '检验类型';
COMMENT ON COLUMN mes_qc_defect_record.qc_id IS '检验单ID';
COMMENT ON COLUMN mes_qc_defect_record.line_id IS '检验行ID';
COMMENT ON COLUMN mes_qc_defect_record.name IS '缺陷描述';
COMMENT ON COLUMN mes_qc_defect_record.level IS '缺陷等级';
COMMENT ON COLUMN mes_qc_defect_record.quantity IS '缺陷数量';
COMMENT ON COLUMN mes_qc_defect_record.remark IS '备注';
COMMENT ON COLUMN mes_qc_defect_record.creator IS '创建者';
COMMENT ON COLUMN mes_qc_defect_record.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_defect_record.updater IS '更新者';
COMMENT ON COLUMN mes_qc_defect_record.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_defect_record.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_defect_record.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_defect_record IS 'MES 质检缺陷记录';

-- ----------------------------
-- Records of mes_qc_defect_record
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_defect_record (id, qc_type, qc_id, line_id, name, level, quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 1, 201, 203, '长度超出上限，实测80.35mm（上限80.20mm）', 2, 1, '3号样品', '1', '2026-02-03 12:00:00', '1', '2026-02-03 16:00:00', '0', 1), (201, 1, 201, 204, '颜色偏差，与标准色板不一致', 3, 1, '2号样品', '1', '2026-02-03 12:30:00', '1', '2026-02-03 16:00:00', '0', 1), (202, 1, 201, 204, '表面有轻微污渍', 3, 1, '7号样品', '1', '2026-02-03 13:00:00', '1', '2026-02-03 16:00:00', '0', 1), (203, 1, 202, 205, '表面含轻微杂质颗粒', 3, 1, '1号样品，不影响使用', '1', '2026-02-10 13:00:00', '104', '2026-02-10 15:00:00', '0', 1), (204, 1, 203, 207, 'xxx', 2, 1, '', '1', '2026-02-21 22:06:23', '1', '2026-02-21 22:06:23', '0', 1), (210, 2, 301, 303, '长度超出上限，实测150.65mm（上限150.50mm）', 2, 1, '5号样品', '1', '2026-02-21 12:00:00', '1', '2026-02-21 16:00:00', '0', 1), (211, 2, 301, 303, '表面有轻微划痕', 3, 1, '8号样品', '1', '2026-02-21 12:30:00', '1', '2026-02-21 16:00:00', '0', 1), (212, 2, 301, 303, '注塑毛刺未清理', 3, 1, '12号样品', '1', '2026-02-21 13:00:00', '1', '2026-02-21 16:00:00', '0', 1), (213, 3, 201, 202, '包装盒长度超出上限，实测120.65mm（上限120.50mm）', 2, 1, '2号样品', '1', '2026-02-19 13:00:00', '1', '2026-02-19 15:00:00', '0', 1), (214, 3, 201, 203, '包装盒正面印刷文字模糊', 3, 1, '3号样品', '1', '2026-02-19 13:30:00', '1', '2026-02-19 15:00:00', '0', 1), (215, 3, 202, 206, '合格证字迹模糊，需重新打印', 3, 1, '1号箱', '1', '2026-02-20 13:00:00', '104', '2026-02-20 14:00:00', '0', 1), (220, 4, 301, 303, '退回刀头长度超出上限，实测80.72mm（上限80.50mm）', 2, 1, '5号样品', '1', '2026-02-21 13:00:00', '1', '2026-02-21 15:00:00', '0', 1), (221, 4, 301, 304, '退回数量与退货单不一致，差1件', 3, 1, '清点差异', '1', '2026-02-21 13:30:00', '1', '2026-02-21 15:00:00', '0', 1), (222, 4, 301, 305, '退回产品合格证缺失', 2, 1, '3号样品', '1', '2026-02-21 14:00:00', '1', '2026-02-21 15:00:00', '0', 1), (223, 4, 302, 307, '退料包装袋破损，颗粒轻微溢出', 3, 1, '1号包装袋', '1', '2026-02-22 12:00:00', '104', '2026-02-22 14:00:00', '0', 1), (224, 4, 304, 310, '1', 1, 1, '333', '1', '2026-02-22 14:49:31', '1', '2026-02-22 14:49:31', '0', 1), (225, 1, 208, 219, 'ABC', 1, 1, '', '1', '2026-03-23 23:09:35', '1', '2026-03-23 23:09:35', '0', 1), (226, 1, 204, 210, 'AA', 1, 1, '', '1', '2026-04-17 07:35:41', '1', '2026-04-17 07:35:41', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_defect_record_seq;
CREATE SEQUENCE mes_qc_defect_record_seq
    START 227;

-- ----------------------------
-- Table structure for mes_qc_indicator
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_indicator;
CREATE TABLE mes_qc_indicator (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  type int2 NOT NULL,
  tool varchar(255) NULL DEFAULT NULL,
  result_type int2 NOT NULL,
  result_specification varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_indicator ADD CONSTRAINT pk_mes_qc_indicator PRIMARY KEY (id);

COMMENT ON COLUMN mes_qc_indicator.id IS '编号';
COMMENT ON COLUMN mes_qc_indicator.code IS '检测项编码';
COMMENT ON COLUMN mes_qc_indicator.name IS '检测项名称';
COMMENT ON COLUMN mes_qc_indicator.type IS '';
COMMENT ON COLUMN mes_qc_indicator.tool IS '检测工具';
COMMENT ON COLUMN mes_qc_indicator.result_type IS '结果值类型';
COMMENT ON COLUMN mes_qc_indicator.result_specification IS '结果值属性';
COMMENT ON COLUMN mes_qc_indicator.remark IS '备注';
COMMENT ON COLUMN mes_qc_indicator.creator IS '创建者';
COMMENT ON COLUMN mes_qc_indicator.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_indicator.updater IS '更新者';
COMMENT ON COLUMN mes_qc_indicator.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_indicator.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_indicator.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_indicator IS 'MES 质检指标';

-- ----------------------------
-- Records of mes_qc_indicator
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_indicator (id, code, name, type, tool, result_type, result_specification, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'QI0035', '长度', 1, '卡尺', 1, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (201, 'QI0036', '宽度', 1, '卡尺', 1, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (202, 'QI0038', '颜色是否纯正', 2, NULL, 4, 'sys_yes_no', '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (203, 'QI0040', '外观照片', 2, NULL, 5, 'IMG', '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (204, 'QI0041', '检测过程视频', 2, NULL, 5, 'FILE', '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (205, 'QI0042', '个数清点', 2, NULL, 2, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1), (206, 'QI0043', '合格证编号', 2, NULL, 3, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-04-09 14:38:53', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_indicator_seq;
CREATE SEQUENCE mes_qc_indicator_seq
    START 207;

-- ----------------------------
-- Table structure for mes_qc_indicator_result
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_indicator_result;
CREATE TABLE mes_qc_indicator_result (
    id int8 NOT NULL,
  code varchar(64) NULL DEFAULT NULL,
  qc_id int8 NOT NULL,
  qc_type int2 NOT NULL,
  item_id int8 NULL DEFAULT NULL,
  sn varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_indicator_result ADD CONSTRAINT pk_mes_qc_indicator_result PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_indicator_result_01 ON mes_qc_indicator_result (qc_id);
CREATE INDEX idx_mes_qc_indicator_result_02 ON mes_qc_indicator_result (item_id);

COMMENT ON COLUMN mes_qc_indicator_result.id IS '编号';
COMMENT ON COLUMN mes_qc_indicator_result.code IS '样品编号';
COMMENT ON COLUMN mes_qc_indicator_result.qc_id IS '关联质检单ID（IQC/IPQC/OQC/RQC 的 id）';
COMMENT ON COLUMN mes_qc_indicator_result.qc_type IS '质检类型';
COMMENT ON COLUMN mes_qc_indicator_result.item_id IS '产品物料ID（关联 mes_md_item）';
COMMENT ON COLUMN mes_qc_indicator_result.sn IS '物资SN';
COMMENT ON COLUMN mes_qc_indicator_result.remark IS '备注';
COMMENT ON COLUMN mes_qc_indicator_result.creator IS '创建者';
COMMENT ON COLUMN mes_qc_indicator_result.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_indicator_result.updater IS '更新者';
COMMENT ON COLUMN mes_qc_indicator_result.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_indicator_result.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_indicator_result.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_indicator_result IS 'MES 检验结果记录';

-- ----------------------------
-- Records of mes_qc_indicator_result
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_indicator_result (id, code, qc_id, qc_type, item_id, sn, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'xx', 203, 1, 72, '12', '333', '1', '2026-02-21 23:27:17', '1', '2026-02-22 07:55:38', '1', 1), (2, '33', 203, 1, 72, '12', '4444', '1', '2026-02-21 23:27:41', '1', '2026-02-22 07:55:40', '1', 1), (3, '1', 203, 1, 72, '2', '', '1', '2026-02-22 07:55:51', '1', '2026-02-22 07:57:58', '0', 1), (4, '2', 203, 1, 72, '3', '', '1', '2026-02-22 08:01:04', '1', '2026-02-22 08:01:23', '0', 1), (5, 'QR2026041500001', 204, 1, 70, NULL, '', '1', '2026-04-15 23:54:53', '1', '2026-04-15 23:54:53', '0', 1), (6, 'QR2026041500002', 204, 1, 70, NULL, '', '1', '2026-04-15 23:56:43', '1', '2026-04-15 23:56:43', '0', 1), (7, 'QR2026041700001', 209, 1, 69, NULL, '', '1', '2026-04-17 07:33:12', '1', '2026-04-17 07:33:12', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_indicator_result_seq;
CREATE SEQUENCE mes_qc_indicator_result_seq
    START 8;

-- ----------------------------
-- Table structure for mes_qc_indicator_result_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_indicator_result_detail;
CREATE TABLE mes_qc_indicator_result_detail (
    id int8 NOT NULL,
  result_id int8 NOT NULL,
  indicator_id int8 NULL DEFAULT NULL,
  value varchar(500) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_indicator_result_detail ADD CONSTRAINT pk_mes_qc_indicator_result_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_indicator_result_detail_01 ON mes_qc_indicator_result_detail (result_id);
CREATE INDEX idx_mes_qc_indicator_result_detail_02 ON mes_qc_indicator_result_detail (indicator_id);

COMMENT ON COLUMN mes_qc_indicator_result_detail.id IS '编号';
COMMENT ON COLUMN mes_qc_indicator_result_detail.result_id IS '关联检验结果ID（关联 mes_qc_indicator_result）';
COMMENT ON COLUMN mes_qc_indicator_result_detail.indicator_id IS '检测指标ID（关联 mes_qc_indicator）';
COMMENT ON COLUMN mes_qc_indicator_result_detail.value IS '检测值（统一存为字符串）';
COMMENT ON COLUMN mes_qc_indicator_result_detail.remark IS '备注';
COMMENT ON COLUMN mes_qc_indicator_result_detail.creator IS '创建者';
COMMENT ON COLUMN mes_qc_indicator_result_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_indicator_result_detail.updater IS '更新者';
COMMENT ON COLUMN mes_qc_indicator_result_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_indicator_result_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_indicator_result_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_indicator_result_detail IS 'MES 检验结果明细记录';

-- ----------------------------
-- Records of mes_qc_indicator_result_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_indicator_result_detail (id, result_id, indicator_id, value, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 200, '3', '', '1', '2026-02-21 23:27:17', '1', '2026-02-21 23:55:38', '1', 1), (2, 1, 201, NULL, '', '1', '2026-02-21 23:27:17', '1', '2026-02-21 23:55:38', '1', 1), (3, 1, 206, '6', '', '1', '2026-02-21 23:27:17', '1', '2026-02-21 23:55:38', '1', 1), (4, 2, 200, '3', '', '1', '2026-02-21 23:27:41', '1', '2026-02-21 23:55:40', '1', 1), (5, 2, 201, '4', '', '1', '2026-02-21 23:27:41', '1', '2026-02-21 23:55:40', '1', 1), (6, 2, 206, '6', '', '1', '2026-02-21 23:27:41', '1', '2026-02-21 23:55:40', '1', 1), (7, 3, 200, '5', '', '1', '2026-02-22 07:55:51', '1', '2026-02-22 07:57:58', '0', 1), (8, 3, 201, '8', '', '1', '2026-02-22 07:55:51', '1', '2026-02-22 07:57:58', '0', 1), (9, 3, 206, '10', '', '1', '2026-02-22 07:55:51', '1', '2026-02-22 07:57:58', '0', 1), (10, 4, 200, NULL, '', '1', '2026-02-22 08:01:04', '1', '2026-02-22 08:01:23', '0', 1), (11, 4, 201, NULL, '', '1', '2026-02-22 08:01:04', '1', '2026-02-22 08:01:23', '0', 1), (12, 4, 206, '6', '', '1', '2026-02-22 08:01:04', '1', '2026-02-22 08:01:23', '0', 1), (13, 5, 200, '200', '', '1', '2026-04-15 23:54:53', '1', '2026-04-15 23:54:53', '0', 1), (14, 5, 205, NULL, '', '1', '2026-04-15 23:54:53', '1', '2026-04-15 23:54:53', '0', 1), (15, 5, 206, NULL, '', '1', '2026-04-15 23:54:53', '1', '2026-04-15 23:54:53', '0', 1), (16, 6, 200, '10.5', '', '1', '2026-04-15 23:56:43', '1', '2026-04-15 23:56:43', '0', 1), (17, 6, 205, '100', '', '1', '2026-04-15 23:56:43', '1', '2026-04-15 23:56:43', '0', 1), (18, 6, 206, 'CERT-OK-20260415', '', '1', '2026-04-15 23:56:43', '1', '2026-04-15 23:56:43', '0', 1), (19, 7, 200, '1', '', '1', '2026-04-17 07:33:12', '1', '2026-04-17 07:33:12', '0', 1), (20, 7, 205, '2', '', '1', '2026-04-17 07:33:12', '1', '2026-04-17 07:33:12', '0', 1), (21, 7, 206, '3', '', '1', '2026-04-17 07:33:12', '1', '2026-04-17 07:33:12', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_indicator_result_detail_seq;
CREATE SEQUENCE mes_qc_indicator_result_detail_seq
    START 22;

-- ----------------------------
-- Table structure for mes_qc_ipqc
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_ipqc;
CREATE TABLE mes_qc_ipqc (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(500) NOT NULL,
  type int2 NOT NULL,
  template_id int8 NOT NULL,
  source_doc_type int4 NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_line_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  work_order_id int8 NOT NULL,
  task_id int8 NULL DEFAULT NULL,
  workstation_id int8 NOT NULL,
  process_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  check_quantity numeric(14,2) NULL DEFAULT NULL,
  qualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  unqualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  labor_scrap_quantity numeric(14,2) NULL DEFAULT 0.00,
  material_scrap_quantity numeric(14,2) NULL DEFAULT 0.00,
  other_scrap_quantity numeric(14,2) NULL DEFAULT 0.00,
  critical_rate numeric(14,2) NULL DEFAULT 0.00,
  major_rate numeric(14,2) NULL DEFAULT 0.00,
  minor_rate numeric(14,2) NULL DEFAULT 0.00,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  check_result int2 NULL DEFAULT NULL,
  inspect_date timestamp NULL DEFAULT NULL,
  inspector_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_ipqc ADD CONSTRAINT pk_mes_qc_ipqc PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_ipqc_01 ON mes_qc_ipqc (code);
CREATE INDEX idx_mes_qc_ipqc_02 ON mes_qc_ipqc (work_order_id);
CREATE INDEX idx_mes_qc_ipqc_03 ON mes_qc_ipqc (workstation_id);
CREATE INDEX idx_mes_qc_ipqc_04 ON mes_qc_ipqc (item_id);

COMMENT ON COLUMN mes_qc_ipqc.id IS '编号';
COMMENT ON COLUMN mes_qc_ipqc.code IS '检验单编号';
COMMENT ON COLUMN mes_qc_ipqc.name IS '检验单名称';
COMMENT ON COLUMN mes_qc_ipqc.type IS 'IPQC 检验类型';
COMMENT ON COLUMN mes_qc_ipqc.template_id IS '检验模板ID';
COMMENT ON COLUMN mes_qc_ipqc.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_qc_ipqc.source_doc_id IS '来源单据ID';
COMMENT ON COLUMN mes_qc_ipqc.source_line_id IS '来源单据行ID';
COMMENT ON COLUMN mes_qc_ipqc.source_doc_code IS '来源单据编号（冗余）';
COMMENT ON COLUMN mes_qc_ipqc.work_order_id IS '生产工单ID';
COMMENT ON COLUMN mes_qc_ipqc.task_id IS '生产任务ID';
COMMENT ON COLUMN mes_qc_ipqc.workstation_id IS '工位ID';
COMMENT ON COLUMN mes_qc_ipqc.process_id IS '工序ID';
COMMENT ON COLUMN mes_qc_ipqc.item_id IS '产品物料ID';
COMMENT ON COLUMN mes_qc_ipqc.check_quantity IS '检测数量';
COMMENT ON COLUMN mes_qc_ipqc.qualified_quantity IS '合格品数量';
COMMENT ON COLUMN mes_qc_ipqc.unqualified_quantity IS '不合格品数量';
COMMENT ON COLUMN mes_qc_ipqc.labor_scrap_quantity IS '工废数量';
COMMENT ON COLUMN mes_qc_ipqc.material_scrap_quantity IS '料废数量';
COMMENT ON COLUMN mes_qc_ipqc.other_scrap_quantity IS '其他废品数量';
COMMENT ON COLUMN mes_qc_ipqc.critical_rate IS '致命缺陷率（%）';
COMMENT ON COLUMN mes_qc_ipqc.major_rate IS '严重缺陷率（%）';
COMMENT ON COLUMN mes_qc_ipqc.minor_rate IS '轻微缺陷率（%）';
COMMENT ON COLUMN mes_qc_ipqc.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc.check_result IS '检测结果';
COMMENT ON COLUMN mes_qc_ipqc.inspect_date IS '检测日期';
COMMENT ON COLUMN mes_qc_ipqc.inspector_user_id IS '检测人员用户 ID';
COMMENT ON COLUMN mes_qc_ipqc.status IS '状态';
COMMENT ON COLUMN mes_qc_ipqc.remark IS '备注';
COMMENT ON COLUMN mes_qc_ipqc.creator IS '创建者';
COMMENT ON COLUMN mes_qc_ipqc.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_ipqc.updater IS '更新者';
COMMENT ON COLUMN mes_qc_ipqc.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_ipqc.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_ipqc.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_ipqc IS 'MES 过程检验单（IPQC）';

-- ----------------------------
-- Records of mes_qc_ipqc
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_ipqc (id, code, name, type, template_id, source_doc_type, source_doc_id, source_line_id, source_doc_code, work_order_id, task_id, workstation_id, process_id, item_id, check_quantity, qualified_quantity, unqualified_quantity, labor_scrap_quantity, material_scrap_quantity, other_scrap_quantity, critical_rate, major_rate, minor_rate, critical_quantity, major_quantity, minor_quantity, check_result, inspect_date, inspector_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (300, 'IPQC20260220001', '螺丝刀组装过程巡检', 1, 102, NULL, NULL, NULL, NULL, 1, NULL, 3, NULL, 75, 50.00, 50.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-02-20 14:30:00', 1, 4, '全部合格，工艺稳定', '1', '2026-02-20 09:00:00', '1', '2026-02-23 21:16:03', '0', 1), (301, 'IPQC20260221002', '刀柄注塑首检', 2, 102, NULL, NULL, NULL, NULL, 2, NULL, 1, NULL, 73, 20.00, 17.00, 3.00, 1.00, 1.00, 1.00, 0.00, 5.00, 10.00, 0, 1, 2, 2, '2026-02-21 16:00:00', 1, 4, '不合格，需调整注塑参数', '1', '2026-02-21 08:30:00', '1', '2026-02-23 21:16:03', '0', 1), (302, 'IPQC20260222003', '包装工序自检', 4, 102, NULL, NULL, NULL, NULL, 1, NULL, 4, NULL, 75, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, NULL, NULL, NULL, 0, '待检验', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (303, 'IPQC20260325000002', 'FB202503180001 过程检验单', 1, 105, 304, 3, NULL, NULL, 1, 1, 1, NULL, 75, 200.00, 200.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2025-03-18 09:00:00', 1, 0, '', '1', '2026-03-25 17:35:19', '1', '2026-03-25 17:35:19', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_ipqc_seq;
CREATE SEQUENCE mes_qc_ipqc_seq
    START 304;

-- ----------------------------
-- Table structure for mes_qc_ipqc_line
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_ipqc_line;
CREATE TABLE mes_qc_ipqc_line (
    id int8 NOT NULL,
  ipqc_id int8 NOT NULL,
  indicator_id int8 NOT NULL,
  tool varchar(255) NULL DEFAULT NULL,
  check_method varchar(500) NULL DEFAULT NULL,
  standard_value numeric(14,4) NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  max_threshold numeric(14,4) NULL DEFAULT NULL,
  min_threshold numeric(14,4) NULL DEFAULT NULL,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_ipqc_line ADD CONSTRAINT pk_mes_qc_ipqc_line PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_ipqc_line_01 ON mes_qc_ipqc_line (ipqc_id);

COMMENT ON COLUMN mes_qc_ipqc_line.id IS '编号';
COMMENT ON COLUMN mes_qc_ipqc_line.ipqc_id IS '过程检验单ID';
COMMENT ON COLUMN mes_qc_ipqc_line.indicator_id IS '检测指标ID';
COMMENT ON COLUMN mes_qc_ipqc_line.tool IS '检测工具';
COMMENT ON COLUMN mes_qc_ipqc_line.check_method IS '检测方法';
COMMENT ON COLUMN mes_qc_ipqc_line.standard_value IS '标准值';
COMMENT ON COLUMN mes_qc_ipqc_line.unit_measure_id IS '计量单位ID';
COMMENT ON COLUMN mes_qc_ipqc_line.max_threshold IS '误差上限';
COMMENT ON COLUMN mes_qc_ipqc_line.min_threshold IS '误差下限';
COMMENT ON COLUMN mes_qc_ipqc_line.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc_line.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc_line.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_ipqc_line.remark IS '备注';
COMMENT ON COLUMN mes_qc_ipqc_line.creator IS '创建者';
COMMENT ON COLUMN mes_qc_ipqc_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_ipqc_line.updater IS '更新者';
COMMENT ON COLUMN mes_qc_ipqc_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_ipqc_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_ipqc_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_ipqc_line IS 'MES 过程检验单行';

-- ----------------------------
-- Records of mes_qc_ipqc_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_ipqc_line (id, ipqc_id, indicator_id, tool, check_method, standard_value, unit_measure_id, max_threshold, min_threshold, critical_quantity, major_quantity, minor_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (300, 300, 200, NULL, '使用千分尺测量成品长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '实测值均在公差范围内', '1', '2026-02-20 10:00:00', '1', '2026-02-20 14:30:00', '0', 1), (301, 300, 205, NULL, '人工清点成品数量', NULL, NULL, NULL, NULL, 0, 0, 0, '数量与报工单一致', '1', '2026-02-20 11:00:00', '1', '2026-02-20 14:30:00', '0', 1), (302, 300, 206, NULL, '核对合格证编号与批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '编号一致', '1', '2026-02-20 11:30:00', '1', '2026-02-20 14:30:00', '0', 1), (303, 301, 200, NULL, '使用千分尺测量刀柄长度', 150.0000, 206, 0.5000, -0.5000, 0, 1, 2, '5号样品长度超上限', '1', '2026-02-21 09:00:00', '1', '2026-02-21 16:00:00', '0', 1), (304, 301, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '数量一致', '1', '2026-02-21 10:00:00', '1', '2026-02-21 16:00:00', '0', 1), (305, 301, 206, NULL, '核对合格证编号', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-21 10:30:00', '1', '2026-02-21 16:00:00', '0', 1), (306, 302, 200, NULL, '使用千分尺测量成品长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (307, 302, 205, NULL, '人工清点成品数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (308, 302, 206, NULL, '核对合格证编号与批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (309, 303, 200, '卡尺', '游标卡尺测量', 15.0000, NULL, 0.5000, 0.5000, 0, 0, 0, '', '1', '2026-03-25 17:35:19', '1', '2026-03-25 17:35:19', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_ipqc_line_seq;
CREATE SEQUENCE mes_qc_ipqc_line_seq
    START 310;

-- ----------------------------
-- Table structure for mes_qc_iqc
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_iqc;
CREATE TABLE mes_qc_iqc (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(500) NOT NULL,
  template_id int8 NOT NULL,
  source_doc_type int2 NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_line_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  vendor_id int8 NOT NULL,
  vendor_batch varchar(64) NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  received_quantity numeric(14,2) NOT NULL,
  check_quantity numeric(14,2) NULL DEFAULT NULL,
  qualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  unqualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  critical_rate numeric(14,2) NULL DEFAULT 0.00,
  major_rate numeric(14,2) NULL DEFAULT 0.00,
  minor_rate numeric(14,2) NULL DEFAULT 0.00,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  check_result int2 NULL DEFAULT NULL,
  receive_date timestamp NULL DEFAULT NULL,
  inspect_date timestamp NULL DEFAULT NULL,
  inspector_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_iqc ADD CONSTRAINT pk_mes_qc_iqc PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_iqc_01 ON mes_qc_iqc (code);
CREATE INDEX idx_mes_qc_iqc_02 ON mes_qc_iqc (vendor_id);
CREATE INDEX idx_mes_qc_iqc_03 ON mes_qc_iqc (item_id);

COMMENT ON COLUMN mes_qc_iqc.id IS '编号';
COMMENT ON COLUMN mes_qc_iqc.code IS '检验单编号';
COMMENT ON COLUMN mes_qc_iqc.name IS '检验单名称';
COMMENT ON COLUMN mes_qc_iqc.template_id IS '检验模板ID';
COMMENT ON COLUMN mes_qc_iqc.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_qc_iqc.source_doc_id IS '来源单据ID';
COMMENT ON COLUMN mes_qc_iqc.source_line_id IS '来源单据行ID';
COMMENT ON COLUMN mes_qc_iqc.source_doc_code IS '来源单据编号（冗余）';
COMMENT ON COLUMN mes_qc_iqc.vendor_id IS '供应商ID';
COMMENT ON COLUMN mes_qc_iqc.vendor_batch IS '供应商批次号';
COMMENT ON COLUMN mes_qc_iqc.item_id IS '产品物料ID';
COMMENT ON COLUMN mes_qc_iqc.received_quantity IS '本次接收数量';
COMMENT ON COLUMN mes_qc_iqc.check_quantity IS '本次检测数量';
COMMENT ON COLUMN mes_qc_iqc.qualified_quantity IS '合格品数量';
COMMENT ON COLUMN mes_qc_iqc.unqualified_quantity IS '不合格品数量';
COMMENT ON COLUMN mes_qc_iqc.critical_rate IS '致命缺陷率（%）';
COMMENT ON COLUMN mes_qc_iqc.major_rate IS '严重缺陷率（%）';
COMMENT ON COLUMN mes_qc_iqc.minor_rate IS '轻微缺陷率（%）';
COMMENT ON COLUMN mes_qc_iqc.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_iqc.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_iqc.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_iqc.check_result IS '检测结果';
COMMENT ON COLUMN mes_qc_iqc.receive_date IS '来料日期';
COMMENT ON COLUMN mes_qc_iqc.inspect_date IS '检测日期';
COMMENT ON COLUMN mes_qc_iqc.inspector_user_id IS '检测人员用户 ID';
COMMENT ON COLUMN mes_qc_iqc.status IS '状态';
COMMENT ON COLUMN mes_qc_iqc.remark IS '备注';
COMMENT ON COLUMN mes_qc_iqc.creator IS '创建者';
COMMENT ON COLUMN mes_qc_iqc.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_iqc.updater IS '更新者';
COMMENT ON COLUMN mes_qc_iqc.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_iqc.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_iqc.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_iqc IS 'MES 来料检验单（IQC）';

-- ----------------------------
-- Records of mes_qc_iqc
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_iqc (id, code, name, template_id, source_doc_type, source_doc_id, source_line_id, source_doc_code, vendor_id, vendor_batch, item_id, received_quantity, check_quantity, qualified_quantity, unqualified_quantity, critical_rate, major_rate, minor_rate, critical_quantity, major_quantity, minor_quantity, check_result, receive_date, inspect_date, inspector_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'IQC20260201001', '螺丝刀刀柄【蓝色】来料检验', 100, NULL, NULL, NULL, NULL, 200, 'VB20260201-A', 73, 500.00, 5.00, 5.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-02-01 09:00:00', '2026-02-01 14:30:00', 1, 4, '全部合格，质量稳定', '1', '2026-02-01 09:00:00', '1', '2026-02-23 21:16:03', '0', 1), (201, 'IQC20260203002', '螺丝刀刀头来料检验', 100, NULL, NULL, NULL, NULL, 201, 'VB20260203-B', 74, 1000.00, 10.00, 7.00, 3.00, 0.00, 10.00, 20.00, 0, 1, 2, 2, '2026-02-03 08:30:00', '2026-02-03 16:00:00', 1, 4, '不合格，已通知供应商退货', '1', '2026-02-03 08:30:00', '1', '2026-02-23 21:16:03', '0', 1), (202, 'IQC20260210003', 'PVC颗粒来料检验', 102, NULL, NULL, NULL, NULL, 202, 'VB20260210-C', 70, 200.00, 3.00, 2.00, 1.00, 0.00, 0.00, 33.33, 0, 0, 1, 3, '2026-02-10 10:00:00', '2026-02-10 15:00:00', 104, 4, '轻微外观问题，让步接收', '1', '2026-02-10 10:00:00', '104', '2026-02-23 21:16:03', '0', 1), (203, 'IQC20260215004', '钢筋来料检验', 102, 100, NULL, NULL, NULL, 200, 'VB20260215-D', 72, 300.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 1, 0, NULL, '2026-02-15 09:00:00', '1970-01-01 08:00:00', 1, 0, '待检验', '1', '2026-02-15 09:00:00', '1', '2026-02-24 04:35:52', '0', 1), (204, 'IQCFsDNvt7ZHX', '呃呃呃', 102, NULL, NULL, NULL, NULL, 202, NULL, 70, 3.00, 2.00, 2.00, 0.00, 50.00, 0.00, 0.00, 1, 0, 0, NULL, '1970-01-01 08:00:00', '1970-01-01 08:00:00', 1, 0, '', '1', '2026-02-24 04:37:19', '1', '2026-04-17 07:35:41', '0', 1), (206, 'IQC8vMXxJFbUe', 'aaa', 102, NULL, NULL, NULL, NULL, 200, 'eee', 70, 3.00, 3.00, 2.00, 1.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '1970-01-01 08:00:00', '1970-01-01 08:00:00', 1, 4, '', '1', '2026-02-24 04:40:18', '1', '2026-03-27 22:32:56', '0', 1), (207, 'IQCRFgr3hdsbJ', 'ANbhv1kzQHtc 来料检验单', 102, 100, 106, 110, NULL, 200, NULL, 69, 1.00, 1.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-03-04 00:00:00', '1970-01-01 08:00:00', 1, 4, '12321', '1', '2026-03-23 19:45:55', '1', '2026-03-23 19:51:39', '0', 1), (208, 'IQCDPBIp5v1S8', 'ABC', 102, NULL, NULL, NULL, NULL, 201, NULL, 69, 1.00, 1.00, 0.00, 1.00, 100.00, 0.00, 0.00, 1, 0, 0, 1, '2026-03-03 00:00:00', '2026-03-12 00:00:00', 100, 4, '', '1', '2026-03-23 21:52:59', '1', '2026-03-27 22:30:11', '0', 1), (209, 'IQC20260325001', 'ANpcHrG0Q7S2 来料检验单', 102, 100, 105, 109, NULL, 200, NULL, 69, 10.00, 10.00, 10.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-03-23 00:00:00', '2026-03-10 00:00:00', 1, 4, '', '1', '2026-03-25 17:36:12', '1', '2026-03-27 22:32:18', '0', 1), (210, 'IQC20260529001', '测试一下', 102, NULL, NULL, NULL, NULL, 202, NULL, 69, 3.00, 3.00, 1.00, 2.00, 0.00, 0.00, 0.00, 0, 0, 0, NULL, '2026-05-13 09:56:23', '2026-05-14 09:56:25', 100, 0, '', '1', '2026-05-29 09:56:51', '1', '2026-05-29 09:56:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_iqc_seq;
CREATE SEQUENCE mes_qc_iqc_seq
    START 211;

-- ----------------------------
-- Table structure for mes_qc_iqc_line
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_iqc_line;
CREATE TABLE mes_qc_iqc_line (
    id int8 NOT NULL,
  iqc_id int8 NOT NULL,
  indicator_id int8 NOT NULL,
  tool varchar(255) NULL DEFAULT NULL,
  check_method varchar(500) NULL DEFAULT NULL,
  standard_value numeric(14,4) NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  max_threshold numeric(14,4) NULL DEFAULT NULL,
  min_threshold numeric(14,4) NULL DEFAULT NULL,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_iqc_line ADD CONSTRAINT pk_mes_qc_iqc_line PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_iqc_line_01 ON mes_qc_iqc_line (iqc_id);

COMMENT ON COLUMN mes_qc_iqc_line.id IS '编号';
COMMENT ON COLUMN mes_qc_iqc_line.iqc_id IS '来料检验单ID';
COMMENT ON COLUMN mes_qc_iqc_line.indicator_id IS '检测指标ID';
COMMENT ON COLUMN mes_qc_iqc_line.tool IS '检测工具';
COMMENT ON COLUMN mes_qc_iqc_line.check_method IS '检测方法';
COMMENT ON COLUMN mes_qc_iqc_line.standard_value IS '标准值';
COMMENT ON COLUMN mes_qc_iqc_line.unit_measure_id IS '计量单位ID';
COMMENT ON COLUMN mes_qc_iqc_line.max_threshold IS '误差上限';
COMMENT ON COLUMN mes_qc_iqc_line.min_threshold IS '误差下限';
COMMENT ON COLUMN mes_qc_iqc_line.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_iqc_line.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_iqc_line.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_iqc_line.remark IS '备注';
COMMENT ON COLUMN mes_qc_iqc_line.creator IS '创建者';
COMMENT ON COLUMN mes_qc_iqc_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_iqc_line.updater IS '更新者';
COMMENT ON COLUMN mes_qc_iqc_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_iqc_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_iqc_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_iqc_line IS 'MES 来料检验单行';

-- ----------------------------
-- Records of mes_qc_iqc_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_iqc_line (id, iqc_id, indicator_id, tool, check_method, standard_value, unit_measure_id, max_threshold, min_threshold, critical_quantity, major_quantity, minor_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 200, 200, NULL, '使用千分尺测量刀柄长度', 150.0000, 206, 150.5000, 149.5000, 0, 0, 0, '实测值 150.02mm，合格', '1', '2026-02-01 10:00:00', '1', '2026-02-01 14:30:00', '0', 1), (201, 200, 201, NULL, '使用千分尺测量刀柄宽度', 25.0000, 206, 25.2000, 24.8000, 0, 0, 0, '实测值 24.98mm，合格', '1', '2026-02-01 10:30:00', '1', '2026-02-01 14:30:00', '0', 1), (202, 200, 202, NULL, '目视对比标准色板', NULL, NULL, NULL, NULL, 0, 0, 0, '颜色纯正，合格', '1', '2026-02-01 11:00:00', '1', '2026-02-01 14:30:00', '0', 1), (203, 201, 200, NULL, '使用千分尺测量刀头长度', 80.0000, 206, 80.2000, 79.8000, 0, 1, 0, '3号样品实测 80.35mm 超上限', '1', '2026-02-03 09:00:00', '1', '2026-02-03 16:00:00', '0', 1), (204, 201, 202, NULL, '目视对比标准色板', NULL, NULL, NULL, NULL, 0, 0, 2, '2号和7号样品颜色偏差', '1', '2026-02-03 10:00:00', '1', '2026-02-03 16:00:00', '0', 1), (205, 202, 203, NULL, '拍摄外观照片存档', NULL, NULL, NULL, NULL, 0, 0, 1, '1号样品表面有轻微杂质', '1', '2026-02-10 11:00:00', '104', '2026-02-10 15:00:00', '0', 1), (206, 202, 205, NULL, '人工清点入库数量', NULL, NULL, NULL, NULL, 0, 0, 0, '数量与送货单一致', '1', '2026-02-10 11:30:00', '104', '2026-02-10 15:00:00', '0', 1), (207, 203, 200, NULL, '使用千分尺测量钢筋长度', 6000.0000, 206, 6010.0000, 5990.0000, 0, 1, 0, '', '1', '2026-02-15 09:00:00', '1', '2026-02-21 22:06:23', '0', 1), (208, 203, 201, NULL, '使用千分尺测量钢筋直径', 12.0000, 206, 12.1000, 11.9000, 0, 0, 0, '', '1', '2026-02-15 09:00:00', '1', '2026-02-21 22:06:23', '0', 1), (209, 203, 206, NULL, '核对合格证编号', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-15 09:00:00', '1', '2026-02-21 22:06:23', '0', 1), (210, 204, 200, NULL, '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 1, 0, 0, '', '1', '2026-02-24 04:37:19', '1', '2026-04-17 07:35:41', '0', 1), (211, 204, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-24 04:37:19', '1', '2026-04-17 07:35:41', '0', 1), (212, 204, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-24 04:37:19', '1', '2026-04-17 07:35:41', '0', 1), (213, 206, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-02-24 04:40:18', '1', '2026-02-24 04:40:18', '0', 1), (214, 206, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-24 04:40:18', '1', '2026-02-24 04:40:18', '0', 1), (215, 206, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-24 04:40:18', '1', '2026-02-24 04:40:18', '0', 1), (216, 207, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-23 19:45:55', '1', '2026-03-23 19:45:55', '0', 1), (217, 207, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-23 19:45:55', '1', '2026-03-23 19:45:55', '0', 1), (218, 207, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-23 19:45:55', '1', '2026-03-23 19:45:55', '0', 1), (219, 208, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 1, 0, 0, '', '1', '2026-03-23 21:52:59', '1', '2026-03-23 23:09:35', '0', 1), (220, 208, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-23 21:52:59', '1', '2026-03-23 23:09:35', '0', 1), (221, 208, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-23 21:52:59', '1', '2026-03-23 23:09:35', '0', 1), (222, 209, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-25 17:36:12', '1', '2026-03-25 17:36:12', '0', 1), (223, 209, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-25 17:36:12', '1', '2026-03-25 17:36:12', '0', 1), (224, 209, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-25 17:36:12', '1', '2026-03-25 17:36:12', '0', 1), (225, 210, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-05-29 09:56:51', '1', '2026-05-29 09:56:51', '0', 1), (226, 210, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-05-29 09:56:51', '1', '2026-05-29 09:56:51', '0', 1), (227, 210, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-05-29 09:56:51', '1', '2026-05-29 09:56:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_iqc_line_seq;
CREATE SEQUENCE mes_qc_iqc_line_seq
    START 228;

-- ----------------------------
-- Table structure for mes_qc_oqc
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_oqc;
CREATE TABLE mes_qc_oqc (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(500) NOT NULL,
  template_id int8 NOT NULL,
  source_doc_type int2 NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_line_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  client_id int8 NOT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  min_check_quantity int4 NULL DEFAULT 1,
  max_unqualified_quantity int4 NULL DEFAULT 0,
  out_quantity numeric(14,2) NOT NULL,
  check_quantity numeric(14,2) NULL DEFAULT NULL,
  qualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  unqualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  critical_rate numeric(14,2) NULL DEFAULT 0.00,
  major_rate numeric(14,2) NULL DEFAULT 0.00,
  minor_rate numeric(14,2) NULL DEFAULT 0.00,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  check_result int2 NULL DEFAULT NULL,
  out_date timestamp NULL DEFAULT NULL,
  inspect_date timestamp NULL DEFAULT NULL,
  inspector_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_oqc ADD CONSTRAINT pk_mes_qc_oqc PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_oqc_01 ON mes_qc_oqc (code);
CREATE INDEX idx_mes_qc_oqc_02 ON mes_qc_oqc (client_id);
CREATE INDEX idx_mes_qc_oqc_03 ON mes_qc_oqc (item_id);

COMMENT ON COLUMN mes_qc_oqc.id IS '编号';
COMMENT ON COLUMN mes_qc_oqc.code IS '检验单编号';
COMMENT ON COLUMN mes_qc_oqc.name IS '检验单名称';
COMMENT ON COLUMN mes_qc_oqc.template_id IS '检验模板ID（关联 mes_qc_template）';
COMMENT ON COLUMN mes_qc_oqc.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_qc_oqc.source_doc_id IS '来源单据ID';
COMMENT ON COLUMN mes_qc_oqc.source_line_id IS '来源单据行ID';
COMMENT ON COLUMN mes_qc_oqc.source_doc_code IS '来源单据编号';
COMMENT ON COLUMN mes_qc_oqc.client_id IS '客户ID（关联 mes_md_client）';
COMMENT ON COLUMN mes_qc_oqc.batch_code IS '批次号';
COMMENT ON COLUMN mes_qc_oqc.item_id IS '产品物料ID（关联 mes_md_item）';
COMMENT ON COLUMN mes_qc_oqc.min_check_quantity IS '最低检测数';
COMMENT ON COLUMN mes_qc_oqc.max_unqualified_quantity IS '最大不合格数';
COMMENT ON COLUMN mes_qc_oqc.out_quantity IS '本次出货数量';
COMMENT ON COLUMN mes_qc_oqc.check_quantity IS '本次检测数量';
COMMENT ON COLUMN mes_qc_oqc.qualified_quantity IS '合格品数量';
COMMENT ON COLUMN mes_qc_oqc.unqualified_quantity IS '不合格品数量';
COMMENT ON COLUMN mes_qc_oqc.critical_rate IS '致命缺陷率（%）';
COMMENT ON COLUMN mes_qc_oqc.major_rate IS '严重缺陷率（%）';
COMMENT ON COLUMN mes_qc_oqc.minor_rate IS '轻微缺陷率（%）';
COMMENT ON COLUMN mes_qc_oqc.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_oqc.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_oqc.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_oqc.check_result IS '检测结果（枚举 MesQcCheckResultEnum）';
COMMENT ON COLUMN mes_qc_oqc.out_date IS '出货日期';
COMMENT ON COLUMN mes_qc_oqc.inspect_date IS '检测日期';
COMMENT ON COLUMN mes_qc_oqc.inspector_user_id IS '检测人员用户 ID';
COMMENT ON COLUMN mes_qc_oqc.status IS '状态';
COMMENT ON COLUMN mes_qc_oqc.remark IS '备注';
COMMENT ON COLUMN mes_qc_oqc.creator IS '创建者';
COMMENT ON COLUMN mes_qc_oqc.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_oqc.updater IS '更新者';
COMMENT ON COLUMN mes_qc_oqc.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_oqc.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_oqc.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_oqc IS 'MES 出货检验单（OQC）';

-- ----------------------------
-- Records of mes_qc_oqc
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_oqc (id, code, name, template_id, source_doc_type, source_doc_id, source_line_id, source_doc_code, client_id, batch_code, item_id, min_check_quantity, max_unqualified_quantity, out_quantity, check_quantity, qualified_quantity, unqualified_quantity, critical_rate, major_rate, minor_rate, critical_quantity, major_quantity, minor_quantity, check_result, out_date, inspect_date, inspector_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'OQC20260218001', '螺丝刀成品【蓝色一字型】出货检验', 101, NULL, NULL, NULL, NULL, 200, 'OB20260218-A', 75, 5, 1, 2000.00, 5.00, 5.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-02-18 08:00:00', '2026-02-18 11:30:00', 1, 1, '全部合格，准予出货', '1', '2026-02-18 08:00:00', '1', '2026-02-18 11:30:00', '0', 1), (201, 'OQC20260219002', '小包装盒出货检验', 101, NULL, NULL, NULL, NULL, 207, 'OB20260219-B', 94, 3, 0, 500.00, 3.00, 1.00, 2.00, 0.00, 33.33, 33.33, 0, 1, 1, 2, '2026-02-19 09:00:00', '2026-02-19 15:00:00', 1, 1, '不合格，已拦截出货', '1', '2026-02-19 09:00:00', '1', '2026-02-19 15:00:00', '0', 1), (202, 'OQC20260220003', '大包装箱出货检验', 102, NULL, NULL, NULL, NULL, 208, 'OB20260220-C', 95, 3, 0, 300.00, 3.00, 2.00, 1.00, 0.00, 0.00, 33.33, 0, 0, 1, 3, '2026-02-20 10:00:00', '2026-02-20 14:00:00', 104, 1, '轻微外观问题，客户接受', '1', '2026-02-20 10:00:00', '104', '2026-02-20 14:00:00', '0', 1), (203, 'OQC20260222004', 'PVC颗粒出货检验', 102, NULL, NULL, NULL, NULL, 200, 'OB20260222-D', 70, 3, 0, 1000.00, NULL, 0.00, 0.00, 0.00, 0.00, 0.00, 0, 0, 0, NULL, '2026-02-22 09:00:00', NULL, NULL, 0, '待检验', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (204, 'OQC20260327001', 'AAA', 102, NULL, NULL, NULL, NULL, 200, NULL, 69, 1, 0, 2.00, 2.00, 1.00, 1.00, 0.00, 0.00, 0.00, 0, 0, 0, NULL, '1970-01-01 08:00:00', '1970-01-01 08:00:00', 1, 0, '', '1', '2026-03-27 17:07:22', '1', '2026-03-27 17:07:22', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_oqc_seq;
CREATE SEQUENCE mes_qc_oqc_seq
    START 205;

-- ----------------------------
-- Table structure for mes_qc_oqc_line
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_oqc_line;
CREATE TABLE mes_qc_oqc_line (
    id int8 NOT NULL,
  oqc_id int8 NOT NULL,
  indicator_id int8 NOT NULL,
  tool varchar(255) NULL DEFAULT NULL,
  check_method varchar(500) NULL DEFAULT NULL,
  standard_value numeric(14,4) NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  max_threshold numeric(14,4) NULL DEFAULT NULL,
  min_threshold numeric(14,4) NULL DEFAULT NULL,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_oqc_line ADD CONSTRAINT pk_mes_qc_oqc_line PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_oqc_line_01 ON mes_qc_oqc_line (oqc_id);

COMMENT ON COLUMN mes_qc_oqc_line.id IS '编号';
COMMENT ON COLUMN mes_qc_oqc_line.oqc_id IS '出货检验单ID（关联 mes_qc_oqc）';
COMMENT ON COLUMN mes_qc_oqc_line.indicator_id IS '检测指标ID（关联 mes_qc_indicator）';
COMMENT ON COLUMN mes_qc_oqc_line.tool IS '检测工具（冗余自 mes_qc_indicator）';
COMMENT ON COLUMN mes_qc_oqc_line.check_method IS '检测方法';
COMMENT ON COLUMN mes_qc_oqc_line.standard_value IS '标准值';
COMMENT ON COLUMN mes_qc_oqc_line.unit_measure_id IS '计量单位ID（关联 mes_md_unit_measure）';
COMMENT ON COLUMN mes_qc_oqc_line.max_threshold IS '误差上限';
COMMENT ON COLUMN mes_qc_oqc_line.min_threshold IS '误差下限';
COMMENT ON COLUMN mes_qc_oqc_line.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_oqc_line.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_oqc_line.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_oqc_line.remark IS '备注';
COMMENT ON COLUMN mes_qc_oqc_line.creator IS '创建者';
COMMENT ON COLUMN mes_qc_oqc_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_oqc_line.updater IS '更新者';
COMMENT ON COLUMN mes_qc_oqc_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_oqc_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_oqc_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_oqc_line IS 'MES 出货检验单行';

-- ----------------------------
-- Records of mes_qc_oqc_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_oqc_line (id, oqc_id, indicator_id, tool, check_method, standard_value, unit_measure_id, max_threshold, min_threshold, critical_quantity, major_quantity, minor_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 200, 200, NULL, '使用卡尺测量成品总长度', 150.0000, 206, 150.3000, 149.7000, 0, 0, 0, '实测值 150.05mm，合格', '1', '2026-02-18 09:00:00', '1', '2026-02-18 11:30:00', '0', 1), (201, 200, 203, NULL, '拍摄外观照片存档', NULL, NULL, NULL, NULL, 0, 0, 0, '外观完好', '1', '2026-02-18 09:30:00', '1', '2026-02-18 11:30:00', '0', 1), (202, 201, 200, NULL, '使用卡尺测量包装盒长度', 120.0000, 206, 120.5000, 119.5000, 0, 1, 0, '2号样品实测 120.65mm 超上限', '1', '2026-02-19 10:00:00', '1', '2026-02-19 15:00:00', '0', 1), (203, 201, 203, NULL, '拍摄外观照片', NULL, NULL, NULL, NULL, 0, 0, 1, '3号样品印刷模糊', '1', '2026-02-19 10:30:00', '1', '2026-02-19 15:00:00', '0', 1), (204, 202, 200, NULL, '使用卷尺测量纸箱长度', 600.0000, 206, 605.0000, 595.0000, 0, 0, 0, '实测值 601mm，合格', '1', '2026-02-20 11:00:00', '104', '2026-02-20 14:00:00', '0', 1), (205, 202, 205, NULL, '人工清点纸箱数量', NULL, NULL, NULL, NULL, 0, 0, 0, '数量与发货单一致', '1', '2026-02-20 11:30:00', '104', '2026-02-20 14:00:00', '0', 1), (206, 202, 206, NULL, '核对合格证编号', NULL, NULL, NULL, NULL, 0, 0, 1, '1号箱合格证字迹模糊', '1', '2026-02-20 12:00:00', '104', '2026-02-20 14:00:00', '0', 1), (207, 203, 200, NULL, '使用千分尺测量颗粒粒径', 3.0000, 206, 3.5000, 2.5000, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (208, 203, 205, NULL, '人工清点包装数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (209, 203, 206, NULL, '核对合格证编号', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (210, 204, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-27 17:07:22', '1', '2026-03-27 17:07:22', '0', 1), (211, 204, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-27 17:07:22', '1', '2026-03-27 17:07:22', '0', 1), (212, 204, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-27 17:07:22', '1', '2026-03-27 17:07:22', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_oqc_line_seq;
CREATE SEQUENCE mes_qc_oqc_line_seq
    START 213;

-- ----------------------------
-- Table structure for mes_qc_rqc
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_rqc;
CREATE TABLE mes_qc_rqc (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(500) NOT NULL,
  template_id int8 NOT NULL,
  source_doc_type int2 NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_line_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  type int4 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  batch_code varchar(128) NULL DEFAULT NULL,
  check_quantity numeric(14,2) NULL DEFAULT NULL,
  qualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  unqualified_quantity numeric(14,2) NULL DEFAULT 0.00,
  critical_rate numeric(5,2) NULL DEFAULT 0.00,
  major_rate numeric(5,2) NULL DEFAULT 0.00,
  minor_rate numeric(5,2) NULL DEFAULT 0.00,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  check_result int2 NULL DEFAULT NULL,
  inspect_date timestamp NULL DEFAULT NULL,
  inspector_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_rqc ADD CONSTRAINT pk_mes_qc_rqc PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_rqc_01 ON mes_qc_rqc (code);
CREATE INDEX idx_mes_qc_rqc_02 ON mes_qc_rqc (item_id);

COMMENT ON COLUMN mes_qc_rqc.id IS '编号';
COMMENT ON COLUMN mes_qc_rqc.code IS '检验单编号';
COMMENT ON COLUMN mes_qc_rqc.name IS '检验单名称';
COMMENT ON COLUMN mes_qc_rqc.template_id IS '检验模板ID（关联 mes_qc_template）';
COMMENT ON COLUMN mes_qc_rqc.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_qc_rqc.source_doc_id IS '来源单据ID';
COMMENT ON COLUMN mes_qc_rqc.source_line_id IS '来源单据行ID';
COMMENT ON COLUMN mes_qc_rqc.source_doc_code IS '来源单据编码（冗余）';
COMMENT ON COLUMN mes_qc_rqc.type IS '检验类型';
COMMENT ON COLUMN mes_qc_rqc.item_id IS '产品物料ID（关联 mes_md_item）';
COMMENT ON COLUMN mes_qc_rqc.batch_code IS '批次号';
COMMENT ON COLUMN mes_qc_rqc.check_quantity IS '检测数量';
COMMENT ON COLUMN mes_qc_rqc.qualified_quantity IS '合格品数量';
COMMENT ON COLUMN mes_qc_rqc.unqualified_quantity IS '不合格数量';
COMMENT ON COLUMN mes_qc_rqc.critical_rate IS '致命缺陷率（%）';
COMMENT ON COLUMN mes_qc_rqc.major_rate IS '严重缺陷率（%）';
COMMENT ON COLUMN mes_qc_rqc.minor_rate IS '轻微缺陷率（%）';
COMMENT ON COLUMN mes_qc_rqc.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_rqc.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_rqc.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_rqc.check_result IS '检测结果（枚举 MesQcCheckResultEnum）';
COMMENT ON COLUMN mes_qc_rqc.inspect_date IS '检测日期';
COMMENT ON COLUMN mes_qc_rqc.inspector_user_id IS '检测人员用户 ID';
COMMENT ON COLUMN mes_qc_rqc.status IS '状态';
COMMENT ON COLUMN mes_qc_rqc.remark IS '备注';
COMMENT ON COLUMN mes_qc_rqc.creator IS '创建者';
COMMENT ON COLUMN mes_qc_rqc.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_rqc.updater IS '更新者';
COMMENT ON COLUMN mes_qc_rqc.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_rqc.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_rqc.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_rqc IS 'MES 退货检验单（RQC）';

-- ----------------------------
-- Records of mes_qc_rqc
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_rqc (id, code, name, template_id, source_doc_type, source_doc_id, source_line_id, source_doc_code, type, item_id, batch_code, check_quantity, qualified_quantity, unqualified_quantity, critical_rate, major_rate, minor_rate, critical_quantity, major_quantity, minor_quantity, check_result, inspect_date, inspector_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (300, 'RQC20260220001', '螺丝刀刀柄【蓝色】生产退料检验', 102, NULL, NULL, NULL, NULL, 1, 73, 'RB20260220-A', 50.00, 48.00, 2.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '2026-02-20 10:00:00', 1, 4, '退料合格，可重新入库使用', '1', '2026-02-20 08:00:00', '1', '2026-02-23 21:16:03', '0', 1), (301, 'RQC20260221002', '螺丝刀刀头销售退货检验', 102, NULL, NULL, NULL, NULL, 2, 74, 'RB20260221-B', 30.00, 22.00, 8.00, 0.00, 0.00, 0.00, 0, 0, 0, 2, '2026-02-21 15:00:00', 1, 4, '不合格，部分产品存在严重缺陷需报废', '1', '2026-02-21 09:00:00', '1', '2026-02-23 21:16:03', '0', 1), (302, 'RQC20260222003', 'PVC颗粒生产退料检验', 102, NULL, NULL, NULL, NULL, 1, 70, 'RB20260222-C', 20.00, 18.00, 2.00, 0.00, 0.00, 0.00, 0, 0, 0, 3, '2026-02-22 14:00:00', 104, 4, '轻微外观问题，让步接收重新使用', '1', '2026-02-22 10:00:00', '104', '2026-02-23 21:16:03', '0', 1), (303, 'RQC20260222004', '螺丝刀成品【蓝色一字型】销售退货检验', 102, NULL, NULL, NULL, NULL, 2, 75, 'RB20260222-D', 5.00, 2.00, 3.00, 0.00, 0.00, 0.00, 0, 0, 0, NULL, NULL, NULL, 4, '待检验', '1', '2026-02-22 09:00:00', '1', '2026-02-23 21:16:03', '0', 1), (304, 'RQCV5CsmJAiJg', '3', 102, NULL, NULL, NULL, NULL, 1, 94, '555', 1.00, 2.00, 3.00, 0.00, 0.00, 0.00, 0, 0, 0, 2, '1970-01-01 08:00:00', 103, 0, '', '1', '2026-02-22 14:49:23', '1', '2026-02-22 14:49:33', '0', 1), (305, 'RQC20260326000002', 'RITmmKTJb4p0 退货检验单', 102, 116, 7, 9, NULL, 1, 69, NULL, 10.00, 9.00, 1.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '1970-01-01 08:00:00', 1, 0, '', '1', '2026-03-26 13:21:00', '1', '2026-03-26 13:21:00', '0', 1), (306, 'RQC20260326000003', 'RIpV4sw33lVy 退货检验单', 102, 116, 4, 6, 'RIpV4sw33lVy', 2, 70, NULL, 5.00, 1.00, 4.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '1970-01-01 08:00:00', 1, 0, '', '1', '2026-03-26 21:10:07', '1', '2026-03-26 21:10:07', '0', 1), (307, 'RQC20260326000004', 'RIpV4sw33lVy 退货检验单', 102, 116, 4, 6, 'RIpV4sw33lVy', 1, 70, NULL, 5.00, 1.00, 4.00, 0.00, 0.00, 0.00, 0, 0, 0, 1, '1970-01-01 08:00:00', 100, 0, '', '1', '2026-03-26 22:38:03', '1', '2026-03-26 22:38:03', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_rqc_seq;
CREATE SEQUENCE mes_qc_rqc_seq
    START 308;

-- ----------------------------
-- Table structure for mes_qc_rqc_line
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_rqc_line;
CREATE TABLE mes_qc_rqc_line (
    id int8 NOT NULL,
  rqc_id int8 NOT NULL,
  indicator_id int8 NOT NULL,
  tool varchar(255) NULL DEFAULT NULL,
  check_method varchar(500) NULL DEFAULT NULL,
  standard_value numeric(14,4) NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  max_threshold numeric(14,4) NULL DEFAULT NULL,
  min_threshold numeric(14,4) NULL DEFAULT NULL,
  critical_quantity int4 NULL DEFAULT 0,
  major_quantity int4 NULL DEFAULT 0,
  minor_quantity int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_rqc_line ADD CONSTRAINT pk_mes_qc_rqc_line PRIMARY KEY (id);

CREATE INDEX idx_mes_qc_rqc_line_01 ON mes_qc_rqc_line (rqc_id);

COMMENT ON COLUMN mes_qc_rqc_line.id IS '编号';
COMMENT ON COLUMN mes_qc_rqc_line.rqc_id IS '退货检验单ID（关联 mes_qc_rqc）';
COMMENT ON COLUMN mes_qc_rqc_line.indicator_id IS '检测指标ID（关联 mes_qc_indicator）';
COMMENT ON COLUMN mes_qc_rqc_line.tool IS '检测工具';
COMMENT ON COLUMN mes_qc_rqc_line.check_method IS '检测方法';
COMMENT ON COLUMN mes_qc_rqc_line.standard_value IS '标准值';
COMMENT ON COLUMN mes_qc_rqc_line.unit_measure_id IS '计量单位ID（关联 mes_md_unit_measure）';
COMMENT ON COLUMN mes_qc_rqc_line.max_threshold IS '误差上限';
COMMENT ON COLUMN mes_qc_rqc_line.min_threshold IS '误差下限';
COMMENT ON COLUMN mes_qc_rqc_line.critical_quantity IS '致命缺陷数量';
COMMENT ON COLUMN mes_qc_rqc_line.major_quantity IS '严重缺陷数量';
COMMENT ON COLUMN mes_qc_rqc_line.minor_quantity IS '轻微缺陷数量';
COMMENT ON COLUMN mes_qc_rqc_line.remark IS '备注';
COMMENT ON COLUMN mes_qc_rqc_line.creator IS '创建者';
COMMENT ON COLUMN mes_qc_rqc_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_rqc_line.updater IS '更新者';
COMMENT ON COLUMN mes_qc_rqc_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_rqc_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_rqc_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_rqc_line IS 'MES 退货检验行';

-- ----------------------------
-- Records of mes_qc_rqc_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_rqc_line (id, rqc_id, indicator_id, tool, check_method, standard_value, unit_measure_id, max_threshold, min_threshold, critical_quantity, major_quantity, minor_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (300, 300, 200, NULL, '使用千分尺测量退回刀柄长度', 200.0000, 206, 201.0000, 199.0000, 0, 0, 0, '实测值 200.05mm，合格', '1', '2026-02-20 09:00:00', '1', '2026-02-20 10:00:00', '0', 1), (301, 300, 205, NULL, '人工清点退料数量', NULL, NULL, NULL, NULL, 0, 0, 0, '数量与退料单一致', '1', '2026-02-20 09:30:00', '1', '2026-02-20 10:00:00', '0', 1), (302, 300, 206, NULL, '核对退料批次合格证', NULL, NULL, NULL, NULL, 0, 0, 0, '合格证信息完整', '1', '2026-02-20 09:45:00', '1', '2026-02-20 10:00:00', '0', 1), (303, 301, 200, NULL, '使用千分尺测量退回刀头长度', 80.0000, 206, 80.5000, 79.5000, 0, 1, 0, '5号样品实测 80.72mm 超上限', '1', '2026-02-21 10:00:00', '1', '2026-02-21 15:00:00', '0', 1), (304, 301, 205, NULL, '人工清点退货数量', NULL, NULL, NULL, NULL, 0, 0, 1, '实际退回数量与退货单差1件', '1', '2026-02-21 10:30:00', '1', '2026-02-21 15:00:00', '0', 1), (305, 301, 206, NULL, '核对退货批次合格证', NULL, NULL, NULL, NULL, 0, 1, 0, '3号样品合格证缺失', '1', '2026-02-21 11:00:00', '1', '2026-02-21 15:00:00', '0', 1), (306, 302, 200, NULL, '使用千分尺测量PVC颗粒粒径', 3.0000, 206, 3.5000, 2.5000, 0, 0, 0, '实测值 3.1mm，合格', '1', '2026-02-22 11:00:00', '104', '2026-02-22 14:00:00', '0', 1), (307, 302, 205, NULL, '人工清点退料包装数量', NULL, NULL, NULL, NULL, 0, 0, 1, '1袋包装破损，颗粒轻微溢出', '1', '2026-02-22 11:30:00', '104', '2026-02-22 14:00:00', '0', 1), (308, 303, 200, NULL, '使用千分尺测量退回成品总长度', 150.0000, 206, 151.0000, 149.0000, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (309, 303, 205, NULL, '人工清点退货数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 09:00:00', '1', '2026-02-22 09:00:00', '0', 1), (310, 304, 200, NULL, '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 1, 0, 0, '', '1', '2026-02-22 14:49:23', '1', '2026-02-22 14:49:31', '0', 1), (311, 304, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 14:49:23', '1', '2026-02-22 14:49:31', '0', 1), (312, 304, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-02-22 14:49:23', '1', '2026-02-22 14:49:31', '0', 1), (313, 305, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-26 13:21:00', '1', '2026-03-26 13:21:00', '0', 1), (314, 305, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 13:21:00', '1', '2026-03-26 13:21:00', '0', 1), (315, 305, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 13:21:00', '1', '2026-03-26 13:21:00', '0', 1), (316, 306, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-26 21:10:07', '1', '2026-03-26 21:10:07', '0', 1), (317, 306, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 21:10:07', '1', '2026-03-26 21:10:07', '0', 1), (318, 306, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 21:10:07', '1', '2026-03-26 21:10:07', '0', 1), (319, 307, 200, '卡尺', '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, 0, 0, 0, '', '1', '2026-03-26 22:38:03', '1', '2026-03-26 22:38:03', '0', 1), (320, 307, 205, NULL, '人工清点数量', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 22:38:03', '1', '2026-03-26 22:38:03', '0', 1), (321, 307, 206, NULL, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, 0, 0, 0, '', '1', '2026-03-26 22:38:03', '1', '2026-03-26 22:38:03', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_rqc_line_seq;
CREATE SEQUENCE mes_qc_rqc_line_seq
    START 322;

-- ----------------------------
-- Table structure for mes_qc_template
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_template;
CREATE TABLE mes_qc_template (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  types varchar(255) NOT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NOT NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_template ADD CONSTRAINT pk_mes_qc_template PRIMARY KEY (id);

COMMENT ON COLUMN mes_qc_template.id IS '编号';
COMMENT ON COLUMN mes_qc_template.code IS '方案编号';
COMMENT ON COLUMN mes_qc_template.name IS '方案名称';
COMMENT ON COLUMN mes_qc_template.types IS '检测种类（逗号分隔：IQC,IPQC,OQC,RQC）';
COMMENT ON COLUMN mes_qc_template.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN mes_qc_template.remark IS '备注';
COMMENT ON COLUMN mes_qc_template.creator IS '创建者';
COMMENT ON COLUMN mes_qc_template.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_template.updater IS '更新者';
COMMENT ON COLUMN mes_qc_template.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_template.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_template.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_template IS '质检方案';

-- ----------------------------
-- Records of mes_qc_template
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_template (id, code, name, types, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (100, 'QCT0000000001', 'IQC 通用来料检验方案', '1', 0, '适用于常规来料检验', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (101, 'QCT0000000002', 'IQC+OQC 综合检验方案', '1,3', 0, '同时适用于来料检验和出货检验', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (102, 'QCT0000000003', '全流程质检方案', '1,2,3,4', 0, '覆盖 IQC/IPQC/OQC/RQC 全流程', '1', '2026-02-21 13:35:58', '1', '2026-03-23 19:45:52', '0', 1), (105, 'QT-IPQC-001', '螺丝刀过程检验方案', '2', 0, '自动新增的过程检验方案', '1', '2026-03-25 09:34:27', '1', '2026-03-25 09:34:27', '0', 1), (106, 'QCT9VAvjfa2pc', 'EEE', '1,2', 0, NULL, '1', '2026-04-04 22:20:23', '1', '2026-04-04 22:20:23', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_template_seq;
CREATE SEQUENCE mes_qc_template_seq
    START 107;

-- ----------------------------
-- Table structure for mes_qc_template_indicator
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_template_indicator;
CREATE TABLE mes_qc_template_indicator (
    id int8 NOT NULL,
  template_id int8 NOT NULL,
  indicator_id int8 NOT NULL,
  check_method varchar(500) NULL DEFAULT NULL,
  standard_value numeric(12,4) NULL DEFAULT NULL,
  unit_measure_id int8 NULL DEFAULT NULL,
  threshold_max numeric(12,4) NULL DEFAULT NULL,
  threshold_min numeric(12,4) NULL DEFAULT NULL,
  doc_url varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NOT NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_template_indicator ADD CONSTRAINT pk_mes_qc_template_indicator PRIMARY KEY (id);

COMMENT ON COLUMN mes_qc_template_indicator.id IS '编号';
COMMENT ON COLUMN mes_qc_template_indicator.template_id IS '质检方案ID';
COMMENT ON COLUMN mes_qc_template_indicator.indicator_id IS '质检指标ID（关联 mes_qc_indicator，通过 JOIN 查询指标信息）';
COMMENT ON COLUMN mes_qc_template_indicator.check_method IS '检测方法/检测要求';
COMMENT ON COLUMN mes_qc_template_indicator.standard_value IS '标准值';
COMMENT ON COLUMN mes_qc_template_indicator.unit_measure_id IS '计量单位ID（关联 mes_md_unit_measure）';
COMMENT ON COLUMN mes_qc_template_indicator.threshold_max IS '误差上限';
COMMENT ON COLUMN mes_qc_template_indicator.threshold_min IS '误差下限';
COMMENT ON COLUMN mes_qc_template_indicator.doc_url IS '说明图URL';
COMMENT ON COLUMN mes_qc_template_indicator.remark IS '备注';
COMMENT ON COLUMN mes_qc_template_indicator.creator IS '创建者';
COMMENT ON COLUMN mes_qc_template_indicator.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_template_indicator.updater IS '更新者';
COMMENT ON COLUMN mes_qc_template_indicator.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_template_indicator.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_template_indicator.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_template_indicator IS '质检方案-检测指标项';

-- ----------------------------
-- Records of mes_qc_template_indicator
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_template_indicator (id, template_id, indicator_id, check_method, standard_value, unit_measure_id, threshold_max, threshold_min, doc_url, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 100, 200, '使用卡尺测量长度，记录实测值', 100.0000, 206, 0.5000, -0.5000, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (201, 100, 201, '使用卡尺测量宽度，记录实测值', 50.0000, 206, 0.3000, -0.3000, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (202, 100, 202, '目视检查颜色是否符合标准色板', NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (203, 101, 200, '使用卡尺测量长度', 150.0000, 206, 0.3000, -0.3000, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (204, 101, 203, '拍摄外观照片存档', NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (205, 102, 200, '使用千分尺测量长度', 200.0000, 206, 1.0000, -1.0000, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (206, 102, 205, '人工清点数量', NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (207, 102, 206, '核对合格证编号与送检批次一致性', NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (208, 105, 200, '游标卡尺测量', 15.0000, NULL, 0.5000, 0.5000, NULL, NULL, '1', '2026-03-25 09:34:27', '1', '2026-03-25 09:34:27', '0', 1), (209, 106, 205, '4', 1.0000, 200, 2.0000, 3.0000, NULL, NULL, '1', '2026-05-28 23:56:55', '1', '2026-05-28 23:57:01', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_template_indicator_seq;
CREATE SEQUENCE mes_qc_template_indicator_seq
    START 210;

-- ----------------------------
-- Table structure for mes_qc_template_item
-- ----------------------------
DROP TABLE IF EXISTS mes_qc_template_item;
CREATE TABLE mes_qc_template_item (
    id int8 NOT NULL,
  template_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity_check int4 NOT NULL DEFAULT 1,
  quantity_unqualified int4 NOT NULL DEFAULT 0,
  critical_rate numeric(12,2) NOT NULL DEFAULT 0.00,
  major_rate numeric(12,2) NOT NULL DEFAULT 0.00,
  minor_rate numeric(12,2) NOT NULL DEFAULT 100.00,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NOT NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_qc_template_item ADD CONSTRAINT pk_mes_qc_template_item PRIMARY KEY (id);

COMMENT ON COLUMN mes_qc_template_item.id IS '编号';
COMMENT ON COLUMN mes_qc_template_item.template_id IS '质检方案ID';
COMMENT ON COLUMN mes_qc_template_item.item_id IS '产品物料ID（关联 mes_md_item，通过 ID 查询物料信息）';
COMMENT ON COLUMN mes_qc_template_item.quantity_check IS '最低检测数';
COMMENT ON COLUMN mes_qc_template_item.quantity_unqualified IS '最大不合格数';
COMMENT ON COLUMN mes_qc_template_item.critical_rate IS '最大致命缺陷率（%）';
COMMENT ON COLUMN mes_qc_template_item.major_rate IS '最大严重缺陷率（%）';
COMMENT ON COLUMN mes_qc_template_item.minor_rate IS '最大轻微缺陷率（%）';
COMMENT ON COLUMN mes_qc_template_item.remark IS '备注';
COMMENT ON COLUMN mes_qc_template_item.creator IS '创建者';
COMMENT ON COLUMN mes_qc_template_item.create_time IS '创建时间';
COMMENT ON COLUMN mes_qc_template_item.updater IS '更新者';
COMMENT ON COLUMN mes_qc_template_item.update_time IS '更新时间';
COMMENT ON COLUMN mes_qc_template_item.deleted IS '是否删除';
COMMENT ON COLUMN mes_qc_template_item.tenant_id IS '租户编号';
COMMENT ON TABLE mes_qc_template_item IS '质检方案-产品关联';

-- ----------------------------
-- Records of mes_qc_template_item
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_qc_template_item (id, template_id, item_id, quantity_check, quantity_unqualified, critical_rate, major_rate, minor_rate, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (300, 100, 73, 5, 1, 0.00, 5.00, 20.00, '螺丝刀刀柄蓝色', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (301, 100, 74, 10, 2, 0.00, 5.00, 15.00, '螺丝刀刀头', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (302, 101, 75, 5, 1, 0.00, 3.00, 10.00, '螺丝刀成品蓝色一字型', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (303, 101, 94, 3, 0, 0.00, 0.00, 10.00, '小包装盒', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (304, 102, 70, 3, 0, 0.00, 0.00, 100.00, 'PVC颗粒', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (305, 102, 72, 5, 1, 0.00, 5.00, 20.00, '钢筋', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (306, 102, 95, 3, 0, 0.00, 0.00, 100.00, '大包装箱', '1', '2026-02-21 13:35:58', '1', '2026-02-21 13:35:58', '0', 1), (307, 102, 69, 1, 0, 0.00, 0.00, 100.00, NULL, '1', '2026-03-23 19:45:51', '1', '2026-03-23 19:45:51', '0', 1), (308, 105, 75, 5, 1, 0.00, 3.00, 10.00, '自动绑定螺丝刀', '1', '2026-03-25 09:34:27', '1', '2026-03-25 09:34:27', '0', 1), (309, 106, 95, 1, 0, 0.00, 0.00, 100.00, '12332131', '1', '2026-05-28 23:57:13', '1', '2026-05-28 23:57:13', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_qc_template_item_seq;
CREATE SEQUENCE mes_qc_template_item_seq
    START 310;

-- ----------------------------
-- Table structure for mes_tm_tool
-- ----------------------------
DROP TABLE IF EXISTS mes_tm_tool;
CREATE TABLE mes_tm_tool (
    id int8 NOT NULL,
  code varchar(64) NULL DEFAULT NULL,
  name varchar(255) NOT NULL,
  brand varchar(255) NULL DEFAULT NULL,
  specification varchar(255) NULL DEFAULT NULL,
  tool_type_id int8 NOT NULL,
  quantity int4 NOT NULL DEFAULT 1,
  available_quantity int4 NULL DEFAULT NULL,
  mainten_type int2 NULL DEFAULT NULL,
  next_mainten_period int4 NULL DEFAULT NULL,
  next_mainten_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_tm_tool ADD CONSTRAINT pk_mes_tm_tool PRIMARY KEY (id);

COMMENT ON COLUMN mes_tm_tool.id IS '编号';
COMMENT ON COLUMN mes_tm_tool.code IS '工具编码';
COMMENT ON COLUMN mes_tm_tool.name IS '工具名称';
COMMENT ON COLUMN mes_tm_tool.brand IS '品牌';
COMMENT ON COLUMN mes_tm_tool.specification IS '型号规格';
COMMENT ON COLUMN mes_tm_tool.tool_type_id IS '工具类型编号';
COMMENT ON COLUMN mes_tm_tool.quantity IS '数量';
COMMENT ON COLUMN mes_tm_tool.available_quantity IS '可用数量';
COMMENT ON COLUMN mes_tm_tool.mainten_type IS '保养维护类型';
COMMENT ON COLUMN mes_tm_tool.next_mainten_period IS '下次保养周期（次数）';
COMMENT ON COLUMN mes_tm_tool.next_mainten_date IS '下次保养日期';
COMMENT ON COLUMN mes_tm_tool.status IS '状态';
COMMENT ON COLUMN mes_tm_tool.remark IS '备注';
COMMENT ON COLUMN mes_tm_tool.creator IS '创建者';
COMMENT ON COLUMN mes_tm_tool.create_time IS '创建时间';
COMMENT ON COLUMN mes_tm_tool.updater IS '更新者';
COMMENT ON COLUMN mes_tm_tool.update_time IS '更新时间';
COMMENT ON COLUMN mes_tm_tool.deleted IS '是否删除';
COMMENT ON COLUMN mes_tm_tool.tenant_id IS '租户编号';
COMMENT ON TABLE mes_tm_tool IS 'MES 工具台账';

-- ----------------------------
-- Records of mes_tm_tool
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_tm_tool (id, code, name, brand, specification, tool_type_id, quantity, available_quantity, mainten_type, next_mainten_period, next_mainten_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, NULL, 'XX刀具', 'XX', 'XXX', 200, 60, 60, 1, NULL, NULL, 1, '', '1', '2023-11-11 11:10:38', '1', '2023-11-11 11:10:38', '0', 1), (201, 'T00060', 'XXX模具', 'XXX牌', 'XXX型号', 201, 1, 1, 1, NULL, NULL, 1, '', '1', '2023-11-11 11:12:04', '1', '2023-11-11 11:12:04', '0', 1), (202, 'T00061', '精密夹具A', '米思米', 'JG-100', 202, 1, 1, 2, 50000, NULL, 1, '产线A使用', '1', '2023-12-01 09:00:00', '1', '2023-12-01 09:00:00', '0', 1), (203, 'T00062', '精密夹具B', '米思米', 'JG-200', 202, 1, 0, 2, 30000, NULL, 2, '已领用给张三', '1', '2023-12-01 09:05:00', '1', '2024-01-15 10:00:00', '0', 1), (204, 'T00063', '测试千分尺', '三丰', 'MDC-25', 204, 1, 0, 1, NULL, '2024-06-01 00:00:00', 3, '送修中', '1', '2024-01-10 08:30:00', '1', '2024-03-20 14:00:00', '0', 1), (205, 'T00064', '旧量具', '国产', 'LJ-50', 205, 1, 0, 1, NULL, NULL, 4, '已报废', '1', '2022-10-01 10:00:00', '1', '2024-02-01 16:00:00', '0', 1), (206, NULL, '通用刀片', '山特维克', 'CNMG120408', 200, 100, 85, NULL, NULL, NULL, 1, '批量耗材，无需单独编码', '1', '2024-03-01 08:00:00', '1', '2024-03-01 08:00:00', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_tm_tool_seq;
CREATE SEQUENCE mes_tm_tool_seq
    START 207;

-- ----------------------------
-- Table structure for mes_tm_tool_type
-- ----------------------------
DROP TABLE IF EXISTS mes_tm_tool_type;
CREATE TABLE mes_tm_tool_type (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  code_flag bool NOT NULL DEFAULT '1',
  mainten_type int2 NULL DEFAULT NULL,
  mainten_period int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_tm_tool_type ADD CONSTRAINT pk_mes_tm_tool_type PRIMARY KEY (id);

COMMENT ON COLUMN mes_tm_tool_type.id IS '编号';
COMMENT ON COLUMN mes_tm_tool_type.code IS '类型编码';
COMMENT ON COLUMN mes_tm_tool_type.name IS '类型名称';
COMMENT ON COLUMN mes_tm_tool_type.code_flag IS '是否编码管理';
COMMENT ON COLUMN mes_tm_tool_type.mainten_type IS '保养维护类型';
COMMENT ON COLUMN mes_tm_tool_type.mainten_period IS '保养周期';
COMMENT ON COLUMN mes_tm_tool_type.remark IS '备注';
COMMENT ON COLUMN mes_tm_tool_type.creator IS '创建者';
COMMENT ON COLUMN mes_tm_tool_type.create_time IS '创建时间';
COMMENT ON COLUMN mes_tm_tool_type.updater IS '更新者';
COMMENT ON COLUMN mes_tm_tool_type.update_time IS '更新时间';
COMMENT ON COLUMN mes_tm_tool_type.deleted IS '是否删除';
COMMENT ON COLUMN mes_tm_tool_type.tenant_id IS '租户编号';
COMMENT ON TABLE mes_tm_tool_type IS 'MES 工具类型';

-- ----------------------------
-- Records of mes_tm_tool_type
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_tm_tool_type (id, code, name, code_flag, mainten_type, mainten_period, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (200, 'TT002', '刀具', '0', NULL, NULL, '', '1', '2022-05-11 00:24:04', '1', '2022-05-11 00:24:04', '0', 1), (201, 'TT022', '模具', '1', 2, 500000, '', '1', '2022-05-11 00:28:22', '1', '2022-08-16 18:56:56', '0', 1), (202, 'TT024', '夹具', '1', 2, 30, '', '1', '2022-05-11 00:28:46', '1', '2022-08-16 19:58:17', '0', 1), (204, 'TT039', '测试工具', '1', 1, 13, '', '1', '2022-08-19 15:04:41', '1', '2022-08-19 15:04:41', '0', 1), (205, 'TT049', '量具', '1', 1, 33, '', '1', '2022-08-22 09:52:47', '1', '2022-08-22 09:52:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_tm_tool_type_seq;
CREATE SEQUENCE mes_tm_tool_type_seq
    START 206;

-- ----------------------------
-- Table structure for mes_wm_arrival_notice
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_arrival_notice;
CREATE TABLE mes_wm_arrival_notice (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  purchase_order_code varchar(64) NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  arrival_date timestamp NULL DEFAULT NULL,
  contact_name varchar(64) NULL DEFAULT NULL,
  contact_telephone varchar(128) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_arrival_notice ADD CONSTRAINT pk_mes_wm_arrival_notice PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_arrival_notice_01 ON mes_wm_arrival_notice (tenant_id, code);

COMMENT ON COLUMN mes_wm_arrival_notice.id IS '编号';
COMMENT ON COLUMN mes_wm_arrival_notice.code IS '通知单编码';
COMMENT ON COLUMN mes_wm_arrival_notice.name IS '通知单名称';
COMMENT ON COLUMN mes_wm_arrival_notice.purchase_order_code IS '采购订单编号';
COMMENT ON COLUMN mes_wm_arrival_notice.vendor_id IS '供应商编号（关联 mes_md_vendor.id）';
COMMENT ON COLUMN mes_wm_arrival_notice.arrival_date IS '到货日期';
COMMENT ON COLUMN mes_wm_arrival_notice.contact_name IS '联系人';
COMMENT ON COLUMN mes_wm_arrival_notice.contact_telephone IS '联系电话';
COMMENT ON COLUMN mes_wm_arrival_notice.status IS '状态';
COMMENT ON COLUMN mes_wm_arrival_notice.remark IS '备注';
COMMENT ON COLUMN mes_wm_arrival_notice.creator IS '创建者';
COMMENT ON COLUMN mes_wm_arrival_notice.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_arrival_notice.updater IS '更新者';
COMMENT ON COLUMN mes_wm_arrival_notice.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_arrival_notice.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_arrival_notice.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_arrival_notice IS 'MES 到货通知单';

-- ----------------------------
-- Records of mes_wm_arrival_notice
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_arrival_notice (id, code, name, purchase_order_code, vendor_id, arrival_date, contact_name, contact_telephone, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'AN2026020001', '2 月份钢板到货', 'PO20260101', 1, '2026-02-15 00:00:00', '张三', '13800138000', 2, '含需检物料，草稿状态', '1', '2026-02-22 14:53:50', '1', '2026-02-27 23:05:55', '0', 1), (2, 'AN2026020002', '2 月份螺丝到货', 'PO20260102', 2, '2026-02-18 00:00:00', '李四', '13900139000', 2, '待质检，IQC 已完成', '1', '2026-02-22 14:53:50', '1', '2026-02-26 08:23:36', '0', 1), (3, 'AN2026020003', '2 月份铝材到货', 'PO20260103', 1, '2026-02-20 00:00:00', '王五', '13700137000', 2, '待入库，等待入库执行完成', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (4, 'AN2026020004', '1 月份铜线到货', 'PO20260050', 2, '2026-01-25 00:00:00', '赵六', '13600136000', 3, '已完成入库', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (5, 'AN2026020005', '2 月份标准件到货', 'PO20260104', 1, '2026-02-22 00:00:00', '张三', '13800138000', 3, '全部免检物料，草稿状态', '1', '2026-02-22 14:53:50', '1', '2026-02-26 13:18:53', '0', 1), (6, 'ANFCnFEfb9NT', 'xxx', NULL, 200, '2026-02-18 00:00:00', NULL, NULL, 0, '', '1', '2026-02-23 00:49:25', '1', '2026-02-23 00:49:25', '0', 1), (100, 'AN2026020100', '待检-钢材批次到货', 'PO20260201', 1, '2026-02-20 08:00:00', '张三', '13800138000', 2, '3 行全部需检，用于待检任务测试', '1', '2026-02-23 07:30:48', '1', '2026-02-26 08:23:38', '0', 1), (101, 'AN2026020101', '待检-电子元件到货', 'PO20260202', 2, '2026-02-21 10:00:00', '李四', '13900139000', 2, '1 行需检 + 1 行免检', '1', '2026-02-23 07:30:48', '1', '2026-02-26 08:23:39', '0', 1), (102, 'AN2026020102', '待检-紧固件到货', 'PO20260203', 1, '2026-02-22 14:00:00', '王五', '13700137000', 2, '1 行已关联 IQC + 1 行未关联', '1', '2026-02-23 07:30:48', '1', '2026-02-26 08:23:40', '0', 1), (103, 'ANSXI1qnG1Ue', '测试到货通知-无检验', NULL, 200, '2026-02-25 00:00:00', '张三', '13800138000', 3, '', '1', '2026-02-25 20:00:46', '1', '2026-02-26 00:24:44', '0', 1), (104, 'AN3asCTmLzYb', '111', NULL, 201, '2026-01-28 00:00:00', NULL, NULL, 2, '', '1', '2026-02-26 01:10:29', '1', '2026-02-26 01:10:43', '0', 1), (105, 'ANpcHrG0Q7S2', 'IQC Test Notice', NULL, 200, '2026-03-23 00:00:00', NULL, NULL, 3, '', '1', '2026-03-23 19:28:57', '1', '2026-03-27 22:32:18', '0', 1), (106, 'ANbhv1kzQHtc', 'AABBC', NULL, 200, '2026-03-04 00:00:00', NULL, NULL, 3, '', '1', '2026-03-23 19:44:08', '1', '2026-03-23 19:51:39', '0', 1), (107, 'ANETKfDzjmKB', 'IQC Test Notice', NULL, 200, '2026-03-23 00:00:00', NULL, NULL, 3, '', '1', '2026-03-23 19:47:25', '1', '2026-03-29 19:35:01', '0', 1), (108, 'ANiiCf2vISKg', 'IQC', NULL, 200, '2026-03-23 00:00:00', NULL, NULL, 2, '', '1', '2026-03-23 19:52:48', '1', '2026-03-23 19:56:35', '0', 1), (109, 'AN20260329000001', 'ABC', NULL, 200, '2026-03-04 00:00:00', NULL, NULL, 3, '', '1', '2026-03-29 19:35:17', '1', '2026-03-29 19:35:24', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_arrival_notice_seq;
CREATE SEQUENCE mes_wm_arrival_notice_seq
    START 110;

-- ----------------------------
-- Table structure for mes_wm_arrival_notice_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_arrival_notice_line;
CREATE TABLE mes_wm_arrival_notice_line (
    id int8 NOT NULL,
  notice_id int8 NOT NULL,
  item_id int8 NOT NULL,
  arrival_quantity numeric(14,2) NULL DEFAULT NULL,
  qualified_quantity numeric(14,2) NULL DEFAULT NULL,
  iqc_check_flag bool NOT NULL DEFAULT '0',
  iqc_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_arrival_notice_line ADD CONSTRAINT pk_mes_wm_arrival_notice_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_arrival_notice_line_01 ON mes_wm_arrival_notice_line (notice_id);
CREATE INDEX idx_mes_wm_arrival_notice_line_02 ON mes_wm_arrival_notice_line (item_id);

COMMENT ON COLUMN mes_wm_arrival_notice_line.id IS '编号';
COMMENT ON COLUMN mes_wm_arrival_notice_line.notice_id IS '到货通知单编号（关联 mes_wm_arrival_notice.id）';
COMMENT ON COLUMN mes_wm_arrival_notice_line.item_id IS '物料编号（关联 mes_md_item.id）';
COMMENT ON COLUMN mes_wm_arrival_notice_line.arrival_quantity IS '到货数量';
COMMENT ON COLUMN mes_wm_arrival_notice_line.qualified_quantity IS '合格数量';
COMMENT ON COLUMN mes_wm_arrival_notice_line.iqc_check_flag IS '是否需要来料检验';
COMMENT ON COLUMN mes_wm_arrival_notice_line.iqc_id IS '来料检验单编号（关联 mes_qc_iqc.id）';
COMMENT ON COLUMN mes_wm_arrival_notice_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_arrival_notice_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_arrival_notice_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_arrival_notice_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_arrival_notice_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_arrival_notice_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_arrival_notice_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_arrival_notice_line IS 'MES 到货通知单行';

-- ----------------------------
-- Records of mes_wm_arrival_notice_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_arrival_notice_line (id, notice_id, item_id, arrival_quantity, qualified_quantity, iqc_check_flag, iqc_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 500.00, NULL, '1', NULL, '钢板-需来料检验', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (2, 1, 2, 200.00, 200.00, '0', NULL, '螺栓-免检物料', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (3, 2, 3, 1000.00, 980.00, '1', 1, '六角螺丝-检验合格', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (4, 2, 4, 500.00, 500.00, '0', NULL, '垫片-免检', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (5, 3, 5, 800.00, 800.00, '1', 2, '铝板-检验合格', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (6, 3, 6, 300.00, 300.00, '0', NULL, '铝棒-免检', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (7, 4, 7, 600.00, 580.00, '1', 3, '铜线-已入库完成', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (8, 5, 2, 300.00, 300.00, '0', NULL, '螺栓-免检', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (9, 5, 4, 400.00, 400.00, '0', NULL, '垫片-免检', '1', '2026-02-22 14:53:50', '1', '2026-02-22 14:53:50', '0', 1), (100, 100, 1, 500.00, NULL, '1', NULL, '钢板-待检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (101, 100, 3, 300.00, NULL, '1', NULL, '六角螺丝-待检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (102, 100, 5, 200.00, NULL, '1', NULL, '铝板-待检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (103, 101, 7, 600.00, NULL, '1', NULL, '铜线-待检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (104, 101, 2, 400.00, 400.00, '0', NULL, '螺栓-免检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (105, 102, 3, 800.00, 780.00, '1', 1, '六角螺丝-已关联IQC', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (106, 102, 4, 250.00, NULL, '1', NULL, '垫片-待检', '1', '2026-02-23 07:30:48', '1', '2026-02-23 07:30:48', '0', 1), (107, 103, 69, 100.00, 100.00, '0', NULL, '', '1', '2026-02-25 20:01:01', '1', '2026-02-25 20:01:01', '0', 1), (108, 104, 69, 222.00, 222.00, '0', NULL, '', '1', '2026-02-26 01:10:34', '1', '2026-02-26 01:10:34', '0', 1), (109, 105, 69, 10.00, 10.00, '1', 209, '', '1', '2026-03-23 19:30:23', '1', '2026-03-27 22:32:18', '0', 1), (110, 106, 69, 1.00, 1.00, '1', 207, '', '1', '2026-03-23 19:44:14', '1', '2026-03-23 19:51:39', '0', 1), (111, 108, 69, 100.00, NULL, '1', NULL, '', '1', '2026-03-23 19:55:00', '1', '2026-03-23 19:55:00', '0', 1), (112, 107, 69, 10.00, 10.00, '0', NULL, '', '1', '2026-03-29 19:34:59', '1', '2026-03-29 19:34:59', '0', 1), (113, 109, 69, 111.00, 111.00, '0', NULL, '', '1', '2026-03-29 19:35:22', '1', '2026-03-29 19:35:22', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_arrival_notice_line_seq;
CREATE SEQUENCE mes_wm_arrival_notice_line_seq
    START 114;

-- ----------------------------
-- Table structure for mes_wm_barcode
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_barcode;
CREATE TABLE mes_wm_barcode (
    id int8 NOT NULL,
  config_id int8 NULL DEFAULT NULL,
  format int2 NOT NULL,
  biz_type int2 NOT NULL,
  content varchar(255) NOT NULL,
  biz_id int8 NOT NULL,
  biz_code varchar(64) NULL DEFAULT NULL,
  biz_name varchar(255) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_barcode ADD CONSTRAINT pk_mes_wm_barcode PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_barcode_01 ON mes_wm_barcode (config_id);
CREATE INDEX idx_mes_wm_barcode_02 ON mes_wm_barcode (biz_type, biz_id);
CREATE INDEX idx_mes_wm_barcode_03 ON mes_wm_barcode (content);

COMMENT ON COLUMN mes_wm_barcode.id IS '编号';
COMMENT ON COLUMN mes_wm_barcode.config_id IS '条码配置编号';
COMMENT ON COLUMN mes_wm_barcode.format IS '条码格式';
COMMENT ON COLUMN mes_wm_barcode.biz_type IS '';
COMMENT ON COLUMN mes_wm_barcode.content IS '条码内容';
COMMENT ON COLUMN mes_wm_barcode.biz_id IS '业务编号';
COMMENT ON COLUMN mes_wm_barcode.biz_code IS '业务编码';
COMMENT ON COLUMN mes_wm_barcode.biz_name IS '业务名称';
COMMENT ON COLUMN mes_wm_barcode.status IS '状态';
COMMENT ON COLUMN mes_wm_barcode.remark IS '备注';
COMMENT ON COLUMN mes_wm_barcode.creator IS '创建者';
COMMENT ON COLUMN mes_wm_barcode.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_barcode.updater IS '更新者';
COMMENT ON COLUMN mes_wm_barcode.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_barcode.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_barcode.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_barcode IS 'MES 条码清单表';

-- ----------------------------
-- Records of mes_wm_barcode
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_barcode (id, config_id, format, biz_type, content, biz_id, biz_code, biz_name, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 101, 'WH-WH001', 1001, 'WH001', '一号仓库', 0, '主仓库', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (2, 1, 1, 101, 'WH-WH002', 1002, 'WH002', '二号仓库', 0, '备用仓库', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (3, 1, 2, 101, 'WH-WH003', 1003, 'WH003', '三号仓库', 1, '停用仓库', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (4, 2, 1, 102, 'AREA-A01', 2001, 'A01', 'A区-01库位', 0, '一号库位', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (5, 2, 1, 102, 'AREA-A02', 2002, 'A02', 'A区-02库位', 0, '二号库位', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (6, 2, 1, 102, 'AREA-B01', 2003, 'B01', 'B区-01库位', 0, '三号库位', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (7, 2, 3, 102, 'AREA-B02', 2004, 'B02', 'B区-02库位', 0, '四号库位', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (8, 3, 1, 103, 'PKG-PKG001', 3001, 'PKG001', '装箱单-001', 0, '第一批装箱', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (9, 3, 1, 103, 'PKG-PKG002', 3002, 'PKG002', '装箱单-002', 0, '第二批装箱', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (10, 3, 2, 103, 'PKG-PKG003', 3003, 'PKG003', '装箱单-003', 0, '第三批装箱', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (11, 4, 1, 104, 'STK-STK001', 4001, 'STK001', '库存-001', 0, '库存记录1', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (12, 4, 1, 104, 'STK-STK002', 4002, 'STK002', '库存-002', 0, '库存记录2', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (13, 4, 3, 104, 'STK-STK003', 4003, 'STK003', '库存-003', 0, '库存记录3', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (14, 5, 1, 105, 'BATCH-B001', 5001, 'B001', '批次-001', 0, '2024年1月批次', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (15, 5, 1, 105, 'BATCH-B002', 5002, 'B002', '批次-002', 0, '2024年2月批次', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (16, 5, 2, 105, 'BATCH-B003', 5003, 'B003', '批次-003', 1, '已过期批次', '1', '2026-03-05 14:49:52', '1', '2026-03-05 14:50:14', '0', 1), (17, 2, 1, 102, 'AREA-WH-RAW', 701, 'WH-RAW', '原料仓', 0, '', '1', '2026-03-07 17:16:01', '1', '2026-03-07 17:23:08', '0', 1), (18, 2, 1, 102, 'AREA-WH-FIN', 702, 'WH-FIN', '成品仓', 0, '', '1', '2026-03-07 18:58:45', '1', '2026-03-07 18:58:45', '0', 1), (19, 5, 1, 105, 'BATCH-PKG202603080007', 4, 'PKG202603080007', NULL, 0, '', '1', '2026-03-08 13:04:38', '1', '2026-03-08 13:04:38', '0', 1), (20, 5, 1, 105, 'BATCH-PKG202603310001', 5, 'PKG202603310001', NULL, 0, '', '1', '2026-03-31 20:06:23', '1', '2026-03-31 20:06:23', '0', 1), (21, 43, 1, 400, 'MAC-M00001', 9, 'M00001', 'ABCED', 0, '', '1', '2026-04-02 23:20:15', '1', '2026-04-02 23:20:15', '0', 1), (22, 43, 1, 400, 'MAC-M00002', 10, 'M00002', 'AAA', 0, '', '1', '2026-04-02 23:37:18', '1', '2026-04-02 23:37:18', '0', 1), (23, 40, 1, 300, 'CARD-CARD20260404000001', 4, 'CARD20260404000001', 'CARD20260404000001', 0, '', '1', '2026-04-04 20:30:36', '1', '2026-04-04 20:30:36', '0', 1), (24, 37, 1, 105, 'PKG-PKG202604060001', 6, 'PKG202604060001', NULL, 0, '', '1', '2026-04-06 00:52:34', '1', '2026-04-06 00:52:34', '0', 1), (25, 37, 1, 105, 'PKG-PKG202604060002', 7, 'PKG202604060002', NULL, 0, '', '1', '2026-04-06 01:07:52', '1', '2026-04-06 01:07:52', '0', 1), (26, 39, 1, 107, 'BATCH-BATCH_ITEM_72', 8, 'BATCH_ITEM_72', '钢筋', 0, '', '1', '2026-04-06 16:57:36', '1', '2026-04-06 16:57:36', '0', 1), (27, 39, 1, 107, 'BATCH-PC202600003', 10, 'PC202600003', 'ABC', 0, '', '1', '2026-04-17 09:38:09', '1', '2026-04-17 09:38:09', '0', 1), (28, 45, 1, 600, 'ITEM-IF2022082437', 69, 'IF2022082437', '色粉【黑色】', 0, '', '1', '2026-05-21 10:32:37', '1', '2026-05-21 10:32:37', '0', 1), (29, 34, 1, 102, 'WH-WIP_VIRTUAL_WAREHOUSE', 703, 'WIP_VIRTUAL_WAREHOUSE', '虚拟线边仓库', 0, '', '1', '2026-05-29 08:38:03', '1', '2026-05-29 08:38:03', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_barcode_seq;
CREATE SEQUENCE mes_wm_barcode_seq
    START 30;

-- ----------------------------
-- Table structure for mes_wm_barcode_config
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_barcode_config;
CREATE TABLE mes_wm_barcode_config (
    id int8 NOT NULL,
  format int2 NOT NULL,
  biz_type int2 NOT NULL,
  content_format varchar(255) NOT NULL,
  content_example varchar(255) NULL DEFAULT NULL,
  auto_generate_flag bool NOT NULL DEFAULT '1',
  default_template varchar(255) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_barcode_config ADD CONSTRAINT pk_mes_wm_barcode_config PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_barcode_config_01 ON mes_wm_barcode_config (biz_type, deleted, tenant_id);

CREATE INDEX idx_mes_wm_barcode_config_01 ON mes_wm_barcode_config (tenant_id);

COMMENT ON COLUMN mes_wm_barcode_config.id IS '编号';
COMMENT ON COLUMN mes_wm_barcode_config.format IS '条码格式';
COMMENT ON COLUMN mes_wm_barcode_config.biz_type IS '';
COMMENT ON COLUMN mes_wm_barcode_config.content_format IS '内容格式模板（支持{BUSINESSCODE}占位符）';
COMMENT ON COLUMN mes_wm_barcode_config.content_example IS '内容样例';
COMMENT ON COLUMN mes_wm_barcode_config.auto_generate_flag IS '是否自动生成';
COMMENT ON COLUMN mes_wm_barcode_config.default_template IS '默认打印模板';
COMMENT ON COLUMN mes_wm_barcode_config.status IS '状态';
COMMENT ON COLUMN mes_wm_barcode_config.remark IS '备注';
COMMENT ON COLUMN mes_wm_barcode_config.creator IS '创建者';
COMMENT ON COLUMN mes_wm_barcode_config.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_barcode_config.updater IS '更新者';
COMMENT ON COLUMN mes_wm_barcode_config.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_barcode_config.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_barcode_config.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_barcode_config IS 'MES 条码配置表';

-- ----------------------------
-- Records of mes_wm_barcode_config
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_barcode_config (id, format, biz_type, content_format, content_example, auto_generate_flag, default_template, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (34, 1, 102, 'WH-{BUSINESSCODE}', 'WH-WH001', '1', NULL, 0, '仓库条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (35, 1, 103, 'LOC-{BUSINESSCODE}', 'LOC-L001', '1', NULL, 0, '库区条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (36, 1, 104, 'AREA-{BUSINESSCODE}', 'AREA-A01', '1', NULL, 0, '库位条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (37, 1, 105, 'PKG-{BUSINESSCODE}', 'PKG-P001', '1', NULL, 0, '装箱单条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (38, 1, 106, 'STK-{BUSINESSCODE}', 'STK-S001', '1', NULL, 0, '库存条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (39, 1, 107, 'BATCH-{BUSINESSCODE}', 'BATCH-B001', '1', NULL, 0, '批次条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (40, 1, 300, 'CARD-{BUSINESSCODE}', 'CARD-C001', '1', NULL, 0, '流转卡条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (41, 1, 301, 'WO-{BUSINESSCODE}', 'WO-W001', '1', NULL, 0, '工单条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (42, 1, 302, 'TO-{BUSINESSCODE}', 'TO-T001', '1', NULL, 0, '流转单条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (43, 1, 400, 'MAC-{BUSINESSCODE}', 'MAC-M001', '1', NULL, 0, '设备条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (44, 1, 500, 'TOOL-{BUSINESSCODE}', 'TOOL-T001', '1', NULL, 0, '工装条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (45, 1, 600, 'ITEM-{BUSINESSCODE}', 'ITEM-I001', '1', NULL, 0, '物料条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (46, 1, 601, 'VEN-{BUSINESSCODE}', 'VEN-V001', '1', NULL, 0, '供应商条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (47, 1, 602, 'WS-{BUSINESSCODE}', 'WS-W001', '1', NULL, 0, '工作站条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (48, 1, 603, 'WSH-{BUSINESSCODE}', 'WSH-W001', '1', NULL, 0, '车间条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (49, 1, 604, 'USER-{BUSINESSCODE}', 'USER-U001', '1', NULL, 0, '人员条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (50, 1, 605, 'CLI-{BUSINESSCODE}', 'CLI-C001', '1', NULL, 0, '客户条码配置', '1', '2026-03-31 13:29:48', '1', '2026-03-31 13:29:48', '0', 1), (51, 1, 109, 'SN-{BUSINESSCODE}', 'SN-SN20260305000001', '1', NULL, 0, 'SN码条码配置', '1', '2026-06-13 10:25:39', '1', '2026-06-13 10:25:39', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_barcode_config_seq;
CREATE SEQUENCE mes_wm_barcode_config_seq
    START 52;

-- ----------------------------
-- Table structure for mes_wm_batch
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_batch;
CREATE TABLE mes_wm_batch (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  item_id int8 NOT NULL,
  produce_date timestamp NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  receipt_date timestamp NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  client_id int8 NULL DEFAULT NULL,
  sales_order_code varchar(64) NULL DEFAULT NULL,
  purchase_order_code varchar(64) NULL DEFAULT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  task_id int8 NULL DEFAULT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  tool_id int8 NULL DEFAULT NULL,
  mold_id int8 NULL DEFAULT NULL,
  lot_number varchar(128) NULL DEFAULT NULL,
  quality_status int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 1
);

ALTER TABLE mes_wm_batch ADD CONSTRAINT pk_mes_wm_batch PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_batch_01 ON mes_wm_batch (code, tenant_id);

CREATE INDEX idx_mes_wm_batch_01 ON mes_wm_batch (item_id);

COMMENT ON COLUMN mes_wm_batch.id IS '批次ID';
COMMENT ON COLUMN mes_wm_batch.code IS '批次编码';
COMMENT ON COLUMN mes_wm_batch.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_batch.produce_date IS '生产日期';
COMMENT ON COLUMN mes_wm_batch.expire_date IS '有效期';
COMMENT ON COLUMN mes_wm_batch.receipt_date IS '入库日期';
COMMENT ON COLUMN mes_wm_batch.vendor_id IS '供应商ID';
COMMENT ON COLUMN mes_wm_batch.client_id IS '客户ID';
COMMENT ON COLUMN mes_wm_batch.sales_order_code IS '销售订单编号';
COMMENT ON COLUMN mes_wm_batch.purchase_order_code IS '采购订单编号';
COMMENT ON COLUMN mes_wm_batch.work_order_id IS '生产工单ID';
COMMENT ON COLUMN mes_wm_batch.task_id IS '生产任务ID';
COMMENT ON COLUMN mes_wm_batch.workstation_id IS '工作站ID';
COMMENT ON COLUMN mes_wm_batch.tool_id IS '工具ID';
COMMENT ON COLUMN mes_wm_batch.mold_id IS '模具ID';
COMMENT ON COLUMN mes_wm_batch.lot_number IS '生产批号';
COMMENT ON COLUMN mes_wm_batch.quality_status IS '质检状态';
COMMENT ON COLUMN mes_wm_batch.remark IS '备注';
COMMENT ON COLUMN mes_wm_batch.creator IS '创建者';
COMMENT ON COLUMN mes_wm_batch.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_batch.updater IS '更新者';
COMMENT ON COLUMN mes_wm_batch.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_batch.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_batch.tenant_id IS '租户ID';
COMMENT ON TABLE mes_wm_batch IS '批次管理表';

-- ----------------------------
-- Records of mes_wm_batch
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_batch (id, code, item_id, produce_date, expire_date, receipt_date, vendor_id, client_id, sales_order_code, purchase_order_code, work_order_id, task_id, workstation_id, tool_id, mold_id, lot_number, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PC', 100, NULL, '2026-03-12 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-13 23:49:42', '1', '2026-03-13 23:49:42', '0', 1), (2, 'PC20260', 100, NULL, '2026-02-26 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-13 23:54:53', '1', '2026-03-13 23:54:53', '0', 1), (3, 'PC202600001', 75, '2026-03-21 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (4, 'PC202600002', 75, '2026-03-24 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (5, 'BATCH_ITEM_1', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', '2026-03-30 02:46:05', '', '2026-03-30 02:46:05', '0', 1), (6, 'BATCH_ITEM_2', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', '2026-03-30 02:46:05', '', '2026-03-30 02:46:05', '0', 1), (7, 'BATCH_ITEM_94', 94, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', '2026-03-30 02:46:05', '', '2026-03-30 02:46:05', '0', 1), (8, 'BATCH_ITEM_72', 72, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', '2026-03-30 02:46:05', '', '2026-03-30 02:46:05', '0', 1), (9, 'RAW-BATCH-001', 94, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, 1, NULL, '1', '2026-04-05 01:32:43', '1', '2026-04-05 01:32:43', '0', 1), (10, 'PC202600003', 100, NULL, '2026-04-17 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-04-17 09:38:09', '1', '2026-04-17 09:38:09', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_batch_seq;
CREATE SEQUENCE mes_wm_batch_seq
    START 11;

-- ----------------------------
-- Table structure for mes_wm_item_consume
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_consume;
CREATE TABLE mes_wm_item_consume (
    id int8 NOT NULL,
  work_order_id int8 NOT NULL,
  task_id int8 NOT NULL,
  workstation_id int8 NOT NULL,
  process_id int8 NOT NULL,
  feedback_id int8 NOT NULL,
  consume_date timestamp NOT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_consume ADD CONSTRAINT pk_mes_wm_item_consume PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_item_consume_01 ON mes_wm_item_consume (feedback_id);
CREATE INDEX idx_mes_wm_item_consume_02 ON mes_wm_item_consume (work_order_id);
CREATE INDEX idx_mes_wm_item_consume_03 ON mes_wm_item_consume (task_id);

COMMENT ON COLUMN mes_wm_item_consume.id IS '编号';
COMMENT ON COLUMN mes_wm_item_consume.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_wm_item_consume.task_id IS '生产任务编号';
COMMENT ON COLUMN mes_wm_item_consume.workstation_id IS '工作站编号';
COMMENT ON COLUMN mes_wm_item_consume.process_id IS '工序编号';
COMMENT ON COLUMN mes_wm_item_consume.feedback_id IS '报工记录编号';
COMMENT ON COLUMN mes_wm_item_consume.consume_date IS '消耗日期';
COMMENT ON COLUMN mes_wm_item_consume.status IS '状态';
COMMENT ON COLUMN mes_wm_item_consume.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_consume.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_consume.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_consume.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_consume.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_consume.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_consume.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_consume IS 'MES 物料消耗记录';

-- ----------------------------
-- Records of mes_wm_item_consume
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_consume (id, work_order_id, task_id, workstation_id, process_id, feedback_id, consume_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 4, 3, 3, 7, '2026-03-19 23:02:57', 4, '', '1', '2026-03-19 23:02:57', '1', '2026-03-19 23:02:57', '0', 1), (18, 1, 4, 3, 3, 5, '2026-03-21 15:07:46', 4, '', '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (19, 1, 4, 3, 3, 6, '2026-03-21 15:09:48', 4, '', '1', '2026-03-21 15:09:48', '1', '2026-03-21 15:09:48', '0', 1), (20, 1, 4, 3, 3, 8, '2026-03-24 23:17:24', 4, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (21, 1, 1, 1, 1, 9, '2026-03-24 23:19:17', 4, '', '1', '2026-03-24 23:19:17', '1', '2026-03-24 23:19:17', '0', 1), (22, 17, 8, 8, 4, 12, '2026-05-26 00:33:47', 4, '验收造数：待检验消耗单', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (23, 17, 7, 7, 1, 13, '2026-05-26 00:33:47', 4, '验收造数：已完成消耗单', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_consume_seq;
CREATE SEQUENCE mes_wm_item_consume_seq
    START 24;

-- ----------------------------
-- Table structure for mes_wm_item_consume_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_consume_detail;
CREATE TABLE mes_wm_item_consume_detail (
    id int8 NOT NULL,
  consume_id int8 NOT NULL,
  line_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(128) NULL DEFAULT '',
  warehouse_id int8 NOT NULL,
  location_id int8 NOT NULL,
  area_id int8 NOT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_consume_detail ADD CONSTRAINT pk_mes_wm_item_consume_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_item_consume_detail_01 ON mes_wm_item_consume_detail (consume_id);
CREATE INDEX idx_mes_wm_item_consume_detail_02 ON mes_wm_item_consume_detail (line_id);
CREATE INDEX idx_mes_wm_item_consume_detail_03 ON mes_wm_item_consume_detail (item_id);

COMMENT ON COLUMN mes_wm_item_consume_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.consume_id IS '消耗记录编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.line_id IS '消耗记录行编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.material_stock_id IS '库存台账编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.quantity IS '消耗数量';
COMMENT ON COLUMN mes_wm_item_consume_detail.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_item_consume_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_item_consume_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_consume_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_consume_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_consume_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_consume_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_consume_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_consume_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_consume_detail IS 'MES 物料消耗记录明细';

-- ----------------------------
-- Records of mes_wm_item_consume_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_consume_detail (id, consume_id, line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, NULL, 94, 10.00, 9, 'RAW-BATCH-001', 1, 1, 1, '', '1', '2026-04-05 01:32:43', '1', '2026-04-05 01:32:43', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_consume_detail_seq;
CREATE SEQUENCE mes_wm_item_consume_detail_seq
    START 2;

-- ----------------------------
-- Table structure for mes_wm_item_consume_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_consume_line;
CREATE TABLE mes_wm_item_consume_line (
    id int8 NOT NULL,
  consume_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(128) NULL DEFAULT '',
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_consume_line ADD CONSTRAINT pk_mes_wm_item_consume_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_item_consume_line_01 ON mes_wm_item_consume_line (consume_id);
CREATE INDEX idx_mes_wm_item_consume_line_02 ON mes_wm_item_consume_line (item_id);

COMMENT ON COLUMN mes_wm_item_consume_line.id IS '编号';
COMMENT ON COLUMN mes_wm_item_consume_line.consume_id IS '消耗记录编号';
COMMENT ON COLUMN mes_wm_item_consume_line.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_item_consume_line.quantity IS '消耗数量';
COMMENT ON COLUMN mes_wm_item_consume_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_item_consume_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_item_consume_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_consume_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_consume_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_consume_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_consume_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_consume_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_consume_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_consume_line IS 'MES 物料消耗记录行';

-- ----------------------------
-- Records of mes_wm_item_consume_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_consume_line (id, consume_id, item_id, quantity, batch_id, batch_code, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 72, 0.50, NULL, '', '', '1', '2026-03-19 23:02:57', '1', '2026-03-19 23:02:57', '0', 1), (18, 18, 72, 0.50, NULL, '', '', '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (19, 19, 72, 0.50, NULL, '', '', '1', '2026-03-21 15:09:48', '1', '2026-03-21 15:09:48', '0', 1), (20, 20, 72, 1.00, NULL, '', '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (21, 21, 72, 45.00, NULL, '', '', '1', '2026-03-24 23:19:17', '1', '2026-03-24 23:19:17', '0', 1), (22, 22, 72, 7.50, NULL, 'IC-ACCEPT-UNCHECK', '验收造数：待检验消耗行', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (23, 23, 72, 10.00, NULL, 'IC-ACCEPT-FINISHED', '验收造数：已完成消耗行', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_consume_line_seq;
CREATE SEQUENCE mes_wm_item_consume_line_seq
    START 24;

-- ----------------------------
-- Table structure for mes_wm_item_receipt
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_receipt;
CREATE TABLE mes_wm_item_receipt (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  iqc_id int8 NULL DEFAULT NULL,
  notice_id int8 NULL DEFAULT NULL,
  purchase_order_code varchar(64) NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  receipt_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_receipt ADD CONSTRAINT pk_mes_wm_item_receipt PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_item_receipt_01 ON mes_wm_item_receipt (tenant_id, code);

CREATE INDEX idx_mes_wm_item_receipt_01 ON mes_wm_item_receipt (notice_id);
CREATE INDEX idx_mes_wm_item_receipt_02 ON mes_wm_item_receipt (vendor_id);

COMMENT ON COLUMN mes_wm_item_receipt.id IS '编号';
COMMENT ON COLUMN mes_wm_item_receipt.code IS '入库单编码';
COMMENT ON COLUMN mes_wm_item_receipt.name IS '入库单名称';
COMMENT ON COLUMN mes_wm_item_receipt.iqc_id IS '来料检验单编号（关联 mes_qc_iqc.id）';
COMMENT ON COLUMN mes_wm_item_receipt.notice_id IS '到货通知单编号（关联 mes_wm_arrival_notice.id）';
COMMENT ON COLUMN mes_wm_item_receipt.purchase_order_code IS '采购订单号';
COMMENT ON COLUMN mes_wm_item_receipt.vendor_id IS '供应商编号（关联 mes_md_vendor.id）';
COMMENT ON COLUMN mes_wm_item_receipt.receipt_date IS '入库日期';
COMMENT ON COLUMN mes_wm_item_receipt.status IS '状态';
COMMENT ON COLUMN mes_wm_item_receipt.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_receipt.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_receipt.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_receipt.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_receipt.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_receipt.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_receipt.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_receipt IS 'MES 采购入库单';

-- ----------------------------
-- Records of mes_wm_item_receipt
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_receipt (id, code, name, iqc_id, notice_id, purchase_order_code, vendor_id, receipt_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'IR2026020001', '铝材入库-草稿', NULL, 3, NULL, 200, '2026-02-20 00:00:00', 0, '草稿状态，可测 CRUD + submit', '1', '2026-02-22 14:54:33', '1', '2026-02-22 23:06:31', '0', 1), (2, 'IR2026020002', '螺丝入库-待上架', 1, 2, NULL, 2, '2026-02-19 00:00:00', 4, '待上架，明细数量已匹配，可测 shelving', '1', '2026-02-22 14:54:33', '1', '2026-02-26 08:17:43', '0', 1), (3, 'IR2026020003', '铝材入库-待入库', 2, 3, NULL, 1, '2026-02-21 00:00:00', 5, '待入库，可测 execute（会 finish 通知 3）', '1', '2026-02-22 14:54:33', '1', '2026-02-26 05:18:31', '0', 1), (4, 'IR2026020004', '铜线入库-已完成', 3, 4, NULL, 2, '2026-01-26 00:00:00', 5, '已完成入库', '1', '2026-02-22 14:54:33', '1', '2026-02-26 08:17:43', '0', 1), (5, 'IR2026020005', '钢板入库-已取消', NULL, 1, NULL, 1, '2026-02-22 00:00:00', 5, '已取消', '1', '2026-02-22 14:54:33', '1', '2026-02-26 05:18:31', '0', 1), (6, 'IRo3krPXYf2D', 'xx', NULL, NULL, NULL, 200, '2026-02-03 00:00:00', 3, '', '1', '2026-02-22 23:07:11', '1', '2026-02-26 08:17:43', '0', 1), (7, 'IRsr2UM5pbmU', '测试入库-无检验（必填）', NULL, 103, NULL, 200, '2026-02-08 00:00:00', 5, '', '1', '2026-02-25 20:03:40', '1', '2026-02-26 08:17:43', '0', 1), (8, 'IRLqesOjVdQU', '111', NULL, 3, NULL, 200, '2026-02-10 00:00:00', 0, '', '1', '2026-02-26 00:29:51', '1', '2026-02-26 00:29:51', '0', 1), (9, 'IRoUJ96EoC20', '11', NULL, NULL, NULL, 200, NULL, 0, '', '1', '2026-02-26 00:40:34', '1', '2026-02-26 00:41:36', '0', 1), (10, 'IRjrLOepPdD5', NULL, NULL, NULL, NULL, 200, '2026-02-10 00:00:00', 5, '', '1', '2026-02-26 00:41:49', '1', '2026-02-26 08:17:43', '0', 1), (11, 'IRmZ3ZLok1RM', '1122', NULL, 104, NULL, 201, '2026-02-03 00:00:00', 3, '', '1', '2026-02-26 00:43:00', '1', '2026-02-26 08:17:43', '0', 1), (12, '111', '23213', NULL, 104, NULL, 201, '2026-01-28 00:00:00', 2, '', '1', '2026-02-26 01:19:23', '1', '2026-02-27 23:06:31', '0', 1), (13, 'IRtxch5t3oqA', '32132321', NULL, 4, NULL, 200, '2026-03-03 00:00:00', 0, '', '1', '2026-03-13 23:20:40', '1', '2026-03-13 23:20:40', '0', 1), (14, 'IR5rhFfpNJJq', '3213213', NULL, NULL, NULL, 200, '2026-03-02 00:00:00', 3, '', '1', '2026-03-13 23:46:19', '1', '2026-03-29 18:23:55', '0', 1), (15, 'IR20260329000001', 'XXXX', NULL, NULL, 'AAA', 200, '2026-03-18 00:00:00', 4, '', '1', '2026-03-29 16:19:18', '1', '2026-03-29 18:06:09', '0', 1), (16, 'IR-20260412-001', 'ä¸´æ—¶é‡‡è´­å…¥åº“å•çš„æ¨¡æ‹Ÿæ•°æ®', NULL, NULL, 'PO-123456', 1, '2026-04-11 18:28:00', 0, '', '', '2026-04-11 18:28:00', '1', '2026-04-17 21:55:59', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_receipt_seq;
CREATE SEQUENCE mes_wm_item_receipt_seq
    START 17;

-- ----------------------------
-- Table structure for mes_wm_item_receipt_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_receipt_detail;
CREATE TABLE mes_wm_item_receipt_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_receipt_detail ADD CONSTRAINT pk_mes_wm_item_receipt_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_item_receipt_detail_01 ON mes_wm_item_receipt_detail (line_id);
CREATE INDEX idx_mes_wm_item_receipt_detail_02 ON mes_wm_item_receipt_detail (receipt_id);
CREATE INDEX idx_mes_wm_item_receipt_detail_03 ON mes_wm_item_receipt_detail (item_id);

COMMENT ON COLUMN mes_wm_item_receipt_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_item_receipt_detail.line_id IS '入库单行编号（关联 mes_wm_item_receipt_line.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.receipt_id IS '入库单编号（关联 mes_wm_item_receipt.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.item_id IS '物料编号（关联 mes_md_item.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.quantity IS '上架数量';
COMMENT ON COLUMN mes_wm_item_receipt_detail.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_item_receipt_detail.warehouse_id IS '仓库编号（关联 mes_wm_warehouse.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.location_id IS '库区编号（关联 mes_wm_warehouse_location.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.area_id IS '库位编号（关联 mes_wm_warehouse_area.id）';
COMMENT ON COLUMN mes_wm_item_receipt_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_receipt_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_receipt_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_receipt_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_receipt_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_receipt_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_receipt_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_receipt_detail IS 'MES 采购入库明细';

-- ----------------------------
-- Records of mes_wm_item_receipt_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_receipt_detail (id, line_id, receipt_id, item_id, quantity, batch_id, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 2, 3, 600.00, NULL, 701, 711, 721, '上架至原料区 A-01', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (2, 3, 2, 3, 400.00, NULL, 701, 711, 722, '上架至原料区 A-02', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (3, 4, 2, 4, 500.00, NULL, 701, 711, 722, '上架至原料区 A-02', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (4, 5, 3, 5, 500.00, NULL, 701, 712, 722, '上架至原料区 B-01', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (5, 5, 3, 5, 300.00, NULL, 701, 712, 723, '上架至原料区 B-02', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (6, 6, 3, 6, 300.00, NULL, 701, 712, 723, '上架至原料区 B-02', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (7, 7, 4, 7, 400.00, NULL, 701, 711, 721, '上架至原料区 A-01', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (8, 7, 4, 7, 200.00, NULL, 701, 711, 722, '上架至原料区 A-02', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (9, 9, 6, 69, 123.00, NULL, 702, 713, 724, '', '1', '2026-02-23 01:22:25', '1', '2026-02-23 01:40:02', '1', 1), (10, 9, 6, 69, 4.00, NULL, 702, 713, 724, '', '1', '2026-02-23 01:55:09', '1', '2026-02-23 02:03:16', '0', 1), (11, 9, 6, 69, 321321.00, NULL, 702, 713, 724, '', '1', '2026-02-23 02:03:24', '1', '2026-02-23 02:15:31', '1', 1), (12, 9, 6, 69, 555.00, NULL, 701, 712, 723, '', '1', '2026-02-23 02:15:27', '1', '2026-02-23 02:15:27', '0', 1), (13, 10, 7, 69, 222.00, NULL, 702, 713, 724, '', '1', '2026-02-26 00:24:22', '1', '2026-02-26 00:24:37', '0', 1), (14, 11, 10, 69, 1.00, NULL, 702, 713, 724, '', '1', '2026-02-26 00:42:45', '1', '2026-02-26 00:42:45', '0', 1), (15, 13, 12, 69, 1.00, NULL, 702, 713, 724, '', '1', '2026-02-27 23:11:16', '1', '2026-02-27 23:11:16', '0', 1), (16, 19, 15, 94, 1.00, NULL, 702, 713, 724, '', '1', '2026-03-29 18:04:37', '1', '2026-03-29 18:04:37', '0', 1), (17, 14, 14, 94, 11.00, NULL, 703, 714, 725, '', '1', '2026-03-29 18:23:46', '1', '2026-03-29 18:23:53', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_receipt_detail_seq;
CREATE SEQUENCE mes_wm_item_receipt_detail_seq
    START 18;

-- ----------------------------
-- Table structure for mes_wm_item_receipt_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_item_receipt_line;
CREATE TABLE mes_wm_item_receipt_line (
    id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  arrival_notice_line_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  received_quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  production_date timestamp NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  lot_number varchar(128) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_item_receipt_line ADD CONSTRAINT pk_mes_wm_item_receipt_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_item_receipt_line_01 ON mes_wm_item_receipt_line (receipt_id);
CREATE INDEX idx_mes_wm_item_receipt_line_02 ON mes_wm_item_receipt_line (item_id);

COMMENT ON COLUMN mes_wm_item_receipt_line.id IS '编号';
COMMENT ON COLUMN mes_wm_item_receipt_line.receipt_id IS '入库单编号（关联 mes_wm_item_receipt.id）';
COMMENT ON COLUMN mes_wm_item_receipt_line.arrival_notice_line_id IS '到货通知单行编号';
COMMENT ON COLUMN mes_wm_item_receipt_line.item_id IS '物料编号（关联 mes_md_item.id）';
COMMENT ON COLUMN mes_wm_item_receipt_line.received_quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_item_receipt_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_item_receipt_line.batch_code IS '批次编码';
COMMENT ON COLUMN mes_wm_item_receipt_line.production_date IS '生产日期';
COMMENT ON COLUMN mes_wm_item_receipt_line.expire_date IS '有效期';
COMMENT ON COLUMN mes_wm_item_receipt_line.lot_number IS '生产批号';
COMMENT ON COLUMN mes_wm_item_receipt_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_item_receipt_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_item_receipt_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_item_receipt_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_item_receipt_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_item_receipt_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_item_receipt_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_item_receipt_line IS 'MES 采购入库单行';

-- ----------------------------
-- Records of mes_wm_item_receipt_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_item_receipt_line (id, receipt_id, arrival_notice_line_id, item_id, received_quantity, batch_id, batch_code, production_date, expire_date, lot_number, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 5, 69, 800.00, NULL, NULL, '2026-01-15 00:00:00', '2027-01-15 00:00:00', 'PB20260115-A', '铝板入库行', '1', '2026-02-22 14:54:33', '1', '2026-02-22 22:58:56', '0', 1), (2, 1, 6, 70, 300.00, NULL, NULL, '2026-01-16 00:00:00', '2027-06-16 00:00:00', 'PB20260116-B', '铝棒入库行', '1', '2026-02-22 14:54:33', '1', '2026-02-22 22:58:59', '0', 1), (3, 2, 3, 3, 1000.00, NULL, NULL, '2026-01-10 00:00:00', '2027-01-10 00:00:00', 'PB20260110-C', '六角螺丝入库', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (4, 2, 4, 4, 500.00, NULL, NULL, '2026-01-12 00:00:00', '2028-01-12 00:00:00', 'PB20260112-D', '垫片入库', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (5, 3, 5, 5, 800.00, NULL, NULL, '2026-01-15 00:00:00', '2027-01-15 00:00:00', 'PB20260115-E', '铝板入库-待执行', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (6, 3, 6, 6, 300.00, NULL, NULL, '2026-01-16 00:00:00', '2027-06-16 00:00:00', 'PB20260116-F', '铝棒入库-待执行', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (7, 4, 7, 7, 600.00, NULL, NULL, '2025-12-20 00:00:00', '2026-12-20 00:00:00', 'PB20251220-G', '铜线入库-已完成', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (8, 5, 1, 1, 500.00, NULL, NULL, '2026-01-20 00:00:00', '2027-01-20 00:00:00', 'PB20260120-H', '钢板入库-已取消', '1', '2026-02-22 14:54:33', '1', '2026-02-22 14:54:33', '0', 1), (9, 6, NULL, 69, 5.00, NULL, NULL, '2026-02-03 00:00:00', '2026-02-13 00:00:00', NULL, '', '1', '2026-02-22 23:14:39', '1', '2026-02-22 23:14:39', '0', 1), (10, 7, NULL, 69, 222.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-25 21:53:46', '1', '2026-02-25 21:53:46', '0', 1), (11, 10, NULL, 69, 1.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-26 00:42:10', '1', '2026-02-26 00:42:10', '0', 1), (12, 11, 108, 69, 3.00, NULL, NULL, NULL, NULL, '1', '', '1', '2026-02-26 01:11:17', '1', '2026-02-26 01:11:17', '0', 1), (13, 12, 108, 69, 222.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-02-27 23:06:17', '1', '2026-02-27 23:06:17', '0', 1), (14, 14, NULL, 94, 11.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-03-13 23:47:17', '1', '2026-03-13 23:47:53', '0', 1), (15, 14, NULL, 100, 10.00, 1, 'PC', NULL, '2026-03-12 00:00:00', NULL, '', '1', '2026-03-13 23:49:42', '1', '2026-03-13 23:52:19', '1', 1), (16, 14, NULL, 100, 100.00, 2, 'PC20260', NULL, '2026-02-26 00:00:00', NULL, '', '1', '2026-03-13 23:54:53', '1', '2026-03-14 00:23:09', '1', 1), (17, 14, NULL, 94, 10.00, NULL, NULL, '2026-03-19 00:00:00', '2026-03-04 00:00:00', '112', '', '1', '2026-03-14 00:23:02', '1', '2026-03-14 00:23:05', '1', 1), (18, 14, NULL, 94, 10.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-03-14 00:23:16', '1', '2026-03-14 00:23:19', '1', 1), (19, 15, NULL, 94, 1.00, NULL, NULL, '2026-03-29 00:00:00', '2026-03-12 00:00:00', 'xxx', '', '1', '2026-03-29 16:23:56', '1', '2026-03-29 16:23:56', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_item_receipt_line_seq;
CREATE SEQUENCE mes_wm_item_receipt_line_seq
    START 20;

-- ----------------------------
-- Table structure for mes_wm_material_stock
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_material_stock;
CREATE TABLE mes_wm_material_stock (
    id int8 NOT NULL,
  item_type_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  quantity numeric(14,4) NOT NULL DEFAULT 0.0000,
  receipt_time timestamp NULL DEFAULT NULL,
  frozen bool NULL DEFAULT '0',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_material_stock ADD CONSTRAINT pk_mes_wm_material_stock PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_material_stock_01 ON mes_wm_material_stock (tenant_id, item_id, batch_id, warehouse_id, location_id, area_id);

CREATE INDEX idx_mes_wm_material_stock_01 ON mes_wm_material_stock (item_id);
CREATE INDEX idx_mes_wm_material_stock_02 ON mes_wm_material_stock (warehouse_id);
CREATE INDEX idx_mes_wm_material_stock_03 ON mes_wm_material_stock (batch_id);

COMMENT ON COLUMN mes_wm_material_stock.id IS '编号';
COMMENT ON COLUMN mes_wm_material_stock.item_type_id IS '物料分类编号（mes_md_item_type.id）';
COMMENT ON COLUMN mes_wm_material_stock.item_id IS '物料编号（mes_md_item.id）';
COMMENT ON COLUMN mes_wm_material_stock.batch_id IS '批次编号（mes_wm_batch.id）';
COMMENT ON COLUMN mes_wm_material_stock.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_material_stock.warehouse_id IS '仓库编号（mes_wm_warehouse.id）';
COMMENT ON COLUMN mes_wm_material_stock.location_id IS '库区编号（mes_wm_warehouse_location.id）';
COMMENT ON COLUMN mes_wm_material_stock.area_id IS '库位编号（mes_wm_warehouse_area.id）';
COMMENT ON COLUMN mes_wm_material_stock.vendor_id IS '供应商编号（mes_md_vendor.id）';
COMMENT ON COLUMN mes_wm_material_stock.quantity IS '在库数量';
COMMENT ON COLUMN mes_wm_material_stock.receipt_time IS '入库时间';
COMMENT ON COLUMN mes_wm_material_stock.frozen IS '是否冻结';
COMMENT ON COLUMN mes_wm_material_stock.creator IS '创建者';
COMMENT ON COLUMN mes_wm_material_stock.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_material_stock.updater IS '更新者';
COMMENT ON COLUMN mes_wm_material_stock.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_material_stock.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_material_stock.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_material_stock IS 'MES 库存台账（仓库现有量）';

-- ----------------------------
-- Records of mes_wm_material_stock
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_material_stock (id, item_type_id, item_id, batch_id, batch_code, warehouse_id, location_id, area_id, vendor_id, quantity, receipt_time, frozen, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 70, 5, 'BATCH_ITEM_1', 701, 711, 721, 1, 500.0000, '2026-01-15 10:00:00', '0', '1', '2026-02-20 01:02:46', '1', '2026-03-31 15:26:14', '0', 1), (2, 1, 2, 6, 'BATCH_ITEM_2', 701, 711, 722, NULL, 200.0000, '2026-02-01 14:30:00', '0', '1', '2026-02-20 01:02:46', '1', '2026-03-30 02:46:05', '0', 1), (3, 1, 1, 5, 'BATCH_ITEM_1', 702, 713, 724, 1, 150.0000, '2026-02-10 09:00:00', '1', '1', '2026-02-20 01:02:46', '1', '2026-03-30 02:46:05', '0', 1), (4, 275, 69, 1, 'TEST', 702, 713, 724, 200, 0.0000, '2026-02-26 00:24:44', '0', '1', '2026-02-26 00:24:44', '1', '2026-03-30 13:54:28', '0', 1), (5, 272, 100, 1, 'PC', 701, NULL, NULL, NULL, 800.0000, '2026-03-01 10:00:00', '0', '1', '2026-03-10 11:14:37', '1', '2026-03-30 02:46:05', '0', 1), (7, 272, 100, 1, 'PC', 702, NULL, NULL, NULL, 200.0000, '2026-03-08 09:15:00', '0', '1', '2026-03-10 11:14:37', '1', '2026-03-30 02:46:05', '0', 1), (9, 282, 94, 7, 'BATCH_ITEM_94', 702, 713, 724, NULL, 321321444.0000, '2026-03-22 21:38:40', '0', '1', '2026-03-22 21:38:40', '1', '2026-03-31 01:41:22', '0', 1), (10, 282, 94, 7, 'BATCH_ITEM_94', 701, 712, 723, NULL, 0.0000, '2026-03-22 23:11:51', '0', '1', '2026-03-22 23:11:51', '1', '2026-03-30 03:11:49', '0', 1), (11, 274, 72, 8, 'BATCH_ITEM_72', 703, 714, 725, NULL, -46.0000, '2026-03-24 23:17:24', '0', '1', '2026-03-24 23:17:24', '1', '2026-03-30 02:46:05', '0', 1), (12, 277, 75, 4, 'PC202600002', 703, 714, 725, NULL, 2.0000, '2026-03-24 23:17:24', '0', '1', '2026-03-24 23:17:24', '1', '2026-03-29 02:21:39', '0', 1), (14, 282, 94, 7, 'BATCH_ITEM_94', 703, 714, 725, NULL, 1.0000, '2026-03-30 11:11:50', '0', '1', '2026-03-30 11:11:50', '1', '2026-03-30 03:11:49', '0', 1), (16, 275, 69, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (17, 275, 70, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (18, 275, 71, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (19, 274, 72, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (20, 276, 73, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (21, 276, 74, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (22, 277, 75, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (23, 282, 94, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (24, 282, 95, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (25, 276, 96, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (26, 272, 100, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (27, 275, 101, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (28, 274, 102, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (29, 273, 103, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (30, 200, 104, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (31, 274, 105, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (32, 274, 106, 0, 'B-TEST', 702, 711, 721, 0, 100.0000, '2026-03-31 15:28:21', '0', 'admin', '2026-03-31 15:28:21', 'admin', '2026-03-31 15:28:21', '0', 1), (48, 275, 69, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (49, 275, 70, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (50, 275, 71, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (51, 274, 72, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (52, 276, 73, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (53, 276, 74, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (54, 277, 75, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (55, 282, 94, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (56, 282, 95, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (57, 276, 96, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (58, 272, 100, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (59, 275, 101, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (60, 274, 102, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (61, 273, 103, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (62, 200, 104, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (63, 274, 105, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (64, 274, 106, 999, 'B-TEST-2', 702, 711, 721, 0, 50.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (79, 275, 69, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (80, 275, 70, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (81, 275, 71, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (82, 274, 72, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (83, 276, 73, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (84, 276, 74, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (85, 277, 75, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (86, 282, 94, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (87, 282, 95, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (88, 276, 96, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (89, 272, 100, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (90, 275, 101, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (91, 274, 102, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (92, 273, 103, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (93, 200, 104, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (94, 274, 105, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1), (95, 274, 106, 888, 'B-TEST-3', 702, 711, 721, 0, 80.0000, '2026-03-31 15:29:46', '0', 'admin', '2026-03-31 15:29:46', 'admin', '2026-03-31 15:29:46', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_material_stock_seq;
CREATE SEQUENCE mes_wm_material_stock_seq
    START 96;

-- ----------------------------
-- Table structure for mes_wm_misc_issue
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_issue;
CREATE TABLE mes_wm_misc_issue (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  type int4 NOT NULL,
  source_doc_type varchar(64) NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(64) NULL DEFAULT NULL,
  issue_date timestamp NULL DEFAULT NULL,
  status int4 NOT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT NULL,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_misc_issue ADD CONSTRAINT pk_mes_wm_misc_issue PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_misc_issue_01 ON mes_wm_misc_issue (code, deleted, tenant_id);

COMMENT ON COLUMN mes_wm_misc_issue.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_issue.code IS '出库单编号';
COMMENT ON COLUMN mes_wm_misc_issue.name IS '出库单名称';
COMMENT ON COLUMN mes_wm_misc_issue.type IS '杂项类型';
COMMENT ON COLUMN mes_wm_misc_issue.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_wm_misc_issue.source_doc_id IS '来源单据ID';
COMMENT ON COLUMN mes_wm_misc_issue.source_doc_code IS '来源单据编号';
COMMENT ON COLUMN mes_wm_misc_issue.issue_date IS '出库日期';
COMMENT ON COLUMN mes_wm_misc_issue.status IS '状态';
COMMENT ON COLUMN mes_wm_misc_issue.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_issue.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_issue.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_issue.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_issue.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_issue.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_issue.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_issue IS 'MES 杂项出库单';

-- ----------------------------
-- Records of mes_wm_misc_issue
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_issue (id, code, name, type, source_doc_type, source_doc_id, source_doc_code, issue_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'MI2026030201', '测试杂项出库单', 1, NULL, NULL, NULL, '1970-01-01 08:00:00', 4, '测试创建杂项出库单', '1', '2026-03-02 22:14:27', '1', '2026-03-03 07:34:33', '0', 1), (2, 'MI2026030202', '测试报废出库单', 1, NULL, NULL, NULL, '1970-01-01 08:00:00', 4, '测试报废出库', '1', '2026-03-02 22:14:28', '1', '2026-03-22 20:10:24', '0', 1), (3, 'MIokj1zVk4I6', 'eee', 1, NULL, NULL, NULL, NULL, 4, NULL, '1', '2026-03-03 19:01:24', '1', '2026-03-03 19:11:27', '0', 1), (4, 'MI47CkggsQpp', '1231321', 1, NULL, NULL, NULL, '2026-03-10 00:00:00', 4, NULL, '1', '2026-03-22 20:10:43', '1', '2026-03-22 20:10:57', '0', 1), (5, 'MIwCYpUvm6gE', 'AAA', 1, NULL, NULL, NULL, NULL, 3, NULL, '1', '2026-03-22 21:36:23', '1', '2026-03-22 21:37:07', '0', 1), (6, 'MIhtTkVvZZgk', '123', 1, NULL, NULL, NULL, '2026-03-17 00:00:00', 3, NULL, '1', '2026-03-22 21:50:45', '1', '2026-03-30 22:48:11', '0', 1), (7, 'AABBB', 'EEE', 1, NULL, NULL, NULL, '2026-04-02 00:00:00', 3, NULL, '1', '2026-03-30 23:04:55', '1', '2026-03-30 23:05:13', '0', 1), (8, 'MISCI20260417001', 'ABC', 1, NULL, NULL, NULL, '2026-04-16 00:00:00', 0, NULL, '1', '2026-04-17 08:41:42', '1', '2026-04-17 08:41:42', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_issue_seq;
CREATE SEQUENCE mes_wm_misc_issue_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_misc_issue_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_issue_detail;
CREATE TABLE mes_wm_misc_issue_detail (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  line_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(128) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_misc_issue_detail ADD CONSTRAINT pk_mes_wm_misc_issue_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_misc_issue_detail_01 ON mes_wm_misc_issue_detail (issue_id);
CREATE INDEX idx_mes_wm_misc_issue_detail_02 ON mes_wm_misc_issue_detail (line_id);
CREATE INDEX idx_mes_wm_misc_issue_detail_03 ON mes_wm_misc_issue_detail (material_stock_id);
CREATE INDEX idx_mes_wm_misc_issue_detail_04 ON mes_wm_misc_issue_detail (item_id);

COMMENT ON COLUMN mes_wm_misc_issue_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.issue_id IS '出库单编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.line_id IS '出库单行编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.material_stock_id IS '库存记录ID';
COMMENT ON COLUMN mes_wm_misc_issue_detail.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.quantity IS '出库数量';
COMMENT ON COLUMN mes_wm_misc_issue_detail.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_misc_issue_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_issue_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_issue_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_issue_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_issue_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_issue_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_issue_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_issue_detail IS 'MES 杂项出库明细';

-- ----------------------------
-- Records of mes_wm_misc_issue_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_issue_detail (id, issue_id, line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 4, NULL, 69, 1.00, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-03-03 19:06:15', '1', '2026-03-03 19:11:23', '0', 1), (2, 4, 5, NULL, 69, 123.00, NULL, NULL, 702, 713, 724, '', '1', '2026-03-22 20:10:50', '1', '2026-03-22 20:10:50', '0', 1), (3, 5, 6, NULL, 69, 123.00, NULL, NULL, 701, 712, 723, '', '1', '2026-03-22 21:36:28', '1', '2026-03-22 21:36:48', '0', 1), (4, 6, 7, NULL, 69, 321321.00, NULL, NULL, 702, 713, 724, '', '1', '2026-03-22 21:50:56', '1', '2026-03-22 21:50:56', '0', 1), (5, 7, 8, 14, 94, 123.00, NULL, 'BATCH_ITEM_94', 703, 714, 725, '', '1', '2026-03-30 23:05:11', '1', '2026-03-30 23:05:11', '0', 1), (6, 8, 9, 1, 70, 123.00, NULL, 'BATCH_ITEM_1', 701, 711, 721, '', '1', '2026-04-17 08:42:16', '1', '2026-04-17 08:42:16', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_issue_detail_seq;
CREATE SEQUENCE mes_wm_misc_issue_detail_seq
    START 7;

-- ----------------------------
-- Table structure for mes_wm_misc_issue_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_issue_line;
CREATE TABLE mes_wm_misc_issue_line (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  source_doc_line_id int8 NULL DEFAULT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(15,2) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(128) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT NULL,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_misc_issue_line ADD CONSTRAINT pk_mes_wm_misc_issue_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_misc_issue_line_01 ON mes_wm_misc_issue_line (issue_id);

COMMENT ON COLUMN mes_wm_misc_issue_line.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.issue_id IS '出库单编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.source_doc_line_id IS '来源单据行ID';
COMMENT ON COLUMN mes_wm_misc_issue_line.material_stock_id IS '库存记录ID';
COMMENT ON COLUMN mes_wm_misc_issue_line.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.quantity IS '出库数量';
COMMENT ON COLUMN mes_wm_misc_issue_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_misc_issue_line.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_misc_issue_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_issue_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_issue_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_issue_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_issue_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_issue_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_issue_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_issue_line IS 'MES 杂项出库单行';

-- ----------------------------
-- Records of mes_wm_misc_issue_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_issue_line (id, issue_id, source_doc_line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, NULL, NULL, 69, 10.00, NULL, NULL, 702, 713, 724, NULL, '1', '2026-03-03 12:51:11', '1', '2026-03-03 12:51:11', '0', 1), (4, 3, NULL, NULL, 69, 1.00, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-03 19:06:15', '1', '2026-03-03 19:11:23', '0', 1), (5, 4, NULL, NULL, 69, 123.00, NULL, NULL, 702, 713, 724, NULL, '1', '2026-03-22 20:10:50', '1', '2026-03-22 20:10:50', '0', 1), (6, 5, NULL, NULL, 69, 123.00, NULL, NULL, 701, 712, 723, NULL, '1', '2026-03-22 21:36:28', '1', '2026-03-22 21:36:48', '0', 1), (7, 6, NULL, NULL, 69, 321321.00, NULL, NULL, 702, 713, 724, NULL, '1', '2026-03-22 21:50:56', '1', '2026-03-22 21:50:56', '0', 1), (8, 7, NULL, 14, 94, 123.00, NULL, 'BATCH_ITEM_94', 703, 714, 725, NULL, '1', '2026-03-30 23:05:11', '1', '2026-03-30 23:05:11', '0', 1), (9, 8, NULL, 1, 70, 123.00, NULL, 'BATCH_ITEM_1', 701, 711, 721, NULL, '1', '2026-04-17 08:42:16', '1', '2026-04-17 08:42:16', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_issue_line_seq;
CREATE SEQUENCE mes_wm_misc_issue_line_seq
    START 10;

-- ----------------------------
-- Table structure for mes_wm_misc_receipt
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_receipt;
CREATE TABLE mes_wm_misc_receipt (
    id int8 NOT NULL,
  code varchar(50) NOT NULL,
  name varchar(100) NOT NULL,
  type int4 NOT NULL,
  source_doc_type varchar(20) NULL DEFAULT NULL,
  source_doc_id int8 NULL DEFAULT NULL,
  source_doc_code varchar(50) NULL DEFAULT NULL,
  receipt_date timestamp NOT NULL,
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL
);

ALTER TABLE mes_wm_misc_receipt ADD CONSTRAINT pk_mes_wm_misc_receipt PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_misc_receipt_01 ON mes_wm_misc_receipt (code, deleted);

CREATE INDEX idx_mes_wm_misc_receipt_01 ON mes_wm_misc_receipt (tenant_id);
CREATE INDEX idx_mes_wm_misc_receipt_02 ON mes_wm_misc_receipt (status);
CREATE INDEX idx_mes_wm_misc_receipt_03 ON mes_wm_misc_receipt (receipt_date);
CREATE INDEX idx_mes_wm_misc_receipt_04 ON mes_wm_misc_receipt (source_doc_id, source_doc_type);

COMMENT ON COLUMN mes_wm_misc_receipt.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_receipt.code IS '入库单编码';
COMMENT ON COLUMN mes_wm_misc_receipt.name IS '入库单名称';
COMMENT ON COLUMN mes_wm_misc_receipt.type IS '杂项类型';
COMMENT ON COLUMN mes_wm_misc_receipt.source_doc_type IS '来源单据类型';
COMMENT ON COLUMN mes_wm_misc_receipt.source_doc_id IS '来源单据 ID';
COMMENT ON COLUMN mes_wm_misc_receipt.source_doc_code IS '来源单据编号';
COMMENT ON COLUMN mes_wm_misc_receipt.receipt_date IS '入库日期';
COMMENT ON COLUMN mes_wm_misc_receipt.status IS '状态';
COMMENT ON COLUMN mes_wm_misc_receipt.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_receipt.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_receipt.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_receipt.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_receipt.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_receipt.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_receipt.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_receipt IS 'MES 杂项入库单';

-- ----------------------------
-- Records of mes_wm_misc_receipt
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_receipt (id, code, name, type, source_doc_type, source_doc_id, source_doc_code, receipt_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'MRRvmpXXBVDn', '1321', 1, NULL, NULL, NULL, '2026-03-09 00:00:00', 4, NULL, '1', '2026-03-03 19:31:29', '1', '2026-03-03 19:31:45', '0', 1), (2, 'MRbJ5HI6aOMZ', 'AAA', 1, NULL, NULL, NULL, '2026-03-15 00:00:00', 4, NULL, '1', '2026-03-22 20:08:52', '1', '2026-03-22 20:09:34', '0', 1), (3, 'MRbWmZ1QXwg8', '31221', 1, NULL, NULL, NULL, '2026-03-11 00:00:00', 3, NULL, '1', '2026-03-22 21:37:32', '1', '2026-03-22 21:38:02', '0', 1), (4, 'MRlD8OQUII96', '32132', 1, NULL, NULL, NULL, '2026-03-03 00:00:00', 4, NULL, '1', '2026-03-22 21:38:24', '1', '2026-03-22 21:38:40', '0', 1), (5, 'MROO8b4vMA5x', '321321', 1, 'EEE', NULL, 'ABC', '2026-03-03 00:00:00', 4, NULL, '1', '2026-03-22 21:51:34', '1', '2026-03-31 09:41:23', '0', 1), (6, 'MISCR20260417001', 'EEE', 1, NULL, NULL, NULL, '2026-04-23 00:00:00', 0, NULL, '1', '2026-04-17 08:43:37', '1', '2026-05-29 23:41:02', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_receipt_seq;
CREATE SEQUENCE mes_wm_misc_receipt_seq
    START 7;

-- ----------------------------
-- Table structure for mes_wm_misc_receipt_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_receipt_detail;
CREATE TABLE mes_wm_misc_receipt_detail (
    id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  line_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NOT NULL,
  batch_code varchar(128) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  production_date timestamp NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  production_batch_number varchar(128) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_misc_receipt_detail ADD CONSTRAINT pk_mes_wm_misc_receipt_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_misc_receipt_detail_01 ON mes_wm_misc_receipt_detail (receipt_id);
CREATE INDEX idx_mes_wm_misc_receipt_detail_02 ON mes_wm_misc_receipt_detail (line_id);
CREATE INDEX idx_mes_wm_misc_receipt_detail_03 ON mes_wm_misc_receipt_detail (item_id);

COMMENT ON COLUMN mes_wm_misc_receipt_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.receipt_id IS '入库单编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.line_id IS '入库单行编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.production_date IS '生产日期';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.expire_date IS '有效期';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.production_batch_number IS '生产批号';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_receipt_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_receipt_detail IS 'MES 杂项入库明细';

-- ----------------------------
-- Records of mes_wm_misc_receipt_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_receipt_detail (id, receipt_id, line_id, item_id, quantity, batch_code, warehouse_id, location_id, area_id, production_date, expire_date, production_batch_number, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 69, 123.00, NULL, 702, 713, 724, NULL, NULL, NULL, '', '1', '2026-03-03 19:31:39', '1', '2026-03-03 19:31:39', '0', 1), (2, 2, 2, 69, 123.00, NULL, 702, NULL, NULL, NULL, NULL, NULL, '', '1', '2026-03-22 20:09:00', '1', '2026-03-22 12:09:13', '1', 1), (3, 2, 3, 69, 123.00, '123', 702, 713, 724, NULL, NULL, NULL, '', '1', '2026-03-22 20:09:22', '1', '2026-03-22 20:09:22', '0', 1), (4, 3, 4, 69, 1.00, NULL, 702, 713, 724, NULL, NULL, NULL, '', '1', '2026-03-22 21:37:40', '1', '2026-03-22 21:37:40', '0', 1), (5, 4, 5, 94, 123.00, NULL, 702, 713, 724, NULL, NULL, NULL, '312321', '1', '2026-03-22 21:38:34', '1', '2026-03-22 21:38:34', '0', 1), (6, 5, 6, 94, 321321321.00, NULL, 702, 713, 724, NULL, NULL, NULL, '', '1', '2026-03-22 21:51:43', '1', '2026-03-22 21:51:43', '0', 1), (7, 6, 7, 103, 123.00, NULL, 702, 713, 724, NULL, NULL, NULL, '1213', '1', '2026-04-17 08:43:50', '1', '2026-05-29 23:40:52', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_receipt_detail_seq;
CREATE SEQUENCE mes_wm_misc_receipt_detail_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_misc_receipt_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_misc_receipt_line;
CREATE TABLE mes_wm_misc_receipt_line (
    id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(20,6) NOT NULL,
  batch_code varchar(50) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  production_date timestamp NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  lot_number varchar(50) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL
);

ALTER TABLE mes_wm_misc_receipt_line ADD CONSTRAINT pk_mes_wm_misc_receipt_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_misc_receipt_line_01 ON mes_wm_misc_receipt_line (receipt_id);
CREATE INDEX idx_mes_wm_misc_receipt_line_02 ON mes_wm_misc_receipt_line (tenant_id);
CREATE INDEX idx_mes_wm_misc_receipt_line_03 ON mes_wm_misc_receipt_line (item_id);
CREATE INDEX idx_mes_wm_misc_receipt_line_04 ON mes_wm_misc_receipt_line (warehouse_id, location_id, area_id);

COMMENT ON COLUMN mes_wm_misc_receipt_line.id IS '编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.receipt_id IS '入库单编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_misc_receipt_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_misc_receipt_line.production_date IS '生产日期（暂不使用）';
COMMENT ON COLUMN mes_wm_misc_receipt_line.expire_date IS '有效期（暂不使用）';
COMMENT ON COLUMN mes_wm_misc_receipt_line.lot_number IS '生产批号（暂不使用）';
COMMENT ON COLUMN mes_wm_misc_receipt_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_misc_receipt_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_misc_receipt_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_misc_receipt_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_misc_receipt_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_misc_receipt_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_misc_receipt_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_misc_receipt_line IS 'MES 杂项入库单行';

-- ----------------------------
-- Records of mes_wm_misc_receipt_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_misc_receipt_line (id, receipt_id, item_id, quantity, batch_code, warehouse_id, location_id, area_id, production_date, expire_date, lot_number, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 69, 123.000000, NULL, 702, 713, 724, NULL, NULL, NULL, NULL, '1', '2026-03-03 19:31:39', '1', '2026-03-03 19:31:39', '0', 1), (2, 2, 69, 123.000000, NULL, 702, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-22 20:09:00', '1', '2026-03-22 20:09:13', '1', 1), (3, 2, 69, 123.000000, '123', 702, 713, 724, NULL, NULL, NULL, NULL, '1', '2026-03-22 20:09:22', '1', '2026-03-22 20:09:22', '0', 1), (4, 3, 69, 1.000000, NULL, 702, 713, 724, NULL, NULL, NULL, NULL, '1', '2026-03-22 21:37:40', '1', '2026-03-22 21:37:40', '0', 1), (5, 4, 94, 123.000000, NULL, 702, 713, 724, NULL, NULL, NULL, '312321', '1', '2026-03-22 21:38:34', '1', '2026-03-22 21:38:34', '0', 1), (6, 5, 94, 321321321.000000, NULL, 702, 713, 724, NULL, NULL, NULL, NULL, '1', '2026-03-22 21:51:43', '1', '2026-03-22 21:51:43', '0', 1), (7, 6, 103, 123.000000, NULL, 702, 713, 724, NULL, NULL, NULL, '1213', '1', '2026-04-17 08:43:50', '1', '2026-05-29 23:40:52', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_misc_receipt_line_seq;
CREATE SEQUENCE mes_wm_misc_receipt_line_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_outsource_issue
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_issue;
CREATE TABLE mes_wm_outsource_issue (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  vendor_id int8 NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  issue_date timestamp NULL DEFAULT NULL,
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_issue ADD CONSTRAINT pk_mes_wm_outsource_issue PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_outsource_issue_01 ON mes_wm_outsource_issue (code, tenant_id);

CREATE INDEX idx_mes_wm_outsource_issue_01 ON mes_wm_outsource_issue (vendor_id);
CREATE INDEX idx_mes_wm_outsource_issue_02 ON mes_wm_outsource_issue (work_order_id);
CREATE INDEX idx_mes_wm_outsource_issue_03 ON mes_wm_outsource_issue (status);
CREATE INDEX idx_mes_wm_outsource_issue_04 ON mes_wm_outsource_issue (tenant_id);

COMMENT ON COLUMN mes_wm_outsource_issue.id IS '发料单ID';
COMMENT ON COLUMN mes_wm_outsource_issue.code IS '发料单编号';
COMMENT ON COLUMN mes_wm_outsource_issue.name IS '发料单名称';
COMMENT ON COLUMN mes_wm_outsource_issue.vendor_id IS '供应商ID';
COMMENT ON COLUMN mes_wm_outsource_issue.work_order_id IS '生产工单ID';
COMMENT ON COLUMN mes_wm_outsource_issue.issue_date IS '发料日期';
COMMENT ON COLUMN mes_wm_outsource_issue.status IS '单据状态';
COMMENT ON COLUMN mes_wm_outsource_issue.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_issue.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_issue.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_issue.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_issue.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_issue.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_issue.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_issue IS 'MES 外协发料单主表';

-- ----------------------------
-- Records of mes_wm_outsource_issue
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_issue (id, code, name, vendor_id, work_order_id, issue_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'WOS202603020001', '外协发料单001-修改', 200, NULL, '1970-01-01 08:00:00', 4, '测试发料单-已修改', '1', '2026-03-02 22:14:59', '1', '2026-03-02 22:17:02', '0', 1), (2, 'WOS202603020002', '外协发料单002', 201, NULL, '1970-01-01 08:00:00', 0, '测试删除', '1', '2026-03-02 22:17:46', '1', '2026-03-02 22:17:57', '1', 1), (3, 'WOSZsffkmATpU', '呃呃', 200, NULL, '2026-03-18 00:00:00', 2, '', '1', '2026-03-03 20:35:46', '1', '2026-03-04 01:18:08', '0', 1), (4, 'WOScd6ieKFrT1', '123', 200, 123456, '2026-03-10 00:00:00', 0, '', '1', '2026-03-04 01:22:47', '1', '2026-03-04 01:27:27', '1', 1), (5, 'WOScrc6yVP8ir', '123', 200, 33, '2026-03-04 00:00:00', 3, '32131', '1', '2026-03-04 01:27:21', '1', '2026-03-04 09:48:33', '0', 1), (6, 'OSI202603310002', 'abce', 200, 1, NULL, 2, '呃呃呃', '1', '2026-03-31 22:43:14', '1', '2026-03-31 22:51:51', '0', 1), (7, 'OSI202603310003', 'ABCED', 201, 7, '2026-03-12 00:00:00', 2, '', '1', '2026-03-31 23:16:09', '1', '2026-03-31 23:16:28', '0', 1), (8, 'OSI202603310004', '啊呃呃呃呃呃呃呃呃呃呃呃呃', 201, 7, NULL, 0, '', '1', '2026-03-31 23:17:25', '1', '2026-04-17 01:35:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_issue_seq;
CREATE SEQUENCE mes_wm_outsource_issue_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_outsource_issue_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_issue_detail;
CREATE TABLE mes_wm_outsource_issue_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  issue_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_issue_detail ADD CONSTRAINT pk_mes_wm_outsource_issue_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_outsource_issue_detail_01 ON mes_wm_outsource_issue_detail (line_id);
CREATE INDEX idx_mes_wm_outsource_issue_detail_02 ON mes_wm_outsource_issue_detail (issue_id);
CREATE INDEX idx_mes_wm_outsource_issue_detail_03 ON mes_wm_outsource_issue_detail (material_stock_id);
CREATE INDEX idx_mes_wm_outsource_issue_detail_04 ON mes_wm_outsource_issue_detail (item_id);
CREATE INDEX idx_mes_wm_outsource_issue_detail_05 ON mes_wm_outsource_issue_detail (warehouse_id);
CREATE INDEX idx_mes_wm_outsource_issue_detail_06 ON mes_wm_outsource_issue_detail (tenant_id);

COMMENT ON COLUMN mes_wm_outsource_issue_detail.id IS '明细ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.line_id IS '行ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.issue_id IS '发料单ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.material_stock_id IS '库存ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.quantity IS '数量';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.location_id IS '库位ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.area_id IS '库区ID';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_issue_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_issue_detail IS 'MES 外协发料单明细表';

-- ----------------------------
-- Records of mes_wm_outsource_issue_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_issue_detail (id, line_id, issue_id, material_stock_id, item_id, quantity, batch_id, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, 69, 100.00, NULL, 701, NULL, NULL, '测试发料明细', '1', '2026-03-02 22:16:49', '1', '2026-03-02 22:16:49', '0', 1), (2, 3, 5, NULL, 69, 19.00, NULL, 702, 713, 724, '', '1', '2026-03-04 09:44:45', '1', '2026-03-04 09:47:56', '0', 1), (3, 7, 8, NULL, 70, 500.00, 5, 701, 711, 721, '', '1', '2026-03-31 23:26:19', '1', '2026-03-31 23:26:29', '0', 1), (4, 7, 8, 17, 70, 100.00, 0, 702, 711, 721, '', '1', '2026-03-31 23:40:29', '1', '2026-03-31 23:40:29', '0', 1), (5, 7, 8, 17, 70, 100.00, 0, 702, 711, 721, '', '1', '2026-03-31 23:41:15', '1', '2026-03-31 23:41:15', '0', 1), (6, 7, 8, 17, 70, 100.00, 0, 702, 711, 721, '', '1', '2026-03-31 23:41:20', '1', '2026-03-31 23:41:20', '0', 1), (7, 7, 8, 17, 70, 100.00, 0, 702, 711, 721, '', '1', '2026-03-31 23:42:58', '1', '2026-03-31 23:42:58', '0', 1), (8, 7, 8, 17, 70, 100.00, 0, 702, 711, 721, '', '1', '2026-03-31 23:44:05', '1', '2026-03-31 23:44:05', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_issue_detail_seq;
CREATE SEQUENCE mes_wm_outsource_issue_detail_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_outsource_issue_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_issue_line;
CREATE TABLE mes_wm_outsource_issue_line (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_issue_line ADD CONSTRAINT pk_mes_wm_outsource_issue_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_outsource_issue_line_01 ON mes_wm_outsource_issue_line (issue_id);
CREATE INDEX idx_mes_wm_outsource_issue_line_02 ON mes_wm_outsource_issue_line (item_id);
CREATE INDEX idx_mes_wm_outsource_issue_line_03 ON mes_wm_outsource_issue_line (tenant_id);

COMMENT ON COLUMN mes_wm_outsource_issue_line.id IS '行ID';
COMMENT ON COLUMN mes_wm_outsource_issue_line.issue_id IS '发料单ID';
COMMENT ON COLUMN mes_wm_outsource_issue_line.material_stock_id IS '库存ID';
COMMENT ON COLUMN mes_wm_outsource_issue_line.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_outsource_issue_line.quantity IS '发料数量';
COMMENT ON COLUMN mes_wm_outsource_issue_line.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_outsource_issue_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_issue_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_issue_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_issue_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_issue_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_issue_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_issue_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_issue_line IS 'MES 外协发料单行表';

-- ----------------------------
-- Records of mes_wm_outsource_issue_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_issue_line (id, issue_id, material_stock_id, item_id, quantity, batch_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, NULL, 69, 100.00, NULL, '测试发料行', '1', '2026-03-02 22:16:25', '1', '2026-03-02 22:16:25', '0', 1), (2, 3, NULL, 69, 123.00, NULL, '321321', '1', '2026-03-03 20:36:35', '1', '2026-03-03 20:36:35', '0', 1), (3, 5, NULL, 69, 19.00, NULL, '', '1', '2026-03-04 01:36:34', '1', '2026-03-04 01:36:34', '0', 1), (4, 6, NULL, 69, 1.00, NULL, '', '1', '2026-03-31 22:51:42', '1', '2026-03-31 22:51:42', '0', 1), (5, 7, NULL, 69, 1.00, NULL, '', '1', '2026-03-31 23:16:26', '1', '2026-03-31 23:16:26', '0', 1), (6, 8, NULL, 70, 1.00, NULL, '', '1', '2026-03-31 23:17:31', '1', '2026-03-31 23:17:31', '0', 1), (7, 8, NULL, 70, 2.00, NULL, '', '1', '2026-03-31 23:17:35', '1', '2026-03-31 23:17:35', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_issue_line_seq;
CREATE SEQUENCE mes_wm_outsource_issue_line_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_outsource_receipt
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_receipt;
CREATE TABLE mes_wm_outsource_receipt (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  vendor_id int8 NULL DEFAULT NULL,
  receipt_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_receipt ADD CONSTRAINT pk_mes_wm_outsource_receipt PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_outsource_receipt_01 ON mes_wm_outsource_receipt (tenant_id, code);

CREATE INDEX idx_mes_wm_outsource_receipt_01 ON mes_wm_outsource_receipt (vendor_id);
CREATE INDEX idx_mes_wm_outsource_receipt_02 ON mes_wm_outsource_receipt (work_order_id);

COMMENT ON COLUMN mes_wm_outsource_receipt.id IS '编号';
COMMENT ON COLUMN mes_wm_outsource_receipt.code IS '入库单编码';
COMMENT ON COLUMN mes_wm_outsource_receipt.name IS '入库单名称';
COMMENT ON COLUMN mes_wm_outsource_receipt.work_order_id IS '外协工单编号';
COMMENT ON COLUMN mes_wm_outsource_receipt.vendor_id IS '供应商编号';
COMMENT ON COLUMN mes_wm_outsource_receipt.receipt_date IS '入库日期';
COMMENT ON COLUMN mes_wm_outsource_receipt.status IS '状态';
COMMENT ON COLUMN mes_wm_outsource_receipt.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_receipt.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_receipt.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_receipt.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_receipt.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_receipt.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_receipt.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_receipt IS 'MES 外协入库单';

-- ----------------------------
-- Records of mes_wm_outsource_receipt
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_receipt (id, code, name, work_order_id, vendor_id, receipt_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'WXRK202603020001', '外协加工入库单-测试（已修改）', NULL, 1, '1970-01-01 08:00:00', 3, '测试修改外协入库单', '1', '2026-03-02 22:11:34', '1', '2026-03-02 22:19:58', '0', 1), (2, 'WXRK202603020002', '外协入库测试单（已修改）', NULL, 1, '1970-01-01 08:00:00', 0, '测试修改备注', '1', '2026-03-02 22:19:53', '1', '2026-03-02 22:20:11', '0', 1), (3, 'ORKmNEby6zRM', '112', NULL, 200, '2026-03-04 00:00:00', 0, '321321312', '1', '2026-03-03 20:29:34', '1', '2026-03-03 20:29:34', '0', 1), (4, 'ORtzeQxWKcWI', '呃呃呃', NULL, 200, '2026-03-10 00:00:00', 4, '32312', '1', '2026-03-03 21:08:32', '1', '2026-03-03 22:46:50', '0', 1), (5, 'ORmX7bwErplO', 'EEE', 1, 200, '2026-03-18 00:00:00', 2, '', '1', '2026-03-24 08:52:24', '1', '2026-04-17 01:39:09', '0', 1), (6, 'ORGo10IXNGtn', 'AAA', 1, 200, '2026-02-25 00:00:00', 1, '', '1', '2026-03-24 08:53:31', '1', '2026-03-24 08:58:36', '0', 1), (7, 'WWO', 'WWO', 1, 200, '2026-03-04 00:00:00', 1, '', '1', '2026-03-24 08:59:28', '1', '2026-03-24 08:59:41', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_receipt_seq;
CREATE SEQUENCE mes_wm_outsource_receipt_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_outsource_receipt_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_receipt_detail;
CREATE TABLE mes_wm_outsource_receipt_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_receipt_detail ADD CONSTRAINT pk_mes_wm_outsource_receipt_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_outsource_receipt_detail_01 ON mes_wm_outsource_receipt_detail (line_id);
CREATE INDEX idx_mes_wm_outsource_receipt_detail_02 ON mes_wm_outsource_receipt_detail (receipt_id);
CREATE INDEX idx_mes_wm_outsource_receipt_detail_03 ON mes_wm_outsource_receipt_detail (item_id);

COMMENT ON COLUMN mes_wm_outsource_receipt_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.line_id IS '入库单行编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.receipt_id IS '入库单编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.quantity IS '上架数量';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_receipt_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_receipt_detail IS 'MES 外协入库明细';

-- ----------------------------
-- Records of mes_wm_outsource_receipt_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_receipt_detail (id, line_id, receipt_id, item_id, quantity, batch_id, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, 100.00, NULL, 1, 1, 1, '测试明细数据', 'admin', '2026-03-02 14:19:30', '', '2026-03-02 14:19:30', '0', 1), (2, 2, 4, 69, 123.00, NULL, 702, 713, 724, '', '1', '2026-03-03 22:42:19', '1', '2026-03-03 22:42:19', '0', 1), (3, 3, 5, 69, 1.00, NULL, 702, 713, 724, '', '1', '2026-03-24 08:53:06', '1', '2026-03-24 08:53:06', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_receipt_detail_seq;
CREATE SEQUENCE mes_wm_outsource_receipt_detail_seq
    START 4;

-- ----------------------------
-- Table structure for mes_wm_outsource_receipt_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_outsource_receipt_line;
CREATE TABLE mes_wm_outsource_receipt_line (
    id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  production_date timestamp NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  lot_number varchar(128) NULL DEFAULT NULL,
  iqc_id int8 NULL DEFAULT NULL,
  iqc_check_flag bool NULL DEFAULT '0',
  quality_status int2 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_outsource_receipt_line ADD CONSTRAINT pk_mes_wm_outsource_receipt_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_outsource_receipt_line_01 ON mes_wm_outsource_receipt_line (receipt_id);
CREATE INDEX idx_mes_wm_outsource_receipt_line_02 ON mes_wm_outsource_receipt_line (item_id);

COMMENT ON COLUMN mes_wm_outsource_receipt_line.id IS '编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.receipt_id IS '入库单编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.batch_code IS '批次编码';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.production_date IS '生产日期';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.expire_date IS '有效期';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.lot_number IS '生产批号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.iqc_id IS '来料检验单编号';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.iqc_check_flag IS '是否需要来料检验';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.quality_status IS '质量状态';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_outsource_receipt_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_outsource_receipt_line IS 'MES 外协入库单行';

-- ----------------------------
-- Records of mes_wm_outsource_receipt_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_outsource_receipt_line (id, receipt_id, item_id, quantity, batch_id, batch_code, production_date, expire_date, lot_number, iqc_id, iqc_check_flag, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 100.00, NULL, NULL, '2026-03-02 00:00:00', '2027-03-02 00:00:00', 'BATCH001', NULL, NULL, NULL, '测试行数据', 'admin', '2026-03-02 14:19:30', '', '2026-03-02 14:19:30', '0', 1), (2, 4, 69, 1.00, NULL, NULL, '2026-03-04 00:00:00', '2026-03-12 00:00:00', '3', NULL, NULL, 1, '', '1', '2026-03-03 21:47:29', '1', '2026-03-03 21:47:48', '0', 1), (3, 5, 69, 1.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '', '1', '2026-03-24 08:52:32', '1', '2026-03-24 08:52:32', '0', 1), (4, 6, 69, 1.00, NULL, NULL, NULL, NULL, NULL, NULL, '1', 0, '', '1', '2026-03-24 08:53:37', '1', '2026-03-24 08:58:32', '0', 1), (5, 7, 69, 2.00, NULL, NULL, NULL, NULL, NULL, NULL, '1', 0, '', '1', '2026-03-24 08:59:36', '1', '2026-03-24 08:59:36', '0', 1), (6, 3, 100, 1.00, 10, 'PC202600003', '2026-04-16 00:00:00', '2026-04-17 00:00:00', NULL, NULL, '0', 1, '', '1', '2026-04-17 09:38:09', '1', '2026-04-17 09:38:09', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_outsource_receipt_line_seq;
CREATE SEQUENCE mes_wm_outsource_receipt_line_seq
    START 7;

-- ----------------------------
-- Table structure for mes_wm_package
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_package;
CREATE TABLE mes_wm_package (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  parent_id int8 NOT NULL DEFAULT 0,
  package_date timestamp NULL DEFAULT NULL,
  sales_order_code varchar(64) NULL DEFAULT NULL,
  invoice_code varchar(64) NULL DEFAULT NULL,
  client_id int8 NULL DEFAULT NULL,
  length numeric(12,2) NULL DEFAULT NULL,
  width numeric(12,2) NULL DEFAULT NULL,
  height numeric(12,2) NULL DEFAULT NULL,
  size_unit_id int8 NULL DEFAULT NULL,
  net_weight numeric(12,2) NULL DEFAULT NULL,
  gross_weight numeric(12,2) NULL DEFAULT NULL,
  weight_unit_id int8 NULL DEFAULT NULL,
  inspector_user_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_package ADD CONSTRAINT pk_mes_wm_package PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_package_01 ON mes_wm_package (code, deleted, tenant_id);

CREATE INDEX idx_mes_wm_package_01 ON mes_wm_package (parent_id);
CREATE INDEX idx_mes_wm_package_02 ON mes_wm_package (client_id);
CREATE INDEX idx_mes_wm_package_03 ON mes_wm_package (package_date);
CREATE INDEX idx_mes_wm_package_04 ON mes_wm_package (tenant_id);

COMMENT ON COLUMN mes_wm_package.id IS '装箱单 ID';
COMMENT ON COLUMN mes_wm_package.code IS '装箱单编号';
COMMENT ON COLUMN mes_wm_package.parent_id IS '父箱 ID（0 表示顶级箱）';
COMMENT ON COLUMN mes_wm_package.package_date IS '装箱日期';
COMMENT ON COLUMN mes_wm_package.sales_order_code IS '销售订单编号';
COMMENT ON COLUMN mes_wm_package.invoice_code IS '发票编号';
COMMENT ON COLUMN mes_wm_package.client_id IS '客户 ID';
COMMENT ON COLUMN mes_wm_package.length IS '箱长度';
COMMENT ON COLUMN mes_wm_package.width IS '箱宽度';
COMMENT ON COLUMN mes_wm_package.height IS '箱高度';
COMMENT ON COLUMN mes_wm_package.size_unit_id IS '尺寸单位 ID';
COMMENT ON COLUMN mes_wm_package.net_weight IS '净重';
COMMENT ON COLUMN mes_wm_package.gross_weight IS '毛重';
COMMENT ON COLUMN mes_wm_package.weight_unit_id IS '重量单位 ID';
COMMENT ON COLUMN mes_wm_package.inspector_user_id IS '检查员用户 ID';
COMMENT ON COLUMN mes_wm_package.status IS '状态';
COMMENT ON COLUMN mes_wm_package.remark IS '备注';
COMMENT ON COLUMN mes_wm_package.creator IS '创建者';
COMMENT ON COLUMN mes_wm_package.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_package.updater IS '更新者';
COMMENT ON COLUMN mes_wm_package.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_package.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_package.tenant_id IS '租户 ID';
COMMENT ON TABLE mes_wm_package IS 'MES - 装箱单';

-- ----------------------------
-- Records of mes_wm_package
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_package (id, code, parent_id, package_date, sales_order_code, invoice_code, client_id, length, width, height, size_unit_id, net_weight, gross_weight, weight_unit_id, inspector_user_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PKGl4uxKEk1ck', 3, '2026-03-10 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, '1', '2026-03-08 10:53:45', '1', '2026-03-08 11:58:39', '0', 1), (2, 'PKGwDvyH9z962', 0, '2026-03-04 00:00:00', NULL, NULL, 207, NULL, NULL, NULL, 200, NULL, NULL, 200, NULL, 0, NULL, '1', '2026-03-08 10:54:00', '1', '2026-04-06 00:52:20', '0', 1), (3, 'PKGSWNYn4Nbpm', 2, '2026-03-18 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, '1', '2026-03-08 11:57:57', '1', '2026-03-08 12:18:55', '0', 1), (4, 'PKG202603080007', 0, '2026-03-20 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-03-08 13:04:38', '1', '2026-03-08 13:04:38', '0', 1), (5, 'PKG202603310001', 4, '2026-03-12 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 4, NULL, '1', '2026-03-31 20:06:23', '1', '2026-04-07 09:48:27', '0', 1), (6, 'PKG202604060001', 0, '2026-04-14 00:00:00', NULL, NULL, 207, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-04-06 00:52:34', '1', '2026-04-06 00:52:40', '0', 1), (7, 'PKG202604060002', 0, '2026-04-14 00:00:00', NULL, NULL, 207, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-04-06 01:07:52', '1', '2026-04-06 01:07:59', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_package_seq;
CREATE SEQUENCE mes_wm_package_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_package_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_package_line;
CREATE TABLE mes_wm_package_line (
    id int8 NOT NULL,
  package_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  expire_date date NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_package_line ADD CONSTRAINT pk_mes_wm_package_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_package_line_01 ON mes_wm_package_line (package_id);
CREATE INDEX idx_mes_wm_package_line_02 ON mes_wm_package_line (item_id);
CREATE INDEX idx_mes_wm_package_line_03 ON mes_wm_package_line (work_order_id);
CREATE INDEX idx_mes_wm_package_line_04 ON mes_wm_package_line (tenant_id);

COMMENT ON COLUMN mes_wm_package_line.id IS '明细行 ID';
COMMENT ON COLUMN mes_wm_package_line.package_id IS '装箱单 ID';
COMMENT ON COLUMN mes_wm_package_line.material_stock_id IS '库存记录 ID';
COMMENT ON COLUMN mes_wm_package_line.item_id IS '产品物料 ID';
COMMENT ON COLUMN mes_wm_package_line.quantity IS '装箱数量';
COMMENT ON COLUMN mes_wm_package_line.work_order_id IS '生产工单 ID';
COMMENT ON COLUMN mes_wm_package_line.expire_date IS '有效期';
COMMENT ON COLUMN mes_wm_package_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_package_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_package_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_package_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_package_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_package_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_package_line.tenant_id IS '租户 ID';
COMMENT ON TABLE mes_wm_package_line IS 'MES - 装箱明细';

-- ----------------------------
-- Records of mes_wm_package_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_package_line (id, package_id, material_stock_id, item_id, quantity, work_order_id, expire_date, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 2, NULL, 94, 123.00, 1, '2026-03-12', '3232', '1', '2026-03-08 12:21:54', '1', '2026-03-08 12:21:54', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_package_line_seq;
CREATE SEQUENCE mes_wm_package_line_seq
    START 2;

-- ----------------------------
-- Table structure for mes_wm_product_issue
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_issue;
CREATE TABLE mes_wm_product_issue (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  task_id int8 NULL DEFAULT NULL,
  issue_date timestamp NULL DEFAULT NULL,
  required_time timestamp NULL DEFAULT NULL,
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_issue ADD CONSTRAINT pk_mes_wm_product_issue PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_issue_01 ON mes_wm_product_issue (code);
CREATE INDEX idx_mes_wm_product_issue_02 ON mes_wm_product_issue (workstation_id);
CREATE INDEX idx_mes_wm_product_issue_03 ON mes_wm_product_issue (work_order_id);
CREATE INDEX idx_mes_wm_product_issue_04 ON mes_wm_product_issue (status);
CREATE INDEX idx_mes_wm_product_issue_05 ON mes_wm_product_issue (tenant_id);

COMMENT ON COLUMN mes_wm_product_issue.id IS '编号';
COMMENT ON COLUMN mes_wm_product_issue.code IS '领料单编号';
COMMENT ON COLUMN mes_wm_product_issue.name IS '领料单名称';
COMMENT ON COLUMN mes_wm_product_issue.workstation_id IS '工作站ID';
COMMENT ON COLUMN mes_wm_product_issue.work_order_id IS '生产工单ID';
COMMENT ON COLUMN mes_wm_product_issue.task_id IS '生产任务 ID';
COMMENT ON COLUMN mes_wm_product_issue.issue_date IS '领料日期';
COMMENT ON COLUMN mes_wm_product_issue.required_time IS '需求时间';
COMMENT ON COLUMN mes_wm_product_issue.status IS '单据状态';
COMMENT ON COLUMN mes_wm_product_issue.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_issue.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_issue.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_issue.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_issue.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_issue.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_issue.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_issue IS 'MES 领料出库单主表';

-- ----------------------------
-- Records of mes_wm_product_issue
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_issue (id, code, name, workstation_id, work_order_id, task_id, issue_date, required_time, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'ISSUE-2026-0001', '工单WO-001领料单', 1, 1, NULL, '2026-02-26 10:00:00', '2026-02-26 14:00:00', 0, '测试领料单1', '1', '2026-02-26 16:43:29', '1', '2026-02-27 23:13:45', '1', 1), (2, 'ISSUE-2026-0002', '工单WO-002领料单', 2, 2, NULL, '2026-02-26 11:00:00', '2026-02-26 15:00:00', 3, '测试领料单2', '1', '2026-02-26 16:43:29', '1', '2026-03-30 10:59:22', '0', 1), (3, 'ISSUE-2026-0003', '工单WO-003领料单', 1, 3, NULL, '2026-02-26 12:00:00', '2026-02-26 16:00:00', 3, '测试领料单3', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (4, 'ISSUE-2026-0004', '工单WO-004领料单', 3, 4, NULL, '2026-02-26 13:00:00', '2026-02-26 17:00:00', 4, '测试领料单4', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (5, 'PIvQ6NaiEFGL', '112', 2, 1, NULL, NULL, '1970-01-01 08:00:00', 5, '', '1', '2026-02-27 20:07:49', '1', '2026-02-27 23:52:52', '0', 1), (6, 'PIKp8EIm2d3A', '132', NULL, 1, NULL, NULL, '1970-01-01 08:00:00', 3, '32321', '1', '2026-02-28 00:50:45', '1', '2026-02-28 00:51:44', '0', 1), (7, 'PIF6HVZGjbf9', '111', 4, 1, NULL, NULL, '1970-01-01 08:00:00', 3, '', '1', '2026-02-28 01:14:36', '1', '2026-03-30 10:59:25', '0', 1), (8, 'PI20260330000002', '0303', 1, 1, NULL, '2026-03-30 11:11:50', '1970-01-01 08:00:00', 4, '', '1', '2026-03-30 11:02:15', '1', '2026-03-30 11:11:50', '0', 1), (9, 'PI20260416000002', 'Test Picking', 1, 8, NULL, NULL, '2026-04-01 00:00:00', 2, '', '1', '2026-04-16 01:51:57', '1', '2026-04-16 01:58:38', '0', 1), (10, 'PI20260417000001', 'abc', 6, 8, NULL, NULL, '2026-04-16 00:00:00', 0, '', '1', '2026-04-17 15:27:44', '1', '2026-04-17 15:27:44', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_issue_seq;
CREATE SEQUENCE mes_wm_product_issue_seq
    START 11;

-- ----------------------------
-- Table structure for mes_wm_product_issue_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_issue_detail;
CREATE TABLE mes_wm_product_issue_detail (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  line_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_issue_detail ADD CONSTRAINT pk_mes_wm_product_issue_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_issue_detail_01 ON mes_wm_product_issue_detail (issue_id);
CREATE INDEX idx_mes_wm_product_issue_detail_02 ON mes_wm_product_issue_detail (line_id);
CREATE INDEX idx_mes_wm_product_issue_detail_03 ON mes_wm_product_issue_detail (item_id);
CREATE INDEX idx_mes_wm_product_issue_detail_04 ON mes_wm_product_issue_detail (warehouse_id);
CREATE INDEX idx_mes_wm_product_issue_detail_05 ON mes_wm_product_issue_detail (tenant_id);

COMMENT ON COLUMN mes_wm_product_issue_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_product_issue_detail.issue_id IS '领料单ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.line_id IS '行ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.material_stock_id IS '库存记录ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.quantity IS '领料数量';
COMMENT ON COLUMN mes_wm_product_issue_detail.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_issue_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.location_id IS '库区ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.area_id IS '库位ID';
COMMENT ON COLUMN mes_wm_product_issue_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_issue_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_issue_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_issue_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_issue_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_issue_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_issue_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_issue_detail IS 'MES 领料出库明细表';

-- ----------------------------
-- Records of mes_wm_product_issue_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_issue_detail (id, issue_id, line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 6, 5001, 106, 60.00, 1006, NULL, 1, 101, 1001, '仓库A-库区1-库位1', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (2, 3, 6, 5002, 106, 60.00, 1006, NULL, 1, 101, 1002, '仓库A-库区1-库位2', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (3, 3, 7, 5003, 107, 90.00, 1007, NULL, 1, 102, 1003, '仓库A-库区2-库位3', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (4, 3, 8, 5004, 108, 60.00, 1008, NULL, 2, 201, 2001, '仓库B-库区1-库位1', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (5, 4, 9, 5005, 109, 100.00, 1009, NULL, 1, 101, 1001, '仓库A-库区1-库位1', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (6, 4, 9, 5006, 109, 80.00, 1009, NULL, 1, 101, 1002, '仓库A-库区1-库位2', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (7, 4, 10, 5007, 110, 75.00, 1010, NULL, 2, 201, 2001, '仓库B-库区1-库位1', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (8, 5, 11, NULL, 70, 12.00, NULL, '123', 702, 713, 724, '', '1', '2026-02-27 23:14:31', '1', '2026-02-27 23:45:04', '0', 1), (9, 5, 11, NULL, 70, 10.00, NULL, NULL, 702, 713, 724, '', '1', '2026-02-27 23:15:03', '1', '2026-02-27 23:15:03', '0', 1), (10, 7, 13, NULL, 94, 1.00, NULL, NULL, 701, 712, 723, '', '1', '2026-03-30 10:28:02', '1', '2026-03-30 10:28:02', '0', 1), (11, 7, 13, NULL, 94, 1.00, 7, 'BATCH_ITEM_94', 701, 712, 723, '', '1', '2026-03-30 10:47:49', '1', '2026-03-30 10:47:49', '0', 1), (12, 7, 13, NULL, 94, 1.00, 7, 'BATCH_ITEM_94', 701, 712, 723, '', '1', '2026-03-30 10:47:57', '1', '2026-03-30 10:47:57', '0', 1), (13, 8, 14, NULL, 94, 1.00, 7, 'BATCH_ITEM_94', 701, 712, 723, '', '1', '2026-03-30 11:02:31', '1', '2026-03-30 11:02:31', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_issue_detail_seq;
CREATE SEQUENCE mes_wm_product_issue_detail_seq
    START 14;

-- ----------------------------
-- Table structure for mes_wm_product_issue_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_issue_line;
CREATE TABLE mes_wm_product_issue_line (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_issue_line ADD CONSTRAINT pk_mes_wm_product_issue_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_issue_line_01 ON mes_wm_product_issue_line (issue_id);
CREATE INDEX idx_mes_wm_product_issue_line_02 ON mes_wm_product_issue_line (item_id);
CREATE INDEX idx_mes_wm_product_issue_line_03 ON mes_wm_product_issue_line (tenant_id);

COMMENT ON COLUMN mes_wm_product_issue_line.id IS '编号';
COMMENT ON COLUMN mes_wm_product_issue_line.issue_id IS '领料单ID';
COMMENT ON COLUMN mes_wm_product_issue_line.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_issue_line.quantity IS '领料数量';
COMMENT ON COLUMN mes_wm_product_issue_line.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_issue_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_issue_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_issue_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_issue_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_issue_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_issue_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_issue_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_issue_line IS 'MES 领料出库单行表';

-- ----------------------------
-- Records of mes_wm_product_issue_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_issue_line (id, issue_id, item_id, quantity, batch_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 101, 100.00, NULL, '物料A', '1', '2026-02-26 16:43:29', '1', '2026-02-27 15:13:44', '1', 1), (2, 1, 102, 50.00, NULL, '物料B', '1', '2026-02-26 16:43:29', '1', '2026-02-27 15:13:44', '1', 1), (3, 1, 103, 200.00, NULL, '物料C', '1', '2026-02-26 16:43:29', '1', '2026-02-27 15:13:44', '1', 1), (4, 2, 104, 150.00, NULL, '物料D', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (5, 2, 105, 80.00, NULL, '物料E', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (6, 3, 106, 120.00, NULL, '物料F', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (7, 3, 107, 90.00, NULL, '物料G', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (8, 3, 108, 60.00, NULL, '物料H', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (9, 4, 109, 180.00, NULL, '物料I', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (10, 4, 110, 75.00, NULL, '物料J', '1', '2026-02-26 16:43:29', '1', '2026-02-26 16:43:29', '0', 1), (11, 5, 70, 10.00, NULL, '222', '1', '2026-02-27 23:05:18', '1', '2026-02-27 23:05:18', '0', 1), (12, 6, 70, 3.00, NULL, '', '1', '2026-02-28 00:50:50', '1', '2026-02-28 00:50:50', '0', 1), (13, 7, 94, 1.00, NULL, '', '1', '2026-03-30 10:00:38', '1', '2026-03-30 10:00:38', '0', 1), (14, 8, 94, 1.00, NULL, '', '1', '2026-03-30 11:02:23', '1', '2026-03-30 11:02:23', '0', 1), (15, 9, 103, 10.00, NULL, '', '1', '2026-04-16 01:54:31', '1', '2026-04-16 01:54:31', '0', 1), (16, 10, 103, 123.00, NULL, '', '1', '2026-04-17 15:27:51', '1', '2026-04-17 15:27:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_issue_line_seq;
CREATE SEQUENCE mes_wm_product_issue_line_seq
    START 17;

-- ----------------------------
-- Table structure for mes_wm_product_produce
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_produce;
CREATE TABLE mes_wm_product_produce (
    id int8 NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  feedback_id int8 NULL DEFAULT NULL,
  task_id int8 NULL DEFAULT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  process_id int8 NULL DEFAULT NULL,
  produce_date timestamp NULL DEFAULT NULL,
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_produce ADD CONSTRAINT pk_mes_wm_product_produce PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_produce_01 ON mes_wm_product_produce (work_order_id);
CREATE INDEX idx_mes_wm_product_produce_02 ON mes_wm_product_produce (workstation_id);
CREATE INDEX idx_mes_wm_product_produce_03 ON mes_wm_product_produce (status);
CREATE INDEX idx_mes_wm_product_produce_04 ON mes_wm_product_produce (tenant_id);

COMMENT ON COLUMN mes_wm_product_produce.id IS '编号';
COMMENT ON COLUMN mes_wm_product_produce.work_order_id IS '生产工单 ID';
COMMENT ON COLUMN mes_wm_product_produce.feedback_id IS '报工记录 ID';
COMMENT ON COLUMN mes_wm_product_produce.task_id IS '生产任务 ID';
COMMENT ON COLUMN mes_wm_product_produce.workstation_id IS '工作站 ID';
COMMENT ON COLUMN mes_wm_product_produce.process_id IS '工序 ID';
COMMENT ON COLUMN mes_wm_product_produce.produce_date IS '生产日期';
COMMENT ON COLUMN mes_wm_product_produce.status IS '状态';
COMMENT ON COLUMN mes_wm_product_produce.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_produce.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_produce.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_produce.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_produce.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_produce.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_produce.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_produce IS 'MES 生产入库单主表';

-- ----------------------------
-- Records of mes_wm_product_produce
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_produce (id, work_order_id, feedback_id, task_id, workstation_id, process_id, produce_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (17, 1, 5, 4, 3, 3, '2026-03-21 15:07:46', 4, '', '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (18, 1, 6, 4, 3, 3, '2026-03-21 15:09:48', 4, '', '1', '2026-03-21 15:09:48', '1', '2026-03-21 15:09:48', '0', 1), (19, 1, 8, 4, 3, 3, '2026-03-24 23:17:24', 4, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (20, 17, 12, 8, 8, 4, '2026-05-26 00:33:47', 0, '验收造数：待检验产出单', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (21, 17, 13, 7, 7, 1, '2026-05-26 00:33:47', 4, '验收造数：已完成产出单', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_produce_seq;
CREATE SEQUENCE mes_wm_product_produce_seq
    START 22;

-- ----------------------------
-- Table structure for mes_wm_product_produce_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_produce_detail;
CREATE TABLE mes_wm_product_produce_detail (
    id int8 NOT NULL,
  produce_id int8 NOT NULL,
  line_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_produce_detail ADD CONSTRAINT pk_mes_wm_product_produce_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_produce_detail_01 ON mes_wm_product_produce_detail (produce_id);
CREATE INDEX idx_mes_wm_product_produce_detail_02 ON mes_wm_product_produce_detail (line_id);
CREATE INDEX idx_mes_wm_product_produce_detail_03 ON mes_wm_product_produce_detail (item_id);
CREATE INDEX idx_mes_wm_product_produce_detail_04 ON mes_wm_product_produce_detail (warehouse_id);
CREATE INDEX idx_mes_wm_product_produce_detail_05 ON mes_wm_product_produce_detail (tenant_id);

COMMENT ON COLUMN mes_wm_product_produce_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_product_produce_detail.produce_id IS '入库单 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.line_id IS '行 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_product_produce_detail.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_produce_detail.warehouse_id IS '仓库 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.location_id IS '库区 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.area_id IS '库位 ID';
COMMENT ON COLUMN mes_wm_product_produce_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_produce_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_produce_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_produce_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_produce_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_produce_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_produce_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_produce_detail IS 'MES 生产入库明细表';

-- ----------------------------
-- Records of mes_wm_product_produce_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_produce_detail (id, produce_id, line_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 17, 1, 75, 1.00, 3, 'PC202600001', NULL, NULL, NULL, '', '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (2, 18, 2, 75, 1.00, 3, 'PC202600001', NULL, NULL, NULL, '', '1', '2026-03-21 15:09:48', '1', '2026-03-21 15:09:48', '0', 1), (3, 19, 3, 75, 1.00, 4, 'PC202600002', 703, 714, 725, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (4, 19, 4, 75, 1.00, 4, 'PC202600002', 703, 714, 725, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_produce_detail_seq;
CREATE SEQUENCE mes_wm_product_produce_detail_seq
    START 5;

-- ----------------------------
-- Table structure for mes_wm_product_produce_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_produce_line;
CREATE TABLE mes_wm_product_produce_line (
    id int8 NOT NULL,
  produce_id int8 NOT NULL,
  feedback_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  expire_date timestamp NULL DEFAULT NULL,
  lot_number varchar(128) NULL DEFAULT NULL,
  quality_status int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_produce_line ADD CONSTRAINT pk_mes_wm_product_produce_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_produce_line_01 ON mes_wm_product_produce_line (produce_id);
CREATE INDEX idx_mes_wm_product_produce_line_02 ON mes_wm_product_produce_line (item_id);
CREATE INDEX idx_mes_wm_product_produce_line_03 ON mes_wm_product_produce_line (tenant_id);

COMMENT ON COLUMN mes_wm_product_produce_line.id IS '编号';
COMMENT ON COLUMN mes_wm_product_produce_line.produce_id IS '入库单 ID';
COMMENT ON COLUMN mes_wm_product_produce_line.feedback_id IS '报工记录 ID';
COMMENT ON COLUMN mes_wm_product_produce_line.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_product_produce_line.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_product_produce_line.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_product_produce_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_produce_line.expire_date IS '过期日期';
COMMENT ON COLUMN mes_wm_product_produce_line.lot_number IS '生产批号';
COMMENT ON COLUMN mes_wm_product_produce_line.quality_status IS '质量状态';
COMMENT ON COLUMN mes_wm_product_produce_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_produce_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_produce_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_produce_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_produce_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_produce_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_produce_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_produce_line IS 'MES 生产入库单行表';

-- ----------------------------
-- Records of mes_wm_product_produce_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_produce_line (id, produce_id, feedback_id, item_id, quantity, batch_id, batch_code, expire_date, lot_number, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 17, 5, 75, 1.00, 3, 'PC202600001', NULL, NULL, 1, '', '1', '2026-03-21 15:07:46', '1', '2026-03-21 15:07:46', '0', 1), (2, 18, 6, 75, 1.00, 3, 'PC202600001', NULL, NULL, 2, '', '1', '2026-03-21 15:09:48', '1', '2026-03-21 15:09:48', '0', 1), (3, 19, 8, 75, 1.00, 4, 'PC202600002', NULL, NULL, 2, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (4, 19, 8, 75, 1.00, 4, 'PC202600002', NULL, NULL, 1, '', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (5, 20, 12, 75, 15.00, NULL, 'PC-ACCEPT-UNCHECK', '2026-11-22 00:00:00', 'LOT-FB-UNCHECK', 0, '验收造数：待检验产出行', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (6, 21, 13, 75, 18.00, NULL, 'PC-ACCEPT-PASS', '2026-11-22 00:00:00', 'LOT-FB-FINISHED', 1, '验收造数：合格产出行', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1), (7, 21, 13, 75, 2.00, NULL, 'PC-ACCEPT-FAIL', '2026-11-22 00:00:00', 'LOT-FB-FINISHED', 2, '验收造数：不合格产出行', '1', '2026-05-26 00:33:47', '1', '2026-05-26 00:33:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_produce_line_seq;
CREATE SEQUENCE mes_wm_product_produce_line_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_product_receipt
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_receipt;
CREATE TABLE mes_wm_product_receipt (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  item_id int8 NULL DEFAULT NULL,
  receipt_date timestamp NULL DEFAULT NULL,
  status int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_receipt ADD CONSTRAINT pk_mes_wm_product_receipt PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_receipt_01 ON mes_wm_product_receipt (code);
CREATE INDEX idx_mes_wm_product_receipt_02 ON mes_wm_product_receipt (work_order_id);
CREATE INDEX idx_mes_wm_product_receipt_03 ON mes_wm_product_receipt (item_id);
CREATE INDEX idx_mes_wm_product_receipt_04 ON mes_wm_product_receipt (status);
CREATE INDEX idx_mes_wm_product_receipt_05 ON mes_wm_product_receipt (tenant_id);

COMMENT ON COLUMN mes_wm_product_receipt.id IS '入库单ID';
COMMENT ON COLUMN mes_wm_product_receipt.code IS '入库单编码';
COMMENT ON COLUMN mes_wm_product_receipt.name IS '入库单名称';
COMMENT ON COLUMN mes_wm_product_receipt.work_order_id IS '生产工单ID';
COMMENT ON COLUMN mes_wm_product_receipt.item_id IS '产品物料ID';
COMMENT ON COLUMN mes_wm_product_receipt.receipt_date IS '入库日期';
COMMENT ON COLUMN mes_wm_product_receipt.status IS '状态（0草稿 2待上架 3待执行入库 4已完成 5已取消）';
COMMENT ON COLUMN mes_wm_product_receipt.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_receipt.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_receipt.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_receipt.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_receipt.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_receipt.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_receipt.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_receipt IS '产品入库单';

-- ----------------------------
-- Records of mes_wm_product_receipt
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_receipt (id, code, name, work_order_id, item_id, receipt_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PR202603010001', '产品入库单-工单001', 1001, 2001, '2026-03-01 10:00:00', 0, '测试入库单1', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (2, 'PR202603010002', '产品入库单-工单002', 1002, 2002, '2026-03-01 11:00:00', 2, '测试入库单2', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (3, 'PR202603010003', '产品入库单-工单003', 1003, 2003, '2026-03-01 12:00:00', 4, '测试入库单3', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (4, 'PRWZbODiG5NB', '3231', 6, NULL, '2026-03-10 00:00:00', 4, '', '1', '2026-03-01 12:33:14', '1', '2026-03-01 13:12:40', '0', 1), (5, 'PRyhCGt9TzjO', '呃呃呃', 1, 75, '2026-03-11 00:00:00', 3, '', '1', '2026-03-01 13:13:12', '1', '2026-03-01 13:13:29', '0', 1), (6, 'PRF0d2CB7sar', 'aaa', 2, 73, '2026-03-11 00:00:00', 4, '', '1', '2026-03-01 13:13:53', '1', '2026-03-01 13:15:00', '0', 1), (7, 'PRoDYEoZY1BK', 'eee', 1, 75, '2026-03-11 00:00:00', 2, '', '1', '2026-03-01 13:15:59', '1', '2026-03-01 13:16:08', '0', 1), (8, 'PR20260330000001', 'AAAEEE', 1, 75, '2026-02-24 00:00:00', 3, '', '1', '2026-03-30 15:17:24', '1', '2026-03-30 15:27:44', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_receipt_seq;
CREATE SEQUENCE mes_wm_product_receipt_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_product_receipt_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_receipt_detail;
CREATE TABLE mes_wm_product_receipt_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_receipt_detail ADD CONSTRAINT pk_mes_wm_product_receipt_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_receipt_detail_01 ON mes_wm_product_receipt_detail (line_id);
CREATE INDEX idx_mes_wm_product_receipt_detail_02 ON mes_wm_product_receipt_detail (receipt_id);
CREATE INDEX idx_mes_wm_product_receipt_detail_03 ON mes_wm_product_receipt_detail (item_id);
CREATE INDEX idx_mes_wm_product_receipt_detail_04 ON mes_wm_product_receipt_detail (batch_id);
CREATE INDEX idx_mes_wm_product_receipt_detail_05 ON mes_wm_product_receipt_detail (warehouse_id);
CREATE INDEX idx_mes_wm_product_receipt_detail_06 ON mes_wm_product_receipt_detail (tenant_id);

COMMENT ON COLUMN mes_wm_product_receipt_detail.id IS '明细ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.line_id IS '行ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.receipt_id IS '入库单ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.quantity IS '上架数量';
COMMENT ON COLUMN mes_wm_product_receipt_detail.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.location_id IS '库区ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.area_id IS '库位ID';
COMMENT ON COLUMN mes_wm_product_receipt_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_receipt_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_receipt_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_receipt_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_receipt_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_receipt_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_receipt_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_receipt_detail IS '产品入库单明细';

-- ----------------------------
-- Records of mes_wm_product_receipt_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_receipt_detail (id, line_id, receipt_id, item_id, quantity, batch_id, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 3, 2, 2002, 100.00, 3003, 4001, 5001, 6001, '明细1', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (2, 3, 2, 2002, 100.00, 3003, 4001, 5002, 6002, '明细2', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (3, 4, 2, 2003, 150.00, 3004, 4002, 5003, 6003, '明细3', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (4, 5, 3, 2003, 300.00, 3005, 4003, 5004, 6004, '明细4', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (5, 6, 4, 69, 123.00, NULL, 702, 713, 724, '', '1', '2026-03-01 12:46:01', '1', '2026-03-01 12:46:01', '0', 1), (6, 6, 4, 69, 22.00, NULL, 702, 713, 724, '', '1', '2026-03-01 12:46:26', '1', '2026-03-01 12:46:26', '0', 1), (7, 6, 4, 69, 123.00, NULL, 702, 713, 724, '', '1', '2026-03-01 13:00:36', '1', '2026-03-01 13:00:36', '0', 1), (8, 10, 8, 69, 1.00, NULL, 703, 714, 725, '', '1', '2026-03-30 15:27:41', '1', '2026-03-30 15:27:41', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_receipt_detail_seq;
CREATE SEQUENCE mes_wm_product_receipt_detail_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_product_receipt_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_receipt_line;
CREATE TABLE mes_wm_product_receipt_line (
    id int8 NOT NULL,
  receipt_id int8 NOT NULL,
  item_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_receipt_line ADD CONSTRAINT pk_mes_wm_product_receipt_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_receipt_line_01 ON mes_wm_product_receipt_line (receipt_id);
CREATE INDEX idx_mes_wm_product_receipt_line_02 ON mes_wm_product_receipt_line (item_id);
CREATE INDEX idx_mes_wm_product_receipt_line_03 ON mes_wm_product_receipt_line (batch_id);
CREATE INDEX idx_mes_wm_product_receipt_line_04 ON mes_wm_product_receipt_line (tenant_id);

COMMENT ON COLUMN mes_wm_product_receipt_line.id IS '行ID';
COMMENT ON COLUMN mes_wm_product_receipt_line.receipt_id IS '入库单ID';
COMMENT ON COLUMN mes_wm_product_receipt_line.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_receipt_line.material_stock_id IS '库存物资记录编号';
COMMENT ON COLUMN mes_wm_product_receipt_line.quantity IS '入库数量';
COMMENT ON COLUMN mes_wm_product_receipt_line.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_receipt_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_receipt_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_receipt_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_receipt_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_receipt_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_receipt_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_receipt_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_receipt_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_receipt_line IS '产品入库单行';

-- ----------------------------
-- Records of mes_wm_product_receipt_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_receipt_line (id, receipt_id, item_id, material_stock_id, quantity, batch_id, batch_code, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 2001, NULL, 100.00, 3001, 'BATCH20260301001', '行1', '1', '2026-03-01 04:15:25', '1', '2026-03-30 15:48:28', '1', 1), (2, 1, 94, NULL, 1.00, 7, 'BATCH_ITEM_94', '行2', '1', '2026-03-01 04:15:25', '1', '2026-03-30 15:48:29', '1', 1), (3, 2, 2002, NULL, 200.00, 3003, 'BATCH20260301003', '行3', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (4, 2, 2003, NULL, 150.00, 3004, 'BATCH20260301004', '行4', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (5, 3, 2003, NULL, 300.00, 3005, 'BATCH20260301005', '行5', '1', '2026-03-01 04:15:25', '1', '2026-03-01 04:15:25', '0', 1), (6, 4, 69, NULL, 22.00, NULL, NULL, '', '1', '2026-03-01 12:42:04', '1', '2026-03-01 12:42:04', '0', 1), (7, 5, 69, NULL, 123.00, NULL, NULL, '', '1', '2026-03-01 13:13:18', '1', '2026-03-01 13:13:18', '0', 1), (8, 6, 69, NULL, 123.00, NULL, NULL, '', '1', '2026-03-01 13:13:59', '1', '2026-03-01 13:13:59', '0', 1), (9, 7, 69, NULL, 123.00, NULL, NULL, '', '1', '2026-03-01 13:16:03', '1', '2026-03-01 13:16:03', '0', 1), (10, 8, 69, NULL, 1.00, NULL, 'ABCE', '', '1', '2026-03-30 15:27:16', '1', '2026-03-30 15:27:16', '0', 1), (11, 1, 94, 9, 123.00, 7, 'BATCH_ITEM_94', '123', '1', '2026-03-30 15:48:40', '1', '2026-03-30 15:48:40', '0', 1), (12, 1, 94, 9, 123.00, 7, 'BATCH_ITEM_94', '啊啊啊啊', '1', '2026-03-30 15:50:18', '1', '2026-03-30 15:50:18', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_receipt_line_seq;
CREATE SEQUENCE mes_wm_product_receipt_line_seq
    START 13;

-- ----------------------------
-- Table structure for mes_wm_product_sales
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_sales;
CREATE TABLE mes_wm_product_sales (
    id int8 NOT NULL,
  code varchar(50) NOT NULL,
  name varchar(100) NOT NULL,
  client_id int8 NOT NULL,
  sales_order_code varchar(50) NULL DEFAULT NULL,
  sales_date timestamp NOT NULL,
  notice_id int8 NULL DEFAULT NULL,
  contact_name varchar(50) NULL DEFAULT NULL,
  contact_telephone varchar(20) NULL DEFAULT NULL,
  contact_address varchar(500) NULL DEFAULT NULL,
  carrier varchar(100) NULL DEFAULT NULL,
  shipping_number varchar(50) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_sales ADD CONSTRAINT pk_mes_wm_product_sales PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_product_sales_01 ON mes_wm_product_sales (code, deleted, tenant_id);

CREATE INDEX idx_mes_wm_product_sales_01 ON mes_wm_product_sales (client_id);
CREATE INDEX idx_mes_wm_product_sales_02 ON mes_wm_product_sales (notice_id);
CREATE INDEX idx_mes_wm_product_sales_03 ON mes_wm_product_sales (status);
CREATE INDEX idx_mes_wm_product_sales_04 ON mes_wm_product_sales (sales_date);
CREATE INDEX idx_mes_wm_product_sales_05 ON mes_wm_product_sales (create_time);

COMMENT ON COLUMN mes_wm_product_sales.id IS '编号';
COMMENT ON COLUMN mes_wm_product_sales.code IS '出库单号';
COMMENT ON COLUMN mes_wm_product_sales.name IS '出库单名称';
COMMENT ON COLUMN mes_wm_product_sales.client_id IS '客户ID';
COMMENT ON COLUMN mes_wm_product_sales.sales_order_code IS '销售订单号';
COMMENT ON COLUMN mes_wm_product_sales.sales_date IS '出库日期';
COMMENT ON COLUMN mes_wm_product_sales.notice_id IS '发货通知单ID';
COMMENT ON COLUMN mes_wm_product_sales.contact_name IS '收货人';
COMMENT ON COLUMN mes_wm_product_sales.contact_telephone IS '联系方式';
COMMENT ON COLUMN mes_wm_product_sales.contact_address IS '收货地址';
COMMENT ON COLUMN mes_wm_product_sales.carrier IS '承运商';
COMMENT ON COLUMN mes_wm_product_sales.shipping_number IS '运输单号';
COMMENT ON COLUMN mes_wm_product_sales.status IS '状态';
COMMENT ON COLUMN mes_wm_product_sales.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_sales.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_sales.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_sales.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_sales.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_sales.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_sales.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_sales IS 'MES 销售出库单';

-- ----------------------------
-- Records of mes_wm_product_sales
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_sales (id, code, name, client_id, sales_order_code, sales_date, notice_id, contact_name, contact_telephone, contact_address, carrier, shipping_number, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PS202603020001', '更新后的销售出库单', 200, 'SO202603020001', '1970-01-01 08:00:00', 1, '李四', '13900139000', NULL, '中通快递', 'ZTO9876543210', 0, '测试更新销售出库单', '1', '2026-03-02 17:12:07', '1', '2026-03-02 17:12:48', '1', 1), (2, 'PSe6mfNzWE7n', '3313213', 207, NULL, '2026-03-11 00:00:00', 2, 'xx', 'ee', NULL, NULL, NULL, 5, NULL, '1', '2026-03-02 17:15:00', '1', '2026-03-02 18:46:19', '0', 1), (3, 'PSatKKrWp6sH', '呃呃呃', 207, NULL, '2026-03-11 00:00:00', NULL, NULL, NULL, NULL, '555', '3333', 3, NULL, '1', '2026-03-02 18:46:50', '1', '2026-04-17 01:54:29', '0', 1), (99999, 'SALES-TEST-OQC', 'OQC出库单测试', 200, NULL, '2026-03-27 10:02:54', NULL, NULL, NULL, NULL, NULL, NULL, 3, NULL, '', '2026-03-27 10:02:54', '', '2026-03-27 10:02:54', '0', 1), (100000, 'PS20260330000003', 'AAAA', 200, 'SO202603020001', '2026-03-31 00:00:00', 1, '张三', '13800138000', '北京市朝阳区测试地址', NULL, NULL, 0, NULL, '1', '2026-03-30 20:58:50', '1', '2026-03-30 20:58:50', '0', 1), (100001, 'PS20260330000004', 'ABCD', 200, NULL, '2026-03-11 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, 2, NULL, '1', '2026-03-30 21:05:31', '1', '2026-03-30 21:08:14', '0', 1), (100002, 'PS20260330000005', 'ABC', 207, NULL, '2026-03-17 00:00:00', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '1', '2026-03-30 21:09:02', '1', '2026-03-30 21:09:02', '0', 1), (100003, 'A0', 'A0', 207, NULL, '2026-03-18 00:00:00', NULL, NULL, NULL, NULL, 'AAABBB', 'EEE', 10, NULL, '1', '2026-03-30 21:10:54', '1', '2026-04-17 01:54:54', '0', 1), (100004, 'PS-20260412-001', 'ä¸´æ—¶é”€å”®å‡ºåº“å•çš„æ¨¡æ‹Ÿæ•°æ®', 1, 'SO-123456', '2026-04-11 18:28:00', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, '', '2026-04-11 18:28:00', '1', '2026-04-17 09:50:07', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_sales_seq;
CREATE SEQUENCE mes_wm_product_sales_seq
    START 100005;

-- ----------------------------
-- Table structure for mes_wm_product_sales_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_sales_detail;
CREATE TABLE mes_wm_product_sales_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  sales_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(20,6) NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_sales_detail ADD CONSTRAINT pk_mes_wm_product_sales_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_sales_detail_01 ON mes_wm_product_sales_detail (line_id);
CREATE INDEX idx_mes_wm_product_sales_detail_02 ON mes_wm_product_sales_detail (sales_id);
CREATE INDEX idx_mes_wm_product_sales_detail_03 ON mes_wm_product_sales_detail (item_id);
CREATE INDEX idx_mes_wm_product_sales_detail_04 ON mes_wm_product_sales_detail (batch_id);
CREATE INDEX idx_mes_wm_product_sales_detail_05 ON mes_wm_product_sales_detail (warehouse_id);

COMMENT ON COLUMN mes_wm_product_sales_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_product_sales_detail.line_id IS '出库单行ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.sales_id IS '出库单ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.quantity IS '数量';
COMMENT ON COLUMN mes_wm_product_sales_detail.material_stock_id IS '库存记录ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_sales_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.location_id IS '库区ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.area_id IS '库位ID';
COMMENT ON COLUMN mes_wm_product_sales_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_sales_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_sales_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_sales_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_sales_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_sales_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_sales_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_sales_detail IS 'MES 销售出库明细';

-- ----------------------------
-- Records of mes_wm_product_sales_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_sales_detail (id, line_id, sales_id, item_id, quantity, material_stock_id, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 2, 69, 1.000000, NULL, NULL, NULL, 702, 713, 724, NULL, '1', '2026-03-02 17:53:47', '1', '2026-03-02 17:53:55', '0', 1), (2, 2, 3, 69, 123.000000, NULL, NULL, NULL, 702, 713, 724, NULL, '1', '2026-03-02 18:47:49', '1', '2026-03-02 18:48:00', '0', 1), (3, 100002, 100003, 69, 736.000000, 4, 1, 'TEST', 702, 713, 724, NULL, '1', '2026-03-30 21:54:11', '1', '2026-03-30 21:54:11', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_sales_detail_seq;
CREATE SEQUENCE mes_wm_product_sales_detail_seq
    START 4;

-- ----------------------------
-- Table structure for mes_wm_product_sales_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_product_sales_line;
CREATE TABLE mes_wm_product_sales_line (
    id int8 NOT NULL,
  sales_id int8 NOT NULL,
  notice_line_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(20,6) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  oqc_check_flag int2 NULL DEFAULT NULL,
  oqc_id int8 NULL DEFAULT NULL,
  quality_status int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_product_sales_line ADD CONSTRAINT pk_mes_wm_product_sales_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_product_sales_line_01 ON mes_wm_product_sales_line (sales_id);
CREATE INDEX idx_mes_wm_product_sales_line_02 ON mes_wm_product_sales_line (item_id);
CREATE INDEX idx_mes_wm_product_sales_line_03 ON mes_wm_product_sales_line (batch_id);
CREATE INDEX idx_mes_wm_product_sales_line_04 ON mes_wm_product_sales_line (material_stock_id);

COMMENT ON COLUMN mes_wm_product_sales_line.id IS '编号';
COMMENT ON COLUMN mes_wm_product_sales_line.sales_id IS '出库单ID';
COMMENT ON COLUMN mes_wm_product_sales_line.notice_line_id IS '发货通知单行ID';
COMMENT ON COLUMN mes_wm_product_sales_line.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_product_sales_line.quantity IS '出库数量';
COMMENT ON COLUMN mes_wm_product_sales_line.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_product_sales_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_product_sales_line.material_stock_id IS '库存记录ID';
COMMENT ON COLUMN mes_wm_product_sales_line.oqc_check_flag IS '是否出厂检验';
COMMENT ON COLUMN mes_wm_product_sales_line.oqc_id IS '出厂检验单ID';
COMMENT ON COLUMN mes_wm_product_sales_line.quality_status IS '质检状态';
COMMENT ON COLUMN mes_wm_product_sales_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_product_sales_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_product_sales_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_product_sales_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_product_sales_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_product_sales_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_product_sales_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_product_sales_line IS 'MES 销售出库单行';

-- ----------------------------
-- Records of mes_wm_product_sales_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_product_sales_line (id, sales_id, notice_line_id, item_id, quantity, batch_id, batch_code, material_stock_id, oqc_check_flag, oqc_id, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 2, NULL, 69, 1.000000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-02 17:18:22', '1', '2026-03-02 17:18:22', '0', 1), (2, 3, NULL, 69, 123.000000, 123, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-02 18:47:30', '1', '2026-03-02 18:47:30', '0', 1), (99999, 99999, NULL, 69, 100.500000, NULL, NULL, NULL, 1, NULL, NULL, NULL, '', '2026-03-27 10:02:54', '', '2026-03-27 10:02:54', '0', 1), (100000, 100001, NULL, 69, 10.000000, 1, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-30 21:08:09', '1', '2026-03-30 21:08:09', '0', 1), (100001, 100002, NULL, 69, 1.000000, 1, 'TTTT', NULL, 1, NULL, NULL, NULL, '1', '2026-03-30 21:09:06', '1', '2026-03-30 22:09:39', '0', 1), (100002, 100003, NULL, 69, 1.000000, 1, NULL, NULL, NULL, NULL, NULL, NULL, '1', '2026-03-30 21:11:22', '1', '2026-03-30 21:11:22', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_product_sales_line_seq;
CREATE SEQUENCE mes_wm_product_sales_line_seq
    START 100003;

-- ----------------------------
-- Table structure for mes_wm_return_issue
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_issue;
CREATE TABLE mes_wm_return_issue (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  workstation_id int8 NULL DEFAULT NULL,
  type int4 NULL DEFAULT NULL,
  return_date timestamp NULL DEFAULT NULL,
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_issue ADD CONSTRAINT pk_mes_wm_return_issue PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_issue_01 ON mes_wm_return_issue (code);
CREATE INDEX idx_mes_wm_return_issue_02 ON mes_wm_return_issue (work_order_id);
CREATE INDEX idx_mes_wm_return_issue_03 ON mes_wm_return_issue (workstation_id);
CREATE INDEX idx_mes_wm_return_issue_04 ON mes_wm_return_issue (status);
CREATE INDEX idx_mes_wm_return_issue_05 ON mes_wm_return_issue (tenant_id);

COMMENT ON COLUMN mes_wm_return_issue.id IS '编号';
COMMENT ON COLUMN mes_wm_return_issue.code IS '退料单编号';
COMMENT ON COLUMN mes_wm_return_issue.name IS '退料单名称';
COMMENT ON COLUMN mes_wm_return_issue.work_order_id IS '生产工单 ID';
COMMENT ON COLUMN mes_wm_return_issue.workstation_id IS '工作站 ID';
COMMENT ON COLUMN mes_wm_return_issue.type IS '退料类型';
COMMENT ON COLUMN mes_wm_return_issue.return_date IS '退料日期';
COMMENT ON COLUMN mes_wm_return_issue.status IS '状态';
COMMENT ON COLUMN mes_wm_return_issue.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_issue.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_issue.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_issue.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_issue.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_issue.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_issue.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_issue IS 'MES 生产退料单主表';

-- ----------------------------
-- Records of mes_wm_return_issue
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_issue (id, code, name, work_order_id, workstation_id, type, return_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'RI-20260228-001', '生产退料单-草稿', 1, 1, 1, '2026-02-28 10:00:00', 0, '测试退料单-草稿', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (2, 'RI-20260228-002', '生产退料单-已完成', 2, 2, 2, '2026-02-28 11:00:00', 3, '测试退料单-已完成', '1', '2026-02-28 14:12:22', '1', '2026-04-17 07:37:35', '0', 1), (3, 'RI-20260228-003', '生产退料单-已取消', 1, 1, 3, '2026-02-28 12:00:00', 5, '测试退料单-已取消', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (4, 'RIpV4sw33lVy', 'XXX', 1, NULL, 1, '1970-01-01 08:00:00', 1, '', '1', '2026-02-28 22:46:01', '1', '2026-02-28 23:13:38', '0', 1), (5, 'RIKygemCMm69', '111', 1, 1, 1, '1970-01-01 08:00:00', 5, '', '1', '2026-03-01 00:20:56', '1', '2026-03-01 00:22:38', '0', 1), (6, 'RIOSAqiWoaXI', '12323', 1, 1, 2, NULL, 4, '', '1', '2026-03-01 00:52:07', '1', '2026-03-01 00:54:50', '0', 1), (7, 'RITmmKTJb4p0', 'aaa', 1, NULL, 1, NULL, 1, '', '1', '2026-03-26 13:02:39', '1', '2026-03-26 13:02:51', '0', 1), (8, 'RI20260330000001', 'EEE', 1, 2, 1, '1970-01-01 08:00:00', 5, '', '1', '2026-03-30 11:52:28', '1', '2026-03-30 12:03:02', '0', 1), (9, 'RI20260330000002', 'AAA', 1, 1, 1, '1970-01-01 08:00:00', 2, '', '1', '2026-03-30 12:07:40', '1', '2026-04-14 03:24:07', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_issue_seq;
CREATE SEQUENCE mes_wm_return_issue_seq
    START 10;

-- ----------------------------
-- Table structure for mes_wm_return_issue_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_issue_detail;
CREATE TABLE mes_wm_return_issue_detail (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  line_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_issue_detail ADD CONSTRAINT pk_mes_wm_return_issue_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_issue_detail_01 ON mes_wm_return_issue_detail (issue_id);
CREATE INDEX idx_mes_wm_return_issue_detail_02 ON mes_wm_return_issue_detail (line_id);
CREATE INDEX idx_mes_wm_return_issue_detail_03 ON mes_wm_return_issue_detail (item_id);
CREATE INDEX idx_mes_wm_return_issue_detail_04 ON mes_wm_return_issue_detail (warehouse_id);
CREATE INDEX idx_mes_wm_return_issue_detail_05 ON mes_wm_return_issue_detail (tenant_id);

COMMENT ON COLUMN mes_wm_return_issue_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_return_issue_detail.issue_id IS '退料单 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.line_id IS '行 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.material_stock_id IS '库存记录 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.quantity IS '退料数量';
COMMENT ON COLUMN mes_wm_return_issue_detail.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_return_issue_detail.warehouse_id IS '仓库 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.location_id IS '库区 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.area_id IS '库位 ID';
COMMENT ON COLUMN mes_wm_return_issue_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_issue_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_issue_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_issue_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_issue_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_issue_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_issue_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_issue_detail IS 'MES 生产退料明细表';

-- ----------------------------
-- Records of mes_wm_return_issue_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_issue_detail (id, issue_id, line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, NULL, 101, 60.00, NULL, 'BATCH-001', 1, 101, 1001, '仓库 A-库区 1-库位 1', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (2, 1, 1, NULL, 101, 40.00, NULL, 'BATCH-001', 1, 101, 1002, '仓库 A-库区 1-库位 2', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (3, 2, 3, NULL, 103, 200.00, NULL, 'BATCH-002', 1, 101, 1001, '仓库 A-库区 1-库位 1', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (4, 2, 4, NULL, 104, 30.00, NULL, 'BATCH-002', 2, 201, 2001, '仓库 B-库区 1-库位 1', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (5, 5, 7, NULL, 69, 1.00, NULL, '32321', 702, 713, 724, '', '1', '2026-03-01 00:22:04', '1', '2026-03-01 00:22:04', '0', 1), (6, 6, 8, NULL, 70, 1.00, NULL, NULL, 702, 713, 724, '', '1', '2026-03-01 00:52:45', '1', '2026-03-01 00:52:45', '0', 1), (7, 9, 11, 14, 94, 1.00, 7, 'BATCH_ITEM_94', 703, 714, 725, '', '1', '2026-04-14 03:28:34', '1', '2026-04-14 03:28:34', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_issue_detail_seq;
CREATE SEQUENCE mes_wm_return_issue_detail_seq
    START 8;

-- ----------------------------
-- Table structure for mes_wm_return_issue_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_issue_line;
CREATE TABLE mes_wm_return_issue_line (
    id int8 NOT NULL,
  issue_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  rqc_id int8 NULL DEFAULT NULL,
  rqc_check_flag bool NOT NULL DEFAULT '0',
  quality_status int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_issue_line ADD CONSTRAINT pk_mes_wm_return_issue_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_issue_line_01 ON mes_wm_return_issue_line (issue_id);
CREATE INDEX idx_mes_wm_return_issue_line_02 ON mes_wm_return_issue_line (item_id);
CREATE INDEX idx_mes_wm_return_issue_line_03 ON mes_wm_return_issue_line (tenant_id);

COMMENT ON COLUMN mes_wm_return_issue_line.id IS '编号';
COMMENT ON COLUMN mes_wm_return_issue_line.issue_id IS '退料单 ID';
COMMENT ON COLUMN mes_wm_return_issue_line.material_stock_id IS '库存记录 ID';
COMMENT ON COLUMN mes_wm_return_issue_line.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_return_issue_line.quantity IS '退料数量';
COMMENT ON COLUMN mes_wm_return_issue_line.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_return_issue_line.batch_code IS '批次编码';
COMMENT ON COLUMN mes_wm_return_issue_line.rqc_id IS '退货检验单 ID';
COMMENT ON COLUMN mes_wm_return_issue_line.rqc_check_flag IS '是否需要质检';
COMMENT ON COLUMN mes_wm_return_issue_line.quality_status IS '质量状态';
COMMENT ON COLUMN mes_wm_return_issue_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_issue_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_issue_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_issue_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_issue_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_issue_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_issue_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_issue_line IS 'MES 生产退料单行表';

-- ----------------------------
-- Records of mes_wm_return_issue_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_issue_line (id, issue_id, material_stock_id, item_id, quantity, batch_id, batch_code, rqc_id, rqc_check_flag, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, NULL, 101, 100.00, NULL, NULL, NULL, '0', 1, '余料退回-合格', '1', '2026-02-28 14:12:22', '1', '2026-02-28 23:01:33', '1', 1), (2, 1, NULL, 102, 50.00, NULL, NULL, NULL, '1', 0, '需要质检', '1', '2026-02-28 14:12:22', '1', '2026-02-28 23:01:31', '1', 1), (3, 2, NULL, 103, 200.00, NULL, NULL, NULL, '0', 2, '不良退料', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (4, 2, NULL, 104, 30.00, NULL, NULL, NULL, '0', 1, '余料退回', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (5, 3, NULL, 105, 80.00, NULL, NULL, NULL, '0', 1, '其他退料', '1', '2026-02-28 14:12:22', '1', '2026-02-28 14:12:22', '0', 1), (6, 4, NULL, 70, 5.00, NULL, NULL, NULL, '1', 0, '', '1', '2026-02-28 22:51:18', '1', '2026-02-28 22:56:10', '0', 1), (7, 5, NULL, 69, 323.00, NULL, NULL, NULL, '0', 1, '1133', '1', '2026-03-01 00:21:04', '1', '2026-03-01 00:21:04', '0', 1), (8, 6, NULL, 70, 1.00, NULL, NULL, NULL, '0', 2, '2', '1', '2026-03-01 00:52:12', '1', '2026-03-01 00:52:12', '0', 1), (9, 7, NULL, 69, 10.00, NULL, NULL, NULL, '1', 0, '', '1', '2026-03-26 13:02:45', '1', '2026-03-26 13:02:45', '0', 1), (10, 8, 9, 94, 123.00, 7, 'BATCH_ITEM_94', NULL, '1', 0, '', '1', '2026-03-30 11:53:29', '1', '2026-03-30 11:59:27', '0', 1), (11, 9, 9, 94, 123.00, 7, 'BATCH_ITEM_94', NULL, '0', 1, '', '1', '2026-03-30 12:07:50', '1', '2026-03-30 12:07:50', '0', 1), (12, 1, 14, 94, 1.00, 7, 'BATCH_ITEM_94', NULL, '0', 1, '', '1', '2026-04-17 15:34:51', '1', '2026-04-17 15:34:51', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_issue_line_seq;
CREATE SEQUENCE mes_wm_return_issue_line_seq
    START 13;

-- ----------------------------
-- Table structure for mes_wm_return_sales
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_sales;
CREATE TABLE mes_wm_return_sales (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  sales_order_code varchar(64) NULL DEFAULT NULL,
  client_id int8 NULL DEFAULT NULL,
  return_date timestamp NULL DEFAULT NULL,
  return_reason varchar(500) NULL DEFAULT NULL,
  status int4 NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_sales ADD CONSTRAINT pk_mes_wm_return_sales PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_return_sales_01 ON mes_wm_return_sales (tenant_id, code);

CREATE INDEX idx_mes_wm_return_sales_01 ON mes_wm_return_sales (client_id);
CREATE INDEX idx_mes_wm_return_sales_02 ON mes_wm_return_sales (sales_order_code);
CREATE INDEX idx_mes_wm_return_sales_03 ON mes_wm_return_sales (return_date);
CREATE INDEX idx_mes_wm_return_sales_04 ON mes_wm_return_sales (status);
CREATE INDEX idx_mes_wm_return_sales_05 ON mes_wm_return_sales (tenant_id);

COMMENT ON COLUMN mes_wm_return_sales.id IS '退货单ID';
COMMENT ON COLUMN mes_wm_return_sales.code IS '退货单编码';
COMMENT ON COLUMN mes_wm_return_sales.name IS '退货单名称';
COMMENT ON COLUMN mes_wm_return_sales.sales_order_code IS '销售订单编号';
COMMENT ON COLUMN mes_wm_return_sales.client_id IS '客户ID';
COMMENT ON COLUMN mes_wm_return_sales.return_date IS '退货日期';
COMMENT ON COLUMN mes_wm_return_sales.return_reason IS '退货原因';
COMMENT ON COLUMN mes_wm_return_sales.status IS '状态（0草稿 1待执行 2待上架 3已完成 4已取消）';
COMMENT ON COLUMN mes_wm_return_sales.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_sales.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_sales.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_sales.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_sales.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_sales.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_sales.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_sales IS '销售退货单';

-- ----------------------------
-- Records of mes_wm_return_sales
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_sales (id, code, name, sales_order_code, client_id, return_date, return_reason, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'RS202603010001', '销售退货单-客户001', 'SO202603010001', 1001, '2026-03-01 10:00:00', '质量问题', 0, '测试退货单1', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (2, 'RS202603010002', '销售退货单-客户002', 'SO202603010002', 1002, '2026-03-01 11:00:00', '规格不符', 2, '测试退货单2', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (3, 'RS202603010003', '销售退货单-客户003', 'SO202603010003', 1003, '2026-03-01 12:00:00', '客户原因', 3, '测试退货单3', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (4, 'RS20260301001', '测试退货单001', NULL, 200, '1970-01-01 08:00:00', '产品质量问题', 0, '', '1', '2026-03-01 18:33:14', '1', '2026-03-01 18:33:14', '0', 1), (7, 'RS202603010099', '测试销售退货单', 'SO202603010001', 200, '1970-01-01 08:00:00', '质量问题', 2, '测试备注', '1', '2026-03-01 18:36:00', '1', '2026-03-30 19:15:14', '0', 1), (8, 'RS202603010101', '测试销售退货单', 'SO202603010001', 200, '1970-01-01 08:00:00', '质量问题', 4, '测试备注', '1', '2026-03-01 18:36:32', '1', '2026-03-01 18:36:33', '0', 1), (9, 'RS202603010102', '测试取消退货单', 'SO202603010002', 200, '1970-01-01 08:00:00', '测试取消', 5, '测试取消', '1', '2026-03-01 18:36:33', '1', '2026-03-01 18:36:33', '0', 1), (10, 'RSZFhj41kGEq', '32321', NULL, 200, '1970-01-01 08:00:00', NULL, 4, '', '1', '2026-03-01 22:12:15', '1', '2026-03-01 22:54:53', '0', 1), (11, 'RSrRH6uk69T5', 'AAA', NULL, 200, '1970-01-01 08:00:00', '321321312', 1, '', '1', '2026-03-26 21:43:36', '1', '2026-03-26 22:23:33', '0', 1), (12, 'RS20260330001', 'ABC', NULL, 200, '1970-01-01 08:00:00', 'AAAA', 1, '', '1', '2026-03-30 19:13:12', '1', '2026-03-30 19:15:04', '0', 1), (13, 'RS-20260412-001', 'ä¸´æ—¶é”€å”®é€€è´§å•çš„æ¨¡æ‹Ÿæ•°æ®', 'SO-123456', 1, '2026-04-11 18:28:00', NULL, 0, '', '', '2026-04-11 18:28:00', '1', '2026-04-17 09:55:32', '1', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_sales_seq;
CREATE SEQUENCE mes_wm_return_sales_seq
    START 14;

-- ----------------------------
-- Table structure for mes_wm_return_sales_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_sales_detail;
CREATE TABLE mes_wm_return_sales_detail (
    id int8 NOT NULL,
  return_id int8 NOT NULL,
  line_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_sales_detail ADD CONSTRAINT pk_mes_wm_return_sales_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_sales_detail_01 ON mes_wm_return_sales_detail (line_id);
CREATE INDEX idx_mes_wm_return_sales_detail_02 ON mes_wm_return_sales_detail (return_id);
CREATE INDEX idx_mes_wm_return_sales_detail_03 ON mes_wm_return_sales_detail (item_id);
CREATE INDEX idx_mes_wm_return_sales_detail_04 ON mes_wm_return_sales_detail (batch_id);
CREATE INDEX idx_mes_wm_return_sales_detail_05 ON mes_wm_return_sales_detail (warehouse_id);
CREATE INDEX idx_mes_wm_return_sales_detail_06 ON mes_wm_return_sales_detail (tenant_id);

COMMENT ON COLUMN mes_wm_return_sales_detail.id IS '明细ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.return_id IS '退货单ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.line_id IS '行ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.quantity IS '上架数量';
COMMENT ON COLUMN mes_wm_return_sales_detail.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_return_sales_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.location_id IS '库区ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.area_id IS '库位ID';
COMMENT ON COLUMN mes_wm_return_sales_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_sales_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_sales_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_sales_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_sales_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_sales_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_sales_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_sales_detail IS '销售退货单明细';

-- ----------------------------
-- Records of mes_wm_return_sales_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_sales_detail (id, return_id, line_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 2, 3, 2002, 100.00, 3003, NULL, 4001, 5001, 6001, '明细1', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (2, 2, 3, 2002, 100.00, 3003, NULL, 4001, 5002, 6002, '明细2', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (3, 2, 4, 2003, 150.00, 3004, NULL, 4002, 5003, 6003, '明细3', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (4, 3, 5, 2003, 300.00, 3005, NULL, 4003, 5004, 6004, '明细4', '1', '2026-03-01 10:30:34', '1', '2026-03-01 10:30:34', '0', 1), (5, 8, 6, 69, 100.00, NULL, NULL, 701, 711, 721, '测试明细', '1', '2026-03-01 18:36:33', '1', '2026-03-01 18:36:33', '0', 1), (6, 10, 9, 69, 123.00, NULL, NULL, 702, 713, 724, '', '1', '2026-03-01 22:54:50', '1', '2026-03-01 22:54:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_sales_detail_seq;
CREATE SEQUENCE mes_wm_return_sales_detail_seq
    START 7;

-- ----------------------------
-- Table structure for mes_wm_return_sales_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_sales_line;
CREATE TABLE mes_wm_return_sales_line (
    id int8 NOT NULL,
  return_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT NULL,
  rqc_id int8 NULL DEFAULT NULL,
  rqc_check_flag bool NULL DEFAULT '0',
  quality_status int4 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_sales_line ADD CONSTRAINT pk_mes_wm_return_sales_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_sales_line_01 ON mes_wm_return_sales_line (return_id);
CREATE INDEX idx_mes_wm_return_sales_line_02 ON mes_wm_return_sales_line (item_id);
CREATE INDEX idx_mes_wm_return_sales_line_03 ON mes_wm_return_sales_line (batch_id);
CREATE INDEX idx_mes_wm_return_sales_line_04 ON mes_wm_return_sales_line (tenant_id);

COMMENT ON COLUMN mes_wm_return_sales_line.id IS '行ID';
COMMENT ON COLUMN mes_wm_return_sales_line.return_id IS '退货单ID';
COMMENT ON COLUMN mes_wm_return_sales_line.item_id IS '物料ID';
COMMENT ON COLUMN mes_wm_return_sales_line.quantity IS '退货数量';
COMMENT ON COLUMN mes_wm_return_sales_line.batch_id IS '批次ID';
COMMENT ON COLUMN mes_wm_return_sales_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_return_sales_line.rqc_id IS '退货检验单 ID';
COMMENT ON COLUMN mes_wm_return_sales_line.rqc_check_flag IS '是否需要质检';
COMMENT ON COLUMN mes_wm_return_sales_line.quality_status IS '质检状态';
COMMENT ON COLUMN mes_wm_return_sales_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_sales_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_sales_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_sales_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_sales_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_sales_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_sales_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_sales_line IS '销售退货单行';

-- ----------------------------
-- Records of mes_wm_return_sales_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_sales_line (id, return_id, item_id, quantity, batch_id, batch_code, rqc_id, rqc_check_flag, quality_status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 2001, 100.00, 3001, NULL, NULL, '0', 2, '行1', '1', '2026-03-01 10:30:34', '1', '2026-03-24 09:12:54', '0', 1), (2, 1, 2002, 50.00, 3002, NULL, NULL, '0', NULL, '行2', '1', '2026-03-01 10:30:34', '1', '2026-03-24 09:12:54', '0', 1), (3, 2, 2002, 200.00, 3003, NULL, NULL, '0', 2, '行3', '1', '2026-03-01 10:30:34', '1', '2026-03-24 09:12:54', '0', 1), (4, 2, 2003, 150.00, 3004, NULL, NULL, '0', NULL, '行4', '1', '2026-03-01 10:30:34', '1', '2026-03-24 09:12:54', '0', 1), (5, 3, 2003, 300.00, 3005, NULL, NULL, '0', NULL, '行5', '1', '2026-03-01 10:30:34', '1', '2026-03-24 09:12:54', '0', 1), (6, 8, 69, 100.00, NULL, NULL, NULL, '0', NULL, '测试行', '1', '2026-03-01 18:36:32', '1', '2026-03-24 09:12:54', '0', 1), (7, 9, 69, 50.00, NULL, NULL, NULL, '0', NULL, '', '1', '2026-03-01 18:36:33', '1', '2026-03-24 09:12:54', '0', 1), (8, 7, 69, 10.00, NULL, NULL, NULL, '0', NULL, '', '1', '2026-03-01 22:01:26', '1', '2026-03-01 22:01:26', '0', 1), (9, 10, 69, 333.00, NULL, NULL, NULL, '0', NULL, '', '1', '2026-03-01 22:12:25', '1', '2026-03-01 22:12:25', '0', 1), (10, 11, 94, 1.00, NULL, NULL, NULL, '1', 0, '', '1', '2026-03-26 22:23:29', '1', '2026-03-26 22:23:29', '0', 1), (11, 12, 70, 12.00, 5, NULL, NULL, '1', 0, '', '1', '2026-03-30 19:14:52', '1', '2026-03-30 19:14:56', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_sales_line_seq;
CREATE SEQUENCE mes_wm_return_sales_line_seq
    START 12;

-- ----------------------------
-- Table structure for mes_wm_return_vendor
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_vendor;
CREATE TABLE mes_wm_return_vendor (
    id int8 NOT NULL,
  code varchar(64) NULL DEFAULT '',
  name varchar(255) NULL DEFAULT '',
  purchase_order_code varchar(64) NULL DEFAULT '',
  vendor_id int8 NULL DEFAULT NULL,
  return_date timestamp NULL DEFAULT NULL,
  return_reason varchar(255) NULL DEFAULT '',
  transport_code varchar(128) NULL DEFAULT '',
  transport_telephone varchar(128) NULL DEFAULT '',
  status int4 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_vendor ADD CONSTRAINT pk_mes_wm_return_vendor PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_vendor_01 ON mes_wm_return_vendor (vendor_id);
CREATE INDEX idx_mes_wm_return_vendor_02 ON mes_wm_return_vendor (status);
CREATE INDEX idx_mes_wm_return_vendor_03 ON mes_wm_return_vendor (tenant_id);

COMMENT ON COLUMN mes_wm_return_vendor.id IS '编号';
COMMENT ON COLUMN mes_wm_return_vendor.code IS '退货单编号';
COMMENT ON COLUMN mes_wm_return_vendor.name IS '退货单名称';
COMMENT ON COLUMN mes_wm_return_vendor.purchase_order_code IS '采购订单号';
COMMENT ON COLUMN mes_wm_return_vendor.vendor_id IS '供应商 ID';
COMMENT ON COLUMN mes_wm_return_vendor.return_date IS '退货日期';
COMMENT ON COLUMN mes_wm_return_vendor.return_reason IS '退货原因';
COMMENT ON COLUMN mes_wm_return_vendor.transport_code IS '物流单号';
COMMENT ON COLUMN mes_wm_return_vendor.transport_telephone IS '联系电话';
COMMENT ON COLUMN mes_wm_return_vendor.status IS '状态';
COMMENT ON COLUMN mes_wm_return_vendor.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_vendor.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_vendor.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_vendor.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_vendor.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_vendor.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_vendor.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_vendor IS 'MES 供应商退货单主表';

-- ----------------------------
-- Records of mes_wm_return_vendor
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_vendor (id, code, name, purchase_order_code, vendor_id, return_date, return_reason, transport_code, transport_telephone, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'RTV-20260228-001', '供应商退货单-草稿', 'PO-20260201-001', 200, '2026-02-28 10:00:00', '来料不良', '', '', 5, '测试退货单-草稿', '1', '2026-02-28 09:59:24', '1', '2026-02-28 18:08:21', '0', 1), (2, 'RTV-20260228-002', '供应商退货单-已完成', 'PO-20260201-002', 2, '2026-02-28 11:00:00', '规格不符', 'SF1234567890', '13800138000', 4, '测试退货单-已完成', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (3, 'RTV-20260228-003', '供应商退货单-已取消', 'PO-20260201-003', 1, '2026-02-28 12:00:00', '数量多余', '', '', 5, '测试退货单-已取消', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (4, 'RVfZlUJ5mhRB', 'QQ', '', 200, '1970-01-01 08:00:00', '', '', '', 4, '', '1', '2026-02-28 18:00:17', '1', '2026-02-28 18:07:06', '0', 1), (5, 'RV20260329000001', 'ABC', '', 200, '1970-01-01 08:00:00', '', '', '', 3, '', '1', '2026-03-29 21:29:40', '1', '2026-03-29 21:42:49', '0', 1), (6, 'RV20260329000010', '呃呃呃呃呃呃', '', 200, '1970-01-01 08:00:00', '', '', '', 2, '', '1', '2026-03-29 22:37:21', '1', '2026-03-29 22:40:18', '0', 1), (7, 'RV20260329000011', 'EEEE', '', 200, '1970-01-01 08:00:00', '', '', '', 4, '', '1', '2026-03-29 22:44:59', '1', '2026-03-29 23:04:44', '0', 1), (8, 'RV20260406000001', 'ABE', '', 202, '1970-01-01 08:00:00', '', '', '', 2, '', '1', '2026-04-06 10:41:04', '1', '2026-04-06 10:41:17', '0', 1), (9, 'RV20260406000002', '啊兜底', '', 200, '1970-01-01 08:00:00', '', '', '', 0, '', '1', '2026-04-06 16:58:33', '1', '2026-04-06 16:58:33', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_vendor_seq;
CREATE SEQUENCE mes_wm_return_vendor_seq
    START 10;

-- ----------------------------
-- Table structure for mes_wm_return_vendor_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_vendor_detail;
CREATE TABLE mes_wm_return_vendor_detail (
    id int8 NOT NULL,
  return_id int8 NOT NULL,
  line_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(255) NULL DEFAULT '',
  warehouse_id int8 NULL DEFAULT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_vendor_detail ADD CONSTRAINT pk_mes_wm_return_vendor_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_vendor_detail_01 ON mes_wm_return_vendor_detail (return_id);
CREATE INDEX idx_mes_wm_return_vendor_detail_02 ON mes_wm_return_vendor_detail (line_id);
CREATE INDEX idx_mes_wm_return_vendor_detail_03 ON mes_wm_return_vendor_detail (item_id);
CREATE INDEX idx_mes_wm_return_vendor_detail_04 ON mes_wm_return_vendor_detail (warehouse_id);
CREATE INDEX idx_mes_wm_return_vendor_detail_05 ON mes_wm_return_vendor_detail (tenant_id);

COMMENT ON COLUMN mes_wm_return_vendor_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_return_vendor_detail.return_id IS '退货单 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.line_id IS '行 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.material_stock_id IS '库存记录 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.quantity IS '拣货数量';
COMMENT ON COLUMN mes_wm_return_vendor_detail.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_return_vendor_detail.warehouse_id IS '仓库 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.location_id IS '库区 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.area_id IS '库位 ID';
COMMENT ON COLUMN mes_wm_return_vendor_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_vendor_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_vendor_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_vendor_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_vendor_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_vendor_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_vendor_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_vendor_detail IS 'MES 供应商退货明细表';

-- ----------------------------
-- Records of mes_wm_return_vendor_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_vendor_detail (id, return_id, line_id, material_stock_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, NULL, 101, 60.00, NULL, 'BATCH-001', 1, 101, 1001, '仓库 A-库区 1-库位 1', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (2, 1, 1, NULL, 101, 40.00, NULL, 'BATCH-001', 1, 101, 1002, '仓库 A-库区 1-库位 2', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (3, 2, 3, NULL, 103, 200.00, NULL, 'BATCH-002', 1, 101, 1001, '仓库 A-库区 1-库位 1', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (4, 2, 4, NULL, 104, 30.00, NULL, 'BATCH-002', 2, 201, 2001, '仓库 B-库区 1-库位 1', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (5, 4, 6, NULL, 69, 555.00, NULL, '', 702, 713, 724, '', '1', '2026-02-28 18:06:04', '1', '2026-02-28 18:06:04', '0', 1), (6, 1, 2, NULL, 69, 1.00, NULL, '', 702, 713, 724, '', '1', '2026-02-28 18:08:04', '1', '2026-02-28 18:08:04', '0', 1), (7, 5, 7, NULL, 69, 10.00, NULL, '', 703, 714, 725, '', '1', '2026-03-29 21:42:40', '1', '2026-03-29 21:42:40', '0', 1), (8, 7, 9, 4, 69, 1.00, 1, 'TEST', 702, 713, 724, '', '1', '2026-03-29 23:04:37', '1', '2026-03-29 23:04:37', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_vendor_detail_seq;
CREATE SEQUENCE mes_wm_return_vendor_detail_seq
    START 9;

-- ----------------------------
-- Table structure for mes_wm_return_vendor_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_return_vendor_line;
CREATE TABLE mes_wm_return_vendor_line (
    id int8 NOT NULL,
  return_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(12,2) NOT NULL DEFAULT 0.00,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT '',
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_return_vendor_line ADD CONSTRAINT pk_mes_wm_return_vendor_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_return_vendor_line_01 ON mes_wm_return_vendor_line (return_id);
CREATE INDEX idx_mes_wm_return_vendor_line_02 ON mes_wm_return_vendor_line (item_id);
CREATE INDEX idx_mes_wm_return_vendor_line_03 ON mes_wm_return_vendor_line (tenant_id);

COMMENT ON COLUMN mes_wm_return_vendor_line.id IS '编号';
COMMENT ON COLUMN mes_wm_return_vendor_line.return_id IS '退货单 ID';
COMMENT ON COLUMN mes_wm_return_vendor_line.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_return_vendor_line.quantity IS '退货数量';
COMMENT ON COLUMN mes_wm_return_vendor_line.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_return_vendor_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_return_vendor_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_return_vendor_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_return_vendor_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_return_vendor_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_return_vendor_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_return_vendor_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_return_vendor_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_return_vendor_line IS 'MES 供应商退货单行表';

-- ----------------------------
-- Records of mes_wm_return_vendor_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_return_vendor_line (id, return_id, item_id, quantity, batch_id, batch_code, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 101, 100.00, NULL, '', '物料 A 退货', '1', '2026-02-28 09:59:24', '1', '2026-02-28 18:07:14', '1', 1), (2, 1, 69, 50.00, NULL, '', '物料 B 退货', '1', '2026-02-28 09:59:24', '1', '2026-02-28 18:07:21', '0', 1), (3, 2, 103, 200.00, NULL, '', '物料 C 退货', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (4, 2, 104, 30.00, NULL, '', '物料 D 退货', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (5, 3, 105, 80.00, NULL, '', '物料 E 退货', '1', '2026-02-28 09:59:24', '1', '2026-02-28 09:59:24', '0', 1), (6, 4, 69, 555.00, NULL, '', '', '1', '2026-02-28 18:02:32', '1', '2026-02-28 18:02:32', '0', 1), (7, 5, 69, 10.00, NULL, '', '', '1', '2026-03-29 21:41:36', '1', '2026-03-29 21:41:36', '0', 1), (8, 6, 69, 1.00, NULL, '', '2', '1', '2026-03-29 22:40:16', '1', '2026-03-29 22:40:16', '0', 1), (9, 7, 69, 12.00, 3, 'PC202600001', '', '1', '2026-03-29 22:50:20', '1', '2026-03-29 22:53:38', '0', 1), (10, 8, 102, 1.00, NULL, '', '', '1', '2026-04-06 10:41:15', '1', '2026-04-06 10:41:15', '0', 1), (11, 9, 95, 1.00, 2, 'PC20260', 'AABBB', '1', '2026-04-06 16:58:50', '1', '2026-04-06 16:58:50', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_return_vendor_line_seq;
CREATE SEQUENCE mes_wm_return_vendor_line_seq
    START 12;

-- ----------------------------
-- Table structure for mes_wm_sales_notice
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_sales_notice;
CREATE TABLE mes_wm_sales_notice (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  sales_order_code varchar(64) NULL DEFAULT NULL,
  client_id int8 NULL DEFAULT NULL,
  sales_date date NULL DEFAULT NULL,
  recipient_name varchar(64) NULL DEFAULT NULL,
  recipient_telephone varchar(20) NULL DEFAULT NULL,
  recipient_address varchar(500) NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_sales_notice ADD CONSTRAINT pk_mes_wm_sales_notice PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_sales_notice_01 ON mes_wm_sales_notice (code, tenant_id);

CREATE INDEX idx_mes_wm_sales_notice_01 ON mes_wm_sales_notice (client_id);
CREATE INDEX idx_mes_wm_sales_notice_02 ON mes_wm_sales_notice (status);
CREATE INDEX idx_mes_wm_sales_notice_03 ON mes_wm_sales_notice (sales_date);

COMMENT ON COLUMN mes_wm_sales_notice.id IS 'ID';
COMMENT ON COLUMN mes_wm_sales_notice.code IS '通知单编号';
COMMENT ON COLUMN mes_wm_sales_notice.name IS '通知单名称';
COMMENT ON COLUMN mes_wm_sales_notice.sales_order_code IS '销售订单编号';
COMMENT ON COLUMN mes_wm_sales_notice.client_id IS '客户 ID';
COMMENT ON COLUMN mes_wm_sales_notice.sales_date IS '发货日期';
COMMENT ON COLUMN mes_wm_sales_notice.recipient_name IS '收货人';
COMMENT ON COLUMN mes_wm_sales_notice.recipient_telephone IS '联系方式';
COMMENT ON COLUMN mes_wm_sales_notice.recipient_address IS '收货地址';
COMMENT ON COLUMN mes_wm_sales_notice.status IS '单据状态';
COMMENT ON COLUMN mes_wm_sales_notice.remark IS '备注';
COMMENT ON COLUMN mes_wm_sales_notice.creator IS '创建者';
COMMENT ON COLUMN mes_wm_sales_notice.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_sales_notice.updater IS '更新者';
COMMENT ON COLUMN mes_wm_sales_notice.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_sales_notice.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_sales_notice.tenant_id IS '租户 ID';
COMMENT ON TABLE mes_wm_sales_notice IS '发货通知单';

-- ----------------------------
-- Records of mes_wm_sales_notice
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_sales_notice (id, code, name, sales_order_code, client_id, sales_date, recipient_name, recipient_telephone, recipient_address, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'SN202603020001', '测试发货通知单', 'SO202603020001', 200, '1970-01-01', '张三', '13800138000', '北京市朝阳区测试地址', 1, '测试备注', '1', '2026-03-02 11:50:15', '1', '2026-03-02 11:52:44', '0', 1), (2, 'SN202603020002', '修改后的草稿通知单', 'SO202603020002', 207, '1970-01-01', '修改后的收货人', '13500135000', '杭州市西湖区测试地址', 1, '修改后的备注', '1', '2026-03-02 11:56:16', '1', '2026-03-02 12:02:59', '0', 1), (3, 'SN20260330000003', 'BCE', NULL, 200, '2026-03-10', NULL, NULL, NULL, 3, NULL, '1', '2026-03-30 17:06:00', '1', '2026-03-30 17:52:26', '0', 1), (4, 'SN20260330000004', 'AABBEEE', NULL, 200, '2026-03-18', NULL, NULL, NULL, 3, NULL, '1', '2026-03-30 18:03:04', '1', '2026-03-30 18:17:34', '0', 1), (5, 'SN20260417000001', 'ABCDE', NULL, 208, '2026-04-15', NULL, NULL, NULL, 0, NULL, '1', '2026-04-17 09:45:04', '1', '2026-04-17 09:45:04', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_sales_notice_seq;
CREATE SEQUENCE mes_wm_sales_notice_seq
    START 6;

-- ----------------------------
-- Table structure for mes_wm_sales_notice_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_sales_notice_line;
CREATE TABLE mes_wm_sales_notice_line (
    id int8 NOT NULL,
  notice_id int8 NOT NULL,
  item_id int8 NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  quantity numeric(12,2) NOT NULL,
  oqc_check_flag bool NOT NULL DEFAULT '1',
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_sales_notice_line ADD CONSTRAINT pk_mes_wm_sales_notice_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_sales_notice_line_01 ON mes_wm_sales_notice_line (notice_id);
CREATE INDEX idx_mes_wm_sales_notice_line_02 ON mes_wm_sales_notice_line (item_id);
CREATE INDEX idx_mes_wm_sales_notice_line_03 ON mes_wm_sales_notice_line (batch_id);

COMMENT ON COLUMN mes_wm_sales_notice_line.id IS 'ID';
COMMENT ON COLUMN mes_wm_sales_notice_line.notice_id IS '通知单 ID';
COMMENT ON COLUMN mes_wm_sales_notice_line.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_sales_notice_line.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_sales_notice_line.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_sales_notice_line.quantity IS '发货数量';
COMMENT ON COLUMN mes_wm_sales_notice_line.oqc_check_flag IS '是否检验';
COMMENT ON COLUMN mes_wm_sales_notice_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_sales_notice_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_sales_notice_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_sales_notice_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_sales_notice_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_sales_notice_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_sales_notice_line.tenant_id IS '租户 ID';
COMMENT ON TABLE mes_wm_sales_notice_line IS '发货通知单行';

-- ----------------------------
-- Records of mes_wm_sales_notice_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_sales_notice_line (id, notice_id, item_id, batch_id, batch_code, quantity, oqc_check_flag, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 69, NULL, 'BATCH001', 100.00, '1', '测试行备注', '1', '2026-03-02 11:50:39', '1', '2026-03-02 11:50:39', '0', 1), (2, 2, 69, NULL, NULL, 3.00, '1', NULL, '1', '2026-03-02 12:02:22', '1', '2026-03-02 12:02:22', '0', 1), (3, 3, 94, 7, 'BATCH_ITEM_94', 123.00, '1', 'biubiubiu', '1', '2026-03-30 17:52:24', '1', '2026-03-30 17:52:24', '0', 1), (4, 4, 69, 8, 'BATCH_ITEM_72', 10.00, '1', NULL, '1', '2026-03-30 18:03:14', '1', '2026-03-30 18:08:21', '0', 1), (5, 5, 103, NULL, NULL, 123.00, '1', NULL, '1', '2026-04-17 09:45:15', '1', '2026-04-17 09:45:15', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_sales_notice_line_seq;
CREATE SEQUENCE mes_wm_sales_notice_line_seq
    START 6;

-- ----------------------------
-- Table structure for mes_wm_sn
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_sn;
CREATE TABLE mes_wm_sn (
    id int8 NOT NULL,
  uuid varchar(64) NULL DEFAULT NULL,
  code varchar(64) NOT NULL,
  item_id int8 NOT NULL,
  batch_code varchar(100) NULL DEFAULT NULL,
  work_order_id int8 NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_sn ADD CONSTRAINT pk_mes_wm_sn PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_sn_01 ON mes_wm_sn (code, deleted, tenant_id);

CREATE INDEX idx_mes_wm_sn_01 ON mes_wm_sn (item_id, batch_code, create_time);
CREATE INDEX idx_mes_wm_sn_02 ON mes_wm_sn (work_order_id);
CREATE INDEX idx_mes_wm_sn_03 ON mes_wm_sn (tenant_id);
CREATE INDEX idx_mes_wm_sn_04 ON mes_wm_sn (uuid);

COMMENT ON COLUMN mes_wm_sn.id IS '编号';
COMMENT ON COLUMN mes_wm_sn.uuid IS '批次 UUID（用于标记同一批次生成的 SN 码）';
COMMENT ON COLUMN mes_wm_sn.code IS 'SN 码（唯一）';
COMMENT ON COLUMN mes_wm_sn.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_sn.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_sn.work_order_id IS '生产工单编号';
COMMENT ON COLUMN mes_wm_sn.creator IS '创建者';
COMMENT ON COLUMN mes_wm_sn.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_sn.updater IS '更新者';
COMMENT ON COLUMN mes_wm_sn.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_sn.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_sn.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_sn IS 'MES SN 码';

-- ----------------------------
-- Records of mes_wm_sn
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_sn (id, uuid, code, item_id, batch_code, work_order_id, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (151, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000151', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (152, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000152', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (153, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000153', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (154, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000154', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (155, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000155', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (156, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000156', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (157, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000157', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (158, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000158', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (159, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000159', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (160, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000160', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (161, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000161', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (162, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000162', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (163, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000163', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (164, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000164', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (165, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000165', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (166, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000166', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (167, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000167', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (168, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000168', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (169, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000169', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (170, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000170', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (171, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000171', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (172, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000172', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (173, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000173', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (174, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000174', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (175, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000175', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (176, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000176', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (177, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000177', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (178, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000178', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (179, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000179', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (180, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000180', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (181, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000181', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (182, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000182', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (183, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000183', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (184, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000184', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (185, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000185', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (186, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000186', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (187, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000187', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (188, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000188', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (189, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000189', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (190, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000190', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (191, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000191', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (192, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000192', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (193, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000193', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (194, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000194', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (195, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000195', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (196, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000196', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (197, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000197', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (198, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000198', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (199, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000199', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (200, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000200', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (201, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000201', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (202, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000202', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (203, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000203', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (204, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000204', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (205, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000205', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (206, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000206', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (207, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000207', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (208, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000208', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (209, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000209', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (210, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000210', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (211, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000211', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (212, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000212', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (213, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000213', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (214, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000214', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (215, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000215', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (216, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000216', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (217, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000217', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (218, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000218', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (219, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000219', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (220, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000220', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (221, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000221', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (222, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000222', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (223, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000223', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (224, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000224', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (225, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000225', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (226, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000226', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (227, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000227', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (228, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000228', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (229, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000229', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (230, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000230', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (231, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000231', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (232, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000232', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (233, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000233', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (234, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000234', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (235, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000235', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (236, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000236', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (237, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000237', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (238, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000238', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (239, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000239', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (240, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000240', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (241, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000241', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (242, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000242', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (243, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000243', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (244, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000244', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (245, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000245', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (246, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000246', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (247, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000247', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (248, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000248', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (249, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000249', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (250, '1ebdb59516c54de98a911d943ee6e933', 'SN20260305000250', 69, '100', NULL, '1', '2026-03-05 13:29:20', '1', '2026-03-05 13:29:20', '0', 1), (251, 'doc-snap-sn-20260415-a', 'SN20260415001001', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (252, 'doc-snap-sn-20260415-a', 'SN20260415001002', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (253, 'doc-snap-sn-20260415-a', 'SN20260415001003', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (254, 'doc-snap-sn-20260415-a', 'SN20260415001004', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (255, 'doc-snap-sn-20260415-a', 'SN20260415001005', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (256, 'doc-snap-sn-20260415-a', 'SN20260415001006', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (257, 'doc-snap-sn-20260415-a', 'SN20260415001007', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (258, 'doc-snap-sn-20260415-a', 'SN20260415001008', 69, 'DOC-20260415-A', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (259, 'doc-snap-sn-20260415-b', 'SN20260415002001', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (260, 'doc-snap-sn-20260415-b', 'SN20260415002002', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (261, 'doc-snap-sn-20260415-b', 'SN20260415002003', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (262, 'doc-snap-sn-20260415-b', 'SN20260415002004', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (263, 'doc-snap-sn-20260415-b', 'SN20260415002005', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (264, 'doc-snap-sn-20260415-b', 'SN20260415002006', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (265, 'doc-snap-sn-20260415-b', 'SN20260415002007', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (266, 'doc-snap-sn-20260415-b', 'SN20260415002008', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (267, 'doc-snap-sn-20260415-b', 'SN20260415002009', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (268, 'doc-snap-sn-20260415-b', 'SN20260415002010', 69, 'DOC-20260415-B', NULL, 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1), (269, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000001', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (270, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000002', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (271, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000003', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (272, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000004', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (273, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000005', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (274, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000006', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (275, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000007', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (276, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000008', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (277, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000009', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (278, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000010', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (279, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000011', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (280, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000012', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (281, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000013', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (282, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000014', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (283, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000015', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (284, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000016', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (285, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000017', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (286, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000018', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (287, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000019', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (288, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000020', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (289, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000021', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (290, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000022', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (291, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000023', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (292, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000024', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (293, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000025', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (294, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000026', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (295, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000027', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (296, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000028', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (297, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000029', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (298, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000030', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (299, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000031', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (300, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000032', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (301, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000033', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (302, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000034', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (303, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000035', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (304, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000036', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (305, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000037', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (306, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000038', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (307, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000039', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (308, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000040', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (309, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000041', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (310, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000042', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (311, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000043', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (312, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000044', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (313, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000045', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (314, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000046', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (315, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000047', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (316, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000048', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (317, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000049', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (318, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000050', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (319, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000051', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (320, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000052', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (321, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000053', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (322, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000054', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (323, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000055', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (324, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000056', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (325, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000057', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (326, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000058', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (327, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000059', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (328, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000060', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (329, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000061', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (330, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000062', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (331, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000063', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (332, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000064', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (333, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000065', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (334, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000066', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (335, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000067', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (336, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000068', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (337, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000069', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (338, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000070', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (339, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000071', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (340, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000072', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (341, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000073', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (342, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000074', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (343, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000075', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (344, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000076', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (345, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000077', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (346, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000078', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (347, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000079', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (348, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000080', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (349, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000081', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (350, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000082', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (351, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000083', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (352, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000084', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (353, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000085', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (354, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000086', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (355, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000087', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (356, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000088', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (357, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000089', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (358, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000090', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (359, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000091', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (360, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000092', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (361, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000093', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (362, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000094', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (363, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000095', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (364, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000096', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (365, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000097', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (366, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000098', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (367, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000099', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1), (368, '6578fe3d133c4e3f800e2e3cb5f9873a', 'SN20260415000100', 95, 'EEE', NULL, '1', '2026-04-15 19:31:40', '1', '2026-04-15 19:31:40', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_sn_seq;
CREATE SEQUENCE mes_wm_sn_seq
    START 369;

-- ----------------------------
-- Table structure for mes_wm_stock_taking_plan
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_stock_taking_plan;
CREATE TABLE mes_wm_stock_taking_plan (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(128) NOT NULL,
  type int2 NOT NULL,
  start_time timestamp NULL DEFAULT NULL,
  end_time timestamp NULL DEFAULT NULL,
  blind_flag bool NOT NULL DEFAULT '0',
  frozen bool NULL DEFAULT '0',
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_stock_taking_plan ADD CONSTRAINT pk_mes_wm_stock_taking_plan PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_stock_taking_plan_01 ON mes_wm_stock_taking_plan (status);
CREATE INDEX idx_mes_wm_stock_taking_plan_02 ON mes_wm_stock_taking_plan (create_time);

COMMENT ON COLUMN mes_wm_stock_taking_plan.id IS '编号';
COMMENT ON COLUMN mes_wm_stock_taking_plan.code IS '方案编码';
COMMENT ON COLUMN mes_wm_stock_taking_plan.name IS '方案名称';
COMMENT ON COLUMN mes_wm_stock_taking_plan.type IS '盘点类型';
COMMENT ON COLUMN mes_wm_stock_taking_plan.start_time IS '计划开始时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan.end_time IS '计划结束时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan.blind_flag IS '是否盲盘';
COMMENT ON COLUMN mes_wm_stock_taking_plan.frozen IS '是否冻结库存';
COMMENT ON COLUMN mes_wm_stock_taking_plan.status IS '状态';
COMMENT ON COLUMN mes_wm_stock_taking_plan.remark IS '备注';
COMMENT ON COLUMN mes_wm_stock_taking_plan.creator IS '创建者';
COMMENT ON COLUMN mes_wm_stock_taking_plan.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan.updater IS '更新者';
COMMENT ON COLUMN mes_wm_stock_taking_plan.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_stock_taking_plan.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_stock_taking_plan IS 'MES 库存盘点方案表';

-- ----------------------------
-- Records of mes_wm_stock_taking_plan
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_stock_taking_plan (id, code, name, type, start_time, end_time, blind_flag, frozen, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'PLAN202603001', '2026年第一季度全盘', 1, '2026-03-10 08:00:00', '2026-03-15 18:00:00', '0', '1', 0, '第一季度全面盘点', '1', '2026-03-09 10:00:00', '1', '2026-03-31 18:13:42', '0', 1), (2, 'PLAN202603002', '原料仓抽盘', 1, '2026-03-20 08:00:00', '2026-03-22 18:00:00', '1', '0', 0, '原料仓抽样盘点（盲盘）', '1', '2026-03-09 11:00:00', '1', '2026-03-10 00:38:19', '0', 1), (3, 'PLAN202603003', '成品仓循环盘点', 3, '2026-03-25 08:00:00', '2026-03-30 18:00:00', '0', '0', 1, '成品仓循环盘点（已禁用）', '1', '2026-03-09 12:00:00', '1', '2026-03-10 00:38:24', '1', 1), (4, 'PDP202603310001', 'EEE', 1, NULL, NULL, '0', '0', 1, NULL, '1', '2026-03-31 18:23:53', '1', '2026-03-31 18:23:58', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_stock_taking_plan_seq;
CREATE SEQUENCE mes_wm_stock_taking_plan_seq
    START 5;

-- ----------------------------
-- Table structure for mes_wm_stock_taking_plan_param
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_stock_taking_plan_param;
CREATE TABLE mes_wm_stock_taking_plan_param (
    id int8 NOT NULL,
  plan_id int8 NOT NULL,
  type int2 NOT NULL,
  value_id int8 NOT NULL,
  value_code varchar(64) NOT NULL,
  value_name varchar(128) NOT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_stock_taking_plan_param ADD CONSTRAINT pk_mes_wm_stock_taking_plan_param PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_stock_taking_plan_param_01 ON mes_wm_stock_taking_plan_param (plan_id);
CREATE INDEX idx_mes_wm_stock_taking_plan_param_02 ON mes_wm_stock_taking_plan_param (type);
CREATE INDEX idx_mes_wm_stock_taking_plan_param_03 ON mes_wm_stock_taking_plan_param (create_time);

COMMENT ON COLUMN mes_wm_stock_taking_plan_param.id IS '编号';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.plan_id IS '盘点方案编号';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.type IS '参数值类型';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.value_id IS '参数值编号';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.value_code IS '参数值编码';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.value_name IS '参数值名称';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.remark IS '备注';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.creator IS '创建者';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.updater IS '更新者';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_stock_taking_plan_param.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_stock_taking_plan_param IS 'MES 库存盘点方案参数表';

-- ----------------------------
-- Records of mes_wm_stock_taking_plan_param
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_stock_taking_plan_param (id, plan_id, type, value_id, value_code, value_name, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 1, 1, 'WH001', '原料仓', '全盘-仓库1', '1', '2026-03-09 10:00:00', '1', '2026-03-09 22:42:20', '1', 1), (2, 1, 1, 2, 'WH002', '成品仓', '全盘-仓库2', '1', '2026-03-09 10:00:00', '1', '2026-03-09 22:42:21', '1', 1), (3, 2, 1, 1, 'WH001', '原料仓', '抽盘-仓库', '1', '2026-03-09 11:00:00', '1', '2026-03-09 22:07:03', '1', 1), (4, 2, 4, 101, 'MAT001', '钢材A', '抽盘-物料1', '1', '2026-03-09 11:00:00', '1', '2026-03-09 22:48:46', '1', 1), (5, 2, 4, 102, 'MAT002', '钢材B', '抽盘-物料2', '1', '2026-03-09 11:00:00', '1', '2026-03-09 22:48:45', '1', 1), (6, 3, 1, 2, 'WH002', '成品仓', '循环盘点-仓库', '1', '2026-03-09 12:00:00', '1', '2026-03-09 16:38:23', '1', 1), (7, 3, 2, 201, 'AREA001', 'A区', '循环盘点-库区', '1', '2026-03-09 12:00:00', '1', '2026-03-09 16:38:23', '1', 1), (8, 2, 103, 713, 'LOC-FIN-A', '成品 A 区', '', '1', '2026-03-09 22:28:20', '1', '2026-03-09 22:28:29', '0', 1), (9, 2, 102, 702, 'WH-FIN', '成品仓', '', '1', '2026-03-09 22:55:04', '1', '2026-03-09 22:55:04', '0', 1), (10, 2, 103, 713, 'LOC-FIN-A', '成品 A 区', '', '1', '2026-03-09 22:55:10', '1', '2026-03-09 22:55:10', '0', 1), (11, 2, 104, 724, 'AREA-FIN-A-01', '成品A-01', '123', '1', '2026-03-09 22:55:18', '1', '2026-03-09 22:55:18', '0', 1), (12, 2, 600, 69, 'IF2022082437', '色粉【黑色】', '', '1', '2026-03-09 22:55:53', '1', '2026-03-09 22:55:53', '0', 1), (13, 1, 600, 69, 'IF2022082437', '色粉【黑色】', '', '1', '2026-03-10 19:11:36', '1', '2026-03-10 19:11:36', '0', 1), (14, 4, 102, 703, 'WIP_VIRTUAL_WAREHOUSE', '虚拟线边仓库', 'EEE', '1', '2026-03-31 18:24:18', '1', '2026-03-31 18:24:18', '0', 1), (15, 4, 103, 714, 'WIP_VIRTUAL_LOCATION', '虚拟线边库区', '', '1', '2026-03-31 18:24:30', '1', '2026-03-31 18:24:30', '0', 1), (16, 4, 104, 725, 'WIP_VIRTUAL_AREA', '虚拟线边库位', '', '1', '2026-03-31 18:24:44', '1', '2026-03-31 18:24:44', '0', 1), (17, 4, 107, 2, 'PC20260', 'PC20260', 'EEE', '1', '2026-03-31 18:24:52', '1', '2026-03-31 18:24:52', '0', 1), (18, 4, 900, 2, '2', '不合格', 'EEE', '1', '2026-03-31 18:27:10', '1', '2026-03-31 18:27:24', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_stock_taking_plan_param_seq;
CREATE SEQUENCE mes_wm_stock_taking_plan_param_seq
    START 19;

-- ----------------------------
-- Table structure for mes_wm_stock_taking_task
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_stock_taking_task;
CREATE TABLE mes_wm_stock_taking_task (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(128) NOT NULL,
  taking_date timestamp NOT NULL,
  type int2 NOT NULL,
  user_id int8 NULL DEFAULT NULL,
  plan_id int8 NOT NULL,
  blind_flag bool NOT NULL DEFAULT '0',
  frozen bool NULL DEFAULT '0',
  start_time timestamp NULL DEFAULT NULL,
  end_time timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_stock_taking_task ADD CONSTRAINT pk_mes_wm_stock_taking_task PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_stock_taking_task_01 ON mes_wm_stock_taking_task (plan_id);
CREATE INDEX idx_mes_wm_stock_taking_task_02 ON mes_wm_stock_taking_task (status);
CREATE INDEX idx_mes_wm_stock_taking_task_03 ON mes_wm_stock_taking_task (create_time);

COMMENT ON COLUMN mes_wm_stock_taking_task.id IS '编号';
COMMENT ON COLUMN mes_wm_stock_taking_task.code IS '任务编码';
COMMENT ON COLUMN mes_wm_stock_taking_task.name IS '任务名称';
COMMENT ON COLUMN mes_wm_stock_taking_task.taking_date IS '盘点日期';
COMMENT ON COLUMN mes_wm_stock_taking_task.type IS '盘点类型';
COMMENT ON COLUMN mes_wm_stock_taking_task.user_id IS '盘点人编号';
COMMENT ON COLUMN mes_wm_stock_taking_task.plan_id IS '盘点方案ID';
COMMENT ON COLUMN mes_wm_stock_taking_task.blind_flag IS '是否盲盘';
COMMENT ON COLUMN mes_wm_stock_taking_task.frozen IS '是否冻结库存';
COMMENT ON COLUMN mes_wm_stock_taking_task.start_time IS '开始时间';
COMMENT ON COLUMN mes_wm_stock_taking_task.end_time IS '结束时间';
COMMENT ON COLUMN mes_wm_stock_taking_task.status IS '状态';
COMMENT ON COLUMN mes_wm_stock_taking_task.remark IS '备注';
COMMENT ON COLUMN mes_wm_stock_taking_task.creator IS '创建者';
COMMENT ON COLUMN mes_wm_stock_taking_task.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_stock_taking_task.updater IS '更新者';
COMMENT ON COLUMN mes_wm_stock_taking_task.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_stock_taking_task.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_stock_taking_task.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_stock_taking_task IS 'MES 库存盘点任务表';

-- ----------------------------
-- Records of mes_wm_stock_taking_task
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_stock_taking_task (id, code, name, taking_date, type, user_id, plan_id, blind_flag, frozen, start_time, end_time, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 'TASK2026031001', '原料仓全盘任务', '2026-03-10 08:00:00', 1, 1, 1, '0', '1', '2026-03-10 08:30:00', '2026-03-10 17:30:00', 4, '已完成，差异已调整', '1', '2026-03-09 10:00:00', '1', '2026-03-10 17:30:00', '0', 1), (2, 'TASK2026031101', '成品仓全盘任务', '2026-03-11 08:00:00', 1, 2, 1, '0', '1', '2026-03-11 08:30:00', NULL, 1, '盘点进行中', '1', '2026-03-09 10:00:00', '1', '2026-03-11 09:00:00', '0', 1), (3, 'TASK2026031201', '辅料仓全盘任务', '1970-01-01 08:00:00', 1, 1, 3, '0', '0', NULL, NULL, 2, '待开始盘点', '1', '2026-03-09 10:00:00', '1', '2026-03-12 01:06:56', '0', 1), (4, 'TASK2026032001', '原料仓A区抽盘', '2026-03-20 08:00:00', 2, 3, 2, '1', '0', '2026-03-20 08:30:00', '2026-03-20 12:00:00', 4, '盲盘完成，无差异', '1', '2026-03-09 11:00:00', '1', '2026-03-20 12:00:00', '0', 1), (5, 'TASK2026032101', '原料仓B区抽盘', '2026-03-21 08:00:00', 2, 3, 2, '1', '0', '2026-03-21 08:30:00', NULL, 1, '盲盘进行中', '1', '2026-03-09 11:00:00', '1', '2026-03-21 09:00:00', '0', 1), (6, 'TASK2026030501', '成品仓循环盘点-第1轮', '2026-03-05 08:00:00', 1, 2, 3, '0', '0', '2026-03-05 08:30:00', '2026-03-05 16:00:00', 4, '第1轮盘点完成', '1', '2026-03-04 10:00:00', '1', '2026-03-05 16:00:00', '0', 1), (7, 'TASK2026030601', '成品仓循环盘点-第2轮', '2026-03-06 08:00:00', 1, 2, 3, '0', '0', '2026-03-06 08:30:00', '2026-03-06 11:00:00', 5, '方案禁用，任务取消', '1', '2026-03-04 10:00:00', '1', '2026-03-06 11:00:00', '0', 1), (13, 'STOHHQIEQp0q', '消息', '1970-01-01 08:00:00', 1, 1, 1, '0', '1', '2026-03-10 08:00:00', '2026-03-15 18:00:00', 4, '123321', '1', '2026-03-10 19:15:09', '1', '2026-03-12 01:05:28', '0', 1), (15, 'PDT202604060001', '2026年第一季度全盘', '1970-01-01 08:00:00', 1, 1, 1, '0', '1', '2026-03-10 08:00:00', '2026-03-15 18:00:00', 0, NULL, '1', '2026-04-06 12:05:17', '1', '2026-04-06 12:05:17', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_stock_taking_task_seq;
CREATE SEQUENCE mes_wm_stock_taking_task_seq
    START 16;

-- ----------------------------
-- Table structure for mes_wm_stock_taking_task_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_stock_taking_task_line;
CREATE TABLE mes_wm_stock_taking_task_line (
    id int8 NOT NULL,
  task_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(50) NULL DEFAULT NULL,
  quantity numeric(24,6) NOT NULL DEFAULT 0.000000,
  taking_quantity numeric(24,6) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  status int2 NOT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_stock_taking_task_line ADD CONSTRAINT pk_mes_wm_stock_taking_task_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_stock_taking_task_line_01 ON mes_wm_stock_taking_task_line (task_id);

COMMENT ON COLUMN mes_wm_stock_taking_task_line.id IS '编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.task_id IS '盘点任务编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.material_stock_id IS '库存编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.batch_code IS '批次编码';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.quantity IS '账面数量';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.taking_quantity IS '盘点数量';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.status IS '盘点状态';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_stock_taking_task_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_stock_taking_task_line IS 'MES 盘点任务行';

-- ----------------------------
-- Records of mes_wm_stock_taking_task_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_stock_taking_task_line (id, task_id, material_stock_id, item_id, batch_id, batch_code, quantity, taking_quantity, warehouse_id, location_id, area_id, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 13, 4, 69, NULL, NULL, 737.000000, NULL, 702, 713, 724, 0, NULL, '1', '2026-03-10 19:15:09', '1', '2026-03-10 23:51:23', '1', 1), (2, 13, NULL, 69, NULL, NULL, 123.000000, 1.000000, 702, 713, 724, 3, NULL, '1', '2026-03-10 23:51:19', '1', '2026-03-10 23:51:19', '0', 1), (3, 3, NULL, 69, NULL, NULL, 1.000000, 1.000000, 702, 713, 724, 1, '222222', '1', '2026-03-12 01:06:49', '1', '2026-03-12 01:06:49', '0', 1), (4, 15, 16, 69, 0, NULL, 100.000000, 0.000000, 702, 711, 721, 3, NULL, '1', '2026-04-06 12:05:17', '1', '2026-04-06 12:05:17', '0', 1), (5, 15, 79, 69, 888, NULL, 80.000000, 0.000000, 702, 711, 721, 3, NULL, '1', '2026-04-06 12:05:17', '1', '2026-04-06 12:05:17', '0', 1), (6, 15, 48, 69, 999, NULL, 50.000000, 0.000000, 702, 711, 721, 3, NULL, '1', '2026-04-06 12:05:17', '1', '2026-04-06 12:05:17', '0', 1), (7, 15, NULL, 100, 1, NULL, 800.000000, NULL, 701, NULL, NULL, 0, NULL, '1', '2026-04-06 12:05:25', '1', '2026-04-06 12:07:45', '1', 1), (8, 15, NULL, 100, 1, NULL, 200.000000, NULL, 702, NULL, NULL, 0, NULL, '1', '2026-04-06 12:05:25', '1', '2026-04-06 12:07:48', '1', 1), (9, 15, NULL, 75, 4, NULL, 2.000000, NULL, 703, 714, 725, 0, NULL, '1', '2026-04-06 12:05:30', '1', '2026-04-06 12:07:50', '1', 1), (10, 15, NULL, 94, 7, NULL, 1.000000, NULL, 703, 714, 725, 0, NULL, '1', '2026-04-06 12:05:30', '1', '2026-04-06 12:07:52', '1', 1), (11, 15, 5, 100, 1, NULL, 800.000000, NULL, 701, NULL, NULL, 3, NULL, '1', '2026-04-06 12:14:28', '1', '2026-04-06 12:14:32', '1', 1), (12, 15, 12, 75, 4, NULL, 2.000000, NULL, 703, 714, 725, 3, NULL, '1', '2026-04-06 12:14:35', '1', '2026-04-06 12:14:35', '0', 1), (13, 15, 5, 100, 1, NULL, 800.000000, NULL, 701, NULL, NULL, 3, NULL, '1', '2026-05-30 09:12:48', '1', '2026-05-30 09:12:48', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_stock_taking_task_line_seq;
CREATE SEQUENCE mes_wm_stock_taking_task_line_seq
    START 14;

-- ----------------------------
-- Table structure for mes_wm_stock_taking_task_result
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_stock_taking_task_result;
CREATE TABLE mes_wm_stock_taking_task_result (
    id int8 NOT NULL,
  task_id int8 NOT NULL,
  line_id int8 NULL DEFAULT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  quantity numeric(10,2) NOT NULL,
  taking_quantity numeric(10,2) NOT NULL,
  remark varchar(500) NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_stock_taking_task_result ADD CONSTRAINT pk_mes_wm_stock_taking_task_result PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_stock_taking_task_result_01 ON mes_wm_stock_taking_task_result (task_id);

COMMENT ON COLUMN mes_wm_stock_taking_task_result.id IS '编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.task_id IS '盘点任务编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.line_id IS '盘点任务行编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.material_stock_id IS '库存编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.item_id IS '物料编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.batch_code IS '批次编码';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.warehouse_id IS '仓库编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.location_id IS '库区编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.area_id IS '库位编号';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.quantity IS '差异数量';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.taking_quantity IS '盘点数量';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.remark IS '备注';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.creator IS '创建者';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.updater IS '更新者';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_stock_taking_task_result.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_stock_taking_task_result IS 'MES 盘点结果表';

-- ----------------------------
-- Records of mes_wm_stock_taking_task_result
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_stock_taking_task_result (id, task_id, line_id, material_stock_id, item_id, batch_id, batch_code, warehouse_id, location_id, area_id, quantity, taking_quantity, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 13, 2, NULL, 69, NULL, NULL, 702, 713, 724, 10.00, 0.00, '111', '1', '2026-03-11 09:18:07', '1', '2026-03-12 01:05:10', '1', 1), (2, 13, 2, NULL, 69, NULL, NULL, 702, 713, 724, 123.00, 1.00, '123321321', '1', '2026-03-12 01:04:53', '1', '2026-03-12 01:04:55', '1', 1), (3, 13, 2, NULL, 69, NULL, NULL, 702, 713, 724, 123.00, 5.00, '32312321', '1', '2026-03-12 01:05:02', '1', '2026-03-12 01:05:13', '1', 1), (4, 13, 2, NULL, 69, NULL, NULL, 702, 713, 724, 123.00, 1.00, NULL, '1', '2026-03-12 01:05:18', '1', '2026-03-12 01:05:18', '0', 1), (5, 3, 3, NULL, 69, NULL, NULL, 702, 713, 724, 1.00, 1.00, NULL, '1', '2026-03-12 01:07:04', '1', '2026-03-12 01:07:04', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_stock_taking_task_result_seq;
CREATE SEQUENCE mes_wm_stock_taking_task_result_seq
    START 6;

-- ----------------------------
-- Table structure for mes_wm_transaction
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_transaction;
CREATE TABLE mes_wm_transaction (
    id int8 NOT NULL,
  type int4 NOT NULL,
  biz_type int4 NULL DEFAULT NULL,
  biz_id int8 NULL DEFAULT NULL,
  biz_code varchar(64) NULL DEFAULT NULL,
  biz_line_id int8 NULL DEFAULT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  related_transaction_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,4) NOT NULL,
  batch_id int8 NULL DEFAULT NULL,
  batch_code varchar(64) NULL DEFAULT NULL,
  warehouse_id int8 NOT NULL,
  location_id int8 NULL DEFAULT NULL,
  area_id int8 NULL DEFAULT NULL,
  transaction_time timestamp NULL DEFAULT NULL,
  erp_time timestamp NULL DEFAULT NULL,
  receipt_time timestamp NULL DEFAULT NULL,
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_transaction ADD CONSTRAINT pk_mes_wm_transaction PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_transaction_01 ON mes_wm_transaction (item_id);
CREATE INDEX idx_mes_wm_transaction_02 ON mes_wm_transaction (warehouse_id);
CREATE INDEX idx_mes_wm_transaction_03 ON mes_wm_transaction (biz_type, biz_id);

COMMENT ON COLUMN mes_wm_transaction.id IS '编号';
COMMENT ON COLUMN mes_wm_transaction.type IS '事务类型';
COMMENT ON COLUMN mes_wm_transaction.biz_type IS '业务类型';
COMMENT ON COLUMN mes_wm_transaction.biz_id IS '来源业务主单 ID';
COMMENT ON COLUMN mes_wm_transaction.biz_code IS '来源业务单号';
COMMENT ON COLUMN mes_wm_transaction.biz_line_id IS '来源业务行 ID';
COMMENT ON COLUMN mes_wm_transaction.material_stock_id IS '库存记录 ID';
COMMENT ON COLUMN mes_wm_transaction.related_transaction_id IS '关联的事务 ID';
COMMENT ON COLUMN mes_wm_transaction.item_id IS '物料 ID';
COMMENT ON COLUMN mes_wm_transaction.quantity IS '本次变动数量（正数=入库，负数=出库）';
COMMENT ON COLUMN mes_wm_transaction.batch_id IS '批次 ID';
COMMENT ON COLUMN mes_wm_transaction.batch_code IS '批次号';
COMMENT ON COLUMN mes_wm_transaction.warehouse_id IS '仓库 ID';
COMMENT ON COLUMN mes_wm_transaction.location_id IS '库区 ID';
COMMENT ON COLUMN mes_wm_transaction.area_id IS '库位 ID';
COMMENT ON COLUMN mes_wm_transaction.transaction_time IS '事务发生时间';
COMMENT ON COLUMN mes_wm_transaction.erp_time IS 'ERP 账期';
COMMENT ON COLUMN mes_wm_transaction.receipt_time IS '入库时间';
COMMENT ON COLUMN mes_wm_transaction.creator IS '创建者';
COMMENT ON COLUMN mes_wm_transaction.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_transaction.updater IS '更新者';
COMMENT ON COLUMN mes_wm_transaction.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_transaction.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_transaction.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_transaction IS 'MES 库存事务流水';

-- ----------------------------
-- Records of mes_wm_transaction
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_transaction (id, type, biz_type, biz_id, biz_code, biz_line_id, material_stock_id, related_transaction_id, item_id, quantity, batch_id, batch_code, warehouse_id, location_id, area_id, transaction_time, erp_time, receipt_time, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (1, 1, 114, 2, 'MRbJ5HI6aOMZ', 3, 4, NULL, 69, 123.0000, 1, 'TEST', 702, 713, 724, '2026-03-22 20:09:34', NULL, NULL, '1', '2026-03-22 20:09:34', '1', '2026-03-30 02:46:05', '0', 1), (2, 2, 113, 4, 'MI47CkggsQpp', 5, 4, NULL, 69, -123.0000, 1, 'TEST', 702, 713, 724, '2026-03-22 20:10:57', NULL, NULL, '1', '2026-03-22 20:10:57', '1', '2026-03-30 02:46:05', '0', 1), (3, 1, 114, 4, 'MRlD8OQUII96', 5, 9, NULL, 94, 123.0000, 7, 'BATCH_ITEM_94', 702, 713, 724, '2026-03-22 21:38:40', NULL, NULL, '1', '2026-03-22 21:38:40', '1', '2026-03-30 02:46:05', '0', 1), (5, 3, 111, 9001007, 'TRkOGr5InPcE', 9001107, 9, NULL, 94, -1.0000, 7, 'BATCH_ITEM_94', 702, 713, 724, '2026-03-22 23:11:51', NULL, NULL, '1', '2026-03-22 23:11:51', '1', '2026-03-30 02:46:05', '0', 1), (6, 4, 112, 9001007, 'TRkOGr5InPcE', 9001107, 10, 5, 94, 1.0000, 7, 'BATCH_ITEM_94', 701, 712, 723, '2026-03-22 23:11:51', NULL, NULL, '1', '2026-03-22 23:11:51', '1', '2026-03-30 02:46:05', '0', 1), (7, 2, 122, 20, '', 20, 11, NULL, 72, -1.0000, 8, 'BATCH_ITEM_72', 703, 714, 725, '2026-03-24 23:17:24', NULL, NULL, '1', '2026-03-24 23:17:24', '1', '2026-03-30 02:46:05', '0', 1), (8, 1, 123, 19, '', 3, 12, NULL, 75, 1.0000, 4, 'PC202600002', 703, 714, 725, '2026-03-24 23:17:24', NULL, NULL, '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (9, 1, 123, 19, '', 4, 12, NULL, 75, 1.0000, 4, 'PC202600002', 703, 714, 725, '2026-03-24 23:17:24', NULL, NULL, '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (10, 2, 122, 21, '', 21, 11, NULL, 72, -45.0000, 8, 'BATCH_ITEM_72', 703, 714, 725, '2026-03-24 23:19:17', NULL, NULL, '1', '2026-03-24 23:19:17', '1', '2026-03-30 02:46:05', '0', 1), (11, 1, 110, 15, 'IR20260329000001', 19, 9, NULL, 94, 1.0000, 7, 'BATCH_ITEM_94', 702, 713, 724, '2026-03-29 18:06:09', NULL, NULL, '1', '2026-03-29 18:06:09', '1', '2026-03-30 02:46:05', '0', 1), (12, 2, 124, 7, 'RV20260329000011', 9, 4, NULL, 69, -1.0000, 1, 'TEST', 702, 713, 724, '2026-03-29 23:04:43', NULL, NULL, '1', '2026-03-29 23:04:44', '1', '2026-03-29 23:04:44', '0', 1), (13, 2, 115, 8, 'PI20260330000002', 14, 10, NULL, 94, -1.0000, 7, 'BATCH_ITEM_94', 701, 712, 723, '2026-03-30 11:11:50', NULL, NULL, '1', '2026-03-30 11:11:50', '1', '2026-03-30 11:11:50', '0', 1), (14, 1, 115, 8, 'PI20260330000002', 14, 14, 13, 94, 1.0000, 7, 'BATCH_ITEM_94', 703, 714, 725, '2026-03-30 11:11:50', NULL, NULL, '1', '2026-03-30 11:11:50', '1', '2026-03-30 11:11:50', '0', 1), (16, 2, 118, 100003, 'A0', 100002, 4, NULL, 69, -736.0000, 1, 'PC', 702, 713, 724, '2026-03-30 21:54:28', NULL, NULL, '1', '2026-03-30 21:54:28', '1', '2026-03-30 21:54:28', '0', 1), (17, 1, 114, 5, 'MROO8b4vMA5x', 6, 9, NULL, 94, 321321321.0000, NULL, NULL, 702, 713, 724, '2026-03-31 09:41:23', NULL, NULL, '1', '2026-03-31 09:41:23', '1', '2026-03-31 09:41:23', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_transaction_seq;
CREATE SEQUENCE mes_wm_transaction_seq
    START 18;

-- ----------------------------
-- Table structure for mes_wm_transfer
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_transfer;
CREATE TABLE mes_wm_transfer (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NULL DEFAULT NULL,
  type int2 NOT NULL,
  delivery_flag bool NOT NULL DEFAULT '0',
  recipient_name varchar(64) NULL DEFAULT NULL,
  recipient_telephone varchar(30) NULL DEFAULT NULL,
  destination_address varchar(255) NULL DEFAULT NULL,
  carrier varchar(100) NULL DEFAULT NULL,
  shipping_number varchar(100) NULL DEFAULT NULL,
  confirm_flag bool NOT NULL DEFAULT '0',
  transfer_date timestamp NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_transfer ADD CONSTRAINT pk_mes_wm_transfer PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_transfer_01 ON mes_wm_transfer (tenant_id, code);

CREATE INDEX idx_mes_wm_transfer_01 ON mes_wm_transfer (transfer_date);
CREATE INDEX idx_mes_wm_transfer_02 ON mes_wm_transfer (status);
CREATE INDEX idx_mes_wm_transfer_03 ON mes_wm_transfer (type);

COMMENT ON COLUMN mes_wm_transfer.id IS '编号';
COMMENT ON COLUMN mes_wm_transfer.code IS '调拨单编号';
COMMENT ON COLUMN mes_wm_transfer.name IS '调拨单名称';
COMMENT ON COLUMN mes_wm_transfer.type IS '调拨类型';
COMMENT ON COLUMN mes_wm_transfer.delivery_flag IS '是否配送';
COMMENT ON COLUMN mes_wm_transfer.recipient_name IS '收货人';
COMMENT ON COLUMN mes_wm_transfer.recipient_telephone IS '联系电话';
COMMENT ON COLUMN mes_wm_transfer.destination_address IS '目的地';
COMMENT ON COLUMN mes_wm_transfer.carrier IS '承运商';
COMMENT ON COLUMN mes_wm_transfer.shipping_number IS '运输单号';
COMMENT ON COLUMN mes_wm_transfer.confirm_flag IS '是否已确认';
COMMENT ON COLUMN mes_wm_transfer.transfer_date IS '调拨日期';
COMMENT ON COLUMN mes_wm_transfer.status IS '状态';
COMMENT ON COLUMN mes_wm_transfer.remark IS '备注';
COMMENT ON COLUMN mes_wm_transfer.creator IS '创建者';
COMMENT ON COLUMN mes_wm_transfer.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_transfer.updater IS '更新者';
COMMENT ON COLUMN mes_wm_transfer.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_transfer.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_transfer.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_transfer IS 'MES 调拨单';

-- ----------------------------
-- Records of mes_wm_transfer
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_transfer (id, code, name, type, delivery_flag, recipient_name, recipient_telephone, destination_address, carrier, shipping_number, confirm_flag, transfer_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (9001001, 'TR202603080001', '内部调拨测试单', 1, '0', NULL, NULL, NULL, NULL, NULL, '0', '2026-03-08 11:55:25', 2, '内部调拨测试数据', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001002, 'TR202603080002', '外部调拨测试单', 2, '1', '张三', '13800000000', '苏州市工业园区测试地址', '顺丰', 'SF202603080001', '1', '2026-03-08 11:55:25', 3, '外部调拨测试数据', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001003, 'TRUYLDMn9kCH', '呃呃呃', 1, '0', NULL, NULL, NULL, NULL, NULL, '0', '1970-01-01 08:00:00', 0, '', '1', '2026-03-08 20:17:07', '1', '2026-03-08 20:17:07', '0', 1), (9001004, 'TRhmRNkKyIfO', '111', 2, '1', '12', '321', '3123231', NULL, NULL, '1', '1970-01-01 08:00:00', 2, '', '1', '2026-03-08 21:58:04', '1', '2026-03-08 22:27:14', '0', 1), (9001005, 'TRALcyZ45UwL', 'XXX01', 2, '1', '32132', '3213213', '321321321', NULL, NULL, '1', '1970-01-01 08:00:00', 4, '', '1', '2026-03-08 21:58:32', '1', '2026-03-08 21:58:32', '0', 1), (9001006, 'TRiwBTVEp5un', 'XXX', 1, '0', NULL, NULL, NULL, NULL, NULL, '0', '1970-01-01 08:00:00', 3, '', '1', '2026-03-08 22:24:06', '1', '2026-03-08 22:24:06', '0', 1), (9001007, 'TRkOGr5InPcE', '123', 1, '0', NULL, NULL, NULL, NULL, NULL, '0', '1970-01-01 08:00:00', 4, '321321', '1', '2026-03-22 23:11:15', '1', '2026-03-22 23:11:31', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_transfer_seq;
CREATE SEQUENCE mes_wm_transfer_seq
    START 9001008;

-- ----------------------------
-- Table structure for mes_wm_transfer_detail
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_transfer_detail;
CREATE TABLE mes_wm_transfer_detail (
    id int8 NOT NULL,
  line_id int8 NOT NULL,
  transfer_id int8 NOT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  to_warehouse_id int8 NULL DEFAULT NULL,
  to_location_id int8 NULL DEFAULT NULL,
  to_area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_transfer_detail ADD CONSTRAINT pk_mes_wm_transfer_detail PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_transfer_detail_01 ON mes_wm_transfer_detail (line_id);
CREATE INDEX idx_mes_wm_transfer_detail_02 ON mes_wm_transfer_detail (transfer_id);
CREATE INDEX idx_mes_wm_transfer_detail_03 ON mes_wm_transfer_detail (item_id);

COMMENT ON COLUMN mes_wm_transfer_detail.id IS '编号';
COMMENT ON COLUMN mes_wm_transfer_detail.line_id IS '调拨单行编号（关联 mes_wm_transfer_line.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.transfer_id IS '调拨单编号（关联 mes_wm_transfer.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.item_id IS '物料编号（关联 mes_md_item.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.quantity IS '调拨数量';
COMMENT ON COLUMN mes_wm_transfer_detail.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_transfer_detail.to_warehouse_id IS '移入仓库编号（关联 mes_wm_warehouse.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.to_location_id IS '移入库区编号（关联 mes_wm_warehouse_location.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.to_area_id IS '移入库位编号（关联 mes_wm_warehouse_area.id）';
COMMENT ON COLUMN mes_wm_transfer_detail.remark IS '备注';
COMMENT ON COLUMN mes_wm_transfer_detail.creator IS '创建者';
COMMENT ON COLUMN mes_wm_transfer_detail.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_transfer_detail.updater IS '更新者';
COMMENT ON COLUMN mes_wm_transfer_detail.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_transfer_detail.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_transfer_detail.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_transfer_detail IS 'MES 调拨明细';

-- ----------------------------
-- Records of mes_wm_transfer_detail
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_transfer_detail (id, line_id, transfer_id, item_id, quantity, batch_id, to_warehouse_id, to_location_id, to_area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (9001201, 9001101, 9001001, 20001, 6.00, NULL, 30003, 31003, 32004, '内部调拨明细1', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001202, 9001101, 9001001, 20001, 4.00, NULL, 30003, 31003, 32005, '内部调拨明细2', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001203, 9001103, 9001002, 20003, 8.00, NULL, 30004, 31004, 32006, '外部调拨明细1', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001204, 9001104, 9001005, 69, 10.00, NULL, 702, 713, 724, '', '1', '2026-03-08 22:20:13', '1', '2026-03-08 22:20:13', '0', 1), (9001205, 9001106, 9001004, 69, 1.00, NULL, 702, 713, 724, '', '1', '2026-03-08 22:32:06', '1', '2026-03-08 22:32:06', '0', 1), (9001206, 9001105, 9001006, 69, 10.00, NULL, 702, 713, 724, '', '1', '2026-03-22 23:10:59', '1', '2026-03-22 23:10:59', '0', 1), (9001207, 9001107, 9001007, 94, 1.00, NULL, 701, 712, 723, '', '1', '2026-03-22 23:11:47', '1', '2026-03-22 23:11:47', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_transfer_detail_seq;
CREATE SEQUENCE mes_wm_transfer_detail_seq
    START 9001208;

-- ----------------------------
-- Table structure for mes_wm_transfer_line
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_transfer_line;
CREATE TABLE mes_wm_transfer_line (
    id int8 NOT NULL,
  transfer_id int8 NOT NULL,
  material_stock_id int8 NULL DEFAULT NULL,
  item_id int8 NOT NULL,
  quantity numeric(14,2) NULL DEFAULT NULL,
  batch_id int8 NULL DEFAULT NULL,
  from_warehouse_id int8 NULL DEFAULT NULL,
  from_location_id int8 NULL DEFAULT NULL,
  from_area_id int8 NULL DEFAULT NULL,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_transfer_line ADD CONSTRAINT pk_mes_wm_transfer_line PRIMARY KEY (id);

CREATE INDEX idx_mes_wm_transfer_line_01 ON mes_wm_transfer_line (transfer_id);
CREATE INDEX idx_mes_wm_transfer_line_02 ON mes_wm_transfer_line (material_stock_id);
CREATE INDEX idx_mes_wm_transfer_line_03 ON mes_wm_transfer_line (item_id);

COMMENT ON COLUMN mes_wm_transfer_line.id IS '编号';
COMMENT ON COLUMN mes_wm_transfer_line.transfer_id IS '调拨单编号（关联 mes_wm_transfer.id）';
COMMENT ON COLUMN mes_wm_transfer_line.material_stock_id IS '来源库存记录编号（关联 mes_wm_material_stock.id）';
COMMENT ON COLUMN mes_wm_transfer_line.item_id IS '物料编号（关联 mes_md_item.id）';
COMMENT ON COLUMN mes_wm_transfer_line.quantity IS '调拨数量';
COMMENT ON COLUMN mes_wm_transfer_line.batch_id IS '批次编号';
COMMENT ON COLUMN mes_wm_transfer_line.from_warehouse_id IS '移出仓库编号（关联 mes_wm_warehouse.id）';
COMMENT ON COLUMN mes_wm_transfer_line.from_location_id IS '移出库区编号（关联 mes_wm_warehouse_location.id）';
COMMENT ON COLUMN mes_wm_transfer_line.from_area_id IS '移出库位编号（关联 mes_wm_warehouse_area.id）';
COMMENT ON COLUMN mes_wm_transfer_line.remark IS '备注';
COMMENT ON COLUMN mes_wm_transfer_line.creator IS '创建者';
COMMENT ON COLUMN mes_wm_transfer_line.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_transfer_line.updater IS '更新者';
COMMENT ON COLUMN mes_wm_transfer_line.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_transfer_line.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_transfer_line.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_transfer_line IS 'MES 调拨单行';

-- ----------------------------
-- Records of mes_wm_transfer_line
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_transfer_line (id, transfer_id, material_stock_id, item_id, quantity, batch_id, from_warehouse_id, from_location_id, from_area_id, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (9001101, 9001001, 10001, 20001, 10.00, NULL, 30001, 31001, 32001, '内部调拨行1', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001102, 9001001, 10002, 20002, 5.00, NULL, 30001, 31001, 32002, '内部调拨行2', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001103, 9001002, 10003, 20003, 8.00, NULL, 30002, 31002, 32003, '外部调拨行1', '1', '2026-03-08 11:55:25', '1', '2026-03-08 11:55:25', '0', 1), (9001104, 9001005, NULL, 69, 10.00, NULL, 702, 713, 724, '12323', '1', '2026-03-08 22:08:47', '1', '2026-03-08 22:08:47', '0', 1), (9001105, 9001006, NULL, 69, 10.00, NULL, 702, 713, 724, '12323', '1', '2026-03-08 22:24:16', '1', '2026-03-08 22:24:16', '0', 1), (9001106, 9001004, NULL, 69, 10.00, NULL, 702, 713, 724, '', '1', '2026-03-08 22:27:12', '1', '2026-03-08 22:27:12', '0', 1), (9001107, 9001007, NULL, 94, 1.00, NULL, 702, 713, 724, '123', '1', '2026-03-22 23:11:29', '1', '2026-03-22 23:11:29', '0', 1), (9001108, 9001003, 1, 70, 500.00, 5, 701, 711, 721, '', '1', '2026-04-07 00:31:31', '1', '2026-04-07 00:31:31', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_transfer_line_seq;
CREATE SEQUENCE mes_wm_transfer_line_seq
    START 9001109;

-- ----------------------------
-- Table structure for mes_wm_warehouse
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_warehouse;
CREATE TABLE mes_wm_warehouse (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  address varchar(255) NULL DEFAULT NULL,
  area numeric(14,2) NULL DEFAULT NULL,
  charge_user_id int8 NULL DEFAULT NULL,
  frozen int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_warehouse ADD CONSTRAINT pk_mes_wm_warehouse PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_warehouse_01 ON mes_wm_warehouse (tenant_id, code);
CREATE UNIQUE INDEX uk_mes_wm_warehouse_02 ON mes_wm_warehouse (tenant_id, name);

COMMENT ON COLUMN mes_wm_warehouse.id IS '编号';
COMMENT ON COLUMN mes_wm_warehouse.code IS '仓库编码';
COMMENT ON COLUMN mes_wm_warehouse.name IS '仓库名称';
COMMENT ON COLUMN mes_wm_warehouse.address IS '仓库地址';
COMMENT ON COLUMN mes_wm_warehouse.area IS '仓库面积（平方米）';
COMMENT ON COLUMN mes_wm_warehouse.charge_user_id IS '负责人用户编号（system_users.id）';
COMMENT ON COLUMN mes_wm_warehouse.frozen IS '是否冻结';
COMMENT ON COLUMN mes_wm_warehouse.remark IS '备注';
COMMENT ON COLUMN mes_wm_warehouse.creator IS '创建者';
COMMENT ON COLUMN mes_wm_warehouse.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_warehouse.updater IS '更新者';
COMMENT ON COLUMN mes_wm_warehouse.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_warehouse.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_warehouse.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_warehouse IS 'MES 仓库主数据表';

-- ----------------------------
-- Records of mes_wm_warehouse
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_warehouse (id, code, name, address, area, charge_user_id, frozen, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (701, 'WH-RAW', '原料仓', 'A 区 1 号', 1200.00, 1, 0, '原材料存放仓', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (702, 'WH-FIN', '成品仓', 'B 区 2 号', 900.00, 1, 0, '成品发运仓', '1', '2026-02-17 13:45:34', '1', '2026-03-28 20:16:59', '0', 1), (703, 'WIP_VIRTUAL_WAREHOUSE', '虚拟线边仓库', NULL, NULL, NULL, 0, '系统自动初始化的虚拟线边仓库（用于生产报工与在制品管理解耦）', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_warehouse_seq;
CREATE SEQUENCE mes_wm_warehouse_seq
    START 704;

-- ----------------------------
-- Table structure for mes_wm_warehouse_area
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_warehouse_area;
CREATE TABLE mes_wm_warehouse_area (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  location_id int8 NOT NULL,
  area numeric(14,2) NULL DEFAULT NULL,
  max_load numeric(14,2) NULL DEFAULT NULL,
  position_x int4 NULL DEFAULT NULL,
  position_y int4 NULL DEFAULT NULL,
  position_z int4 NULL DEFAULT NULL,
  status int2 NOT NULL DEFAULT 0,
  frozen int2 NOT NULL DEFAULT 0,
  allow_item_mixing int2 NOT NULL DEFAULT 1,
  allow_batch_mixing int2 NOT NULL DEFAULT 1,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_warehouse_area ADD CONSTRAINT pk_mes_wm_warehouse_area PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_warehouse_area_01 ON mes_wm_warehouse_area (tenant_id, location_id, code);
CREATE UNIQUE INDEX uk_mes_wm_warehouse_area_02 ON mes_wm_warehouse_area (tenant_id, location_id, name);

CREATE INDEX idx_mes_wm_warehouse_area_01 ON mes_wm_warehouse_area (location_id);

COMMENT ON COLUMN mes_wm_warehouse_area.id IS '编号';
COMMENT ON COLUMN mes_wm_warehouse_area.code IS '库位编码';
COMMENT ON COLUMN mes_wm_warehouse_area.name IS '库位名称';
COMMENT ON COLUMN mes_wm_warehouse_area.location_id IS '所属库区编号（mes_wm_warehouse_location.id）';
COMMENT ON COLUMN mes_wm_warehouse_area.area IS '库位面积（平方米）';
COMMENT ON COLUMN mes_wm_warehouse_area.max_load IS '最大载重';
COMMENT ON COLUMN mes_wm_warehouse_area.position_x IS '库位位置 X';
COMMENT ON COLUMN mes_wm_warehouse_area.position_y IS '库位位置 Y';
COMMENT ON COLUMN mes_wm_warehouse_area.position_z IS '库位位置 Z';
COMMENT ON COLUMN mes_wm_warehouse_area.status IS '状态（0正常 1停用）';
COMMENT ON COLUMN mes_wm_warehouse_area.frozen IS '是否冻结';
COMMENT ON COLUMN mes_wm_warehouse_area.allow_item_mixing IS '是否允许物料混放';
COMMENT ON COLUMN mes_wm_warehouse_area.allow_batch_mixing IS '是否允许批次混放';
COMMENT ON COLUMN mes_wm_warehouse_area.remark IS '备注';
COMMENT ON COLUMN mes_wm_warehouse_area.creator IS '创建者';
COMMENT ON COLUMN mes_wm_warehouse_area.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_warehouse_area.updater IS '更新者';
COMMENT ON COLUMN mes_wm_warehouse_area.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_warehouse_area.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_warehouse_area.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_warehouse_area IS 'MES 库位主数据表';

-- ----------------------------
-- Records of mes_wm_warehouse_area
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_warehouse_area (id, code, name, location_id, area, max_load, position_x, position_y, position_z, status, frozen, allow_item_mixing, allow_batch_mixing, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (721, 'AREA-RAW-A-01', '原料A-01', 711, 20.00, 1000.00, 1, 1, 1, 0, 0, 1, 1, '', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (722, 'AREA-RAW-A-02', '原料A-02', 711, 20.00, 1000.00, 1, 2, 1, 0, 0, 1, 1, '', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (723, 'AREA-RAW-B-01', '原料B-01', 712, 25.00, 1200.00, 2, 1, 1, 0, 0, 1, 1, '', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (724, 'AREA-FIN-A-01', '成品A-01', 713, 30.00, 1500.00, 3, 1, 1, 0, 0, 1, 1, '', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (725, 'WIP_VIRTUAL_AREA', '虚拟线边库位', 714, NULL, NULL, NULL, NULL, NULL, 0, 0, 1, 1, '系统自动初始化的虚拟线边库位', '1', '2026-03-24 23:17:24', '1', '2026-03-28 23:26:41', '0', 1), (726, 'AREA-RAW-A-03', '原料A-03', 711, 18.00, 1200.00, 1, 1, 3, 0, 0, 1, 1, '文档截图测试数据', 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_warehouse_area_seq;
CREATE SEQUENCE mes_wm_warehouse_area_seq
    START 727;

-- ----------------------------
-- Table structure for mes_wm_warehouse_location
-- ----------------------------
DROP TABLE IF EXISTS mes_wm_warehouse_location;
CREATE TABLE mes_wm_warehouse_location (
    id int8 NOT NULL,
  code varchar(64) NOT NULL,
  name varchar(255) NOT NULL,
  warehouse_id int8 NOT NULL,
  area numeric(14,2) NULL DEFAULT NULL,
  frozen int2 NOT NULL DEFAULT 0,
  remark varchar(500) NULL DEFAULT '',
  creator varchar(64) NULL DEFAULT '',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater varchar(64) NULL DEFAULT '',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted int2 NOT NULL DEFAULT 0,
  tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE mes_wm_warehouse_location ADD CONSTRAINT pk_mes_wm_warehouse_location PRIMARY KEY (id);

CREATE UNIQUE INDEX uk_mes_wm_warehouse_location_01 ON mes_wm_warehouse_location (tenant_id, warehouse_id, code);
CREATE UNIQUE INDEX uk_mes_wm_warehouse_location_02 ON mes_wm_warehouse_location (tenant_id, warehouse_id, name);

CREATE INDEX idx_mes_wm_warehouse_location_01 ON mes_wm_warehouse_location (warehouse_id);

COMMENT ON COLUMN mes_wm_warehouse_location.id IS '编号';
COMMENT ON COLUMN mes_wm_warehouse_location.code IS '库区编码';
COMMENT ON COLUMN mes_wm_warehouse_location.name IS '库区名称';
COMMENT ON COLUMN mes_wm_warehouse_location.warehouse_id IS '所属仓库编号（mes_wm_warehouse.id）';
COMMENT ON COLUMN mes_wm_warehouse_location.area IS '库区面积（平方米）';
COMMENT ON COLUMN mes_wm_warehouse_location.frozen IS '是否冻结';
COMMENT ON COLUMN mes_wm_warehouse_location.remark IS '备注';
COMMENT ON COLUMN mes_wm_warehouse_location.creator IS '创建者';
COMMENT ON COLUMN mes_wm_warehouse_location.create_time IS '创建时间';
COMMENT ON COLUMN mes_wm_warehouse_location.updater IS '更新者';
COMMENT ON COLUMN mes_wm_warehouse_location.update_time IS '更新时间';
COMMENT ON COLUMN mes_wm_warehouse_location.deleted IS '是否删除';
COMMENT ON COLUMN mes_wm_warehouse_location.tenant_id IS '租户编号';
COMMENT ON TABLE mes_wm_warehouse_location IS 'MES 库区主数据表';

-- ----------------------------
-- Records of mes_wm_warehouse_location
-- ----------------------------
-- @formatter:off
BEGIN;
INSERT INTO mes_wm_warehouse_location (id, code, name, warehouse_id, area, frozen, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES (711, 'LOC-RAW-A', '原料 A 区', 701, 400.00, 0, '大宗原料区', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (712, 'LOC-RAW-B', '原料 B 区', 701, 300.00, 0, '小料区', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (713, 'LOC-FIN-A', '成品 A 区', 702, 500.00, 0, '待发货区', '1', '2026-02-17 13:45:34', '1', '2026-02-17 13:45:34', '0', 1), (714, 'WIP_VIRTUAL_LOCATION', '虚拟线边库区', 703, NULL, 0, '系统自动初始化的虚拟线边库区', '1', '2026-03-24 23:17:24', '1', '2026-03-24 23:17:24', '0', 1), (715, 'LOC-RAW-C', '原料 C 区', 701, 420.00, 0, '文档截图测试数据', 'admin', '2026-04-15 11:02:42', 'admin', '2026-04-15 11:02:42', '0', 1);
COMMIT;
-- @formatter:on

DROP SEQUENCE IF EXISTS mes_wm_warehouse_location_seq;
CREATE SEQUENCE mes_wm_warehouse_location_seq
    START 716;

