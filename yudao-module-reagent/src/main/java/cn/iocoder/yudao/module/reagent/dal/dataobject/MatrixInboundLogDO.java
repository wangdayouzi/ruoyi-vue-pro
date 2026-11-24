package cn.iocoder.yudao.module.reagent.dal.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/** 入库登记历史，保留每次状态变化及操作人，不能由主单的最后登记信息替代。 */
@Data
@TableName("reagent_matrix_inbound_log")
public class MatrixInboundLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long inboundId;
    private String action;
    private Integer fromStatus;
    private Integer toStatus;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createdAt;
}
