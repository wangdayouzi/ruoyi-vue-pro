package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** 试剂标签逻辑打印机。页面只选择本实体，不暴露 Windows 电脑概念。 */
@TableName("reagent_label_printer")
@KeySequence("reagent_label_printer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReagentLabelPrinterDO extends BaseDO {

    @TableId
    private Long id;
    /** 页面展示名称，例如：上海试剂室 PT-P900-01 */
    private String name;
    /** 对应 Windows 代理的唯一编码 */
    private String agentCode;
    /** 代理机器凭证 SHA-256；只在后台配置，不返回给页面，也不保存明文 */
    private String agentTokenHash;
    /** Windows 中 Brother 驱动的队列名称 */
    private String systemPrinterName;
    private String model;
    private Integer tapeWidthMm;
    /** 代理本地模板文件名/模板编码，例如 REAGENT_36MM_V1 */
    private String templateCode;
    /** 0-禁用，1-启用 */
    private Integer status;
    /** 0-离线，1-在线，2-故障 */
    private Integer onlineStatus;
    private LocalDateTime lastHeartbeatTime;
}
