package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MatrixInboundRespVO {
    private Long id;
    private String site;
    private String basNo;
    private String matrixKind;
    private Long applicantId;
    private String submitterName;
    private String applicantName;
    private List<Long> appointmentUserIds;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;
    private Boolean limsDesigned;
    private Boolean limsInbound;
    private Integer inboundStatus;
    private String exceptionRemark;
    private String registeredName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registeredAt;
    private Integer version;
    private Boolean urgent;
}
