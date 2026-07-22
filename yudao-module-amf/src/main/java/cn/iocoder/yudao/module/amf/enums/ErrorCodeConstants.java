package cn.iocoder.yudao.module.amf.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * AMF 错误码枚举类
 *
 * amf 系统，使用 1-040-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 分析方法文件业务单据（1-040-000-000） ==========
    ErrorCode AMF_BUSINESS_NOT_EXISTS = new ErrorCode(1_040_000_000, "分析方法文件不存在");
    ErrorCode AMF_BUSINESS_BAS_NO_DUPLICATE = new ErrorCode(1_040_000_001, "已存在相同方法编号的分析方法文件");

    // ========== 文件版本记录（1-040-001-000） ==========
    ErrorCode AMF_FILE_NOT_EXISTS = new ErrorCode(1_040_000_002, "文件记录不存在");
    ErrorCode AMF_FILE_VERSION_NOT_EXISTS = new ErrorCode(1_040_001_000, "文件版本记录不存在");
    ErrorCode AMF_FILE_VERSION_NOT_BELONG_TO_BUSINESS = new ErrorCode(1_040_001_001, "文件版本记录不属于该业务单据");
    ErrorCode AMF_FILE_VERSION_DUPLICATE = new ErrorCode(1_040_001_002, "版本号已存在，请使用其他版本号");
    ErrorCode AMF_FILE_VERSION_EMPTY = new ErrorCode(1_040_001_003, "版本号不能为空");

}
