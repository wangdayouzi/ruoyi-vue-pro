-- 样品领用台账：不关联试剂库存、ERP 或 LIMS。
CREATE TABLE IF NOT EXISTS `sample_loan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bas_no` varchar(64) NOT NULL COMMENT 'BAS号',
  `requester_id` bigint DEFAULT NULL COMMENT '需求人用户ID',
  `requester` varchar(64) NOT NULL COMMENT '需求人',
  `submitter_id` bigint DEFAULT NULL COMMENT '提单人用户ID',
  `submitter` varchar(64) DEFAULT NULL COMMENT '提单人',
  `sample_info` varchar(500) DEFAULT NULL COMMENT '样品信息',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL COMMENT '状态：1-领用中，2-已归还',
  `return_time` datetime DEFAULT NULL COMMENT '实际归还时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_sample_loan_bas_status` (`bas_no`, `status`),
  KEY `idx_sample_loan_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='样品领用台账';

-- 可见菜单：挂到已有“试剂管理”目录。大屏路由不写入 system_menu。
SET @reagent_parent_id := (
  SELECT `id` FROM `system_menu` WHERE `name` = '试剂管理' AND `deleted` = b'0' ORDER BY `id` LIMIT 1
);

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 9901001, '样品可领用列表', '', 2, 99, @reagent_parent_id, 'sample-loan', 'ep:document', 'reagent/sample-loan/index', 'ReagentSampleLoan', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
WHERE @reagent_parent_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 9901001 OR (`parent_id` = @reagent_parent_id AND `path` = 'sample-loan' AND `deleted` = b'0'));

SET @sample_loan_menu_id := (
  SELECT `id` FROM `system_menu` WHERE `parent_id` = @reagent_parent_id AND `path` = 'sample-loan' AND `deleted` = b'0' ORDER BY `id` LIMIT 1
);

-- 角色授权时给样管分配查询、新增领用、归还三项即可。
INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT x.id, x.name, x.permission, 3, x.sort, @sample_loan_menu_id, '', '', '', '', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM (
  SELECT 9901002 AS id, '查询' AS name, 'reagent:sample-loan:query' AS permission, 1 AS sort
  UNION ALL SELECT 9901003, '新增领用', 'reagent:sample-loan:create', 2
  UNION ALL SELECT 9901004, '归还', 'reagent:sample-loan:return', 3
) x
WHERE @sample_loan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `system_menu` m WHERE m.`id` = x.id OR (m.`parent_id` = @sample_loan_menu_id AND m.`permission` = x.permission AND m.`deleted` = b'0'));
