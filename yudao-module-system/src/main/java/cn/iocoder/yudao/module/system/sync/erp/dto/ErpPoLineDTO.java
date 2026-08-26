package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 老ERP 采购订单明细（sdpm014）—— 采购订单剩余量
 *
 * pm01406=采购数量, pm01411=已入库数量
 * 已领用/退料/采购退货 在 yudao 落地时从 staging 聚合；剩余 = 已入库 − 领用 + 退料 − 采购退货
 *
 * @author yudao
 */
@Data
public class ErpPoLineDTO {

    /** 明细行ID pm01401（唯一） */
    private String lineId;
    /** 采购订单主ID pm01402 */
    private String poId;
    /** PO号 pm01303 */
    private String poCode;
    /** 订单日期 pm01302 */
    private String orderDate;
    /** 供应商名称（sdpm013.pm01305 合同 → sdpd004.pd00404 → sdpf003.pf00302） */
    private String vendorName;
    /** 物料ID pm01403 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 品牌 pm00221 */
    private String brand;
    /** 规格 pm00205 */
    private String spec;
    /** 单位编码 pm01404 */
    private String unitCode;
    /** 单位名称 pa01302 */
    private String unitName;
    /** 物料分类 pm00203→sdpm001.pm00102 */
    private String itemCategory;
    /** 采购数量 pm01406 */
    private BigDecimal qtyOrdered;
    /** 已入库数量 pm01411 */
    private BigDecimal qtyReceived;

}