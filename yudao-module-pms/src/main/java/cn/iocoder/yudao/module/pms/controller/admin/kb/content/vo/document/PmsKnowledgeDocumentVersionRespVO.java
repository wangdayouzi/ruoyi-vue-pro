package cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - PMS 知识库文档历史版本")
@Data
public class PmsKnowledgeDocumentVersionRespVO {
    private Long versionNo;
    private String title;
    private String content;
    private Integer type;
    private String fileType;
    private Long fileSize;
    private Long editorUserId;
    private LocalDateTime createTime;
}
