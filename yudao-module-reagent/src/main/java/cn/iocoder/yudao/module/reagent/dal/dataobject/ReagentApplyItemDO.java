package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 试剂申请明细表 DO
 *
 * @author yudao
 */
@TableName("reagent_apply_item")
@KeySequence("reagent_apply_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentApplyItemDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联 reagent_apply.id */
    private Long applyId;

    /** 试剂编号 */
    private String basId;

    /** 试剂名称 */
    private String reagentName;

    /** 货号 */
    private String catNo;

    /** 批号 */
    private String lotNo;

    /** 规格/浓度（文本，可手改） */
    private String content;

    /** 储存温度（文本，可手改） */
    private String storageTemp;

    /** 储存位置（文本，可手改） */
    private String storageLocation;

    /** 过期日期 */
    private LocalDateTime expirationDate;

    /** 需求总数量 */
    private Integer requestedQty;

    /** 已累计发货数量 */
    private Integer shippedQtyTotal;

}
