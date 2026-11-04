package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.system.enums.dingtalk.DingTalkMessageType;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Component
public class DingTalkTemplateRenderer {
    private static final Pattern VARIABLE = Pattern.compile("\\{([a-zA-Z][a-zA-Z0-9_]*)}");

    public List<String> validate(String typeName, Map<String, String> content) {
        var type = DingTalkMessageType.of(typeName);
        if (content == null) throw invalidParamException("消息内容不能为空");
        Set<String> allowed = new HashSet<>();
        LinkedHashSet<String> variables = new LinkedHashSet<>();
        for (var field : type.getFields()) {
            allowed.add(field.key());
            String value = content.get(field.key());
            if (value == null || value.isBlank()) throw invalidParamException("{}不能为空", field.label());
            if (value.length() > field.maxLength()) throw invalidParamException("{}最多 {} 个字符", field.label(), field.maxLength());
            var matcher = VARIABLE.matcher(value);
            while (matcher.find()) variables.add(matcher.group(1));
            // URL 模板必须已有安全协议；不允许由参数决定协议。
            if (field.editor().equals("url")) validateUrl(matcher.reset().replaceAll("placeholder"), field.label());
        }
        if (!allowed.containsAll(content.keySet())) throw invalidParamException("消息内容包含当前类型不支持的字段");
        return List.copyOf(variables);
    }

    public Map<String, String> render(String type, Map<String, String> content, Map<String, String> params) {
        List<String> required = validate(type, content);
        Map<String, String> values = params == null ? Map.of() : params;
        for (String key : required) {
            if (values.get(key) == null || values.get(key).isBlank()) throw invalidParamException("请填写模板参数：{}", key);
        }
        Map<String, String> rendered = new LinkedHashMap<>();
        for (var field : DingTalkMessageType.of(type).getFields()) {
            var matcher = VARIABLE.matcher(content.get(field.key()));
            StringBuilder result = new StringBuilder();
            while (matcher.find()) matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(values.get(matcher.group(1))));
            matcher.appendTail(result);
            String value = result.toString();
            if (value.length() > field.maxLength()) throw invalidParamException("渲染后的{}最多 {} 个字符", field.label(), field.maxLength());
            if (field.editor().equals("url")) validateUrl(value, field.label());
            rendered.put(field.key(), value);
        }
        if (JSON.toJSONString(rendered).getBytes(StandardCharsets.UTF_8).length > 20000) throw invalidParamException("消息内容超过 20000 字节，请缩短内容");
        return rendered;
    }

    private void validateUrl(String value, String label) {
        try {
            URI uri = URI.create(value);
            if (!Set.of("https", "http", "dingtalk").contains(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null) throw new IllegalArgumentException();
        } catch (Exception ex) { throw invalidParamException("{}必须是有效的 HTTP、HTTPS 或钉钉链接", label); }
    }
}
