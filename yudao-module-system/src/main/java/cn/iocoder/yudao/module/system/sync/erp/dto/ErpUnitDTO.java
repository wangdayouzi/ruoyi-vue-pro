package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

/**
 * 老ERP 单位 DTO（staging 业务数据去重，供按需补缺）
 *
 * @author yudao
 */
@Data
public class ErpUnitDTO {

    /** 单位名称 pa01302（作 mes_md_unit_measure.code/name） */
    private String unitName;

}
