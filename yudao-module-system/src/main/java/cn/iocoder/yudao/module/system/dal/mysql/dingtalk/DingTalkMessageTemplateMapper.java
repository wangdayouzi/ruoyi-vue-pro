package cn.iocoder.yudao.module.system.dal.mysql.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkTemplatePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageTemplateDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DingTalkMessageTemplateMapper extends BaseMapperX<DingTalkMessageTemplateDO> {
    // 关闭多租户时没有租户拦截器，仍需明确钉钉模板所属的主租户，避免同编码模板多行匹配。
    default Long noticeTenantId() { return java.util.Objects.requireNonNullElse(cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),1L); }
    default DingTalkMessageTemplateDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<DingTalkMessageTemplateDO>()
                .eq(DingTalkMessageTemplateDO::getCode,code).eq(DingTalkMessageTemplateDO::getTenantId,noticeTenantId()));
    }
    default PageResult<DingTalkMessageTemplateDO> selectPage(DingTalkTemplatePageReqVO req) {
        return selectPage(req, new LambdaQueryWrapperX<DingTalkMessageTemplateDO>()
                .eq(DingTalkMessageTemplateDO::getTenantId,noticeTenantId())
                .likeIfPresent(DingTalkMessageTemplateDO::getName, req.getName())
                .likeIfPresent(DingTalkMessageTemplateDO::getCode, req.getCode())
                .eqIfPresent(DingTalkMessageTemplateDO::getMessageType, req.getMessageType())
                .eqIfPresent(DingTalkMessageTemplateDO::getStatus, req.getStatus())
                .orderByDesc(DingTalkMessageTemplateDO::getId));
    }
}
