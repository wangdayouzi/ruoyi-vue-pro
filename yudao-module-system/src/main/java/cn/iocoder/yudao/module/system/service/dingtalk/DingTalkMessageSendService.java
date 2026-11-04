package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkMessageSendApi;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageLogDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.*;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.dingtalk.DingTalkMessageType;
import cn.iocoder.yudao.module.system.framework.dingtalk.*;
import com.alibaba.fastjson2.JSON;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
@Validated
@RequiredArgsConstructor
public class DingTalkMessageSendService implements DingTalkMessageSendApi {
    private final DingTalkMessageTemplateMapper templates;
    private final DingTalkMessageLogMapper logs;
    private final AdminUserMapper users;
    private final DingTalkTemplateRenderer renderer;
    private final DingTalkIntegrationProperties properties;
    private final DingTalkRobotClient robotClient;
    private final DingTalkUserIdentityService identities;
    private final DingTalkTemplateParameterService parameters;
    @jakarta.annotation.Resource private DingTalkRecipientResolver recipientResolver;

    @Override
    public List<Long> getDefaultUserIds(String templateCode) {
        var template = templates.selectByCode(templateCode);
        if (template == null) throw invalidParamException("钉钉消息模板不存在");
        return recipientResolver.resolve(template);
    }

    /** 日志独立提交，不能因业务事务回滚而丢失已发送记录。调用方应在事务提交后调用。 */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<DingTalkMessageSendResultDTO> send(DingTalkMessageSendReqDTO req) {
        if (!properties.isEnabled()) throw invalidParamException("钉钉集成未开启");
        String robotCode = properties.getEffectiveRobotCode();
        if (robotCode == null || robotCode.isBlank()) throw invalidParamException("未配置钉钉机器人编码");
        var template = templates.selectByCode(req.getTemplateCode());
        if (template == null) throw invalidParamException("钉钉消息模板不存在");
        if (!Integer.valueOf(0).equals(template.getStatus())) throw invalidParamException("钉钉消息模板已停用");
        parameters.validate(template.getCode(),renderer.validate(template.getMessageType(),template.getContent()));
        var content = renderer.render(template.getMessageType(), template.getContent(), req.getParams());
        String hash = DigestUtil.sha256Hex(JSON.toJSONString(List.of(template.getCode(), template.getMessageType(), robotCode,
                new TreeMap<>(content), Objects.toString(req.getBusinessId(), ""))));
        List<Long> ids = req.getUserIds().stream().distinct().toList();
        Map<Long, AdminUserDO> receivers = new LinkedHashMap<>();
        Map<Long, String> staffIds = new HashMap<>();
        // 全部收件人先校验，避免配置错误造成部分发送。
        for (Long id : ids) {
            var user = users.selectById(id);
            if (user == null || !Integer.valueOf(0).equals(user.getStatus())) throw invalidParamException("接收人 {} 不存在或已停用", id);
            receivers.put(id, user);
            var previous = logs.selectByRequestAndUser(req.getRequestId(), id);
            if (previous != null && !hash.equals(previous.getRequestHash())) throw invalidParamException("同一请求标识不能用于不同的消息内容");
            staffIds.put(id, previous == null ? identities.resolveUserId(user) : previous.getDingTalkUserId());
        }
        List<DingTalkMessageLogDO> all = new ArrayList<>();
        List<DingTalkMessageLogDO> pending = new ArrayList<>();
        for (var user : receivers.values()) {
            var existing = logs.selectByRequestAndUser(req.getRequestId(), user.getId());
            if (existing != null) {
                if (!hash.equals(existing.getRequestHash())) throw invalidParamException("同一请求标识不能用于不同的消息内容");
                all.add(existing); continue;
            }
            var log = new DingTalkMessageLogDO();
            log.setTenantId(template.getTenantId());
            log.setRequestId(req.getRequestId()); log.setRequestHash(hash); log.setBusinessId(req.getBusinessId());
            log.setTemplateId(template.getId()); log.setTemplateCode(template.getCode()); log.setTemplateName(template.getName());
            log.setMessageType(template.getMessageType()); log.setRobotCode(robotCode); log.setContent(content);
            log.setUserId(user.getId()); log.setUserName(user.getNickname()); log.setDingTalkUserId(staffIds.get(user.getId()));
            log.setSendStatus(0);
            try { logs.insert(log); pending.add(log); all.add(log); }
            catch (DuplicateKeyException ex) {
                var duplicate = logs.selectByRequestAndUser(req.getRequestId(), user.getId());
                if (duplicate == null || !hash.equals(duplicate.getRequestHash())) throw invalidParamException("请求标识已被其他消息使用");
                all.add(duplicate);
            }
        }
        if (!pending.isEmpty()) {
            var outcomes = robotClient.send(robotCode, DingTalkMessageType.of(template.getMessageType()).getMsgKey(), content,
                    pending.stream().map(DingTalkMessageLogDO::getDingTalkUserId).distinct().toList());
            for (var log : pending) {
                var outcome = outcomes.get(log.getDingTalkUserId());
                log.setSendStatus(outcome.status()); log.setProcessQueryKey(outcome.processQueryKey());
                log.setErrorCode(outcome.errorCode()); log.setErrorMessage(outcome.errorMessage());
                log.setSendTime(LocalDateTime.now());
                logs.updateById(log);
            }
        }
        return all.stream().map(log -> new DingTalkMessageSendResultDTO(log.getId(), log.getUserId(), log.getSendStatus(), log.getErrorMessage())).toList();
    }
}
