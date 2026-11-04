package cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class DingTalkTemplateSaveReqVO {
    private Long id;
    @NotBlank @Size(max = 100)
    private String name;
    @NotBlank @Size(max = 100) @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_.-]*")
    private String code;
    @NotBlank
    private String messageType;
    @NotNull @Size(min = 1, max = 12)
    private Map<String, String> content;
    @NotNull @Min(0) @Max(1)
    private Integer status;
    @Size(max = 20)
    private List<@NotNull @Positive Long> defaultUserIds;
    @Size(max = 100)
    private List<@NotNull @Positive Long> defaultDeptIds;
    private Boolean includeChildDepts = true;
    @Size(max = 500)
    private String remark;
}
