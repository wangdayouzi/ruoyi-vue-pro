package cn.iocoder.yudao.module.system.enums.dingtalk;

import java.util.List;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/** 平台支持的机器人消息类型；字段定义同时提供给管理界面。 */
public enum DingTalkMessageType {
    TEXT("文本", "sampleText", List.of(field("content", "消息内容", "textarea", 4000))),
    MARKDOWN("Markdown", "sampleMarkdown", List.of(title(), text())),
    LINK("链接", "sampleLink", List.of(title(), text(), url("messageUrl", "跳转地址"), url("picUrl", "封面图片地址"))),
    IMAGE("图片", "sampleImageMsg", List.of(url("photoURL", "图片地址"))),
    ACTION_CARD("单按钮卡片", "sampleActionCard", List.of(title(), text(), field("singleTitle", "按钮文字", "input", 20), url("singleURL", "按钮地址"))),
    ACTION_CARD2("双按钮卡片", "sampleActionCard2", List.of(title(), text(), button(1), action(1), button(2), action(2))),
    ACTION_CARD3("三按钮卡片", "sampleActionCard3", List.of(title(), text(), button(1), action(1), button(2), action(2), button(3), action(3)));

    private final String label;
    private final String msgKey;
    private final List<Field> fields;
    DingTalkMessageType(String label, String msgKey, List<Field> fields) {
        this.label = label; this.msgKey = msgKey; this.fields = fields;
    }
    public String getLabel() { return label; }
    public String getMsgKey() { return msgKey; }
    public List<Field> getFields() { return fields; }
    public record Field(String key, String label, String editor, int maxLength) { }
    public record Definition(String value, String label, String msgKey, List<Field> fields) { }
    public Definition definition() { return new Definition(name(), label, msgKey, fields); }
    private static Field field(String key, String label, String editor, int max) { return new Field(key, label, editor, max); }
    private static Field title() { return field("title", "标题", "input", 100); }
    private static Field text() { return field("text", "消息正文", "textarea", 4000); }
    private static Field url(String key, String label) { return field(key, label, "url", 500); }
    private static Field button(int n) { return field("actionTitle" + n, "按钮 " + n + " 文字", "input", 20); }
    private static Field action(int n) { return url("actionURL" + n, "按钮 " + n + " 地址"); }
    public static DingTalkMessageType of(String value) {
        try { return valueOf(value); }
        catch (Exception ex) { throw invalidParamException("不支持的钉钉消息类型"); }
    }
}
