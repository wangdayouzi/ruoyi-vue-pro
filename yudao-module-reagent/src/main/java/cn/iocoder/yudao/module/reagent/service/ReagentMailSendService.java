package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.system.api.mail.MailSendApi;
import cn.iocoder.yudao.module.system.api.mail.dto.MailSendSingleToUserReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 邮件发送服务（独立事务）
 *
 * 邮件日志（system_mail_log）的写入使用独立事务（REQUIRES_NEW）：
 * 避免在 Flowable 任务监听器等外部事务中发送失败时，污染外部事务
 * （例如 PostgreSQL 事务被终止，导致流程创建 / 发货确认整体失败）。
 *
 * @author yudao
 */
@Service
public class ReagentMailSendService {

    @Resource
    private MailSendApi mailSendApi;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void sendSingleMailToAdmin(Long userId, String templateCode, Map<String, Object> templateParams) {
        mailSendApi.sendSingleMailToAdmin(new MailSendSingleToUserReqDTO()
                .setUserId(userId)
                .setTemplateCode(templateCode)
                .setTemplateParams(templateParams));
    }

}
