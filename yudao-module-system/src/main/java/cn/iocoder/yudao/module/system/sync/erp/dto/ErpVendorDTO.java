package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

/**
 * 老ERP 供应商 DTO（staging 业务数据去重，供按需补缺）
 *
 * @author yudao
 */
@Data
public class ErpVendorDTO {

    /** 供应商ID pm02604（老库 GUID，作 mes_md_vendor.code） */
    private String vendorId;
    /** 供应商名称 pf00302 */
    private String vendorName;

}
