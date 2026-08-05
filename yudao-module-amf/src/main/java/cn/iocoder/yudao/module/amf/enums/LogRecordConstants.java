package cn.iocoder.yudao.module.amf.enums;

/**
 * AMF 操作日志的模块名和子类型常量
 *
 * @author yudao
 */
public interface LogRecordConstants {

    // ==================== 分析方法 ====================

    String AMF_BUSINESS_TYPE = "分析方法";
    String AMF_BUSINESS_CREATE_SUB_TYPE = "创建分析方法";
    String AMF_BUSINESS_CREATE_SUCCESS = "创建了分析方法【{{#business.methodNo}}】";
    String AMF_BUSINESS_UPDATE_SUB_TYPE = "更新分析方法";
    String AMF_BUSINESS_UPDATE_SUCCESS = "更新了分析方法【{{#business.methodNo}}】: {_DIFF{#updateReqVO}}";
    String AMF_BUSINESS_DELETE_SUB_TYPE = "删除分析方法";
    String AMF_BUSINESS_DELETE_SUCCESS = "删除了分析方法【{{#business.methodNo}}】";

    // ==================== 文件 ====================

    String AMF_FILE_TYPE = "分析方法文件";
    String AMF_FILE_UPLOAD_SUB_TYPE = "上传文件";
    String AMF_FILE_UPLOAD_SUCCESS = "上传了文件【{{#fileName}}】（版本{{#versionNo}}）";
    String AMF_FILE_DELETE_SUB_TYPE = "删除文件";
    String AMF_FILE_DELETE_SUCCESS = "删除了文件【{{#fileName}}】";

    // ==================== 文件版本 ====================

    String AMF_FILE_VERSION_TYPE = "分析方法文件版本";
    String AMF_FILE_VERSION_DELETE_SUB_TYPE = "删除文件版本";
    String AMF_FILE_VERSION_DELETE_SUCCESS = "删除了文件【{{#fileName}}】的版本【{{#versionNo}}】";

}
