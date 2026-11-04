package cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DingTalkTemplatePageReqVO extends PageParam {
    private String name;
    private String code;
    private String messageType;
    private Integer status;
}
