package cn.iocoder.yudao.module.system.framework.dingtalk;

import cn.hutool.http.HttpRequest;
import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkAccessTokenApi;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/** 将登录绑定的 UnionID 转为当前企业应用可发送的员工 userId。 */
@Component
@RequiredArgsConstructor
public class DingTalkContactClient {
    private final DingTalkAccessTokenApi tokens;

    public String getUserIdByUnionId(String unionId) {
        try (var response = HttpRequest.post("https://oapi.dingtalk.com/topapi/user/getbyunionid?access_token=" + tokens.getAccessToken())
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(Map.of("unionid", unionId))).timeout(10000).execute()) {
            return decode(response.getStatus(), response.body());
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw invalidParamException("钉钉账号已绑定，但获取企业员工身份失败，请检查应用凭证、通讯录权限和网络");
        }
    }

    String decode(int status, String responseBody) {
        JSONObject body;
        try { body = JSON.parseObject(responseBody); }
        catch (Exception ex) { throw invalidParamException("钉钉员工身份查询返回异常，请稍后重试"); }
        if (status < 200 || status >= 300 || body == null || !Integer.valueOf(0).equals(body.getInteger("errcode"))) {
            throw invalidParamException("钉钉账号已绑定，但无法取得当前企业的员工身份；请检查通讯录查询权限及账号所属企业，或同步通讯录");
        }
        var result = body.getJSONObject("result");
        String id = result == null ? null : result.getString("userid");
        if (id == null || id.isBlank()) throw invalidParamException("钉钉账号已绑定，但当前应用未查到员工 userId，请检查企业及通讯录可见范围");
        return id;
    }
}
