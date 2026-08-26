package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购订单行 聚合数量 DTO（staging 侧：领用/退料/采购退货 按订单行聚合结果）
 *
 * @author yudao
 */
@Data
public class ErpPoLineQtyDTO {

    /** 采购订单明细行ID（pm01402 / stg_pm_inbound.src_po_line_id） */
    private String poLineId;
    /** 聚合数量 */
    private BigDecimal qty;

}
