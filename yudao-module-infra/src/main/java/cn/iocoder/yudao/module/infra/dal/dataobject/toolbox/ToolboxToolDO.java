package cn.iocoder.yudao.module.infra.dal.dataobject.toolbox;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * IT 工具箱工具 DO
 *
 * @author 芋道源码
 */
@TableName("infra_toolbox_tool")
@KeySequence("infra_toolbox_tool_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ToolboxToolDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 工具名称
     */
    private String name;
    /**
     * 工具分类（字典：toolbox_tool_category）
     */
    private String category;
    /**
     * 图标（Element Plus 图标名或图标 URL）
     */
    private String icon;
    /**
     * 工具说明
     */
    private String description;
    /**
     * 版本号
     */
    private String version;
    /**
     * 下载地址（exe 文件 URL）
     */
    private String fileUrl;
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    /**
     * 支持平台
     */
    private String platform;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态（0 开启 1 关闭）
     */
    private Integer status;
    /**
     * 下载次数
     */
    private Integer downloadCount;

}
