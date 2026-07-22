package cn.iocoder.yudao.module.amf.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 分析方法文件 - 文件表
 *
 * @author yudao
 */
@TableName("amf_file")
@KeySequence("amf_file_seq")
@Data
public class AmfFileDO {

    @TableId
    private Long id;

    /** 关联业务单据ID */
    private Long businessId;

    /** 文件名 */
    private String fileName;

    /** 当前文件URL */
    private String fileUrl;

    /** 当前版本号 */
    private String fileVersion;

    /** 签字生效日期 */
    private LocalDate effectiveDate;

    /** 租户编号 */
    private Long tenantId;

    /** 创建者 */
    private String creator;

    /** 创建时间 */
    private LocalDateTime createTime;

}
