package cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DingTalkLogPageReqVO extends PageParam {
    private String templateCode;
    private String requestId;
    private Long userId;
    private Integer sendStatus;
}
