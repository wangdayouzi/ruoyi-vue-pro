package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.MatrixInboundMapper;
import cn.iocoder.yudao.module.reagent.enums.MatrixInboundStatusEnum;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import static cn.iocoder.yudao.module.reagent.service.MatrixInboundSupport.*;

@Component
@RequiredArgsConstructor
public class MatrixInboundNotificationGuard implements DingTalkNotificationGuard {
    private final MatrixInboundMapper mapper;

    public String businessType() { return MatrixInboundNotifier.REMINDER_GUARD; }
    public String displayName() { return "基质加急两小时临期提醒"; }

    public boolean applicable(String businessId, LocalDateTime expiresAt) {
        if (businessId == null || !businessId.matches("matrix-deadline:[0-9]+:[0-9]+:soon")) return false;
        String[] parts = businessId.split(":");
        final Long id;
        final Integer version;
        try {
            id = Long.valueOf(parts[1]);
            version = Integer.valueOf(parts[2]);
        } catch (NumberFormatException exception) {
            return false;
        }
        MatrixInboundDO row = mapper.selectByTenantId(tenantId(), id);
        if (row == null || !Boolean.TRUE.equals(row.getUrgent()) || !version.equals(row.getVersion())
                || MatrixInboundStatusEnum.INBOUND.getValue() == row.getInboundStatus()) return false;

        // 消息入队后可能改期、完成或删除；发送前必须重新检查版本和有效时间。
        LocalDateTime deadline = row.getAppointmentDate();
        LocalDateTime current = now();
        return deadline != null && deadline.equals(expiresAt) && deadline.isAfter(current)
                && !deadline.minusHours(2).isAfter(current);
    }
}
