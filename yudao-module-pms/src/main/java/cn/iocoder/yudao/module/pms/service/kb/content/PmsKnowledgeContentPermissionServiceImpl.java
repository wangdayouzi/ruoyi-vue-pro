package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.number.NumberUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.permission.PmsKnowledgeContentPermissionUpdateReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeContentPermissionDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeContentPermissionMemberDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.library.PmsKnowledgeLibraryDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.library.PmsKnowledgeLibraryMemberDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeContentPermissionMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeContentPermissionMemberMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.library.PmsKnowledgeLibraryMemberMapper;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeContentLevelEnum;
import cn.iocoder.yudao.module.pms.enums.kb.library.PmsKnowledgeLibraryMemberLevelEnum;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.enums.permission.RoleCodeEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.equalsAny;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_ACCESS_DENIED;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_DELETE_ACCESS_DENIED;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_PERMISSION_MEMBERS_INVALID;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_PERMISSION_NOT_EXISTS;
import static cn.iocoder.yudao.module.pms.enums.ErrorCodeConstants.KNOWLEDGE_CONTENT_WRITE_ACCESS_DENIED;

/**
 * PMS 知识内容协作权限 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PmsKnowledgeContentPermissionServiceImpl implements PmsKnowledgeContentPermissionService {

    @Resource
    private PmsKnowledgeContentPermissionMapper permissionMapper;
    @Resource
    private PmsKnowledgeContentPermissionMemberMapper permissionMemberMapper;

    @Resource
    @Lazy
    private PmsKnowledgeFolderService folderService;
    @Resource
    @Lazy
    private PmsKnowledgeDocumentService documentService;
    @Resource
    private PmsKnowledgeLibraryMemberMapper libraryMemberMapper;
    @Resource
    private PmsKnowledgeLibraryMemberService libraryMemberService;

    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private PermissionApi permissionApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDefaultContentPermission(Long libraryId, Long userId) {
        // 1. 创建默认公开且仅预览的内容权限
        PmsKnowledgeContentPermissionDO permission = new PmsKnowledgeContentPermissionDO().setLibraryId(libraryId)
                .setOpenStatus(true).setOpenLevel(PmsKnowledgeContentLevelEnum.PREVIEW.getLevel());
        permission.setCreator(String.valueOf(userId));
        permissionMapper.insert(permission);

        // 2. 创建人固定拥有管理权限
        permissionMemberMapper.insert(new PmsKnowledgeContentPermissionMemberDO().setPermissionId(permission.getId()).setUserId(userId)
                .setLevel(PmsKnowledgeContentLevelEnum.MANAGE.getLevel()));
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<Long, Long> cloneContentPermissions(Collection<Long> permissionIds, Long targetLibraryId) {
        if (CollUtil.isEmpty(permissionIds)) {
            return Collections.emptyMap();
        }
        // 1. 批量查询原权限及协作者
        List<PmsKnowledgeContentPermissionDO> permissions = permissionMapper.selectByIds(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw exception(KNOWLEDGE_CONTENT_PERMISSION_NOT_EXISTS);
        }
        List<PmsKnowledgeContentPermissionMemberDO> members = permissionMemberMapper.selectListByPermissionIds(permissionIds);

        // 2. 逐个复制权限和协作者，保持同一原权限仍对应同一新权限
        Map<Long, Long> clonedPermissionIdMap = new LinkedHashMap<>();
        for (PmsKnowledgeContentPermissionDO source : permissions) {
            PmsKnowledgeContentPermissionDO target = new PmsKnowledgeContentPermissionDO().setLibraryId(targetLibraryId)
                    .setOpenStatus(source.getOpenStatus()).setOpenLevel(source.getOpenLevel());
            target.setCreator(source.getCreator());
            permissionMapper.insert(target);
            List<PmsKnowledgeContentPermissionMemberDO> targetMembers = convertList(members, member ->
                            new PmsKnowledgeContentPermissionMemberDO().setPermissionId(target.getId())
                                    .setUserId(member.getUserId()).setDeptId(member.getDeptId()).setLevel(member.getLevel()),
                    member -> source.getId().equals(member.getPermissionId()));
            if (CollUtil.isNotEmpty(targetMembers)) {
                permissionMemberMapper.insertBatch(targetMembers);
            }
            clonedPermissionIdMap.put(source.getId(), target.getId());
        }
        return clonedPermissionIdMap;
    }

    @Override
    public Integer validateContentPermissionReadable(Long permissionId, Long libraryId, Long userId) {
        Integer level = getCurrentUserContentPermissionLevel(permissionId, libraryId, userId);
        if (level == null) {
            throw exception(KNOWLEDGE_CONTENT_ACCESS_DENIED);
        }
        return level;
    }

    @Override
    public Integer validateContentPermissionWritable(Long permissionId, Long libraryId, Long userId) {
        Integer level = validateContentPermissionReadable(permissionId, libraryId, userId);
        if (!PmsKnowledgeContentLevelEnum.canEdit(level)) {
            throw exception(KNOWLEDGE_CONTENT_WRITE_ACCESS_DENIED);
        }
        return level;
    }

    @Override
    public Integer validateContentPermissionDeletable(Long permissionId, Long libraryId, Long userId) {
        Integer level = validateContentPermissionReadable(permissionId, libraryId, userId);
        if (!PmsKnowledgeContentLevelEnum.canDelete(level)) {
            throw exception(KNOWLEDGE_CONTENT_DELETE_ACCESS_DENIED);
        }
        return level;
    }

    @Override
    public Integer validateContentPermissionManageable(Long permissionId, Long libraryId, Long userId) {
        Integer level = validateContentPermissionReadable(permissionId, libraryId, userId);
        if (ObjectUtil.notEqual(PmsKnowledgeContentLevelEnum.MANAGE.getLevel(), level)) {
            throw exception(KNOWLEDGE_CONTENT_WRITE_ACCESS_DENIED);
        }
        return level;
    }

    @Override
    public boolean isContentReadable(Long permissionId, Long libraryId, Long userId) {
        return getCurrentUserContentPermissionLevel(permissionId, libraryId, userId) != null;
    }

    @Override
    public PmsKnowledgeContentPermissionDO getContentPermission(Long permissionId, Long userId) {
        // 1. 校验内容权限存在
        PmsKnowledgeContentPermissionDO permission = validatePermissionExists(permissionId);
        // 2. 校验当前用户可以读取知识库
        libraryMemberService.validateLibraryReadable(permission.getLibraryId(), userId);
        // 3. 校验当前用户可以读取该内容
        validateContentPermissionReadable(permissionId, permission.getLibraryId(), userId);
        return permission;
    }

    @Override
    public List<PmsKnowledgeContentPermissionMemberDO> getContentPermissionMemberList(Long permissionId, Long userId) {
        // 1. 校验内容权限存在、知识库可读且内容可读
        PmsKnowledgeContentPermissionDO permission = validatePermissionExists(permissionId);
        libraryMemberService.validateLibraryReadable(permission.getLibraryId(), userId);
        validateContentPermissionReadable(permissionId, permission.getLibraryId(), userId);

        // 2. 返回协作者列表
        return permissionMemberMapper.selectListByPermissionId(permissionId);
    }

    @Override
    public Integer getCurrentUserContentPermissionLevel(Long permissionId, Long libraryId, Long userId) {
        return getCurrentUserContentPermissionLevelMap(Collections.singleton(permissionId), libraryId, userId).get(permissionId);
    }

    @Override
    public Map<Long, Integer> getCurrentUserContentPermissionLevelMap(Collection<Long> permissionIds, Long libraryId, Long userId) {
        if (CollUtil.isEmpty(permissionIds)) {
            return Collections.emptyMap();
        }
        // 1. 批量查询内容权限并校验知识库归属
        List<PmsKnowledgeContentPermissionDO> permissions = permissionMapper.selectByIds(permissionIds);
        if (permissions.size() != permissionIds.size()
                || permissions.stream().anyMatch(permission -> ObjectUtil.notEqual(libraryId, permission.getLibraryId()))) {
            throw exception(KNOWLEDGE_CONTENT_PERMISSION_NOT_EXISTS);
        }
        Map<Long, Integer> levelMap = new LinkedHashMap<>();
        if (libraryMemberService.isLibraryAdmin(libraryId, userId)) {
            permissions.forEach(permission -> levelMap.put(
                    permission.getId(), PmsKnowledgeContentLevelEnum.MANAGE.getLevel()));
            return levelMap;
        }

        // 当前用户获得的公开、个人和各级部门权限取并集。
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        List<Long> deptIds = getDeptAndParentIds(user != null ? user.getDeptId() : null);
        Map<Long, Integer> explicitLevels = new LinkedHashMap<>();
        for (PmsKnowledgeContentPermissionMemberDO member : permissionMemberMapper.selectListByPermissionIds(permissionIds)) {
            if (userId.equals(member.getUserId()) || member.getDeptId() != null && deptIds.contains(member.getDeptId())) {
                explicitLevels.merge(member.getPermissionId(), member.getLevel(), this::unionLevel);
            }
        }
        for (PmsKnowledgeContentPermissionDO permission : permissions) {
            Integer level = explicitLevels.get(permission.getId());
            if (Boolean.TRUE.equals(permission.getOpenStatus())) {
                level = level == null ? permission.getOpenLevel() : unionLevel(level, permission.getOpenLevel());
            }
            levelMap.put(permission.getId(), level);
        }
        return levelMap;
    }

    @Override
    public Set<Long> getReadableContentPermissionIdSet(Collection<Long> libraryIds, Long userId) {
        if (CollUtil.isEmpty(libraryIds)) {
            return Collections.emptySet();
        }
        // 1.1 一次查询全部知识库的内容权限
        List<PmsKnowledgeContentPermissionDO> permissions = permissionMapper.selectListByLibraryIds(libraryIds);
        if (CollUtil.isEmpty(permissions)) {
            return Collections.emptySet();
        }
        Set<Long> permissionIds = convertSet(permissions, PmsKnowledgeContentPermissionDO::getId);
        // 1.2 超级管理员可以读取全部内容
        if (permissionApi.hasAnyRoles(userId, RoleCodeEnum.SUPER_ADMIN.getCode())) {
            return permissionIds;
        }

        // 2.1 用户及所有上级部门中只要有管理员身份，就可管理该库内容。
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        List<Long> deptIds = getDeptAndParentIds(user != null ? user.getDeptId() : null);
        Set<Long> adminLibraryIds = new LinkedHashSet<>();
        List<PmsKnowledgeLibraryMemberDO> libraryMembers = libraryMemberMapper.selectListByLibraryIds(libraryIds);
        for (PmsKnowledgeLibraryMemberDO member : libraryMembers) {
            if ((userId.equals(member.getUserId()) || member.getDeptId() != null && deptIds.contains(member.getDeptId()))
                    && equalsAny(member.getLevel(), PmsKnowledgeLibraryMemberLevelEnum.CREATOR.getLevel(),
                    PmsKnowledgeLibraryMemberLevelEnum.ADMIN.getLevel())) {
                adminLibraryIds.add(member.getLibraryId());
            }
        }
        // 2.2 一次查询当前用户及其部门的内容协作权限，用户权限优先于部门权限
        Map<Long, PmsKnowledgeContentPermissionMemberDO> userPermissionMemberMap = new LinkedHashMap<>();
        Map<Long, PmsKnowledgeContentPermissionMemberDO> deptPermissionMemberMap = new LinkedHashMap<>();
        for (PmsKnowledgeContentPermissionMemberDO member : permissionMemberMapper.selectListByPermissionIds(permissionIds)) {
            if (userId.equals(member.getUserId())) {
                userPermissionMemberMap.put(member.getPermissionId(), member);
            } else if (member.getDeptId() != null && deptIds.contains(member.getDeptId())) {
                deptPermissionMemberMap.put(member.getPermissionId(), member);
            }
        }

        // 3. 库管理员、本人和部门的显式权限优先于公开权限
        Set<Long> readablePermissionIds = new LinkedHashSet<>();
        for (PmsKnowledgeContentPermissionDO permission : permissions) {
            if (adminLibraryIds.contains(permission.getLibraryId())
                    || userPermissionMemberMap.containsKey(permission.getId())
                    || deptPermissionMemberMap.containsKey(permission.getId())
                    || Boolean.TRUE.equals(permission.getOpenStatus())) {
                readablePermissionIds.add(permission.getId());
            }
        }
        return readablePermissionIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContentPermission(PmsKnowledgeContentPermissionUpdateReqVO updateReqVO, Long userId) {
        // 1.1 校验内容权限存在
        PmsKnowledgeContentPermissionDO permission = validatePermissionExists(updateReqVO.getId());
        // 1.2 校验知识库可访问
        PmsKnowledgeLibraryDO library = libraryMemberService.validateLibraryReadable(permission.getLibraryId(), userId);
        // 1.3 校验内容管理权限
        if (!PmsKnowledgeContentLevelEnum.MANAGE.getLevel().equals(
                getCurrentUserContentPermissionLevel(permission.getId(), permission.getLibraryId(), userId))) {
            throw exception(KNOWLEDGE_CONTENT_WRITE_ACCESS_DENIED);
        }
        // 1.4 校验协作者唯一
        Set<Long> userIds = convertSet(updateReqVO.getMembers(),
                PmsKnowledgeContentPermissionUpdateReqVO.Member::getUserId);
        Set<Long> deptIds = convertSet(updateReqVO.getMembers(),
                PmsKnowledgeContentPermissionUpdateReqVO.Member::getDeptId);
        if (userIds.size() + deptIds.size() != updateReqVO.getMembers().size()) {
            throw exception(KNOWLEDGE_CONTENT_PERMISSION_MEMBERS_INVALID);
        }
        // 1.5 校验协作者不能包含内容创建人
        if (userIds.contains(NumberUtils.parseLong(permission.getCreator()))) {
            throw exception(KNOWLEDGE_CONTENT_PERMISSION_MEMBERS_INVALID);
        }
        // 1.6 校验用户协作者存在
        adminUserApi.validateUserList(userIds);
        // 1.7 校验部门协作者存在
        deptApi.validateDeptList(deptIds);
        // 1.8 私有知识库的用户协作者必须是知识库成员
        if (!library.getOpenStatus()) {
            List<PmsKnowledgeLibraryMemberDO> libraryMembers = libraryMemberMapper.selectListByLibraryId(
                    library.getId());
            Set<Long> libraryUserIds = convertSet(libraryMembers, PmsKnowledgeLibraryMemberDO::getUserId);
            Set<Long> libraryDeptIds = convertSet(libraryMembers, PmsKnowledgeLibraryMemberDO::getDeptId);
            if (userIds.stream().anyMatch(id -> {
                if (libraryUserIds.contains(id)) return false;
                AdminUserRespDTO targetUser = adminUserApi.getUser(id);
                return targetUser == null || getDeptAndParentIds(targetUser.getDeptId()).stream()
                        .noneMatch(libraryDeptIds::contains);
            })) {
                throw exception(KNOWLEDGE_CONTENT_PERMISSION_MEMBERS_INVALID);
            }
            // 私有库的子部门也属于已加入的上级部门。
            if (deptIds.stream().anyMatch(id -> getDeptAndParentIds(id).stream()
                    .noneMatch(libraryDeptIds::contains))) {
                throw exception(KNOWLEDGE_CONTENT_PERMISSION_MEMBERS_INVALID);
            }
        }

        // 2. 更新公开权限，协作者按差量维护，避免每次保存都产生软删除历史行。
        permissionMapper.updateById(BeanUtils.toBean(updateReqVO, PmsKnowledgeContentPermissionDO.class)
                .setId(permission.getId()).setLibraryId(null));
        List<PmsKnowledgeContentPermissionMemberDO> existing = permissionMemberMapper.selectListByPermissionId(permission.getId());
        Long creatorId = NumberUtils.parseLong(permission.getCreator());
        if (existing.stream().noneMatch(member -> creatorId.equals(member.getUserId()))) {
            permissionMemberMapper.insert(new PmsKnowledgeContentPermissionMemberDO()
                    .setPermissionId(permission.getId()).setUserId(creatorId)
                    .setLevel(PmsKnowledgeContentLevelEnum.MANAGE.getLevel()));
        }
        for (PmsKnowledgeContentPermissionMemberDO member : existing) {
            if (creatorId.equals(member.getUserId())) continue;
            PmsKnowledgeContentPermissionUpdateReqVO.Member requested = updateReqVO.getMembers().stream()
                    .filter(item -> member.getUserId() != null
                            ? member.getUserId().equals(item.getUserId())
                            : member.getDeptId().equals(item.getDeptId()))
                    .findFirst().orElse(null);
            if (requested == null) {
                permissionMemberMapper.deleteById(member.getId());
            } else if (!ObjectUtil.equals(member.getLevel(), requested.getLevel())) {
                permissionMemberMapper.updateById(new PmsKnowledgeContentPermissionMemberDO()
                        .setId(member.getId()).setLevel(requested.getLevel()));
            }
        }
        for (PmsKnowledgeContentPermissionUpdateReqVO.Member requested : updateReqVO.getMembers()) {
            if (existing.stream().noneMatch(member -> requested.getUserId() != null
                    ? requested.getUserId().equals(member.getUserId())
                    : requested.getDeptId().equals(member.getDeptId()))) {
                permissionMemberMapper.insert(new PmsKnowledgeContentPermissionMemberDO()
                        .setPermissionId(permission.getId()).setUserId(requested.getUserId())
                        .setDeptId(requested.getDeptId()).setLevel(requested.getLevel()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUnusedContentPermissions(Set<Long> permissionIds) {
        if (CollUtil.isEmpty(permissionIds)) {
            return;
        }
        // 仅删除已经不被任何文件夹或文档引用的权限，避免同一权限被兄弟内容复用时误删
        permissionIds.removeAll(folderService.getExistingContentPermissionIdSet(permissionIds));
        permissionIds.removeAll(documentService.getExistingContentPermissionIdSet(permissionIds));
        if (CollUtil.isEmpty(permissionIds)) {
            return;
        }

        permissionMemberMapper.deleteByPermissionIds(permissionIds);
        permissionMapper.deleteByIds(permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContentPermissionsByLibraryId(Long libraryId) {
        List<PmsKnowledgeContentPermissionDO> permissions = permissionMapper.selectListByLibraryId(libraryId);
        Set<Long> permissionIds = convertSet(permissions, PmsKnowledgeContentPermissionDO::getId);
        if (CollUtil.isEmpty(permissionIds)) {
            return;
        }

        // 先删除权限成员关系，再删除权限主记录
        permissionMemberMapper.deleteByPermissionIds(permissionIds);
        permissionMapper.deleteByIds(permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContentPermissionMembersByLibraryId(Long libraryId, Collection<Long> userIds, Collection<Long> deptIds) {
        if (CollUtil.isEmpty(userIds) && CollUtil.isEmpty(deptIds)) {
            return;
        }
        Set<Long> permissionIds = convertSet(permissionMapper.selectListByLibraryId(libraryId),
                PmsKnowledgeContentPermissionDO::getId);
        if (CollUtil.isEmpty(permissionIds)) {
            return;
        }

        // 分别清理用户和部门的内容级协作关系
        if (CollUtil.isNotEmpty(userIds)) {
            permissionMemberMapper.deleteByPermissionIdsAndUserIds(permissionIds, userIds);
        }
        if (CollUtil.isNotEmpty(deptIds)) {
            permissionMemberMapper.deleteByPermissionIdsAndDeptIds(permissionIds, deptIds);
        }
    }

    private PmsKnowledgeContentPermissionDO validatePermissionExists(Long permissionId) {
        PmsKnowledgeContentPermissionDO permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw exception(KNOWLEDGE_CONTENT_PERMISSION_NOT_EXISTS);
        }
        return permission;
    }

    private List<Long> getDeptAndParentIds(Long deptId) {
        if (deptId == null) return Collections.emptyList();
        List<Long> ids = new ArrayList<>();
        ids.add(deptId);
        List<DeptRespDTO> parents = deptApi.getParentDeptList(deptId);
        if (parents != null) parents.forEach(parent -> ids.add(parent.getId()));
        return ids;
    }

    private Integer unionLevel(Integer left, Integer right) {
        return levelRank(left) >= levelRank(right) ? left : right;
    }

    private int levelRank(Integer level) {
        if (PmsKnowledgeContentLevelEnum.MANAGE.getLevel().equals(level)) return 5;
        if (PmsKnowledgeContentLevelEnum.EDIT.getLevel().equals(level)) return 4;
        if (PmsKnowledgeContentLevelEnum.UPLOAD_DOWNLOAD.getLevel().equals(level)) return 3;
        if (PmsKnowledgeContentLevelEnum.DOWNLOAD.getLevel().equals(level)) return 2;
        return 1;
    }

}
