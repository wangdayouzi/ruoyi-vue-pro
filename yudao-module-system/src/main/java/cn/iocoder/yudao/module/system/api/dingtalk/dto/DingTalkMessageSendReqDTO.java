package cn.iocoder.yudao.module.system.api.dingtalk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DingTalkMessageSendReqDTO {
    @NotBlank @Size(max = 64)
    @Pattern(regexp = "[a-zA-Z0-9_.:-]+")
    private String requestId;
    @NotBlank @Size(max = 100)
    private String templateCode;
    @NotEmpty @Size(max = 20)
    private List<@NotNull @Positive Long> userIds;
    @NotNull @Size(max = 100)
    private Map<@Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*") @Size(max = 100) String, @NotNull @Size(max = 4000) String> params;
    @Size(max = 128)
    private String businessId;
}
