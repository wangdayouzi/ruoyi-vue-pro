package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkMessageSendApi;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SampleUrgentNotifierTest {
    SampleUrgentNotifier notifier;
    DingTalkMessageSendApi sender;
    SampleUrgentLogMapper logs;
    SampleUrgentDO row;
    ConfigApi config;
    @BeforeEach void setup() {
        TenantContextHolder.setTenantId(1L);
        notifier=new SampleUrgentNotifier(); sender=mock(DingTalkMessageSendApi.class); logs=mock(SampleUrgentLogMapper.class);
        config=mock(ConfigApi.class); var mapper=mock(SampleUrgentMapper.class); var permissions=mock(PermissionApi.class);
        ReflectionTestUtils.setField(notifier,"mapper",mapper); ReflectionTestUtils.setField(notifier,"logs",logs);
        ReflectionTestUtils.setField(notifier,"sender",sender); ReflectionTestUtils.setField(notifier,"config",config);
        ReflectionTestUtils.setField(notifier,"permissions",permissions);
        var people=mock(SampleUrgentPeopleService.class); when(people.administratorEligible(anyLong())).thenReturn(true);
        ReflectionTestUtils.setField(notifier,"people",people);
        var queue=mock(cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue.class);
        ReflectionTestUtils.setField(notifier,"notificationQueue",queue);
        doAnswer(i -> {
            DingTalkNotificationPlanReqDTO plan=i.getArgument(0); java.util.function.Predicate<Long> eligible=i.getArgument(1);
            if(plan.getRequestId().startsWith("urgent-event-")) for(Long id:new LinkedHashSet<>(plan.getUserIds())) {
                if(plan.getExcludedUserIds().contains(id) || !eligible.test(id)) continue;
                var req=new DingTalkMessageSendReqDTO(); req.setRequestId(plan.getRequestId()); req.setTemplateCode(plan.getTemplateCode());
                req.setBusinessId(plan.getBusinessId()); req.setParams(plan.getParams()); req.setUserIds(List.of(id)); sender.send(req);
            }
            return null;
        }).when(queue).enqueuePlan(any(),any());
        when(permissions.hasAnyPermissions(anyLong(),anyString())).thenReturn(true);
        when(sender.getDefaultUserIds("sample_urgent_notice")).thenReturn(List.of(20L,21L));
        doAnswer(i -> {
            DingTalkMessageSendReqDTO r=i.getArgument(0); return List.of(new DingTalkMessageSendResultDTO(1L,r.getUserIds().get(0),1,null));
        }).when(sender).send(any());
        row=new SampleUrgentDO(); row.setId(1L); row.setRequesterId(10L); row.setRequesterName("项目登记人");
        row.setProjectNo("PROJECT"); row.setSampleScope("所有PK管"); row.setContacts("甲、乙");
        row.setDeadline(LocalDateTime.now().plusDays(1)); row.setStatus(0); row.setRevision(0);
        when(mapper.selectById(1L)).thenReturn(row);
    }
    @AfterEach void after() { TenantContextHolder.clear(); }
    @Test void newDemandUsesTemplateSelectedPeopleAndStableRequestId() {
        notifier.notifyEvent(event(10L,"登记需求"));
        var args=ArgumentCaptor.forClass(DingTalkMessageSendReqDTO.class); verify(sender,times(2)).send(args.capture());
        assertEquals(List.of(20L,21L),args.getAllValues().stream().map(r -> r.getUserIds().get(0)).toList());
        assertEquals("urgent-event-100",args.getValue().getRequestId()); assertEquals("sample-urgent:1",args.getValue().getBusinessId());
        verifyNoInteractions(logs);
    }
    @Test void afterClaimChangesOnlyNotifyCurrentHandler() {
        row.setHandlerId(22L); notifier.notifyEvent(event(10L,"修改需求"));
        var args=ArgumentCaptor.forClass(DingTalkMessageSendReqDTO.class); verify(sender).send(args.capture());
        assertEquals(List.of(22L),args.getValue().getUserIds()); verify(sender,never()).getDefaultUserIds(anyString());
    }
    @Test void progressAndSampleSideRepliesNotifyRequesterRatherThanDefaultGroup() {
        notifier.notifyEvent(event(20L,"更新进度"));
        var args=ArgumentCaptor.forClass(DingTalkMessageSendReqDTO.class); verify(sender).send(args.capture());
        assertEquals(List.of(10L),args.getValue().getUserIds());
    }
    @Test void actorIsNotNotifiedAgain() {
        row.setHandlerId(10L); notifier.notifyEvent(event(10L,"补充说明")); verify(sender,never()).send(any());
    }
    @Test void missingDefaultsAndSendFailuresLeaveBusinessVisibleErrors() {
        when(sender.getDefaultUserIds(anyString())).thenReturn(List.of()); notifier.notifyEvent(event(10L,"登记需求"));
        verify(logs).insert(any(SampleUrgentLogDO.class)); verify(sender,never()).send(any());
    }
    @Test void queuePreparationFailureIsRecordedWithoutCallingSender() {
        doThrow(new IllegalStateException("队列配置异常")).when(queue()).enqueuePlan(any(),any());
        assertDoesNotThrow(() -> notifier.notifyEvent(event(10L,"登记需求")));
        verify(logs).insert(any(SampleUrgentLogDO.class)); verify(sender,never()).send(any());
    }
    @Test void onlyDeclaredBusinessFieldsAreProvidedAndLongContentIsBounded() {
        var params=notifier.params(row,"补充说明","a".repeat(5000));
        assertFalse(params.containsKey("detailUrl"));
        assertFalse(params.containsKey("content"));
    }
    @Test void disabledNotificationsDoNotSend() {
        when(config.getConfigValueByKey("reagent.sample-urgent.dingtalk.1.enabled")).thenReturn("false");
        notifier.notifyEvent(event(10L,"登记需求")); verify(sender,never()).send(any());
    }
    @Test void notificationCommitFailureDoesNotEscapeIntoCommittedBusinessRequest() {
        var manager=mock(org.springframework.transaction.PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenReturn(new org.springframework.transaction.support.SimpleTransactionStatus());
        doThrow(new IllegalStateException("模拟通知事务提交失败")).when(manager).commit(any());
        ReflectionTestUtils.setField(notifier,"transactionManager",manager);
        when(config.getConfigValueByKey("reagent.sample-urgent.dingtalk.1.enabled")).thenReturn("false");
        assertDoesNotThrow(() -> notifier.onEvent(event(10L,"登记需求")));
        assertEquals(1L,TenantContextHolder.getRequiredTenantId());
    }
    @Test void singleTenantEventUsesPrimaryNoticeScopeAndRestoresMissingContext() {
        TenantContextHolder.clear();
        var manager=mock(org.springframework.transaction.PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenReturn(new org.springframework.transaction.support.SimpleTransactionStatus());
        ReflectionTestUtils.setField(notifier,"transactionManager",manager);
        notifier.onEvent(new SampleUrgentNoticeEvent(null,1L,100L,10L,"登记需求","说明"));
        verify(sender,times(2)).send(any());
        verify(config,atLeastOnce()).getConfigValueByKey("reagent.sample-urgent.dingtalk.1.enabled");
        assertNull(TenantContextHolder.getTenantId());
    }
    private cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue queue() {
        return (cn.iocoder.yudao.module.system.api.dingtalk.DingTalkNotificationQueue)ReflectionTestUtils.getField(notifier,"notificationQueue");
    }
    @Test void futureReminderIsStoredImmediatelyForDeadlineMinusLeadTime() {
        row.setHandlerId(20L); row.setDeadline(LocalDateTime.now().plusDays(1)); row.setRevision(4);
        notifier.scheduleDeadline(row);
        var req=ArgumentCaptor.forClass(DingTalkNotificationPlanReqDTO.class); verify(queue()).enqueuePlan(req.capture(),any());
        assertEquals(row.getDeadline().minusMinutes(120),req.getValue().getSendAfter());
        assertEquals(row.getDeadline(),req.getValue().getExpiresAt()); assertEquals("sample-urgent-deadline",req.getValue().getGuardType());
        assertEquals("4",req.getValue().getBusinessVersion()); assertEquals(List.of(20L),req.getValue().getUserIds()); verify(sender,never()).send(any());
    }
    @Test void completedCancelledAndExpiredDoNotCreateFutureReminders() {
        row.setStatus(3); notifier.scheduleDeadline(row); row.setStatus(4); notifier.scheduleDeadline(row);
        row.setStatus(0); row.setDeadline(LocalDateTime.now().minusMinutes(1)); notifier.scheduleDeadline(row);
        verify(queue(),never()).enqueuePlan(any(),any());
    }
    @Test void enteringReminderWindowUsesCurrentTimeAndMasterSwitchStillApplies() {
        row.setHandlerId(20L); row.setDeadline(LocalDateTime.now().plusMinutes(60));
        var before=LocalDateTime.now(); notifier.scheduleDeadline(row);
        var plan=ArgumentCaptor.forClass(DingTalkNotificationPlanReqDTO.class);
        verify(queue()).enqueuePlan(plan.capture(),any());
        assertFalse(plan.getValue().getSendAfter().isBefore(before)); assertFalse(plan.getValue().getSendAfter().isAfter(LocalDateTime.now()));
        clearInvocations(queue());
        when(config.getConfigValueByKey("reagent.sample-urgent.dingtalk.1.enabled")).thenReturn("false");
        notifier.scheduleDeadline(row); verifyNoInteractions(queue());
    }
    @Test void changingDemandCancelsOnlyOldDeadlineNoticeAndCreatesNewPlan() {
        notifier.notifyEvent(event(10L,"修改需求"));
        verify(queue()).cancelPending("sample-urgent:1","sample-urgent-deadline");
        verify(queue(),never()).cancel(anyString());
        var plan=ArgumentCaptor.forClass(DingTalkNotificationPlanReqDTO.class); verify(queue(),times(2)).enqueuePlan(plan.capture(),any());
        assertEquals(row.getDeadline().minusMinutes(120),plan.getValue().getSendAfter());
    }
    @Test void commentsDoNotRegenerateRemindersAndStartupDoesNotResendExistingVersion() {
        notifier.notifyEvent(event(10L,"补充说明"));
        verify(queue(),never()).cancelPending(anyString(),anyString());
        clearInvocations(queue());
        var mapper=(SampleUrgentMapper)ReflectionTestUtils.getField(notifier,"mapper");
        when(mapper.lock(1L)).thenReturn(row);
        when(queue().exists("sample-urgent:1","sample-urgent-deadline","0")).thenReturn(true);
        notifier.initializeDeadline(1L);
        verify(queue(),never()).enqueuePlan(any(),any());
    }
    @Test void missingTemplateDuringStartupRecordsProblemWithoutFailingApplication() {
        var mapper=(SampleUrgentMapper)ReflectionTestUtils.getField(notifier,"mapper");
        when(mapper.lock(1L)).thenReturn(row);
        when(sender.getDefaultUserIds(anyString())).thenThrow(
                cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException("钉钉消息模板不存在"));
        assertDoesNotThrow(()->notifier.initializeDeadline(1L));
        var entry=ArgumentCaptor.forClass(SampleUrgentLogDO.class);
        verify(logs).insert(entry.capture());
        assertTrue(entry.getValue().getContent().contains("钉钉消息模板不存在"));
        verify(queue(),never()).enqueuePlan(any(),any());
    }
    @Test void registrationMessageDoesNotRepeatSnapshotOrExposeRawValuesAndEmptyFields() {
        row.setRemark("654654"); row.setNewProject(true);
        var params=notifier.params(row,"登记需求","项目：PROJECT\n新项目：true\n要求完成：2026-11-02T17:59\n备注：");
        assertFalse(params.containsKey("content")); assertEquals("654654",params.get("remark"));
        assertEquals("未接手",params.get("handlerName"));
        row.setExpectedArrival(LocalDateTime.of(2026,11,2,16,0));
        assertFalse(notifier.params(row,"登记需求","").containsKey("content"));
    }
    @Test void changeMessageKeepsReasonAndExcludesBeforeAfterSnapshots() {
        row.setReason("补测需要");
        var content=notifier.params(row,"修改需求","截止时间调整\n修改前：\n项目：旧项目\n修改后：\n项目：新项目").get("changeReason");
        assertEquals("截止时间调整",content);
    }
    @Test void selectedAdministratorsReceiveNewDemandAndAppointedPeopleReceiveProgress() {
        row.setNoticeUserIds(List.of(40L)); row.setAppointmentUserIds(List.of(30L,31L));
        var people=(SampleUrgentPeopleService)ReflectionTestUtils.getField(notifier,"people");

        notifier.notifyEvent(event(10L,"登记需求"));
        var req=ArgumentCaptor.forClass(DingTalkMessageSendReqDTO.class); verify(sender).send(req.capture());
        assertEquals(List.of(40L),req.getValue().getUserIds()); verify(sender,never()).getDefaultUserIds(anyString());
        clearInvocations(sender); notifier.notifyEvent(event(40L,"更新进度"));
        verify(sender,times(2)).send(req.capture());
        assertEquals(Set.of(30L,31L),req.getAllValues().subList(1,3).stream().map(r->r.getUserIds().get(0)).collect(java.util.stream.Collectors.toSet()));
    }
    @Test void selectedDepartmentsArePassedToCommonQueueWithoutBusinessExpansion() {
        row.setNoticeDeptIds(List.of(100L)); row.setNoticeUserIds(List.of(40L));
        notifier.notifyEvent(event(10L,"登记需求"));
        var plan=ArgumentCaptor.forClass(DingTalkNotificationPlanReqDTO.class); verify(queue(),times(2)).enqueuePlan(plan.capture(),any());
        assertEquals(List.of(100L),plan.getAllValues().get(0).getDeptIds());
        assertEquals(List.of(40L),plan.getAllValues().get(0).getUserIds());
        assertEquals(List.of(10L),plan.getAllValues().get(0).getExcludedUserIds());
        var people=(SampleUrgentPeopleService)ReflectionTestUtils.getField(notifier,"people");
        verify(people,never()).administrators(any(),any());
    }
    @Test void registrationInsideReminderWindowProducesOnlyImmediateNotificationEvenAfterRestart() {
        row.setHandlerId(20L); row.setDeadline(LocalDateTime.now().plusMinutes(60)); row.setCreateTime(LocalDateTime.now());
        notifier.notifyEvent(event(10L,"登记需求"));
        verify(queue(),times(1)).enqueuePlan(any(),any());
        var mapper=(SampleUrgentMapper)ReflectionTestUtils.getField(notifier,"mapper"); when(mapper.lock(1L)).thenReturn(row);
        when(queue().exists("sample-urgent:1",null,"0")).thenReturn(true);
        notifier.initializeDeadline(1L); verify(queue(),times(1)).enqueuePlan(any(),any());
    }
    private SampleUrgentNoticeEvent event(Long actor,String action) { return new SampleUrgentNoticeEvent(1L,1L,100L,actor,action,"本次变更"); }
}
