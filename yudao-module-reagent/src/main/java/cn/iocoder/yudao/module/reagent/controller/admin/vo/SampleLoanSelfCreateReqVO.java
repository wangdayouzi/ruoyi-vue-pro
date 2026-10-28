package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "移动端 - 本人领用登记 Request VO")
@Data
public class SampleLoanSelfCreateReqVO {

    @NotBlank(message = "BAS号不能为空")
    @Size(max = 64, message = "BAS号长度不能超过64个字符")
    private String basNo;

    @NotBlank(message = "请选择领用类型")
    @Pattern(regexp = "SAMPLE|REAGENT", message = "领用类型不合法")
    private String materialType;

    @NotBlank(message = "请选择站点")
    @Size(max = 32, message = "站点长度不能超过32个字符")
    private String site;

    @NotBlank(message = "请选择楼层")
    @Size(max = 32, message = "楼层长度不能超过32个字符")
    private String location;

    @Size(max = 500, message = "物料信息长度不能超过500个字符")
    private String sampleInfo;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
