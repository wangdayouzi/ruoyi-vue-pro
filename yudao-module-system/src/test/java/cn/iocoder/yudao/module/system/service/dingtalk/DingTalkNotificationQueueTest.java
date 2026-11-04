package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.*;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 独立临时schema验证队列；不扫描实际待发消息，不调用真实钉钉。 */
@EnabledIfEnvironmentVariable(named="SAMPLE_URGENT_PG_TEST",matches="true")
class DingTalkNotificationQueueTest {
    static String schema;
    static JdbcTemplate admin;
    static DriverManagerDataSource dataSource;
    JdbcTemplate jdbc;
    DingTalkMessageSendApi sender;
    DingTalkNotificationQueueService service;
    @BeforeAll static void init() throws Exception {
        schema="codex_queue_"+UUID.randomUUID().toString().replace("-","");
        String password=System.getenv("SAMPLE_URGENT_TEST_DB_PASSWORD");
        admin=new JdbcTemplate(new DriverManagerDataSource("jdbc:postgresql://localhost:5432/yudao","postgres",password));
        admin.execute("CREATE SCHEMA "+schema);
        dataSource=new DriverManagerDataSource("jdbc:postgresql://localhost:5432/yudao?currentSchema="+schema,"postgres",password);
        new JdbcTemplate(dataSource).execute(new ClassPathResource("dingtalk/queue-schema.sql").getContentAsString(StandardCharsets.UTF_8));
        new JdbcTemplate(dataSource).execute("CREATE TABLE system_users(id bigint,tenant_id bigint,nickname varchar(100),deleted smallint DEFAULT 0)");
    }
    @AfterAll static void cleanup() { if(admin!=null && schema!=null) admin.execute("DROP SCHEMA "+schema+" CASCADE"); }
    @BeforeEach void before() {
        jdbc=new JdbcTemplate(dataSource); jdbc.execute("TRUNCATE system_dingtalk_notification_queue");
        cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.clear();
        sender=mock(DingTalkMessageSendApi.class);
        when(sender.send(any())).thenAnswer(i -> { DingTalkMessageSendReqDTO r=i.getArgument(0); return List.of(new DingTalkMessageSendResultDTO(1L,r.getUserIds().get(0),1,null)); });
        service=new DingTalkNotificationQueueService(jdbc,sender,List.of(),new DataSourceTransactionManager(dataSource));
    }
    @AfterEach void clearTenant() { cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.clear(); }
    private DingTalkNotificationManageService management() { return new DingTalkNotificationManageService(jdbc,List.of()); }
    private long id() { return jdbc.queryForObject("SELECT max(id) FROM system_dingtalk_notification_queue",Long.class); }
    @Test void failedMessageResendUsesNewRequestAndKeepsOriginal() {
        service.enqueue(request(),LocalDateTime.now(),null,null,null);
        long original=id(); jdbc.update("UPDATE system_dingtalk_notification_queue SET status=4");
        management().resend(original,42L,"已修复接收人配置",false);
        var retry=management().get(id());
        assertEquals(original,retry.retryOf()); assertEquals(1,retry.retryNo()); assertEquals(42L,retry.operatorId());
        assertNotEquals("queue-test-1",retry.requestId()); assertEquals(4,management().get(original).status());
        assertThrows(RuntimeException.class,()->management().resend(original,42L,"重复点击",false));
        service.dispatch(); assertEquals(2,management().get(retry.id()).status()); verify(sender,times(1)).send(any());
    }
    @Test void unknownResendRequiresConfirmationAndHasThreeAttemptLimit() {
        service.enqueue(request(),LocalDateTime.now(),null,null,null);
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=3");
        long original=id();
        assertThrows(RuntimeException.class,()->management().resend(original,42L,"尚未确认",false));
        for(int i=0;i<3;i++) {
            management().resend(id(),42L,"已核实未收到",true);
            jdbc.update("UPDATE system_dingtalk_notification_queue SET status=3 WHERE id=?",id());
        }
        assertEquals(4,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
        assertThrows(RuntimeException.class,()->management().resend(id(),42L,"超出次数",true));
    }
    @Test void managementCannotReadCancelOrRetryOtherTenants() {
        service.enqueue(request(),LocalDateTime.now().plusHours(1),null,null,null);
        long original=id();
        cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.setTenantId(2L);
        assertThrows(RuntimeException.class,()->management().get(original));
        assertThrows(RuntimeException.class,()->management().cancel(original,42L,"取消"));
        assertThrows(RuntimeException.class,()->management().resend(original,42L,"重发",true));
        var req=new cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkQueuePageReqVO();
        assertEquals(0,management().page(req).getTotal());
        assertEquals(0,status());
    }
    @Test void cancellationAndResendCheckStateAndExpiry() {
        service.enqueue(request(),LocalDateTime.now().minusMinutes(2),LocalDateTime.now().minusMinutes(1),null,null);
        long original=id();
        management().cancel(original,42L,"不再需要"); assertEquals(5,status());
        assertThrows(RuntimeException.class,()->management().cancel(original,42L,"重复取消"));
        assertThrows(RuntimeException.class,()->management().resend(original,42L,"取消后重发",true));
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=4");
        assertThrows(RuntimeException.class,()->management().resend(original,42L,"过期重发",true));
    }
    @Test void resendRechecksBusinessGuard() {
        var guard=mock(DingTalkNotificationGuard.class); when(guard.businessType()).thenReturn("test");
        service.enqueue(request(),LocalDateTime.now(),null,"test","1");
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=4");
        var manager=new DingTalkNotificationManageService(jdbc,List.of(guard));
        assertThrows(RuntimeException.class,()->manager.resend(id(),42L,"业务已结束",true));
    }
    @Test void concurrentResendsCreateOnlyOneNewNotice() throws Exception {
        service.enqueue(request(),LocalDateTime.now(),null,null,null);
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=4");
        long original=id();
        var start=new java.util.concurrent.CountDownLatch(1);
        var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            java.util.concurrent.Callable<Boolean> task=()->{
                start.await();
                try {
                    new org.springframework.transaction.support.TransactionTemplate(new DataSourceTransactionManager(dataSource))
                            .executeWithoutResult(s->management().resend(original,42L,"并发重发",false));
                    return true;
                } catch(RuntimeException ex) { return false; }
            };
            var one=pool.submit(task); var two=pool.submit(task); start.countDown();
            assertNotEquals(one.get(10,java.util.concurrent.TimeUnit.SECONDS),two.get(10,java.util.concurrent.TimeUnit.SECONDS));
            assertEquals(2,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
        } finally { pool.shutdownNow(); }
    }
    @Test void persistsDeduplicatesAndDoesNotSendAcceptedMessageAgain() {
        var req=request(); service.enqueue(req,LocalDateTime.now().minusMinutes(1),null,null,null);
        service.enqueue(req,LocalDateTime.now().minusMinutes(1),null,null,null);
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
        service.dispatch(); service.dispatch(); assertEquals(2,status()); verify(sender,times(1)).send(any());
        req.setParams(Map.of("name","different"));
        assertThrows(IllegalArgumentException.class,()->service.enqueue(req,LocalDateTime.now(),null,null,null));
    }
    @Test void futureAndExpiredMessagesAreNotSent() {
        service.enqueue(request(),LocalDateTime.now().plusHours(1),null,null,null);
        service.dispatch(); assertEquals(0,status()); verifyNoInteractions(sender);
        jdbc.update("UPDATE system_dingtalk_notification_queue SET send_after=now()-interval '2 hours',expires_at=now()-interval '1 hour'");
        service.dispatch(); assertEquals(6,status()); verifyNoInteractions(sender);
    }
    @Test void businessGuardCancelsObsoleteMessages() {
        var guard=mock(DingTalkNotificationGuard.class); when(guard.businessType()).thenReturn("test");
        service=new DingTalkNotificationQueueService(jdbc,sender,List.of(guard),new DataSourceTransactionManager(dataSource));
        service.enqueue(request(),LocalDateTime.now().minusMinutes(1),null,"test","old");
        service.dispatch(); assertEquals(5,status()); verifyNoInteractions(sender);
    }
    @Test void unknownResultAndStaleProcessingAreNotAutomaticallyRetried() {
        doReturn(List.of(new DingTalkMessageSendResultDTO(1L,10L,3,"unknown"))).when(sender).send(any());
        service.enqueue(request(),LocalDateTime.now().minusMinutes(1),null,null,null);
        service.dispatch(); service.dispatch(); assertEquals(3,status()); verify(sender,times(1)).send(any());
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=1,update_time=now()-interval '11 minutes'");
        service.dispatch(); assertEquals(3,status()); verify(sender,times(1)).send(any());
    }
    @Test void cancellationIsScopedToTenantAndBusiness() {
        service.enqueue(request(),LocalDateTime.now().plusHours(1),null,null,null);
        assertEquals(0,service.cancel("other")); assertEquals(1,service.cancel("test:1")); assertEquals(5,status());
    }
    @Test void replacingDeadlinePlanPreservesImmediateMessagesAndTracksEachRecipient() {
        var immediate=request();
        service.enqueue(immediate,LocalDateTime.now().plusHours(1),null,null,null);
        var due=request(); due.setRequestId("future-deadline"); due.setUserIds(List.of(10L,11L));
        service.enqueue(due,LocalDateTime.now().plusHours(1),null,"deadline","4");
        assertTrue(service.exists("test:1","deadline","4"));
        assertFalse(service.exists("test:1","deadline","3"));
        jdbc.update("UPDATE system_dingtalk_notification_queue SET status=2 WHERE guard_type='deadline' AND user_id=10");
        assertEquals(1,service.cancelPending("test:1","deadline"));
        assertEquals(0,jdbc.queryForObject("SELECT status FROM system_dingtalk_notification_queue WHERE request_id='queue-test-1'",Integer.class));
        cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.setTenantId(2L);
        assertFalse(service.exists("test:1","deadline","4"));
        assertEquals(0,service.cancelPending("test:1","deadline"));
    }
    @Test void queueInsertionRollsBackWithBusinessTransaction() {
        var tx=new org.springframework.transaction.support.TransactionTemplate(new DataSourceTransactionManager(dataSource));
        tx.executeWithoutResult(status -> {
            service.enqueue(request(),LocalDateTime.now(),null,null,null);
            status.setRollbackOnly();
        });
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
    }
    @Test void departmentPlanPersistsOnlyDeduplicatedEligibleIndividualsAndSupportsMoreThanTwenty() {
        var users=mock(cn.iocoder.yudao.module.system.api.user.AdminUserApi.class);
        var depts=mock(cn.iocoder.yudao.module.system.api.dept.DeptApi.class);
        var dept=new cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO(); dept.setId(100L); dept.setStatus(0);
        when(depts.getDeptList(anyCollection())).thenReturn(List.of(dept));
        when(depts.getChildDeptList(anyCollection())).thenReturn(List.of());
        var members=java.util.stream.LongStream.rangeClosed(10,35).mapToObj(id->{
            var user=new cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO(); user.setId(id); user.setStatus(0); return user;
        }).toList();
        when(users.getUserListByDeptIds(anyCollection())).thenReturn(members);
        when(users.getUserList(anyCollection())).thenAnswer(i->{Collection<Long> ids=i.getArgument(0);return members.stream().filter(u->ids.contains(u.getId())).toList();});
        org.springframework.test.util.ReflectionTestUtils.setField(service,"recipients",new DingTalkNotificationRecipientService(users,depts));
        var plan=new DingTalkNotificationPlanReqDTO(); plan.setRequestId("department-plan"); plan.setTemplateCode("test");
        plan.setUserIds(List.of(10L,11L)); plan.setDeptIds(List.of(100L)); plan.setExcludedUserIds(List.of(10L));
        plan.setParams(Map.of("name","test")); plan.setBusinessId("test:scope"); plan.setSendAfter(LocalDateTime.now().minusMinutes(1));
        service.enqueuePlan(plan,id->id!=11L); service.enqueuePlan(plan,id->id!=11L);
        assertEquals(24,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
        var records=jdbc.queryForList("SELECT user_id,payload FROM system_dingtalk_notification_queue");
        for(var record:records) {
            var req=cn.iocoder.yudao.framework.common.util.json.JsonUtils.parseObject((String)record.get("payload"),DingTalkMessageSendReqDTO.class);
            assertEquals(List.of((Long)record.get("user_id")),req.getUserIds());
            assertFalse(((String)record.get("payload")).contains("deptIds"));
        }
        doAnswer(i->{ DingTalkMessageSendReqDTO req=i.getArgument(0);return List.of(new DingTalkMessageSendResultDTO(1L,req.getUserIds().get(0),req.getUserIds().get(0)==12L?2:1,null)); }).when(sender).send(any());
        service.dispatch();
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue WHERE status=4",Integer.class));
        assertEquals(23,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue WHERE status=2",Integer.class));
    }
    @Test void scopePlanDoesNotLeavePartialRecordsWhenBusinessEligibilityCheckFails() {
        var recipients=mock(DingTalkNotificationRecipientApi.class);
        when(recipients.resolve(any(),any(),eq(true))).thenReturn(List.of(10L,11L));
        org.springframework.test.util.ReflectionTestUtils.setField(service,"recipients",recipients);
        var plan=new DingTalkNotificationPlanReqDTO(); plan.setRequestId("atomic-plan"); plan.setTemplateCode("test");
        plan.setDeptIds(List.of(100L)); plan.setParams(Map.of()); plan.setSendAfter(LocalDateTime.now());
        var tx=new org.springframework.transaction.support.TransactionTemplate(new DataSourceTransactionManager(dataSource));
        assertThrows(IllegalStateException.class,()->tx.executeWithoutResult(s->service.enqueuePlan(plan,id->{
            if(id==11L) throw new IllegalStateException("业务校验失败"); return true;
        })));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM system_dingtalk_notification_queue",Integer.class));
    }
    private int status() { return jdbc.queryForObject("SELECT status FROM system_dingtalk_notification_queue",Integer.class); }
    private DingTalkMessageSendReqDTO request() {
        var r=new DingTalkMessageSendReqDTO(); r.setRequestId("queue-test-1"); r.setTemplateCode("test");
        r.setUserIds(List.of(10L)); r.setParams(Map.of("name","test")); r.setBusinessId("test:1"); return r;
    }
}
