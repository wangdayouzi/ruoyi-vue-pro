package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkTemplateSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.DingTalkMessageTemplateMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkTemplateRecipientsTest {
    DingTalkMessageTemplateMapper mapper;
    AdminUserMapper users;
    PermissionApi permissions;
    DingTalkMessageTemplateService service;
    DingTalkTemplateSaveReqVO req;
    @BeforeEach void setup() {
        mapper=mock(DingTalkMessageTemplateMapper.class); users=mock(AdminUserMapper.class); permissions=mock(PermissionApi.class);
        service=new DingTalkMessageTemplateService(mapper,new DingTalkTemplateRenderer(),users,new DingTalkUserIdentityService(mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper.class), mock(cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper.class), mock(cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkContactClient.class)),new DingTalkTemplateParameterService(new org.springframework.beans.factory.support.StaticListableBeanFactory().getBeanProvider(cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider.class)));
        req=new DingTalkTemplateSaveReqVO(); req.setCode("sample_urgent_notice"); req.setName("业务提醒");
        req.setMessageType("TEXT"); req.setContent(Map.of("content","{clinicalProtocolNo}")); req.setStatus(0);
        when(users.selectById(10L)).thenReturn(AdminUserDO.builder().id(10L).nickname("样品组甲").status(0).dingTalkUserId("ding10").build());
        when(permissions.hasAnyPermissions(eq(10L),anyString())).thenReturn(true);
    }
    @Test void defaultsAreDeduplicatedAndEmptySelectionCanBeSaved() {
        req.setDefaultUserIds(List.of(10L,10L)); service.save(req,false);
        var data=ArgumentCaptor.forClass(DingTalkMessageTemplateDO.class); verify(mapper).insert(data.capture());
        assertEquals(List.of(10L),data.getValue().getDefaultUserIds());
        reset(mapper); req.setDefaultUserIds(List.of()); service.save(req,false);
        verify(mapper).insert(data.capture()); assertEquals(List.of(),data.getValue().getDefaultUserIds());
    }
    @Test void unboundOrDisabledEmployeesCannotBeSavedAsDefaultReceivers() {
        req.setDefaultUserIds(List.of(10L));
        when(users.selectById(10L)).thenReturn(AdminUserDO.builder().id(10L).status(0).nickname("甲").build());
        assertThrows(ServiceException.class,()->service.save(req,false));
        when(users.selectById(10L)).thenReturn(AdminUserDO.builder().id(10L).status(1).nickname("甲").dingTalkUserId("ding10").build());
        assertThrows(ServiceException.class,()->service.save(req,false));
        verify(mapper,never()).insert(any(DingTalkMessageTemplateDO.class));
    }
    @Test void genericTemplateDoesNotEnforceBusinessSpecificPermissions() {
        req.setDefaultUserIds(List.of(10L));
        when(permissions.hasAnyPermissions(10L,"reagent:sample-urgent:handle")).thenReturn(false);
        assertDoesNotThrow(()->service.save(req,false));
        verifyNoInteractions(permissions);
    }
}
