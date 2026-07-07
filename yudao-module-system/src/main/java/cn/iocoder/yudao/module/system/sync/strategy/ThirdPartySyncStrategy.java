package cn.iocoder.yudao.module.system.sync.strategy;

import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialClientDO;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyDeptDTO;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyUserDTO;

import java.util.List;

/**
 * 第三方平台同步策略接口
 *
 * 每个第三方平台（钉钉、企业微信、飞书等）实现此接口，
 * Spring 容器自动注入所有实现类，定时任务遍历执行。
 *
 * @author yudao
 */
public interface ThirdPartySyncStrategy {

    /**
     * 返回社交平台类型
     *
     * @return {@link cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum} 的 type 值
     */
    Integer getSocialType();

    /**
     * 拉取全量部门列表（含树形结构信息）
     *
     * @param client 社交客户端配置（含 AppKey/AppSecret）
     * @return 部门列表
     */
    List<ThirdPartyDeptDTO> fetchDepartments(SocialClientDO client);

    /**
     * 拉取全量用户列表
     *
     * @param client 社交客户端配置
     * @return 用户列表
     */
    List<ThirdPartyUserDTO> fetchUsers(SocialClientDO client);

    /**
     * 验证客户端配置是否有效（access_token 能否获取）
     *
     * @param client 社交客户端配置
     * @return true=有效
     */
    boolean validate(SocialClientDO client);

}
