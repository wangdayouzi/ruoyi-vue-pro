package cn.iocoder.yudao.module.system.api.dingtalk;

import jakarta.validation.constraints.*;
import java.util.List;

/** 通知接收范围解析：显式人员与启用部门成员合并，不按钉钉绑定情况静默过滤。 */
public interface DingTalkNotificationRecipientApi {
    List<Long> resolve(@Size(max=1000) List<@NotNull @Positive Long> userIds,
                       @Size(max=100) List<@NotNull @Positive Long> deptIds, boolean includeChildDepts);
}
