package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.mybatis.config.YudaoMybatisAutoConfiguration;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.*;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 显式开启的本地数据库集成验证；每个用例均回滚，不留下业务记录。先执行迁移。 */
@EnabledIfEnvironmentVariable(named="SAMPLE_URGENT_PG_TEST",matches="true")
@SpringBootTest(classes=SampleUrgentPostgresTest.Config.class,properties={
        "spring.datasource.url=jdbc:postgresql://localhost:5432/yudao?currentSchema=public",
        "spring.datasource.username=postgres", "spring.datasource.password=${SAMPLE_URGENT_TEST_DB_PASSWORD:}",
        "spring.datasource.driver-class-name=org.postgresql.Driver", "spring.datasource.dynamic.primary=master",
        "spring.datasource.dynamic.datasource.master.url=jdbc:postgresql://localhost:5432/yudao",
        "yudao.info.base-package=cn.iocoder.yudao.module.reagent.dal.mysql", "mybatis.lazy-initialization=true",
        "mybatis-plus.mapper-locations=classpath*:mapper/urgent-test-only/*.xml",
        "mybatis-plus.global-config.db-config.id-type=INPUT", "mybatis-plus.global-config.db-config.logic-delete-value=1",
        "mybatis-plus.global-config.db-config.logic-not-delete-value=0", "spring.sql.init.mode=never"
})
@Transactional
class SampleUrgentPostgresTest {
    @Configuration(proxyBeanMethods=false)
    @EnableTransactionManagement
    @Import({DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class,
            YudaoMybatisAutoConfiguration.class,MybatisPlusAutoConfiguration.class,SampleUrgentService.class})
    static class Config {
        @Bean SampleUrgentPeopleService people() {
            var p=mock(SampleUrgentPeopleService.class);
            doAnswer(i -> { SampleUrgentSaveReqVO r=i.getArgument(0); if(r.getAppointmentUserIds()==null) r.setAppointmentUserIds(java.util.List.of(987654321L)); if(r.getNoticeUserIds()==null) r.setNoticeUserIds(java.util.List.of(987654321L)); if(r.getNoticeDeptIds()==null) r.setNoticeDeptIds(java.util.List.of()); return null; }).when(p).prepare(any());
            when(p.noticeNames(any(),any())).thenReturn("样品管理员"); return p;
        }
        @Bean AdminUserApi users() {
            var users=mock(AdminUserApi.class); var user=new AdminUserRespDTO(); user.setNickname("加急功能集成测试");
            when(users.getUser(987654321L)).thenReturn(user); return users;
        }
        @Bean PermissionApi permissions() {
            var p=mock(PermissionApi.class);
            when(p.hasAnyPermissions(987654321L,"reagent:sample-urgent:handle")).thenReturn(true);
            when(p.hasAnyPermissions(987654321L,"reagent:sample-urgent:update")).thenReturn(true); return p;
        }
        @Bean SmartInitializingSingleton tenantInterceptor(MybatisPlusInterceptor interceptor) {
            return () -> interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
                public Expression getTenantId() { return new LongValue(TenantContextHolder.getRequiredTenantId()); }
            }));
        }
    }
    @Resource SampleUrgentService service;
    @BeforeEach void before() {
        TenantContextHolder.setTenantId(987654321L);
        var login=new LoginUser(); login.setId(987654321L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login,null));
    }
    @AfterEach void after() { TenantContextHolder.clear(); SecurityContextHolder.clearContext(); }
    @Test void lifecyclePersistsLogsClearsOptionalDatesAndKeepsTenantIsolation() {
        var req=new SampleUrgentSaveReqVO(); req.setProjectNo("URGENT-INTEGRATION-ONLY");
        req.setNewProject(true); req.setBasNos("26BAS0001,26BAS0002-Blind;"); req.setContacts("项目人员甲、乙");
        req.setAppointmentUserIds(java.util.List.of(987654322L));
        req.setNoticeUserIds(java.util.List.of(987654321L)); req.setNoticeDeptIds(java.util.List.of());
        req.setSampleScope("PK管与ADA管"); req.setReason("次日检测"); req.setDeadline(LocalDateTime.now().plusDays(1));
        req.setExpectedArrival(LocalDateTime.now().plusHours(1));
        Long id=service.save(req);
        var row=service.get(id); assertEquals(java.util.List.of(987654322L),row.getAppointmentUserIds()); assertEquals(java.util.List.of(987654321L),row.getNoticeUserIds()); assertEquals(0,row.getStatus()); assertNotNull(row.getCreateTime());
        var mine=new SampleUrgentPageReqVO(); mine.setKeyword("URGENT-INTEGRATION-ONLY"); mine.setView("MINE");
        assertEquals(0,service.page(mine).getTotal()); mine.setView("SUBMITTED"); assertEquals(1,service.page(mine).getTotal());
        var appointed=new LoginUser(); appointed.setId(987654322L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(appointed,null));
        mine.setView("MINE"); assertEquals(1,service.page(mine).getTotal());
        mine.setView("SUBMITTED"); assertEquals(0,service.page(mine).getTotal());
        var submitter=new LoginUser(); submitter.setId(987654321L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(submitter,null));
        req.setId(id); req.setRevision(0); req.setChangeReason("已到样，清空预计时间"); req.setExpectedArrival(null);
        req.setNoticeUserIds(java.util.List.of()); req.setNoticeDeptIds(java.util.List.of(100L,101L));
        service.save(req); assertNull(service.get(id).getExpectedArrival()); assertEquals(java.util.List.of(987654322L),service.get(id).getAppointmentUserIds());
        assertEquals(java.util.List.of(),service.get(id).getNoticeUserIds()); assertEquals(java.util.List.of(100L,101L),service.get(id).getNoticeDeptIds());
        var claim=new SampleUrgentActionReqVO(); claim.setId(id); claim.setRevision(1); claim.setAction("CLAIM");
        service.action(claim);
        var partial=new SampleUrgentActionReqVO(); partial.setId(id); partial.setRevision(2); partial.setAction("PROGRESS");
        partial.setStatus(2); partial.setExceptionType("WAIT_REPLY"); partial.setExceptionNote("ADA管待回复");
        partial.setCompletedScope("全部PK管"); partial.setRemainingScope("ADA管"); service.action(partial);
        assertEquals(2,service.get(id).getStatus());
        var page=new SampleUrgentPageReqVO(); page.setKeyword("URGENT-INTEGRATION-ONLY"); page.setView("HANDLING");
        assertEquals(1,service.page(page).getTotal());
        partial.setRevision(3); partial.setStatus(3); partial.setExceptionType("NONE"); partial.setRemainingScope("");
        partial.setCompletedScope("全部PK和ADA管"); partial.setCompletedTime(LocalDateTime.now()); service.action(partial);
        row=service.get(id); assertEquals(3,row.getStatus()); assertEquals("",row.getExceptionNote());
        assertEquals("",row.getRemainingScope()); assertNotNull(row.getCompletedTime()); assertEquals(5,service.logs(id).size());
        assertEquals(0,service.page(page).getTotal());
        TenantContextHolder.setTenantId(987654322L);
        assertThrows(cn.iocoder.yudao.framework.common.exception.ServiceException.class,()->service.get(id));
        assertEquals(0,service.page(page).getTotal());
    }
}
