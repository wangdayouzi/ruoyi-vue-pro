package cn.iocoder.yudao.module.reagent.dal.dataobject;

import cn.iocoder.yudao.module.reagent.dal.type.MatrixInboundJsonTypeHandler;
import cn.iocoder.yudao.module.reagent.dal.type.MatrixInboundUuidTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/** 基质预约主单；每条记录对应一个批次 BAS 号，不管理单管编号。 */
@Data
@TableName(value = "reagent_matrix_inbound", autoResultMap = true)
public class MatrixInboundDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String site;
    private String matrixKind;
    private String basNo;
    /** 实际提交人；预约人员单独存储，不能用可编辑姓名代替身份。 */
    private Long applicantId;
    private String applicantName;
    @TableField(typeHandler = MatrixInboundUuidTypeHandler.class, jdbcType = JdbcType.OTHER)
    private UUID requestId;
    private LocalDateTime createdAt;
    private LocalDateTime appointmentDate;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Boolean limsDesigned;
    private Boolean limsInbound;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String exceptionRemark;
    private Long registeredBy;
    private String registeredName;
    private LocalDateTime registeredAt;
    /** 每次业务变更递增，同时用于并发更新和临期通知失效检查。 */
    private Integer version;
    private Boolean urgent;
    /** 取值由 MatrixInboundStatusEnum 定义，LIMS 布尔字段不是状态判断依据。 */
    private Integer inboundStatus;
    @TableField(typeHandler = MatrixInboundJsonTypeHandler.class, jdbcType = JdbcType.OTHER)
    private List<Long> appointmentUserIds;
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
