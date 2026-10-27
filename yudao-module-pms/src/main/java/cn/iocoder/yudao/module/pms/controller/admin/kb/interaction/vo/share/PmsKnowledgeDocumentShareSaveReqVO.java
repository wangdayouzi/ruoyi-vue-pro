package cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - PMS 知识库文档分享保存 Request VO")
@Data
public class PmsKnowledgeDocumentShareSaveReqVO {

    @Schema(description = "文档编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "文档编号不能为空")
    private Long documentId;

    @Schema(description = "内部分享成员用户编号列表")
    private List<@NotNull(message = "分享成员用户编号不能为空") Long> shareUserIds;

    @Schema(description = "公开分享失效时间；不传表示永久有效")
    private LocalDateTime expireTime;

    @Schema(description = "公开分享访问码；留空表示无需访问码")
    @Size(max = 64, message = "访问码不能超过 64 个字符")
    private String accessCode;

    @Schema(description = "更新分享时清除现有访问码")
    private Boolean removeAccessCode;

}
