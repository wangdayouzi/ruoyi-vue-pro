package cn.iocoder.yudao.module.system.api.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendReqDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** enqueue 应在业务事务中调用；同租户 requestId + userId 幂等。 */
public interface DingTalkNotificationQueue {
    void enqueue(@NotNull @Valid DingTalkMessageSendReqDTO request, LocalDateTime sendAfter, LocalDateTime expiresAt,
                 String guardType, String businessVersion);
    /** 统一解析部门/人员并逐人入队；业务只提供其人员资格判断，回调不会保存到队列。 */
    void enqueuePlan(@NotNull @Valid cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO request,
                     @NotNull java.util.function.Predicate<Long> eligible);
    default void enqueuePlan(@NotNull @Valid cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO request) {
        enqueuePlan(request,id->true);
    }
    int cancel(String businessId);
    int cancelPending(String businessId, String guardType);
    boolean exists(String businessId, String guardType, String businessVersion);
}
