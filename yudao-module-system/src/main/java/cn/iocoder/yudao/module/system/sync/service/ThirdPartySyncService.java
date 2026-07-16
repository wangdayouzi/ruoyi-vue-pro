package cn.iocoder.yudao.module.system.sync.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.UserRoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialClientDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyDeptDTO;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyUserDTO;
import cn.iocoder.yudao.module.system.sync.strategy.ThirdPartySyncStrategy;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * 第三方平台同步服务
 *
 * 负责将第三方平台拉取到的部门/用户数据映射到本地表。
 * 策略：只做新增+更新，不做删除。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ThirdPartySyncService {

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private SocialUserMapper socialUserMapper;

    @Resource
    private SocialUserBindMapper socialUserBindMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired(required = false)
    private JustAuthProperties justAuthProperties; // 复用 justauth 配置（与 OAuth 登录共用）

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 同步部门挂载的本地父部门ID，不配置则默认挂到根部门(0)
     */
    @Value("${yudao.sync.parent-dept-id:0}")
    private Long syncParentDeptId;

    /**
     * 同步用户默认分配的角色ID，不配置则不分配角色
     */
    @Value("${yudao.sync.default-role-id:0}")
    private Long defaultRoleId;

    private static final String DEFAULT_PASSWORD = "$2a$04$y9izLO/uRwlP/1y.RsZiCumqMYlRsAtncVPWwRbbFhau5U1JVujO."; // 默认密码

    /**
     * 执行同步
     * 部门、用户各自独立事务，一方失败不影响另一方
     */
    public void sync(ThirdPartySyncStrategy strategy) {
        Integer socialType = strategy.getSocialType();
        SocialTypeEnum typeEnum = SocialTypeEnum.valueOfType(socialType);
        log.info("[sync][{}] 开始", typeEnum);

        SocialClientDO client = buildClientFromJustAuth(socialType);
        if (client == null) {
            log.warn("[sync][{}] justauth.type 未配置，跳过", typeEnum);
            return;
        }
        if (!strategy.validate(client)) {
            log.warn("[sync][{}] 验证失败，跳过", typeEnum);
            return;
        }

        // 先拉取部门（只调一次 API），再各自独立事务同步
        List<ThirdPartyDeptDTO> deptList;
        try {
            deptList = transactionTemplate.execute(status -> syncDepts(strategy, client, socialType));
        } catch (Exception e) {
            log.error("[sync][{}] 部门同步失败", typeEnum, e);
            return;
        }
        log.info("[sync][{}] 部门同步完成，获得 {} 个部门，开始用户同步", typeEnum,
                deptList != null ? deptList.size() : 0);

        try {
            transactionTemplate.execute(status -> {
                syncUsers(strategy, client, socialType, deptList);
                return null;
            });
        } catch (Exception e) {
            log.error("[sync][{}] 用户同步失败", typeEnum, e);
        }

        log.info("[sync][{}] 完成", typeEnum);
    }

    /**
     * 从 justauth 配置构建 SocialClientDO（复用 application-{profile}.yaml 中的 justauth.type.XXX 配置）
     */
    private SocialClientDO buildClientFromJustAuth(Integer socialType) {
        if (justAuthProperties == null) return null;
        SocialTypeEnum typeEnum = SocialTypeEnum.valueOfType(socialType);
        if (typeEnum == null) return null;

        AuthConfig authConfig = justAuthProperties.getType().get(typeEnum.name());
        if (authConfig == null || authConfig.getClientId() == null || authConfig.getClientSecret() == null) {
            return null;
        }

        SocialClientDO client = new SocialClientDO();
        client.setName(typeEnum.name() + "同步(justauth配置)");
        client.setSocialType(socialType);
        client.setClientId(authConfig.getClientId());
        client.setClientSecret(authConfig.getClientSecret());
        return client;
    }

    /**
     * 同步部门
     */
    private List<ThirdPartyDeptDTO> syncDepts(ThirdPartySyncStrategy strategy, SocialClientDO client, Integer socialType) {
        List<ThirdPartyDeptDTO> deptList = strategy.fetchDepartments(client);
        if (CollUtil.isEmpty(deptList)) {
            log.info("[sync][{}] 无部门数据", SocialTypeEnum.valueOfType(socialType));
            return deptList;
        }

        // sourceDeptId → DeptDO 映射（用于后续设置 parentId）
        Map<String, DeptDO> sourceDeptMap = new LinkedHashMap<>();

        // 预先收集已有的映射
        List<DeptDO> existingDepts = deptMapper.selectList();
        for (DeptDO d : existingDepts) {
            if (StrUtil.isNotBlank(d.getSourceDeptId())) {
                sourceDeptMap.put(d.getSourceDeptId(), d);
            }
        }

        for (ThirdPartyDeptDTO dto : deptList) {
            DeptDO existing = deptMapper.selectBySourceTypeAndSourceDeptId(socialType, dto.getSourceDeptId());
            if (existing != null) {
                // 更新
                existing.setName(dto.getName());
                existing.setSort(dto.getSort());
                // 父部门：查找第三方父部门映射到的本地部门ID
                resolveParentId(existing, dto.getSourceParentId(), sourceDeptMap);
                deptMapper.updateById(existing);
                sourceDeptMap.put(dto.getSourceDeptId(), existing);
            } else {
                // 新增
                DeptDO newDept = new DeptDO();
                newDept.setName(dto.getName());
                newDept.setSort(dto.getSort() != null ? dto.getSort() : 0);
                newDept.setSourceType(socialType);
                newDept.setSourceDeptId(dto.getSourceDeptId());
                newDept.setStatus(0); // 正常
                resolveParentId(newDept, dto.getSourceParentId(), sourceDeptMap);
                deptMapper.insert(newDept);
                sourceDeptMap.put(dto.getSourceDeptId(), newDept);
                log.info("[sync][{}] 新增部门: name={}, sourceDeptId={}, localId={}, createTime={}",
                        SocialTypeEnum.valueOfType(socialType), dto.getName(), dto.getSourceDeptId(),
                        newDept.getId(), newDept.getCreateTime());
            }
        }

        log.info("[sync][{}] 部门同步完成，共 {} 条", SocialTypeEnum.valueOfType(socialType), deptList.size());
        return deptList;
    }

    /**
     * 解析父部门：将第三方 parentId 映射为本地 deptId
     */
    private void resolveParentId(DeptDO dept, String sourceParentId, Map<String, DeptDO> sourceDeptMap) {
        // 钉钉根部门(0/1) → 映射到配置的本地父部门
        if (StrUtil.isBlank(sourceParentId) || "0".equals(sourceParentId) || "1".equals(sourceParentId)) {
            dept.setParentId(syncParentDeptId);
            return;
        }
        DeptDO parent = sourceDeptMap.get(sourceParentId);
        if (parent != null) {
            dept.setParentId(parent.getId());
        } else {
            dept.setParentId(syncParentDeptId); // 兜底挂到配置的父部门
        }
    }

    /**
     * 同步用户
     */
    private void syncUsers(ThirdPartySyncStrategy strategy, SocialClientDO client,
                            Integer socialType, List<ThirdPartyDeptDTO> deptList) {
        // 优先使用带 deptList 的重载（避免重复拉取部门）
        List<ThirdPartyUserDTO> userList;
        if (strategy instanceof cn.iocoder.yudao.module.system.sync.strategy.DingTalkSyncStrategy dts) {
            userList = dts.fetchUsers(client, deptList);
        } else {
            userList = strategy.fetchUsers(client);
        }

        if (CollUtil.isEmpty(userList)) {
            log.info("[sync][{}] 无用户数据", SocialTypeEnum.valueOfType(socialType));
            return;
        }
        log.info("[sync][{}] 拉取到 {} 个用户, 开始写入", SocialTypeEnum.valueOfType(socialType), userList.size());

        // 预加载现有 sourceDeptId → DeptDO 映射
        Map<String, DeptDO> sourceDeptMap = new HashMap<>();
        for (DeptDO d : deptMapper.selectList()) {
            if (StrUtil.isNotBlank(d.getSourceDeptId())) {
                sourceDeptMap.put(d.getSourceDeptId(), d);
            }
        }

        for (ThirdPartyUserDTO dto : userList) {
            // 1. 查 social_user 是否已有该钉钉用户
            SocialUserDO socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, dto.getSourceUserId());
            // openid 为空时用 sourceUserId
            String openid = StrUtil.isNotBlank(dto.getOpenid()) ? dto.getOpenid() : dto.getSourceUserId();
            if (socialUser == null) {
                socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, openid);
            }

            if (socialUser != null) {
                // 2. 已有 social_user → 查绑定
                SocialUserBindDO bind = socialUserBindMapper.selectByUserTypeAndSocialUserId(2, socialUser.getId());
                boolean bindValid = bind != null && adminUserMapper.selectById(bind.getUserId()) != null;
                if (bindValid) {
                    // 绑定有效 → 更新用户信息
                    updateSystemUser(bind.getUserId(), dto, sourceDeptMap);
                } else {
                    // 绑定失效（用户被删了） → 清理旧绑定，走新建流程
                    if (bind != null) {
                        socialUserBindMapper.deleteById(bind.getId());
                        log.info("[sync][{}] 检测到孤立绑定已清理: socialUserId={}, oldUserId={}",
                                SocialTypeEnum.valueOfType(socialType), socialUser.getId(), bind.getUserId());
                    }
                    // 重新创建用户 + 绑定
                    Long userId = createSystemUser(dto, sourceDeptMap);
                    createBind(userId, socialUser.getId(), socialType);
                    log.info("[sync][{}] 重新创建用户: nickname={}, userId={}",
                            SocialTypeEnum.valueOfType(socialType), dto.getNickname(), userId);
                }
                // 更新 social_user 信息
                socialUser.setNickname(dto.getNickname());
                socialUser.setAvatar(dto.getAvatar());
                socialUser.setOpenid(openid);
                socialUserMapper.updateById(socialUser);
            } else {
                // 3. 不存在 → 三连创建
                log.info("[sync][{}] 新增用户: nickname={}, sourceUserId={}, openid={}",
                        SocialTypeEnum.valueOfType(socialType), dto.getNickname(), dto.getSourceUserId(), openid);
                Long userId = createSystemUser(dto, sourceDeptMap);
                Long socialUserId = createSocialUser(socialType, openid, dto);
                createBind(userId, socialUserId, socialType);
                log.info("[sync][{}] 用户创建完成: nickname={}, userId={}, socialUserId={}",
                        SocialTypeEnum.valueOfType(socialType), dto.getNickname(), userId, socialUserId);
            }
        }

        log.info("[sync][{}] 用户同步完成，共 {} 条", SocialTypeEnum.valueOfType(socialType), userList.size());
    }

    /**
     * 创建系统用户
     */
    private Long createSystemUser(ThirdPartyUserDTO dto, Map<String, DeptDO> sourceDeptMap) {
        AdminUserDO user = new AdminUserDO();
        // 用户名优先用手机号，没有则用 sourceUserId
        String username = StrUtil.isNotBlank(dto.getMobile()) ? dto.getMobile() : dto.getSourceUserId();
        user.setUsername(username);
        user.setNickname(dto.getNickname());
        user.setMobile(dto.getMobile());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setPassword(DEFAULT_PASSWORD);
        user.setStatus(0); // 正常

        // 设置部门：取第一个匹配的部门
        if (CollUtil.isNotEmpty(dto.getDeptIds())) {
            for (String sourceDeptId : dto.getDeptIds()) {
                DeptDO dept = sourceDeptMap.get(sourceDeptId);
                if (dept != null) {
                    user.setDeptId(dept.getId());
                    break;
                }
            }
        }

        adminUserMapper.insert(user);
        // 分配默认角色
        if (defaultRoleId != null && defaultRoleId > 0) {
            UserRoleDO userRole = new UserRoleDO();
            userRole.setUserId(user.getId());
            userRole.setRoleId(defaultRoleId);
            userRoleMapper.insert(userRole);
        }
        log.info("[sync] 创建系统用户: username={}, id={}, roleId={}", username, user.getId(), defaultRoleId);
        return user.getId();
    }

    /**
     * 更新系统用户
     */
    private void updateSystemUser(Long userId, ThirdPartyUserDTO dto, Map<String, DeptDO> sourceDeptMap) {
        AdminUserDO user = adminUserMapper.selectById(userId);
        if (user == null) return;
        user.setNickname(dto.getNickname());
        if (StrUtil.isNotBlank(dto.getMobile())) user.setMobile(dto.getMobile());
        if (StrUtil.isNotBlank(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StrUtil.isNotBlank(dto.getAvatar())) user.setAvatar(dto.getAvatar());
        // 更新部门
        if (CollUtil.isNotEmpty(dto.getDeptIds())) {
            for (String sourceDeptId : dto.getDeptIds()) {
                DeptDO dept = sourceDeptMap.get(sourceDeptId);
                if (dept != null) {
                    user.setDeptId(dept.getId());
                    break;
                }
            }
        }
        adminUserMapper.updateById(user);
    }

    /**
     * 创建社交用户记录
     */
    private Long createSocialUser(Integer socialType, String openid, ThirdPartyUserDTO dto) {
        SocialUserDO socialUser = new SocialUserDO();
        socialUser.setType(socialType);
        socialUser.setOpenid(openid);
        socialUser.setNickname(dto.getNickname());
        socialUser.setAvatar(dto.getAvatar());
        socialUser.setToken("");          // NOT NULL
        socialUser.setRawTokenInfo("{}"); // NOT NULL
        socialUser.setRawUserInfo("{}");  // NOT NULL
        socialUser.setCode("");           // NOT NULL
        socialUser.setState("");          // NOT NULL
        socialUserMapper.insert(socialUser);
        return socialUser.getId();
    }

    /**
     * 创建绑定记录
     */
    private void createBind(Long userId, Long socialUserId, Integer socialType) {
        SocialUserBindDO bind = SocialUserBindDO.builder()
                .userId(userId)
                .userType(2) // 管理后台用户
                .socialUserId(socialUserId)
                .socialType(socialType)
                .build();
        socialUserBindMapper.insert(bind);
    }

}
