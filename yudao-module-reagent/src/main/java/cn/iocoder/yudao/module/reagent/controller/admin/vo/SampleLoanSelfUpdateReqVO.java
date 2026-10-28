package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SampleLoanSelfUpdateReqVO extends SampleLoanSelfCreateReqVO {

    @NotNull(message = "领用记录编号不能为空")
    private Long id;
}
