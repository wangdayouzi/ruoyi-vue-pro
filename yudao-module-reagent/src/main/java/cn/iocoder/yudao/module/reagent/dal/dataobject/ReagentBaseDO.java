package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 试剂主表 DO
 *
 * @author yudao
 */
@TableName("reagent_base")
@KeySequence("reagent_base_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentBaseDO extends TenantBaseDO {

    @TableId
    private Long id;

    /** 生物试剂编号 */
    private String basId;

    /** 试剂名称 */
    private String reagentName;

    /** 供应商 */
    private String vendor;

    /** 货号 */
    private String catNo;

    /** 储存温度 */
    private String storageTemp;

    /** 储存位置 */
    private String storageLocation;

    /** 状态：0正常, 1停用 */
    private Integer status;

}
