package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.hutool.http.HttpRequest;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkAccessTokenApi;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

/** HTTP 边界：不把 token、请求正文或原始异常写入日志。 */
@Component
@RequiredArgsConstructor
public class DingTalkRobotClient {
    private final DingTalkAccessTokenApi tokenApi;
    public record Outcome(int status, String processQueryKey, String errorCode, String errorMessage) { }

    public Map<String, Outcome> send(String robotCode, String msgKey, Map<String, String> content, List<String> users) {
        String token;
        try { token = tokenApi.getAccessToken(); }
        catch (Exception ex) { return all(users, new Outcome(2, null, "TOKEN_UNAVAILABLE", "无法取得应用凭证，请检查应用配置与网络")); }
        try (var response = HttpRequest.post("https://api.dingtalk.com/v1.0/robot/oToMessages/batchSend")
                .header("x-acs-dingtalk-access-token", token).header("Content-Type", "application/json")
                .body(JSON.toJSONString(Map.of("robotCode", robotCode, "msgKey", msgKey,
                        "msgParam", JSON.toJSONString(content), "userIds", users)))
                .timeout(10000).execute()) {
            return decode(response.getStatus(), response.body(), users);
        } catch (Exception ex) {
            return all(users, new Outcome(3, null, "RESULT_UNKNOWN", "请求超时或连接中断，可能已发送；请核实收件情况后再决定是否重新发送"));
        }
    }

    Map<String, Outcome> decode(int httpStatus, String responseBody, List<String> users) {
        if (httpStatus >= 500) return all(users, new Outcome(3, null, "REMOTE_UNAVAILABLE", "钉钉服务异常，发送结果未知，请核实后再发送"));
        JSONObject body;
        try { body = JSON.parseObject(responseBody); }
        catch (Exception ex) { return all(users, new Outcome(3, null, "INVALID_RESPONSE", "钉钉返回无法识别，发送结果未知")); }
        if (body == null) return all(users, new Outcome(3, null, "EMPTY_RESPONSE", "钉钉返回为空，发送结果未知"));
        if (httpStatus < 200 || httpStatus >= 300 || body.containsKey("code") || body.containsKey("errcode")) {
            String code = body.getString("code");
            if (code == null) code = body.getString("errcode");
            if (code == null || !code.matches("[a-zA-Z0-9_.-]{1,150}")) code = "HTTP_" + httpStatus;
            if (code.toLowerCase(Locale.ROOT).contains("token")) tokenApi.invalidateAccessToken();
            String message = code.toLowerCase(Locale.ROOT).contains("permission") || httpStatus == 403
                    ? "权限不足，请检查企业内机器人发送消息权限及应用发布状态"
                    : "钉钉拒绝发送，请根据错误码检查应用、机器人可见范围与接收人";
            return all(users, new Outcome(2, null, code, message));
        }
        String processKey = body.getString("processQueryKey");
        List<String> invalid = body.getList("invalidStaffIdList", String.class);
        List<String> controlled = body.getList("flowControlledStaffIdList", String.class);
        Map<String, Outcome> result = new LinkedHashMap<>();
        for (String user : users) {
            if (invalid != null && invalid.contains(user)) result.put(user, new Outcome(2, processKey, "INVALID_USER", "钉钉用户无效或不在机器人可见范围内"));
            else if (controlled != null && controlled.contains(user)) result.put(user, new Outcome(2, processKey, "FLOW_CONTROLLED", "发送被限流，请稍后手动重试"));
            else if (processKey == null || processKey.isBlank()) result.put(user, new Outcome(3, null, "MISSING_PROCESS_KEY", "未取得钉钉受理编号，发送结果未知"));
            else result.put(user, new Outcome(1, processKey, null, null));
        }
        return result;
    }
    private Map<String, Outcome> all(List<String> users, Outcome outcome) {
        Map<String, Outcome> result = new LinkedHashMap<>();
        users.forEach(user -> result.put(user, outcome));
        return result;
    }
}
