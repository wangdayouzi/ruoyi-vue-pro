package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentMapper;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SampleUrgentNotificationGuardTest {
    SampleUrgentDO row;
    ConfigApi config;
    SampleUrgentNotificationGuard guard;
    @BeforeEach void setup() {
        TenantContextHolder.setTenantId(1L);
        var mapper=mock(SampleUrgentMapper.class); config=mock(ConfigApi.class);
        row=new SampleUrgentDO(); row.setStatus(1); row.setRevision(3); row.setDeadline(LocalDateTime.now().plusMinutes(30));
        when(mapper.selectById(8L)).thenReturn(row);
        guard=new SampleUrgentNotificationGuard(mapper,config);
    }
    @AfterEach void clear() { TenantContextHolder.clear(); }
    @Test void onlyCurrentUnfinishedUnexpiredDemandIsApplicable() {
        assertTrue(guard.applicable("sample-urgent:8","3"));
        assertFalse(guard.applicable("sample-urgent:8","2"));
        assertFalse(guard.applicable("sample-urgent:9","3"));
        assertFalse(guard.applicable("other:8","3"));
        row.setStatus(3); assertFalse(guard.applicable("sample-urgent:8","3"));
        row.setStatus(4); assertFalse(guard.applicable("sample-urgent:8","3"));
        row.setStatus(0); row.setDeadline(LocalDateTime.now().minusMinutes(1));
        assertFalse(guard.applicable("sample-urgent:8","3"));
    }
    @Test void masterSwitchPreventsSending() {
        when(config.getConfigValueByKey(anyString())).thenReturn("false");
        assertFalse(guard.applicable("sample-urgent:8","3"));
        when(config.getConfigValueByKey(anyString())).thenReturn(null);
        assertTrue(guard.applicable("sample-urgent:8","3"));
    }
}
