package cn.iocoder.yudao.module.system.dal.dataobject.dingtalk;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "system_dingtalk_message_log", autoResultMap = true)
@KeySequence("system_dingtalk_message_log_seq")
public class DingTalkMessageLogDO extends TenantBaseDO {
    @TableId
    private Long id;
    private String requestId;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String requestHash;
    private String businessId;
    private Long templateId;
    private String templateCode;
    private String templateName;
    private String messageType;
    private String robotCode;
    private Long userId;
    private String userName;
    private String dingTalkUserId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> content;
    private Integer sendStatus;
    private LocalDateTime sendTime;
    private String processQueryKey;
    private String errorCode;
    private String errorMessage;
}
