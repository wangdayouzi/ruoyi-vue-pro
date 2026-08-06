package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请单 Response VO
 */
@Schema(description = "管理后台 - 申请单 Response VO")
@Data
public class ReagentApplyRespVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    @Schema(description = "申请单号", example = "APL-20240001")
    private String applyNo;

    // ========== 发货方信息 ==========
    @Schema(description = "发货方单位", example = "精翰生物")
    private String consignorUnit;

    @Schema(description = "发货方地址", example = "上海市浦东新区张江高科技园区")
    private String consignorAddress;

    @Schema(description = "发货联系人", example = "仓库管理员")
    private String consignorName;

    @Schema(description = "发货联系电话", example = "021-XXXXXXXX")
    private String consignorPhone;

    // ========== 接收方信息 ==========
    @Schema(description = "接收方单位", example = "复旦大学医学院")
    private String receiverUnit;

    @Schema(description = "接收方地址", example = "上海市徐汇区医学院路138号")
    private String receiverAddress;

    @Schema(description = "接收联系人", example = "张三")
    private String receiverName;

    @Schema(description = "接收联系电话", example = "13800138000")
    private String receiverPhone;

    @Schema(description = "状态：0草稿, 1待发货, 2部分发货, 3已完成, 4已拒单退回")
    private Integer status;

    @Schema(description = "关联 Flowable 流程实例 ID")
    private String processInstanceId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "运费结算方式", example = "月结")
    private String freightSettlement;

    @Schema(description = "项目号")
    private String projectNo;

    @Schema(description = "运输温度", example = "2-8°C")
    private String transportTemp;

    @Schema(description = "是否放置温度记录仪：0否, 1是", example = "1")
    private Integer hasTempLogger;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建者")
    private String creator;

    // ========== 关联明细 ==========
    @Schema(description = "申请明细列表")
    private List<ReagentApplyItemVO> items;

}
