package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.mybatis.config.YudaoMybatisAutoConfiguration;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.*;
import cn.iocoder.yudao.module.reagent.dal.mysql.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.*;
import java.time.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 必须使用一次性隔离 schema；不导入钉钉监听器，不发送真实通知。 */
@EnabledIfEnvironmentVariable(named = "SAMPLE_URGENT_PLAN_PG_TEST", matches = "true")
@SpringBootTest(classes = SampleUrgentPlanPostgresTest.Config.class, properties = {
        "yudao.tenant.enable=false",
        "spring.datasource.url=${SAMPLE_URGENT_PLAN_TEST_DB_URL}",
        "spring.datasource.username=postgres", "spring.datasource.password=${SAMPLE_URGENT_TEST_DB_PASSWORD:}",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.datasource.dynamic.primary=master",
        "spring.datasource.dynamic.datasource.master.url=${SAMPLE_URGENT_PLAN_TEST_DB_URL}",
        "yudao.info.base-package=cn.iocoder.yudao.module.reagent.dal.mysql", "mybatis.lazy-initialization=true",
        "mybatis-plus.mapper-locations=classpath*:mapper/urgent-test-only/*.xml",
        "mybatis-plus.global-config.db-config.id-type=INPUT", "mybatis-plus.global-config.db-config.logic-delete-value=1",
        "mybatis-plus.global-config.db-config.logic-not-delete-value=0", "spring.sql.init.mode=never"
})
class SampleUrgentPlanPostgresTest {
    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    @Import({DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
            YudaoMybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class,
            SampleUrgentPlanService.class, SampleUrgentPlanGenerator.class, SampleUrgentService.class})
    static class Config {
        @Bean AdminUserApi users() { return mock(AdminUserApi.class); }
        @Bean PermissionApi permissions() { return mock(PermissionApi.class); }
        @Bean SampleUrgentPeopleService people() {
            var people = mock(SampleUrgentPeopleService.class);
            doAnswer(i -> { var req = (SampleUrgentSaveReqVO) i.getArgument(0); req.setContacts("隔离测试预约人"); return null; }).when(people).prepare(any());
            when(people.noticeNames(any(), any())).thenReturn("");
            return people;
        }
    }
    @Resource SampleUrgentPlanService service;
    @Resource SampleUrgentPlanGenerator generator;
    @Resource SampleUrgentPlanMapper plans;
    @Resource SampleUrgentMapper records;
    @Resource AdminUserApi users;
    @Resource PermissionApi permissions;
    @BeforeEach void setup() {
        String url = System.getenv("SAMPLE_URGENT_PLAN_TEST_DB_URL");
        assertNotNull(url);
        assertTrue(url.startsWith("jdbc:postgresql://localhost:5432/yudao?currentSchema=urgent_recurring_it_"));
        reset(users, permissions);
        var user = new AdminUserRespDTO(); user.setNickname("隔离测试提交人"); when(users.getUser(10L)).thenReturn(user);
        when(permissions.hasAnyPermissions(eq(10L), anyString())).thenReturn(true);
        var login = new LoginUser(); login.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, null));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    private SampleUrgentPlanSaveReqVO request() {
        var req = new SampleUrgentPlanSaveReqVO(); var today = SampleUrgentPlanRules.today();
        req.setStartDate(today); req.setEndDate(today.plusDays(5)); req.setDailyTime(LocalTime.of(17, 30));
        req.setWeekdays(List.of(1, 2, 3, 4, 5, 6, 7));
        var template = new SampleUrgentSaveReqVO(); template.setProjectNo("RECURRING-ISOLATED-TEST");
        template.setNewProject(false); template.setInboundSite("NINGBO"); template.setBasNos("26BAS-TEST");
        template.setSampleScope("所有检测管"); template.setAppointmentUserIds(List.of(10L));
        template.setNoticeUserIds(List.of()); template.setNoticeDeptIds(List.of());
        req.setTemplate(template); return req;
    }
    private long count(Long id) { return records.selectCount(new LambdaQueryWrapperX<SampleUrgentDO>().eq(SampleUrgentDO::getRecurringPlanId, id)); }

    @Test @Transactional void jsonRoundTripAndDeletedDailyRecordDoNotRegenerate() {
        Long id = service.create(request());
        var plan = plans.selectById(id);
        assertEquals("26BAS-TEST", plan.getTemplate().getBasNos());
        assertEquals(LocalTime.of(17, 30), plan.getDailyTime());
        assertEquals(List.of(1,2,3,4,5,6,7), plan.getWeekdays());
        var row = records.selectOne(new LambdaQueryWrapperX<SampleUrgentDO>().eq(SampleUrgentDO::getRecurringPlanId, id));
        assertEquals(10L, row.getRequesterId()); assertEquals(0, row.getStatus());
        assertEquals(SampleUrgentPlanRules.today().atTime(17, 30), row.getDeadline());
        records.deleteById(row.getId());
        plan.setNextDate(SampleUrgentPlanRules.today()); plans.updateById(plan);
        generator.generate(id, SampleUrgentPlanRules.today());
        assertEquals(0, count(id)); assertEquals(1, records.countPlanDay(id, SampleUrgentPlanRules.today()));
    }
    @Test void twoWorkersGenerateSameDayOnlyOnceWithoutLoginContext() throws Exception {
        Long id = service.create(request());
        var tomorrow = SampleUrgentPlanRules.today().plusDays(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            var first = CompletableFuture.runAsync(() -> generator.generate(id, tomorrow), executor);
            var second = CompletableFuture.runAsync(() -> generator.generate(id, tomorrow), executor);
            CompletableFuture.allOf(first, second).get(20, TimeUnit.SECONDS);
        } finally { executor.shutdownNow(); }
        assertEquals(2, count(id));
        assertEquals(1, records.countPlanDay(id, tomorrow));
        assertEquals(tomorrow.plusDays(1), plans.selectById(id).getNextDate());
    }
    @Test void failureRollsBackAllNewDaysAndCursorTogether() {
        Long id = service.create(request());
        var tomorrow = SampleUrgentPlanRules.today().plusDays(1);
        var calls = new AtomicInteger();
        doAnswer(i -> { if (calls.incrementAndGet() == 2) throw new IllegalStateException("模拟第二天生成失败"); return null; }).when(users).validateUser(10L);
        assertThrows(IllegalStateException.class, () -> generator.generate(id, tomorrow.plusDays(1)));
        assertEquals(1, count(id));
        assertEquals(tomorrow, plans.selectById(id).getNextDate());
        assertEquals(0, records.countPlanDay(id, tomorrow));
    }
    @Test @Transactional void staleWorkerFailureCannotPausePlanAfterAnotherWorkerAdvancedCursor() {
        Long id = service.create(request());
        var before = plans.selectById(id);
        generator.generate(id, before.getNextDate());
        service.pauseOnFailure(id, before.getRevision(), before.getNextDate(), "另一个实例的旧失败结果");
        assertEquals(0, plans.selectById(id).getStatus());
        assertEquals(before.getNextDate().plusDays(1), plans.selectById(id).getNextDate());
    }
}
