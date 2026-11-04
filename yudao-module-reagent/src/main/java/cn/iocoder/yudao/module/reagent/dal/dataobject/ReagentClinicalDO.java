package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 临床方案（老样本系统 think_clinical 同步到本地）
 *
 * <p>id 直接沿用源表主键；is_auto_receive 由本地维护，同步只更新源字段、不覆盖该列。
 *
 * @author yudao
 */
@TableName("reagent_clinical")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentClinicalDO extends TenantBaseDO {

    /** 临床方案ID（= 源表 think_clinical.id） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 临床方案名称 */
    private String name;

    /** 临床方案编号 */
    private String number;

    /** 申办方ID */
    private Long sponsorId;

    /** 是否上传附件：0否 1是 */
    private Integer isUpload;

    /** 生效日期（源表为 varchar） */
    private String effectiveDate;

    /** 版本号 */
    private String versionNumber;

    /** 是否自动发送寄送通知邮件：0否 1是（本地维护，同步不覆盖） */
    private Integer isAutoReceive;

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
