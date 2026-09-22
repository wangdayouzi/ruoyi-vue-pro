package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 样品领用台账 Response VO")
@Data
public class SampleLoanRespVO {
    private Long id;
    private String basNo;
    private Long requesterId;
    private String requester;
    private Long submitterId;
    private String submitter;
    private String sampleInfo;
    private String remark;
    /** 1-领用中；2-已归还 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime returnTime;
}
