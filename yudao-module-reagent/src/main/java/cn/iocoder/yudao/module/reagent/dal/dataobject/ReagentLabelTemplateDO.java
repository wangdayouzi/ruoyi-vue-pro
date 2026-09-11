package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 可供用户手动选择的试剂标签模板；第一期不做模板版本管理。 */
@TableName("reagent_label_template")
@KeySequence("reagent_label_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentLabelTemplateDO extends BaseDO {

    @TableId
    private Long id;
    /** 用户在页面看到的名称 */
    private String name;
    /** 与代理 appsettings.json 的 Templates 键一致 */
    private String code;
    /** 0-禁用，1-启用 */
    private Integer status;
}
