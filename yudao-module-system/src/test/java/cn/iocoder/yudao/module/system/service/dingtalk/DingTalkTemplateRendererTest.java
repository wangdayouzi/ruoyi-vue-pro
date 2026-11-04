package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.enums.dingtalk.DingTalkMessageType;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DingTalkTemplateRendererTest {
    private final DingTalkTemplateRenderer renderer = new DingTalkTemplateRenderer();
    @Test
    void extractsDistinctParametersAcrossTitleBodyAndButtonsInOrder() {
        assertEquals(List.of("task", "name", "id"), renderer.validate("ACTION_CARD", Map.of(
                "title", "{task}", "text", "{name}: {task}", "singleTitle", "查看{name}", "singleURL", "https://example.com/{id}")));
    }
    @Test
    void replacementsAreLiteralAndDoNotRecursivelyInterpretParameters() {
        var result = renderer.render("TEXT", Map.of("content", "{name} / {name}"), Map.of("name", "$1\\test{other}"));
        assertEquals("$1\\test{other} / $1\\test{other}", result.get("content"));
    }
    @Test
    void rejectsMissingAndBlankValues() {
        assertThrows(ServiceException.class, () -> renderer.render("TEXT", Map.of("content", "{name}"), Map.of()));
        assertThrows(ServiceException.class, () -> renderer.render("TEXT", Map.of("content", "{name}"), Map.of("name", " ")));
    }
    @Test
    void rejectsFieldsThatDoNotBelongToTheMessageType() {
        assertThrows(ServiceException.class, () -> renderer.validate("TEXT", Map.of("content", "hi", "title", "bad")));
        assertThrows(ServiceException.class, () -> renderer.validate("LINK", Map.of("title", "hi")));
    }
    @Test
    void validatesEverySupportedTypeAndPreservesExpectedPlatformKeys() {
        for (var type : DingTalkMessageType.values()) {
            Map<String, String> content = new LinkedHashMap<>();
            type.getFields().forEach(field -> content.put(field.key(), field.editor().equals("url") ? "https://example.com/{id}" : "{name}"));
            var rendered = renderer.render(type.name(), content, Map.of("id", "1", "name", "测试"));
            assertEquals(content.keySet(), rendered.keySet());
        }
    }
    @Test
    void rejectsUnsafeUrlsIncludingParameterInjection() {
        assertThrows(ServiceException.class, () -> renderer.validate("IMAGE", Map.of("photoURL", "javascript:alert(1)")));
        assertThrows(ServiceException.class, () -> renderer.validate("IMAGE", Map.of("photoURL", "{scheme}://example.com/a")));
        assertThrows(ServiceException.class, () -> renderer.render("IMAGE", Map.of("photoURL", "https://{host}/a"), Map.of("host", "user:password@evil.com")));
    }
    @Test
    void enforcesRenderedFieldLength() {
        assertThrows(ServiceException.class, () -> renderer.render("MARKDOWN", Map.of("title", "{title}", "text", "正文"), Map.of("title", "a".repeat(101))));
    }
}
