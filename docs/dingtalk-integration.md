# 钉钉通用接入

目前提供应用 token 缓存、Stream 连接生命周期、事件和同步回调分发；消息模板管理和机器人主动发送参见 dingtalk-message-templates.md，尚未自动接入具体业务提醒。正式环境（prod）默认开启通用接入和 Stream，其他环境默认关闭。默认复用当前环境 dingtalk.oauth2 登录应用凭证，支持环境变量覆盖。

在当前运行环境配置（密钥使用环境变量）：

```yaml
dingtalk:
  integration:
    enabled: ${DINGTALK_INTEGRATION_ENABLED:false}
    client-id: ${DINGTALK_INTEGRATION_CLIENT_ID:${dingtalk.oauth2.client-id:}}
    client-secret: ${DINGTALK_INTEGRATION_CLIENT_SECRET:${dingtalk.oauth2.client-secret:}}
    stream-enabled: ${DINGTALK_STREAM_ENABLED:false}
    consume-threads: 4
```

## 钉钉后台

选择同一企业的内部应用，记录 Client ID 和 Client Secret；按需要申请接口权限、订阅事件并选择 Stream 推送模式，按后台要求发布。服务器需要出站 HTTPS/WebSocket，无需提供公网回调地址。机器人回调还需要在后台启用机器人并配置 Stream 模式。

只在指定环境启用 Stream，避免测试环境消费正式事件。SDK 负责连接维护和重连；启动日志仅表示 SDK 已启动，不证明后台订阅或实际收件成功。

## 业务接入

业务模块实现 `DingTalkEventHandler` 并注册为 Spring Bean，通过 supports 选择事件类型，handle 同步处理。DTO 保留企业、应用、事件 ID 和原始 JSON，不依赖 SDK。所有匹配处理器成功才确认，异常返回 LATER 请求重投；未注册处理器的事件直接确认忽略。处理器不得把失败转为正常返回或仅提交异步任务后返回。

同一事件可能重复投递，业务处理器必须按企业和事件 ID 幂等处理，多个处理器部分成功后重投也会再次执行。Stream 线程没有 HTTP 登录或租户上下文，处理器须按 corpId 显式校验企业并建立租户上下文，禁止信任默认租户。

同步回调实现 `DingTalkCallbackHandler`，topic 必须唯一，接收 JSON 并返回符合该 topic 协议的 JSON。回调异常交 SDK 处理，处理器同样需要幂等；当前不提供机器人或卡片业务默认响应。

`DingTalkAccessTokenApi` 获取当前配置应用的服务端 token，进程内缓存并提前过期，不得暴露给前端。此阶段按部署配置接入单个企业应用，不支持租户动态凭证。

## 后续拆分

公共 API 和 DTO 位于 api.dingtalk，SDK 仅存在于内部实现。拆分后将本地 API 替换为受保护的 RPC，将事件处理适配为可靠消息投递；只有持久化成功才能 ACK。多实例、幂等存储和消息队列需按最终部署设计，本阶段不宣称提供跨实例恰好一次消费。

## 验收

默认关闭可启动；启用但缺凭证时应启动失败。配置真实凭证后，在钉钉后台订阅测试事件，用处理器确认收件；制造一次业务失败后验证重投及幂等；停止应用确认连接释放。真实连接需在目标环境单独验证。

官方 SDK：https://github.com/open-dingtalk/dingtalk-stream-sdk-java （固定使用稳定版本 1.3.12）

## 正式环境验证通道

使用 prod 配置发布并重启后端，即自动复用正式登录应用连接 Stream；无需 SQL、无需重复填写密钥。若现有部署设置了 DINGTALK_INTEGRATION_ENABLED 或 DINGTALK_STREAM_ENABLED=false，需移除覆盖或改为 true。临时关闭通道可设置 DINGTALK_STREAM_ENABLED=false。

在钉钉开发者后台打开与正式登录配置对应的同一个应用，进入事件与回调，选择 Stream 模式并验证通道联通性，按后台要求保存、发布。请勿选成另一个登录应用或机器人应用。后台验证成功才表示端到端通道联通；服务端“客户端已启动”只表示启动了 SDK。

SDK 负责系统层的通道检测应答，无需额外业务处理器。触发已订阅事件后，后端应出现“钉钉 Stream 收到事件”，包含事件 ID、类型及接收时间，不记录业务内容或密钥。未注册业务处理器的事件确认后忽略。

若后台提示未连接，先确认后端使用 prod、开关未被环境变量覆盖、后台选择的是同一个应用，再检查钉钉 SDK 的网络连接日志及服务器出站 HTTPS/WebSocket；如需提供日志排查，去掉 token、ticket 和密钥。
