package cn.iocoder.yudao.module.system.api.dingtalk;

import java.util.List;

/** 业务声明模板字段，通用编辑器只展示字段信息，不依赖具体业务模块。 */
public interface DingTalkTemplateParameterProvider {
    record Parameter(String key,String label,String example) {}
    String templateCode();
    List<Parameter> parameters();
}
