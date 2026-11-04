package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.dal.dataobject.social.*;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.*;
import cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkContactClient;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkUserIdentityServiceTest {
    private final SocialUserBindMapper bindings = mock(SocialUserBindMapper.class);
    private final SocialUserMapper socials = mock(SocialUserMapper.class);
    private final DingTalkContactClient contacts = mock(DingTalkContactClient.class);
    private final DingTalkUserIdentityService service = new DingTalkUserIdentityService(bindings, socials, contacts);
    private final AdminUserDO user = AdminUserDO.builder().id(1L).nickname("测试员工").status(0).build();
    private SocialUserDO social() { return SocialUserDO.builder().id(10L).type(20).openid("union-1").build(); }
    private SocialUserBindDO binding() { return SocialUserBindDO.builder().userId(1L).userType(2).socialType(20).socialUserId(10L).build(); }
    private void bind() {
        when(bindings.selectByUserIdAndUserTypeAndSocialType(1L, 2, 20)).thenReturn(binding());
        when(socials.selectById(10L)).thenReturn(social());
    }
    @Test void prefersSyncedStaffIdWithoutNetworkOrBindingLookup() {
        user.setDingTalkUserId("staff-1");
        assertEquals("staff-1",service.resolveUserId(user));
        assertTrue(service.isBound(user));
        verifyNoInteractions(bindings,socials,contacts);
    }
    @Test void loginBindingIsConvertedFromUnionIdInsteadOfUsedAsStaffId() {
        bind(); when(contacts.getUserIdByUnionId("union-1")).thenReturn("staff-from-api");
        assertEquals("staff-from-api",service.resolveUserId(user));
        assertNull(user.getDingTalkUserId());
        verify(contacts).getUserIdByUnionId("union-1");
    }
    @Test void missingBindingIsRejectedWithoutNetwork() {
        assertThrows(ServiceException.class,()->service.resolveUserId(user));
        verifyNoInteractions(contacts);
    }
    @Test void otherSocialPlatformOrDeletedBindingCannotBeUsed() {
        bind(); var data=social(); data.setType(30); when(socials.selectById(10L)).thenReturn(data);
        assertThrows(ServiceException.class,()->service.resolveUserId(user));
        data.setType(20); data.setDeleted(true);
        assertThrows(ServiceException.class,()->service.resolveUserId(user));
        verifyNoInteractions(contacts);
    }
    @Test void conversionFailureIsNotReplacedByUnionId() {
        bind(); when(contacts.getUserIdByUnionId("union-1")).thenThrow(new ServiceException(400,"权限不足"));
        assertThrows(ServiceException.class,()->service.resolveUserId(user));
    }
    @Test void employeeOptionsBatchRecognizeBothSourcesWithoutRemoteCalls() {
        var synced=AdminUserDO.builder().id(2L).dingTalkUserId("staff-2").build();
        when(bindings.selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenReturn(List.of(binding()));
        when(socials.selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenReturn(List.of(social()));
        assertEquals(Set.of(1L,2L),service.boundUserIds(List.of(user,synced)));
        verify(bindings,times(1)).selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class));
        verifyNoInteractions(contacts);
    }
    @Test void staleSocialReferenceDoesNotShowAsBound() {
        when(bindings.selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenReturn(List.of(binding()));
        assertEquals(Set.of(),service.boundUserIds(List.of(user)));
        assertEquals(Set.of(),service.boundUserIds(List.of()));
        verifyNoInteractions(contacts);
    }
}
