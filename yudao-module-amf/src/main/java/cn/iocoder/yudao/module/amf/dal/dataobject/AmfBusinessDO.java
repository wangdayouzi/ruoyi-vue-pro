package cn.iocoder.yudao.module.amf.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

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
     * 方法编号
     */
    private String methodNo;

    /**
     * 版本号
     */
    private String methodVersion;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 测试物
     */
    private String testArticle;

    /**
     * 基质类型
     */
    private String matrixType;

    /**
     * SD
     */
    private String sd;

    /**
     * 签字生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 申办方
     */
    private String sponsor;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

}
