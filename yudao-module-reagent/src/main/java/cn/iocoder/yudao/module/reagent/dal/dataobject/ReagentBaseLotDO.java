package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 试剂批号表 DO
 *
 * @author yudao
 */
@TableName("reagent_base_lot")
@KeySequence("reagent_base_lot_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentBaseLotDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联 reagent_base.id */
    private Long baseId;

    /** 批号 */
    private String lotNo;

    /** 规格/浓度 */
    private String content;

    /** 过期日期 */
    private LocalDateTime expirationDate;

    /** 参考剩余量 */
    private String amountLeft;

    /** 状态：0正常, 1停用 */
    private Integer status;

}
