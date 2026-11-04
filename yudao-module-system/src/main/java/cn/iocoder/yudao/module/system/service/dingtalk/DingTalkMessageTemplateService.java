package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import cn.iocoder.yudao.module.system.dal.mysql.dingtalk.DingTalkMessageTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
@RequiredArgsConstructor
public class DingTalkMessageTemplateService {
    private final DingTalkMessageTemplateMapper mapper;
    private final DingTalkTemplateRenderer renderer;
    private final AdminUserMapper users;
    private final DingTalkUserIdentityService identities;
    private final DingTalkTemplateParameterService parameters;
    @jakarta.annotation.Resource private DingTalkRecipientResolver recipientResolver;

    public Long save(DingTalkTemplateSaveReqVO req, boolean update) {
        if (update) require(req.getId());
        var params = renderer.validate(req.getMessageType(), req.getContent());
        parameters.validate(req.getCode(),params);
        var existing = mapper.selectByCode(req.getCode());
        if (existing != null && (!update || !existing.getId().equals(req.getId()))) throw invalidParamException("模板编码已存在");
        var data = BeanUtils.toBean(req, DingTalkMessageTemplateDO.class);
        data.setTenantId(java.util.Objects.requireNonNullElse(cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),1L));
        List<Long> receivers = req.getDefaultUserIds() == null ? List.of() : req.getDefaultUserIds().stream().distinct().toList();
        for (Long id : receivers) {
            var user = users.selectById(id);
            if (user == null || !Integer.valueOf(0).equals(user.getStatus())) throw invalidParamException("默认接收人 {} 不存在或已停用", id);
            if (!identities.isBound(user)) throw invalidParamException("{} 未关联钉钉，请先绑定钉钉账号或同步通讯录", user.getNickname());

        }
        data.setDefaultUserIds(receivers);
        List<Long> deptIds=req.getDefaultDeptIds()==null ? List.of() : req.getDefaultDeptIds().stream().distinct().toList();
        if(!deptIds.isEmpty()) recipientResolver.validateDepartments(deptIds);
        data.setDefaultDeptIds(deptIds);
        data.setIncludeChildDepts(Boolean.TRUE.equals(req.getIncludeChildDepts()));
        data.setParams(params);
        try {
            if (update) mapper.updateById(data);
            else { data.setId(null); mapper.insert(data); }
        } catch (DuplicateKeyException ex) { throw invalidParamException("模板编码已存在"); }
        return data.getId();
    }
    public DingTalkMessageTemplateDO require(Long id) {
        if (id == null) throw invalidParamException("模板编号不能为空");
        var data = mapper.selectById(id);
        if (data == null) throw invalidParamException("钉钉消息模板不存在");
        if (!java.util.Objects.equals(data.getTenantId(),java.util.Objects.requireNonNullElse(cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),1L)))
            throw invalidParamException("钉钉消息模板不属于当前配置范围");
        return data;
    }
    public void delete(Long id) { require(id); mapper.deleteById(id); }
    public record ReceiverOption(Long id, String nickname, String username, boolean dingTalkBound) {}
    public record DepartmentOption(Long id, Long parentId, String name) {}
    public List<DepartmentOption> departmentOptions() {
        return recipientResolver.departmentOptions().stream().map(d -> new DepartmentOption(d.getId(),d.getParentId(),d.getName())).toList();
    }
    public List<ReceiverOption> receiverOptions() {
        var enabledUsers = users.selectListByStatus(0);
        var bound = identities.boundUserIds(enabledUsers);
        return enabledUsers.stream().map(u -> new ReceiverOption(u.getId(),u.getNickname(),u.getUsername(),
                bound.contains(u.getId()))).toList();
    }
    public PageResult<DingTalkMessageTemplateDO> page(DingTalkTemplatePageReqVO req) { return mapper.selectPage(req); }
    public Map<String, String> preview(DingTalkTemplatePreviewReqVO req) {
        parameters.validate(req.getTemplate().getCode(),renderer.validate(req.getTemplate().getMessageType(),req.getTemplate().getContent()));
        return renderer.render(req.getTemplate().getMessageType(), req.getTemplate().getContent(), req.getParams());
    }
}
