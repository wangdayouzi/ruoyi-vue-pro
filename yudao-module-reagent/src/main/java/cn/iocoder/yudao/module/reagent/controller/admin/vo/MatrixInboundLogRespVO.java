package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatrixInboundLogRespVO {
    private String action;
    private Integer fromStatus;
    private Integer toStatus;
    private String remark;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
