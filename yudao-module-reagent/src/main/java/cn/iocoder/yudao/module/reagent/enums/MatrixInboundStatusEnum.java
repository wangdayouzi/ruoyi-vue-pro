package cn.iocoder.yudao.module.reagent.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Getter
@RequiredArgsConstructor
public enum MatrixInboundStatusEnum {
    BOOKED(0, "已预约"), NOT_INBOUND(1, "未入库"), PARTIAL(2, "部分入库"), INBOUND(3, "已入库");

    private final int value;
    private final String label;

    public static MatrixInboundStatusEnum of(Integer value) {
        return Arrays.stream(values()).filter(status -> Integer.valueOf(status.value).equals(value))
                .findFirst().orElseThrow(() -> invalidParamException("无效的基质入库状态"));
    }
}
