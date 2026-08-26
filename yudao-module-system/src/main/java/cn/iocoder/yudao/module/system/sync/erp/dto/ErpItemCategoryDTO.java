package cn.iocoder.yudao.module.system.sync.erp.dto;

import lombok.Data;

/**
 * 老ERP 物料分类（sdpm001）
 *
 * pm00101=分类键, pm00102=分类名, pm00103=父分类键（顶级为空）
 *
 * @author yudao
 */
@Data
public class ErpItemCategoryDTO {

    /** 分类键 pm00101（≈8位，唯一；mes_md_item_type.code） */
    private String catKey;
    /** 分类名 pm00102（如"（2001.2）合作方寄送实验室常规耗材"） */
    private String catName;
    /** 父分类键 pm00103（顶级分类为空） */
    private String parentKey;

    /** staging 主键（推送标记用） */
    private Long stagingId;

}
