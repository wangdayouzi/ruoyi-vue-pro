package cn.iocoder.yudao.module.system.api.mail.dto;

import lombok.Data;

import java.util.Collection;
import java.util.Map;

/**
 * 邮件发送（直接指定收件邮箱）Request DTO
 *
 * <p>用于把邮件发到任意邮箱（如写死的区域发货人邮箱），不依赖系统用户。
 *
 * @author yudao
 */
@Data
public class MailSendSingleReqDTO {

    /** 收件邮箱（一个或多个） */
    private Collection<String> toMails;

    /** 邮件模版编码 */
    private String templateCode;

    /** 邮件模版参数 */
    private Map<String, Object> templateParams;

}
