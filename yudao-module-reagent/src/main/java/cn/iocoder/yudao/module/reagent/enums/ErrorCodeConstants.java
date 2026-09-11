package cn.iocoder.yudao.module.reagent.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Reagent 错误码枚举
 *
 * @author yudao
 */
public interface ErrorCodeConstants {

    // ========== 试剂基础数据 (1-050-000-000) ==========
    ErrorCode REAGENT_BASE_NOT_EXISTS      = new ErrorCode(1_050_000_000, "试剂不存在");
    ErrorCode REAGENT_BASE_BAS_ID_DUPLICATE = new ErrorCode(1_050_000_001, "试剂编号已存在");

    // ========== 试剂批号 (1-050-001-000) ==========
    ErrorCode REAGENT_LOT_NOT_EXISTS       = new ErrorCode(1_050_001_000, "试剂批号不存在");
    ErrorCode REAGENT_LOT_NO_DUPLICATE     = new ErrorCode(1_050_001_001, "该试剂下批号已存在");

    // ========== 申请单 (1-050-002-000) ==========
    ErrorCode REAGENT_APPLY_NOT_EXISTS     = new ErrorCode(1_050_002_000, "申请单不存在");
    ErrorCode REAGENT_APPLY_CANNOT_EDIT    = new ErrorCode(1_050_002_001, "当前状态不允许修改");
    ErrorCode REAGENT_APPLY_NO_ITEMS       = new ErrorCode(1_050_002_002, "申请单明细不能为空");
    ErrorCode REAGENT_APPLY_QTY_EXCEEDED   = new ErrorCode(1_050_002_003, "发货数量超过需求数量");

    // ========== 发货单 (1-050-003-000) ==========
    ErrorCode REAGENT_SHIPMENT_NOT_EXISTS  = new ErrorCode(1_050_003_000, "发货单不存在");
    ErrorCode REAGENT_SHIPMENT_QTY_REQUIRED = new ErrorCode(1_050_003_002, "本次发货数量必须大于0");

    // ========== 试剂标签打印 (1-050-004-000) ==========
    ErrorCode REAGENT_LABEL_PRINTER_NOT_AVAILABLE = new ErrorCode(1_050_004_000, "标签打印机不存在或未启用");
    ErrorCode REAGENT_LABEL_JOB_NOT_EXISTS = new ErrorCode(1_050_004_001, "标签打印任务不存在");
    ErrorCode REAGENT_LABEL_AGENT_UNAUTHORIZED = new ErrorCode(1_050_004_002, "标签打印代理身份校验失败");
    ErrorCode REAGENT_LABEL_JOB_STATUS_INVALID = new ErrorCode(1_050_004_003, "标签打印任务状态不合法");
    ErrorCode REAGENT_LABEL_TEMPLATE_NOT_AVAILABLE = new ErrorCode(1_050_004_004, "标签模板不存在或未启用");

}
