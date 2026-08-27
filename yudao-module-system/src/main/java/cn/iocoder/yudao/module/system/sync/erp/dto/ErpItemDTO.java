package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

/**
 * 老ERP 物料主档 DTO（源：sdpm002，按编码点查）
 * <p>
 * 用于"基础数据按需补缺"：从 staging 业务数据拿到缺失的物料编码后，去老库 sdpm002 点查补全主档信息。
 *
 * @author yudao
 */
@Data
public class ErpItemDTO {

    /** 物料内部ID pm00200 */
    private String srcItemId;
    /** 物料编码 pm00201 */
    private String itemCode;
    /** 物料名称 pm00202 */
    private String itemName;
    /** 规格 pm00205 */
    private String spec;
    /** 品牌 pm00221 */
    private String brand;
    /** 分类键 pm00203（→sdpm001.pm00101） */
    private String categoryKey;
    /** 分类名 pm00203→sdpm001.pm00102 */
    private String categoryName;
    /** 单位名称 sdpm003→sdpa013.pa01302（默认单位） */
    private String unitName;

}
