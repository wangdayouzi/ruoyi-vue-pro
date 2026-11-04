package cn.iocoder.yudao.module.system.api.dingtalk;

/** 按 topic 注册的同步回调。请求和响应均为 JSON，避免业务模块依赖钉钉 SDK。 */
public interface DingTalkCallbackHandler {
    String topic();
    String handle(String requestJson) throws Exception;
}
