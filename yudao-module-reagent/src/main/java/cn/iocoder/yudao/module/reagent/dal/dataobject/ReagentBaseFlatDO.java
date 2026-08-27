package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 老ERP同步 试剂基础数据（扁平，一行=一批=一个采购入库明细行）
 *
 * <p>数据来自老ERP采购入库单（staging stg_pm_inbound），分类=10 子树过滤后清洗写入；页面只读展示。
 *
 * @author yudao
 */
@TableName("reagent_base_flat")
@KeySequence("reagent_base_flat_id_seq") // bigserial 默认序列名；同步走 XML upsert 不依赖
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReagentBaseFlatDO extends TenantBaseDO {

    @TableId
    private Long id;

    /** 采购入库单号 pm02603 */
    private String basId;

    /** 试剂编号 = 材料编号 pm00201 */
    private String reagentCode;

    /** 试剂名称 pm00202 */
    private String reagentName;

    /** 供应商 */
    private String vendor;

    /** 仓库名（主表 pm00402） */
    private String warehouse;

    /** 货号（从 spec 清洗"货号：xxx"） */
    private String catNo;

    /** 规格 pm00205 */
    private String spec;

    /** 批号 pm02631 */
    private String lotNo;

    /** 过期日期 pm02725 */
    private String expireDate;

    /** 参考剩余量 = 入库单剩余数量（展示用） */
    private String amountLeft;

    /** 存储位置 pm02726 */
    private String storageLocation;

    /** 储存温度（老库无来源，先空/手填） */
    private String storageTemp;

    /** 分类名 pm00102 */
    private String itemCategory;

    /** 分类键 pm00203 */
    private String categoryKey;

    /** 入库明细行ID pm02702（幂等键） */
    private String srcLineId;

    /** 入库主ID pm02601 */
    private String srcReceiptId;

    /** 状态：0正常, 1停用 */
    private Integer status;

    /** 同步批次 */
    private Long syncBatch;

    /** 同步时间 */
    private LocalDateTime syncTime;

}
