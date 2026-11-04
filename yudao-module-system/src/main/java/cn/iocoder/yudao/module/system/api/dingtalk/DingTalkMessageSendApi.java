package cn.iocoder.yudao.module.system.api.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendReqDTO;
import cn.iocoder.yudao.module.system.api.dingtalk.dto.DingTalkMessageSendResultDTO;
import jakarta.validation.Valid;
import java.util.List;

/** 调用方必须建立租户上下文，并在业务事务提交成功后调用。重复请求复用 requestId。 */
public interface DingTalkMessageSendApi {
    /** 读取当前租户模板的人员与部门成员，动态过滤并去重，不影响显式指定的业务接收人。 */
    List<Long> getDefaultUserIds(String templateCode);
    List<DingTalkMessageSendResultDTO> send(@Valid DingTalkMessageSendReqDTO request);
}
