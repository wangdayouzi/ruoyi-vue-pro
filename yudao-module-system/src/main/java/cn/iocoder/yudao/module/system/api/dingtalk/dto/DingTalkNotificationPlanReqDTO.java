package cn.iocoder.yudao.module.system.api.dingtalk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

/** 通知生成请求；部门在入队时展开，最终持久化的仍是逐人发送请求。 */
@Data
public class DingTalkNotificationPlanReqDTO {
    @NotBlank @Size(max=64) @Pattern(regexp="[a-zA-Z0-9_.:-]+") private String requestId;
    @NotBlank @Size(max=100) private String templateCode;
    @Size(max=128) private String businessId;
    @NotNull @Size(max=1000) private List<@NotNull @Positive Long> userIds=List.of();
    @NotNull @Size(max=100) private List<@NotNull @Positive Long> deptIds=List.of();
    @NotNull @Size(max=1000) private List<@NotNull @Positive Long> excludedUserIds=List.of();
    private boolean includeChildDepts=true;
    @NotNull @Size(max=100)
    private Map<@Pattern(regexp="[a-zA-Z][a-zA-Z0-9_]*") @Size(max=100) String,@NotNull @Size(max=4000) String> params;
    @NotNull private LocalDateTime sendAfter;
    private LocalDateTime expiresAt;
    @Size(max=100) private String guardType;
    @Size(max=100) private String businessVersion;
    @AssertTrue(message="请选择通知部门或人员")
    public boolean isTargetPresent() { return userIds!=null && !userIds.isEmpty() || deptIds!=null && !deptIds.isEmpty(); }
}
