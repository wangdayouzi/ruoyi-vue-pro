package cn.iocoder.yudao.module.reagent.dal.dataobject;

import lombok.Data;

/**
 * 老样本系统 MySQL 源：项目号（think_items，关联 think_clinical 取方案名称/编号）
 *
 * @author yudao
 */
@Data
public class ReagentItemSourceDTO {

    /** 项目ID（源表主键，本地同值保存） */
    private Long id;

    /** 城市：1上海，2宁波 */
    private Integer cityId;

    /** 项目号（源表 baseid） */
    private String baseId;

    /** 临床方案ID */
    private Long clinicalId;

    /** 临床方案名称（join think_clinical） */
    private String clinicalName;

    /** 临床方案编号（join think_clinical） */
    private String clinicalNumber;

    /** 项目负责人ID */
    private Long personId;

    /** 样本类型ID */
    private Long typeId;

    /** 是否时效：0否 1是 */
    private Integer isAging;

    /** 复核状态：0未复核 1已复核 */
    private Integer reviewStatus;

    /** 复核人员ID */
    private Long reviewer;

    /** 是否附上报告作为附件：0否 1是 */
    private Integer isReport;

    /** 是否涉及病原微生物：0否 1是 */
    private Integer isPm;

    /** 添加时间（unix 秒） */
    private Long createTime;

    /** 编辑时间（unix 秒，可为空） */
    private Long updateTime;

}
