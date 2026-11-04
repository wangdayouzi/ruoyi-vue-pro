package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service @RequiredArgsConstructor
public class DingTalkTemplateParameterService {
    private final ObjectProvider<DingTalkTemplateParameterProvider> providers;
    public List<DingTalkTemplateParameterProvider.Parameter> parameters(String code) {
        return providers.orderedStream().filter(p->p.templateCode().equals(code)).findFirst().map(p->p.parameters()).orElse(List.of());
    }
    public void validate(String code,Collection<String> used) {
        var provider=providers.orderedStream().filter(p->p.templateCode().equals(code)).findFirst();
        if(provider.isEmpty()) return; // 未登记字段声明的手动模板，不冒充接口声明。
        var allowed=provider.get().parameters().stream().map(p->p.key()).collect(Collectors.toSet());
        var unknown=used.stream().filter(key->!allowed.contains(key)).toList();
        if(!unknown.isEmpty()) throw invalidParamException("对接接口未提供模板变量：{}，请移除或先完善对应配置",String.join("、",unknown));
    }
}
