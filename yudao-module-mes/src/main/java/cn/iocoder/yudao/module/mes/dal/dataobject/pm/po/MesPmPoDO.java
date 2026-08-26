package cn.iocoder.yudao.module.mes.dal.dataobject.pm.po;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 老ERP同步-采购订单明细数据 DO（正式表，页面读取）
 *
 * @author yudao
 */
@TableName("mes_pm_po")
@KeySequence("mes_pm_po_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesPmPoDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购订单主ID pm01402
     */
    private String poId;
    /**
     * 采购订单号 pm01303
     */
    private String poCode;
    /**
     * 订单日期 pm01302
     */
    private String orderDate;
    /**
     * 供应商名称（合同 sdpd004→sdpf003）
     */
    private String vendorName;
    /**
     * 明细行ID pm01401
     */
    private String lineId;
    /**
     * 物料ID pm01403
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
     * 单位编码 pm01404
     */
    private String unitCode;
    /**
     * 单位名称 pa01302
     */
    private String unitName;
    /**
     * 物料分类 pm00203→sdpm001.pm00102
     */
    private String itemCategory;
    /**
     * 采购数量 pm01406
     */
    private BigDecimal qtyOrdered;
    /**
     * 已入库数量 pm01411
     */
    private BigDecimal qtyReceived;
    /**
     * 领用数量（staging 聚合）
     */
    private BigDecimal qtyRequisition;
    /**
     * 退料数量（staging 聚合）
     */
    private BigDecimal qtyReturn;
    /**
     * 采购退货数量（staging 聚合）
     */
    private BigDecimal qtyReturnOut;
    /**
     * 剩余数量 = 已入库 − 领用 + 退料 − 采购退货
     */
    private BigDecimal remainingQty;
    /**
     * 同步批次
     */
    private Long syncBatch;
    /**
     * 同步时间
     */
    private LocalDateTime syncTime;
}
