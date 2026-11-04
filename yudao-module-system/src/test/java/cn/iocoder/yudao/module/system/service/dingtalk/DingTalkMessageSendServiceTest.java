package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.*;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.*;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.framework.dingtalk.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DingTalkMessageSendServiceTest {
    private final DingTalkMessageTemplateMapper templates = mock(DingTalkMessageTemplateMapper.class);
    private final DingTalkMessageLogMapper logs = mock(DingTalkMessageLogMapper.class);
    private final AdminUserMapper users = mock(AdminUserMapper.class);
    private final DingTalkRobotClient client = mock(DingTalkRobotClient.class);
    private final DingTalkIntegrationProperties properties = new DingTalkIntegrationProperties();
    private final Map<Long, DingTalkMessageLogDO> stored = new LinkedHashMap<>();
    private final DingTalkMessageTemplateDO template = new DingTalkMessageTemplateDO();
    private DingTalkMessageSendService service;
    private DingTalkMessageSendReqDTO request;
    @BeforeEach
    void setUp() {
        properties.setEnabled(true); properties.setClientId("app");
        service = new DingTalkMessageSendService(templates, logs, users, new DingTalkTemplateRenderer(), properties, client, new DingTalkUserIdentityService(mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper.class), mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper.class), mock(cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkContactClient.class)),new DingTalkTemplateParameterService(new org.springframework.beans.factory.support.StaticListableBeanFactory().getBeanProvider(cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider.class)));
        template.setId(1L); template.setName("任务提醒"); template.setCode("task"); template.setMessageType("TEXT");
        template.setStatus(0); template.setContent(Map.of("content", "你好，{name}"));
        when(templates.selectByCode("task")).thenReturn(template);
        when(users.selectById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return AdminUserDO.builder().id(id).nickname("员工" + id).status(0).dingTalkUserId("ding" + id).build();
        });
        when(logs.selectByRequestAndUser(anyString(), anyLong())).thenAnswer(invocation -> stored.get((Long) invocation.getArgument(1)));
        when(logs.insert(any(DingTalkMessageLogDO.class))).thenAnswer(invocation -> {
            DingTalkMessageLogDO log = invocation.getArgument(0); log.setId(log.getUserId() + 100); stored.put(log.getUserId(), log); return 1;
        });
        when(client.send(anyString(), anyString(), anyMap(), anyList())).thenReturn(Map.of(
                "ding1", new DingTalkRobotClient.Outcome(1, "p1", null, null),
                "ding2", new DingTalkRobotClient.Outcome(2, "p1", "INVALID_USER", "用户无效")));
        request = new DingTalkMessageSendReqDTO(); request.setRequestId("req1"); request.setTemplateCode("task");
        request.setUserIds(List.of(1L, 2L)); request.setParams(Map.of("name", "张三"));
    }
    @Test
    void sendsOneBatchAndRecordsEachRecipientOutcome() {
        var result = service.send(request);
        assertEquals(List.of(1, 2), result.stream().map(DingTalkMessageSendResultDTO::status).toList());
        assertEquals("你好，张三", stored.get(1L).getContent().get("content"));
        verify(client).send(eq("app"), eq("sampleText"), anyMap(), eq(List.of("ding1", "ding2")));
        verify(logs, times(2)).updateById(any(DingTalkMessageLogDO.class));
    }
    @Test
    void retryOfSameRequestReturnsPreviousResultsWithoutSendingAgain() {
        service.send(request); service.send(request);
        verify(client, times(1)).send(anyString(), anyString(), anyMap(), anyList());
    }
    @Test
    void changedMessageCannotReuseExistingRequestId() {
        service.send(request); request.setParams(Map.of("name", "李四"));
        assertThrows(ServiceException.class, () -> service.send(request));
        verify(client, times(1)).send(anyString(), anyString(), anyMap(), anyList());
    }
    @Test
    void validatesAllRecipientsBeforeSendingOrCreatingLogs() {
        when(users.selectById(2L)).thenReturn(AdminUserDO.builder().id(2L).nickname("未同步用户").status(0).build());
        assertThrows(ServiceException.class, () -> service.send(request));
        verifyNoInteractions(client);
        verify(logs, never()).insert(any(DingTalkMessageLogDO.class));
    }
    @Test
    void disabledTemplateAndIntegrationCannotSend() {
        template.setStatus(1);
        assertThrows(ServiceException.class, () -> service.send(request));
        properties.setEnabled(false);
        assertThrows(ServiceException.class, () -> service.send(request));
        verifyNoInteractions(client);
    }
    @Test
    void unknownResultIsNotAutomaticallyRetried() {
        when(client.send(anyString(), anyString(), anyMap(), anyList())).thenReturn(Map.of(
                "ding1", new DingTalkRobotClient.Outcome(3, null, "TIMEOUT", "结果未知"),
                "ding2", new DingTalkRobotClient.Outcome(3, null, "TIMEOUT", "结果未知")));
        assertEquals(3, service.send(request).get(0).status());
        assertEquals(3, service.send(request).get(0).status());
        verify(client, times(1)).send(anyString(), anyString(), anyMap(), anyList());
    }
    @Test
    void duplicatedRecipientsDoNotGenerateDuplicateLogs() {
        request.setUserIds(List.of(1L, 1L));
        assertEquals(1, service.send(request).size());
        verify(logs, times(1)).insert(any(DingTalkMessageLogDO.class));
    }
    @Test
    void apiValidationIsInheritedByImplementationAndRejectsOversizedBatches() throws Exception {
        request.setUserIds(java.util.stream.LongStream.rangeClosed(1, 21).boxed().toList());
        try (var factory = jakarta.validation.Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator().forExecutables().validateParameters(service,
                    DingTalkMessageSendService.class.getMethod("send", DingTalkMessageSendReqDTO.class), new Object[]{request});
            assertFalse(violations.isEmpty());
            request.setUserIds(List.of(1L));
            request.setParams(Map.of("invalid-key", "value"));
            assertFalse(factory.getValidator().forExecutables().validateParameters(service,
                    DingTalkMessageSendService.class.getMethod("send", DingTalkMessageSendReqDTO.class), new Object[]{request}).isEmpty());
        }
    }

    @Test
    void boundLoginAccountUsesResolvedStaffIdAndRetryDoesNotResolveAgain() {
        var identities = mock(DingTalkUserIdentityService.class);
        service = new DingTalkMessageSendService(templates, logs, users, new DingTalkTemplateRenderer(), properties, client, identities,new DingTalkTemplateParameterService(new org.springframework.beans.factory.support.StaticListableBeanFactory().getBeanProvider(cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider.class)));
        when(users.selectById(1L)).thenReturn(AdminUserDO.builder().id(1L).nickname("已绑定账号").status(0).build());
        when(identities.resolveUserId(any())).thenReturn("resolved-staff");
        when(client.send(anyString(),anyString(),anyMap(),anyList())).thenReturn(Map.of(
                "resolved-staff",new DingTalkRobotClient.Outcome(1,"process",null,null)));
        request.setUserIds(List.of(1L));
        service.send(request);
        service.send(request);
        assertEquals("resolved-staff",stored.get(1L).getDingTalkUserId());
        verify(identities,times(1)).resolveUserId(any());
        verify(client,times(1)).send(eq("app"),eq("sampleText"),anyMap(),eq(List.of("resolved-staff")));
    }

}
