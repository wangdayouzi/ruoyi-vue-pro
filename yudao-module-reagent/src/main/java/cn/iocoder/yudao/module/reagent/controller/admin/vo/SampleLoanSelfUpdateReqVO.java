package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SampleLoanSelfUpdateReqVO extends SampleLoanSelfCreateReqVO {

    @NotNull(message = "领用记录编号不能为空")
    private Long id;
    @PastOrPresent(message = "领用时间不能晚于当前时间")
    private LocalDateTime createTime;
    @PastOrPresent(message = "归还时间不能晚于当前时间")
    private LocalDateTime returnTime;
}
