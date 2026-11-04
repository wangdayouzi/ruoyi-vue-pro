package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 项目号（老样本系统 think_items 同步到本地）
 *
 * <p>id 直接沿用源表主键；同步时一并落地临床方案名称/编号，便于页面直接展示。
 *
 * @author yudao
 */
@TableName("reagent_item")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentItemDO extends TenantBaseDO {

    /** 项目ID（= 源表 think_items.id） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 城市：1上海，2宁波 */
    private Integer cityId;

    /** 项目号（源表 baseid） */
    private String baseId;

    /** 临床方案ID */
    private Long clinicalId;

    /** 临床方案名称（同步时 join think_clinical） */
    private String clinicalName;

    /** 临床方案编号（同步时 join think_clinical） */
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

    /** 源表添加时间 */
    private LocalDateTime srcCreateTime;

    /** 源表编辑时间 */
    private LocalDateTime srcUpdateTime;

    /** 状态：0正常，1停用（同步时源表已不存在则停用） */
    private Integer status;

    /** 同步批次 */
    private Long syncBatch;

    /** 同步时间 */
    private LocalDateTime syncTime;

}
