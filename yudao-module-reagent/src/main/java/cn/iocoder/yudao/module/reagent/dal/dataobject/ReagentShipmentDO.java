package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 发货单主表 DO
 *
 * @author yudao
 */
@TableName("reagent_shipment")
@KeySequence("reagent_shipment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentShipmentDO extends BaseDO {

    @TableId
    private Long id;

    /** 发货单号 */
    private String shipmentNo;

    /** 关联 reagent_apply.id */
    private Long applyId;

    /** 快递单号 */
    private String trackingNumber;

    /** 物流公司 */
    private String expressCompany;

    /** 运费结算方式 */
    private String freightSettlement;

    /** 项目号 */
    private String projectNo;

    /** 运输温度 */
    private String transportTemp;

    /** 是否放置温度记录仪 */
    private Integer hasTempLogger;

    /** 发货时间 */
    private LocalDateTime shipmentDate;

}
