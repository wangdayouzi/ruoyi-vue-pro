package cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PmsKnowledgeDocumentDraftSaveReqVO {
    @NotNull
    private Long documentId;
    @NotNull
    private Long baseVersionNo;
    @NotBlank
    @Size(max = 255)
    private String title;
    private String content;
    private List<Long> labelIds;
}
