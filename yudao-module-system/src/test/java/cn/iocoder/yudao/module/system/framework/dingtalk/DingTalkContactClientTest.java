package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkAccessTokenApi;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkContactClientTest {
    private final DingTalkContactClient client=new DingTalkContactClient(mock(DingTalkAccessTokenApi.class));
    @Test void acceptsOnlyVerifiedCorporateUserId() {
        assertEquals("staff-1",client.decode(200,"{\"errcode\":0,\"result\":{\"userid\":\"staff-1\"}}"));
    }
    @Test void doesNotTreatMissingUserOrPermissionFailureAsUnboundAccount() {
        var error=assertThrows(ServiceException.class,()->client.decode(200,"{\"errcode\":400,\"errmsg\":\"sensitive-data\"}"));
        assertTrue(error.getMessage().contains("已绑定"));
        assertFalse(error.getMessage().contains("sensitive-data"));
        assertThrows(ServiceException.class,()->client.decode(200,"{\"errcode\":0,\"result\":{}}"));
        assertThrows(ServiceException.class,()->client.decode(503,"not json"));
    }
}
