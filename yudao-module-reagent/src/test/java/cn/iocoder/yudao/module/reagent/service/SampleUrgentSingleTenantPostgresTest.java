package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.mybatis.config.YudaoMybatisAutoConfiguration;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.DingTalkMessageTemplateMapper;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 与当前部署相同：关闭多租户、无请求租户上下文。所有测试写入均回滚，不连接钉钉。 */
@EnabledIfEnvironmentVariable(named="SAMPLE_URGENT_PG_TEST",matches="true")
@SpringBootTest(classes=SampleUrgentSingleTenantPostgresTest.Config.class,properties={
        "yudao.tenant.enable=false", "spring.datasource.url=jdbc:postgresql://localhost:5432/yudao?currentSchema=public",
        "spring.datasource.username=postgres", "spring.datasource.password=${SAMPLE_URGENT_TEST_DB_PASSWORD:}",
        "spring.datasource.driver-class-name=org.postgresql.Driver", "spring.datasource.dynamic.primary=master",
        "spring.datasource.dynamic.datasource.master.url=jdbc:postgresql://localhost:5432/yudao",
        "yudao.info.base-package=cn.iocoder.yudao.module.reagent.dal.mysql", "mybatis.lazy-initialization=true",
        "mybatis-plus.mapper-locations=classpath*:mapper/urgent-test-only/*.xml",
        "mybatis-plus.global-config.db-config.id-type=INPUT", "mybatis-plus.global-config.db-config.logic-delete-value=1",
        "mybatis-plus.global-config.db-config.logic-not-delete-value=0", "spring.sql.init.mode=never"
})
@Transactional
class SampleUrgentSingleTenantPostgresTest {
    @Configuration(proxyBeanMethods=false)
    @EnableTransactionManagement
    @MapperScan(basePackageClasses=DingTalkMessageTemplateMapper.class,annotationClass=Mapper.class,lazyInitialization="true")
    @Import({DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class,
            YudaoMybatisAutoConfiguration.class,MybatisPlusAutoConfiguration.class,SampleUrgentService.class})
    static class Config {
        @Bean SampleUrgentPeopleService people() {
            var p=mock(SampleUrgentPeopleService.class);
            doAnswer(i -> { SampleUrgentSaveReqVO r=i.getArgument(0); if(r.getAppointmentUserIds()==null) r.setAppointmentUserIds(java.util.List.of(987654321L)); if(r.getNoticeUserIds()==null) r.setNoticeUserIds(java.util.List.of(987654321L)); if(r.getNoticeDeptIds()==null) r.setNoticeDeptIds(java.util.List.of()); return null; }).when(p).prepare(any());
            when(p.noticeNames(any(),any())).thenReturn("样品管理员"); return p;
        }
        @Bean AdminUserApi users() {
            var users=mock(AdminUserApi.class); var user=new AdminUserRespDTO(); user.setNickname("单租户回归测试");
            when(users.getUser(987654321L)).thenReturn(user); return users;
        }
        @Bean PermissionApi permissions() {
            var p=mock(PermissionApi.class);
            when(p.hasAnyPermissions(eq(987654321L),anyString())).thenReturn(true); return p;
        }
    }
    @Resource SampleUrgentService service;
    @Resource DingTalkMessageTemplateMapper templates;
    @Resource cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentMapper urgentMapper;
    @Test void overdueUnfinishedFirstThenNumericNumberDescendingAcrossPages() {
        Long normal=insertSortRow(0,false);
        Long overdue=insertSortRow(1,true);
        Long partial=insertSortRow(2,true);
        Long completed=insertSortRow(3,true);
        var query=new SampleUrgentPageReqVO(); query.setKeyword("SORT-REGRESSION-ONLY"); query.setPageSize(2);
        var first=service.page(query);
        assertEquals(java.util.List.of(partial,overdue),first.getList().stream().map(r -> r.getId()).toList());
        query.setPageNo(2);
        assertEquals(java.util.List.of(completed,normal),service.page(query).getList().stream().map(r -> r.getId()).toList());
    }
    private Long insertSortRow(int status,boolean expired) {
        var row=new cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO();
        row.setProjectNo("SORT-REGRESSION-ONLY"); row.setNewProject(false); row.setBasNos("");
        row.setSampleScope("test"); row.setDeadline(LocalDateTime.now().plusDays(expired?-1:1));
        row.setContacts("test"); row.setReason("test"); row.setRequesterId(987654321L); row.setRequesterName("test");
        row.setStatus(status); row.setExceptionType("NONE"); row.setRevision(0); urgentMapper.insert(row); return row.getId();
    }
    @BeforeEach void before() {
        TenantContextHolder.clear(); var login=new LoginUser(); login.setId(987654321L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login,null));
    }
    @AfterEach void after() { TenantContextHolder.clear(); SecurityContextHolder.clearContext(); }
    @Test void registrationAndHandlingWorkWithoutTenantContextAndNoticeTemplateIsUnambiguous() {
        assertNull(TenantContextHolder.getTenantId());
        var req=new SampleUrgentSaveReqVO(); req.setProjectNo("URGENT-SINGLE-TENANT-REGRESSION");
        req.setNewProject(true); req.setBasNos("26BAS-TEST"); req.setContacts("项目人员");
        req.setSampleScope("全部PK管"); req.setReason("次日检测"); req.setDeadline(LocalDateTime.now().plusDays(1));
        Long id=service.save(req); assertEquals(0,service.get(id).getStatus());
        var claim=new SampleUrgentActionReqVO(); claim.setId(id); claim.setRevision(0); claim.setAction("CLAIM");
        service.action(claim); assertEquals(1,service.get(id).getStatus()); assertEquals(2,service.logs(id).size());
        assertEquals(1L,templates.selectByCode("sample_urgent_notice").getTenantId());
        assertNull(TenantContextHolder.getTenantId());
    }
}
