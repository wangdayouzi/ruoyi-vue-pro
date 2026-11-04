package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.social.*;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.*;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.framework.dingtalk.DingTalkContactClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

/** 登录绑定与通讯录同步是两种身份来源，UnionID 必须转换后才能作为收件人。 */
@Service
@RequiredArgsConstructor
public class DingTalkUserIdentityService {
    private final SocialUserBindMapper bindings;
    private final SocialUserMapper socials;
    private final DingTalkContactClient contacts;

    public String resolveUserId(AdminUserDO user) {
        if (hasStaffId(user)) return user.getDingTalkUserId();
        var binding = bindings.selectByUserIdAndUserTypeAndSocialType(user.getId(), UserTypeEnum.ADMIN.getValue(), SocialTypeEnum.DINGTALK.getType());
        var social = binding == null ? null : socials.selectById(binding.getSocialUserId());
        if (!validSocial(social)) throw invalidParamException("{} 未关联钉钉用户，请先绑定钉钉账号或同步通讯录", user.getNickname());
        // 不回写用户资料，不把 UnionID/OpenID 猜测为企业 userId。
        return contacts.getUserIdByUnionId(social.getOpenid());
    }

    public boolean isBound(AdminUserDO user) { return boundUserIds(List.of(user)).contains(user.getId()); }

    /** 列表和部门展开只批量查询本地绑定，不发起网络请求。 */
    public Set<Long> boundUserIds(Collection<AdminUserDO> users) {
        Set<Long> bound = new HashSet<>();
        List<Long> missing = new ArrayList<>();
        for (var user : users) {
            if (hasStaffId(user)) bound.add(user.getId());
            else missing.add(user.getId());
        }
        if (missing.isEmpty()) return bound;
        var rows = bindings.selectList(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserType, UserTypeEnum.ADMIN.getValue())
                .eq(SocialUserBindDO::getSocialType, SocialTypeEnum.DINGTALK.getType())
                .in(SocialUserBindDO::getUserId, missing));
        if (rows.isEmpty()) return bound;
        var socialIds = rows.stream().map(SocialUserBindDO::getSocialUserId).filter(Objects::nonNull).distinct().toList();
        if (socialIds.isEmpty()) return bound;
        Set<Long> validIds = new HashSet<>();
        socials.selectList(new LambdaQueryWrapperX<SocialUserDO>().in(SocialUserDO::getId, socialIds)
                .eq(SocialUserDO::getType, SocialTypeEnum.DINGTALK.getType()))
                .stream().filter(this::validSocial).forEach(s -> validIds.add(s.getId()));
        rows.stream().filter(row -> validIds.contains(row.getSocialUserId())).forEach(row -> bound.add(row.getUserId()));
        return bound;
    }

    private boolean hasStaffId(AdminUserDO user) { return user.getDingTalkUserId() != null && !user.getDingTalkUserId().isBlank(); }
    private boolean validSocial(SocialUserDO social) {
        return social != null && !Boolean.TRUE.equals(social.getDeleted())
                && SocialTypeEnum.DINGTALK.getType().equals(social.getType())
                && social.getOpenid() != null && !social.getOpenid().isBlank();
    }
}
