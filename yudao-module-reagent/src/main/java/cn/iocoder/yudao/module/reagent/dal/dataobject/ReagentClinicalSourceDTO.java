package cn.iocoder.yudao.module.reagent.dal.dataobject;

import lombok.Data;

/**
 * 老样本系统 MySQL 源：临床方案（think_clinical）
 *
 * <p>注意：不包含 is_auto_receive（是否自动发送寄送通知邮件）。该字段由本地维护，同步时不会被覆盖。
 *
 * @author yudao
 */
@Data
public class ReagentClinicalSourceDTO {

    /** 临床方案ID（源表主键，本地同值保存） */
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

    /** 添加时间（unix 秒） */
    private Long createTime;

    /** 编辑时间（unix 秒，可为空） */
    private Long updateTime;

}
