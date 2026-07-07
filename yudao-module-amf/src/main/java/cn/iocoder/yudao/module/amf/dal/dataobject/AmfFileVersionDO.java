package cn.iocoder.yudao.module.amf.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分析方法文件 - 文件版本记录表
 *
 * @author yudao
 */
@TableName("amf_file_version")
@KeySequence("amf_file_version_seq")
@Data
public class AmfFileVersionDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /** 关联文件ID */
    private Long fileId;

    /**
     * 关联业务单据ID（冗余，方便直接查询）
     */
    private Long businessId;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件URL / OnlyOffice存储key
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（扩展名，如 docx, xlsx）
     */
    private String fileType;

    /**
     * 变更说明
     */
    private String changeDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    private String creator;

}
