package cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 老ERP同步-采购入库单主表 DO（正式表，页面读取）
 *
 * @author yudao
 */
@TableName("mes_pm_inbound")
@KeySequence("mes_pm_inbound_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesPmInboundDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 老ERP 入库单ID pm02601
     */
    private String srcReceiptId;
    /**
     * 单号/BAS pm02603
     */
    private String basId;
    /**
     * 接受日期 pm02602
     */
    private String receiptDate;
    /**
     * 期间 pm02614
     */
    private String period;
    /**
     * 单据类型 pm02623
     */
    private Integer docType;
    /**
     * 供应商ID pm02604
     */
    private String vendorId;
    /**
     * 供应商名称
     */
    private String vendorName;
    /**
     * 仓库ID pm02605
     */
    private String warehouseId;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 采购订单ID pm02612
     */
    private String poId;
    /**
     * 采购订单号 pm01303
     */
    private String poCode;
    /**
     * 项目ID pm02723
     */
    private String projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 申请人 pm02609
     */
    private String applicant;
    /**
     * 申请人姓名
     */
    private String applicantName;
    /**
     * 审核人 pm02616
     */
    private String auditor;
    /**
     * 审核人姓名
     */
    private String auditorName;
    /**
     * 同步批次
     */
    private Long syncBatch;
    /**
     * 同步时间
     */
    private LocalDateTime syncTime;
}
