package cn.iocoder.yudao.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Schema(description = "管理后台 - 钉钉首次设置本地密码 Request VO")
@Data
public class AuthDingTalkInitializePasswordReqVO {

    @Schema(description = "钉钉认证后获得的一次性设置密码凭证", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "设置密码凭证不能为空")
    private String passwordSetupToken;

    @Schema(description = "新的本地登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 4, max = 16, message = "密码长度为 4-16 位")
    private String password;

}
