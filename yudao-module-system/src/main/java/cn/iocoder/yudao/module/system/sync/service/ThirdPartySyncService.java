package cn.iocoder.yudao.module.system.sync.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialClientDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialClientMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyDeptDTO;
import cn.iocoder.yudao.module.system.sync.dto.ThirdPartyUserDTO;
import cn.iocoder.yudao.module.system.sync.strategy.ThirdPartySyncStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private SocialClientMapper socialClientMapper;

    private static final String DEFAULT_PASSWORD = "$2a$10$mRMFK4ZfjHMQq5kXXqhmXO52FJhrBSiCbGEIM4mGVPFFfRZ5HuQGW"; // 默认密码

    /**
     * 执行同步
     *
     * @param strategy 同步策略
     */
    @Transactional(rollbackFor = Exception.class)
    public void sync(ThirdPartySyncStrategy strategy) {
        Integer socialType = strategy.getSocialType();
        log.info("[sync][{}] 开始同步", SocialTypeEnum.valueOfType(socialType));

        // 1. 获取客户端配置
        List<SocialClientDO> clients = socialClientMapper.selectList(
                SocialClientDO::getSocialType, socialType);
        if (CollUtil.isEmpty(clients)) {
            log.info("[sync][{}] 未配置客户端，跳过", SocialTypeEnum.valueOfType(socialType));
            return;
        }

        for (SocialClientDO client : clients) {
            if (!strategy.validate(client)) {
                log.warn("[sync][{}] 客户端[{}]验证失败，跳过", SocialTypeEnum.valueOfType(socialType), client.getName());
                continue;
            }
            syncDepts(strategy, client, socialType);
            syncUsers(strategy, client, socialType);
        }

        log.info("[sync][{}] 同步完成", SocialTypeEnum.valueOfType(socialType));
    }

    /**
     * 同步部门
     */
    private void syncDepts(ThirdPartySyncStrategy strategy, SocialClientDO client, Integer socialType) {
        List<ThirdPartyDeptDTO> deptList = strategy.fetchDepartments(client);
        if (CollUtil.isEmpty(deptList)) {
            log.info("[sync][{}] 无部门数据", SocialTypeEnum.valueOfType(socialType));
            return;
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
    }

    /**
     * 解析父部门：将第三方 parentId 映射为本地 deptId
     */
    private void resolveParentId(DeptDO dept, String sourceParentId, Map<String, DeptDO> sourceDeptMap) {
        if (StrUtil.isBlank(sourceParentId) || "0".equals(sourceParentId) || "1".equals(sourceParentId)) {
            dept.setParentId(DeptDO.PARENT_ID_ROOT);
            return;
        }
        DeptDO parent = sourceDeptMap.get(sourceParentId);
        if (parent != null) {
            dept.setParentId(parent.getId());
        } else {
            dept.setParentId(DeptDO.PARENT_ID_ROOT); // 兜底置为根部门
        }
    }

    /**
     * 同步用户
     */
    private void syncUsers(ThirdPartySyncStrategy strategy, SocialClientDO client, Integer socialType) {
        List<ThirdPartyUserDTO> userList = strategy.fetchUsers(client);
        if (CollUtil.isEmpty(userList)) {
            log.info("[sync][{}] 无用户数据", SocialTypeEnum.valueOfType(socialType));
            return;
        }

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
        log.info("[sync] 创建系统用户: username={}, id={}", username, user.getId());
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
