package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 发货单明细表 DO
 *
 * @author yudao
 */
@TableName("reagent_shipment_item")
@KeySequence("reagent_shipment_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentShipmentItemDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联 reagent_shipment.id */
    private Long shipmentId;

    /** 关联 reagent_apply_item.id */
    private Long applyItemId;

    /** 批号(继承申请明细) */
    private String lotNo;

    /** 本次实际发货数量 */
    private Integer quantityShipped;

}
