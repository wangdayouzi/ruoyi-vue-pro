package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 试剂申请主表 DO
 *
 * @author yudao
 */
@TableName("reagent_apply")
@KeySequence("reagent_apply_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentApplyDO extends BaseDO {

    @TableId
    private Long id;

    /** 申请单号 */
    private String applyNo;

    /** 发货方单位 */
    private String consignorUnit;

    /** 发货方地址 */
    private String consignorAddress;

    /** 发货联系人 */
    private String consignorName;

    /** 发货联系电话 */
    private String consignorPhone;

    /** 接收方单位 */
    private String receiverUnit;

    /** 接收方地址 */
    private String receiverAddress;

    /** 接收联系人 */
    private String receiverName;

    /** 接收联系电话 */
    private String receiverPhone;

    /** 状态：0草稿, 1待发货, 2部分发货, 3已完成, 4已拒单退回 */
    private Integer status;

    /** 关联 Flowable 流程实例 ID */
    private String processInstanceId;

    /** 备注/拒绝理由 */
    private String remark;

    /** 运费结算方式 */
    private String freightSettlement;

    /** 项目号 */
    private String projectNo;

    /** 运输温度 */
    private String transportTemp;

    /** 是否放置温度记录仪 */
    private Integer hasTempLogger;

}
