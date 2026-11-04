package cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;

@Data
public class DingTalkTemplatePreviewReqVO {
    @Valid @NotNull
    private DingTalkTemplateSaveReqVO template;
    @NotNull @Size(max = 100)
    private Map<@Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*") @Size(max = 100) String, @NotNull @Size(max = 4000) String> params;
}
