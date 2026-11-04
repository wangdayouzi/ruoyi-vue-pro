package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.mybatis.config.YudaoMybatisAutoConfiguration;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkTemplatePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.*;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.*;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** 仅显式开启时运行；目标为本地临时 PostgreSQL，先执行迁移。 */
@EnabledIfSystemProperty(named = "dingtalk.pg.test", matches = "true")
@SpringBootTest(classes = DingTalkPostgresTest.Config.class, properties = {
        "spring.datasource.url=jdbc:postgresql://127.0.0.1:15439/postgres", "spring.datasource.username=postgres",
        "spring.datasource.dynamic.primary=master",
        "spring.datasource.dynamic.datasource.master.url=jdbc:postgresql://127.0.0.1:15439/postgres",
        "spring.datasource.password=", "spring.datasource.driver-class-name=org.postgresql.Driver",
        "yudao.info.base-package=cn.iocoder.yudao.module.system.dal.mysql.dingtalk",
        "mybatis.lazy-initialization=true", "spring.sql.init.mode=never",
        "mybatis-plus.global-config.db-config.id-type=INPUT",
        "mybatis-plus.global-config.db-config.logic-delete-value=1",
        "mybatis-plus.global-config.db-config.logic-not-delete-value=0"
})
class DingTalkPostgresTest {
    @Configuration(proxyBeanMethods = false)
    @Import({DataSourceAutoConfiguration.class, YudaoMybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
    static class Config {
        @Bean
        SmartInitializingSingleton tenantTestInterceptor(MybatisPlusInterceptor interceptor) {
            return () -> {
                var chain = new java.util.ArrayList<>(interceptor.getInterceptors());
                chain.add(0, new TenantLineInnerInterceptor(new TenantLineHandler() {
                public Expression getTenantId() { return new LongValue(TenantContextHolder.getRequiredTenantId()); }
                }));
                interceptor.setInterceptors(chain);
            };
        }
    }
    @Resource DingTalkMessageTemplateMapper templates;
    @Resource DingTalkMessageLogMapper logs;
    @Resource javax.sql.DataSource dataSource;
    @BeforeEach
    void before() { TenantContextHolder.setTenantId(101L); }
    @AfterEach
    void after() {
        var jdbc = new JdbcTemplate(dataSource);
        jdbc.update("DELETE FROM system_dingtalk_message_log WHERE tenant_id IN (101,102)");
        jdbc.update("DELETE FROM system_dingtalk_message_template WHERE tenant_id IN (101,102)");
        TenantContextHolder.clear();
    }
    private DingTalkMessageTemplateDO insert() {
        var template = new DingTalkMessageTemplateDO();
        template.setName("数据库验证"); template.setCode("it_template"); template.setMessageType("MARKDOWN");
        template.setContent(Map.of("title", "标题 {name}", "text", "正文")); template.setParams(List.of("name"));
        template.setStatus(0); templates.insert(template);
        return template;
    }
    @Test
    void generatedSequenceAndJsonFieldsRoundTripAndTenantIsolation() {
        var template = insert();
        assertNotNull(template.getId());
        var loaded = templates.selectById(template.getId());
        assertEquals(template.getContent(), loaded.getContent());
        assertEquals(List.of("name"), loaded.getParams());
        assertEquals(101L, loaded.getTenantId());
        TenantContextHolder.setTenantId(102L);
        assertNull(templates.selectById(template.getId()));
        assertNotNull(insert().getId());
        TenantContextHolder.setTenantId(101L);
        var query = new DingTalkTemplatePageReqVO();
        query.setPageNo(1); query.setPageSize(10);
        var page = templates.selectPage(query);
        assertEquals(1, page.getTotal());
    }
    @Test
    void uniqueCodeAndSoftDeleteAllowRecreate() {
        var first = insert();
        assertThrows(DuplicateKeyException.class, this::insert);
        templates.deleteById(first.getId());
        assertNull(templates.selectById(first.getId()));
        assertNotNull(insert().getId());
    }
    @Test
    void sendLogDeduplicationAndJsonMapping() {
        var template = insert();
        var log = new DingTalkMessageLogDO();
        log.setRequestId("it_req"); log.setRequestHash("h"); log.setTemplateId(template.getId());
        log.setTemplateCode(template.getCode()); log.setTemplateName(template.getName()); log.setMessageType("TEXT");
        log.setRobotCode("robot"); log.setUserId(1L); log.setDingTalkUserId("ding1");
        log.setContent(Map.of("content", "测试")); log.setSendStatus(0);
        logs.insert(log);
        assertEquals("ding1", logs.selectByRequestAndUser("it_req", 1L).getDingTalkUserId());
        assertEquals(Map.of("content", "测试"), logs.selectById(log.getId()).getContent());
        log.setId(null);
        assertThrows(DuplicateKeyException.class, () -> logs.insert(log));
        TenantContextHolder.setTenantId(102L);
        assertNull(logs.selectByRequestAndUser("it_req", 1L));
    }
}
