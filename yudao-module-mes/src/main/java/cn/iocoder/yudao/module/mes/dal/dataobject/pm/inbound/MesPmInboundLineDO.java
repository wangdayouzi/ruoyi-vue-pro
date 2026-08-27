package cn.iocoder.yudao.module.mes.dal.dataobject.pm.inbound;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 老ERP同步-采购入库单明细表 DO（正式表，页面读取）
 *
 * @author yudao
 */
@TableName("mes_pm_inbound_line")
@KeySequence("mes_pm_inbound_line_seq")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesPmInboundLineDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 主表 id
     */
    private Long inboundId;
    /**
     * 老ERP 入库单ID pm02601
     */
    private String srcReceiptId;
    /**
     * 老ERP 明细行ID pm02702
     */
    private String srcLineId;
    /**
     * 采购订单明细行ID pm02712（→ mes_pm_po.line_id，行级关联）
     */
    private String srcPoLineId;
    /**
     * 行号 pm02728
     */
    private Integer lineNo;
    /**
     * 物料ID pm02703
     */
    private String srcItemId;
    /**
     * 物料编码 pm00201
     */
    private String itemCode;
    /**
     * 物料名称 pm00202
     */
    private String itemName;
    /**
     * 品牌 pm00221
     */
    private String brand;
    /**
     * 规格 pm00205
     */
    private String spec;
    /**
     * 单位名称 pa01302
     */
    private String unitName;
    /**
     * 物料分类 pm00203→pm00102
     */
    private String itemCategory;
    /**
     * 物料分类键 pm00203（→sdpm001.pm00101；试剂"10子树"过滤用）
     */
    private String categoryKey;
    /**
     * 批号 pm02631
     */
    private String batchNo;
    /**
     * 过期日期 pm02725
     */
    private String expireDate;
    /**
     * 存储位置 pm02726
     */
    private String storageLocation;
    /**
     * 接收数量 pm02706*pm02705
     */
    private BigDecimal qty;
    /**
     * 批次剩余数量 = 接收 − 领用 + 退料 − 采购退货（按入库明细行聚合）
     */
    private BigDecimal remainingQty;
    /**
     * 批次领用数量（按入库明细行聚合：从该批领走多少）
     */
    private BigDecimal qtyRequisition;
    /**
     * 含税单价 pm02707
     */
    private BigDecimal priceTaxIn;
    /**
     * 含税金额 pm02708
     */
    private BigDecimal amountTaxIn;
    /**
     * 未税单价 pm02709
     */
    private BigDecimal priceExTax;
    /**
     * 未税金额 pm02710
     */
    private BigDecimal amountExTax;
    /**
     * 同步批次
     */
    private Long syncBatch;
    /**
     * 同步时间
     */
    private LocalDateTime syncTime;
}
