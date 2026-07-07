package cn.iocoder.yudao.module.amf.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 文件上传 - Request VO（上传新版本文件）
 */
@Schema(description = "管理后台 - 分析方法文件上传 Request VO")
@Data
public class AmfFileUploadReqVO {

    @Schema(description = "业务单据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "业务单据ID不能为空")
    private Long businessId;

    @Schema(description = "变更说明", example = "更新了分析方法步骤二")
    private String changeDescription;

}
