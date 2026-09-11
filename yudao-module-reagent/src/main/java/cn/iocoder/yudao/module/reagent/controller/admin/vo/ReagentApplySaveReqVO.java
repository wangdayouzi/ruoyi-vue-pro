package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mzt.logapi.starter.annotation.DiffLogField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请单创建/修改 Request VO
 */
@Schema(description = "管理后台 - 申请单创建/修改 Request VO")
@Data
public class ReagentApplySaveReqVO {

    @Schema(description = "主键编号", example = "1")
    private Long id;

    // ========== 发货方信息（默认值，前端只读展示） ==========
    @Schema(description = "发货方单位", example = "精翰生物")
    @DiffLogField(name = "发货方单位")
    private String consignorUnit;

    @Schema(description = "发货方地址", example = "上海市浦东新区张江高科技园区")
    @DiffLogField(name = "发货方地址")
    private String consignorAddress;

    @Schema(description = "发货联系人", example = "仓库管理员")
    @DiffLogField(name = "发货联系人")
    private String consignorName;

    @Schema(description = "发货联系电话", example = "021-XXXXXXXX")
    @DiffLogField(name = "发货联系电话")
    private String consignorPhone;

    @Schema(description = "发货方邮箱：发货任务通知的收件人；留空则不发送邮件", example = "jhsh_sample@accurantbio.com")
    @DiffLogField(name = "发货方邮箱")
    private String consignorEmail;

    @Schema(description = "发货区域：上海 / 宁波（选地址按钮写入，邮件按区域定向）", example = "上海")
    @DiffLogField(name = "发货区域")
    private String region;

    // ========== 接收方信息（项目组填写，必填） ==========
    @Schema(description = "接收方单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "复旦大学医学院")
    @NotBlank(message = "接收方单位不能为空")
    @DiffLogField(name = "接收方单位")
    private String receiverUnit;

    @Schema(description = "接收方地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "上海市徐汇区医学院路138号")
    @NotBlank(message = "接收方地址不能为空")
    @DiffLogField(name = "接收方地址")
    private String receiverAddress;

    @Schema(description = "接收联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "接收联系人不能为空")
    @DiffLogField(name = "接收联系人")
    private String receiverName;

    @Schema(description = "接收联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "接收联系电话不能为空")
    @DiffLogField(name = "接收联系电话")
    private String receiverPhone;

    @Schema(description = "备注")
    @DiffLogField(name = "退回理由")
    private String remark;

    @Schema(description = "运费结算方式", example = "月结")
    @DiffLogField(name = "运费结算方式")
    private String freightSettlement;

    @Schema(description = "项目号")
    @DiffLogField(name = "项目号")
    private String projectNo;

    @Schema(description = "运输温度", example = "2-8°C")
    @DiffLogField(name = "运输温度")
    private String transportTemp;

    @Schema(description = "是否放置温度记录仪：0否, 1是", example = "1")
    @DiffLogField(name = "温度记录仪")
    private Integer hasTempLogger;

    @Schema(description = "计划运出日期", example = "2026-09-05")
    @DiffLogField(name = "计划运出日期")
    private LocalDateTime plannedShipDate;

    @Schema(description = "备注")
    @DiffLogField(name = "备注")
    private String note;

    // ========== 申请明细 ==========
    @Schema(description = "申请明细列表")
    @NotEmpty(message = "申请明细不能为空")
    @Valid
    private List<ReagentApplyItemVO> items;

}
