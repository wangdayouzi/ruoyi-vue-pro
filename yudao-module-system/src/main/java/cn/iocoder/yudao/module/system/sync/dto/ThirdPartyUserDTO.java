package cn.iocoder.yudao.module.system.sync.dto;

import lombok.Data;

import java.util.List;

/**
 * 第三方平台用户统一 DTO
 *
 * @author yudao
 */
@Data
public class ThirdPartyUserDTO {

    /** 第三方平台用户ID（钉钉为 userId，与 openid 可能不同） */
    private String sourceUserId;

    /** 第三方平台 openid（钉钉扫码登录时使用） */
    private String openid;

    /** 用户昵称/姓名 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 所属部门ID列表（第三方平台部门ID） */
    private List<String> deptIds;

}
