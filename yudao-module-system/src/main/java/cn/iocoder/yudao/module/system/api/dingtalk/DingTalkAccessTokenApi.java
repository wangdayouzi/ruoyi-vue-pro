package cn.iocoder.yudao.module.system.api.dingtalk;

/** 服务端集成使用的应用凭证接口，不得作为前端接口暴露或记录 token。 */
public interface DingTalkAccessTokenApi {
    String getAccessToken();
    void invalidateAccessToken();
}
