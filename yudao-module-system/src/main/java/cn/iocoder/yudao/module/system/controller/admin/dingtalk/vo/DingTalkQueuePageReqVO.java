package cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=true)
public class DingTalkQueuePageReqVO extends PageParam {
    @Size(max=128) private String businessId;
    @Size(max=100) private String templateCode;
    @Min(0) @Max(6) private Integer status;
}
