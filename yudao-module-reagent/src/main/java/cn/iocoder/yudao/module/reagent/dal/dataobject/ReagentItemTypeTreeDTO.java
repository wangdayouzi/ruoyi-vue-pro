package cn.iocoder.yudao.module.reagent.dal.dataobject;

import lombok.Data;

/**
 * 物料分类树节点（mes_md_item_type）—— 试剂分类识别/子孙展开用
 *
 * @author yudao
 */
@Data
public class ReagentItemTypeTreeDTO {

    /** 分类 id */
    private Long id;
    /** 分类键 pm00101 */
    private String code;
    /** 父分类 id（根=0） */
    private Long parentId;
    /** 分类名称 pm00102（试剂关键词识别用） */
    private String name;

}
