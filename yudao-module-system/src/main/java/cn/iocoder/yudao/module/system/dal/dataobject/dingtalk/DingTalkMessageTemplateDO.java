package cn.iocoder.yudao.module.system.dal.dataobject.dingtalk;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "system_dingtalk_message_template", autoResultMap = true)
@KeySequence("system_dingtalk_message_template_seq")
public class DingTalkMessageTemplateDO extends TenantBaseDO {
    @TableId
    private Long id;
    private String name;
    private String code;
    private String messageType;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> content;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> defaultUserIds;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> defaultDeptIds;
    private Boolean includeChildDepts;
    private Integer status;
    private String remark;
}
