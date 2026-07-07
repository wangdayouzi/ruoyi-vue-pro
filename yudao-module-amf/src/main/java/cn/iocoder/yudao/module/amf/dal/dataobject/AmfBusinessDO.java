package cn.iocoder.yudao.module.amf.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析方法文件 - 业务单据主表
 *
 * @author yudao
 */
@TableName("amf_business")
@KeySequence("amf_business_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AmfBusinessDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * BAS编号
     */
    private String basNo;

    /**
     * 临床方案编号
     */
    private String protocolNo;

    /**
     * 申办方
     */
    private String sponsor;

    /**
     * 分析方法
     */
    private String analysisMethod;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

}
