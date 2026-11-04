package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.*;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendReqDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 使用真实校验器与 Spring 类代理，覆盖启动查询及入队时的接口继承校验。 */
class DingTalkNotificationValidationTest {
    ValidatorFactory factory;
    DingTalkNotificationQueueService proxy;
    JdbcTemplate jdbc;
    DingTalkNotificationRecipientApi recipients;
    @BeforeEach void setup() {
        factory=Validation.buildDefaultValidatorFactory();
        jdbc=mock(JdbcTemplate.class);
        var target=new DingTalkNotificationQueueService(jdbc,mock(DingTalkMessageSendApi.class),List.of(),mock(PlatformTransactionManager.class));
        recipients=mock(DingTalkNotificationRecipientApi.class);
        org.springframework.test.util.ReflectionTestUtils.setField(target,"recipients",recipients);
        var pf=new ProxyFactory(target); pf.setProxyTargetClass(true);
        pf.addAdvice(new MethodValidationInterceptor(factory.getValidator()));
        proxy=(DingTalkNotificationQueueService)pf.getProxy();
    }
    @AfterEach void close() { factory.close(); }
    @Test void startupExistsQueryDoesNotFailOnOtherMethodConstraintMetadata() {
        assertDoesNotThrow(()->assertFalse(proxy.exists("sample-urgent:8","sample-urgent-deadline","0")));
    }
    @Test void validRequestPassesProxyValidationAndReachesDatabase() {
        when(jdbc.update(anyString(),any(Object[].class))).thenReturn(1);
        assertDoesNotThrow(()->proxy.enqueue(request(),LocalDateTime.now(),null,null,null));
        verify(jdbc).update(anyString(),any(Object[].class));
    }
    @Test void invalidAndNullRequestsAreRejectedBeforeAnyDatabaseWrite() {
        var request=request(); request.setUserIds(List.of());
        assertThrows(ConstraintViolationException.class,()->proxy.enqueue(request,LocalDateTime.now(),null,null,null));
        assertThrows(ConstraintViolationException.class,()->proxy.enqueue(null,LocalDateTime.now(),null,null,null));
        verifyNoInteractions(jdbc);
    }
    private DingTalkMessageSendReqDTO request() {
        var req=new DingTalkMessageSendReqDTO(); req.setRequestId("validation-test-1");
        req.setTemplateCode("sample_urgent_notice"); req.setUserIds(List.of(20L)); req.setParams(Map.of());
        return req;
    }
    @Test void departmentOnlyPlanPassesClassProxyValidationIncludingDefaultOverload() {
        when(recipients.resolve(any(),any(),eq(true))).thenReturn(List.of(20L));
        when(jdbc.update(anyString(),any(Object[].class))).thenReturn(1);
        var plan=plan(); plan.setDeptIds(List.of(100L));
        assertDoesNotThrow(()->proxy.enqueuePlan(plan));
        verify(jdbc).update(anyString(),any(Object[].class));
    }
    @Test void emptyOrInvalidAudienceIsRejectedBeforeResolutionAndPersistence() {
        var plan=plan(); assertThrows(ConstraintViolationException.class,()->proxy.enqueuePlan(plan,id->true));
        plan.setDeptIds(List.of(-1L)); assertThrows(ConstraintViolationException.class,()->proxy.enqueuePlan(plan,id->true));
        verifyNoInteractions(jdbc,recipients);
    }
    private cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO plan() {
        var req=new cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkNotificationPlanReqDTO();
        req.setRequestId("plan-validation"); req.setTemplateCode("test"); req.setParams(Map.of()); req.setSendAfter(LocalDateTime.now()); return req;
    }
}
