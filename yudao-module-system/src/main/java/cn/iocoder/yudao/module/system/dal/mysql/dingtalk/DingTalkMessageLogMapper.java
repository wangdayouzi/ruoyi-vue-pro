package cn.iocoder.yudao.module.system.dal.mysql.dingtalk;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.dingtalk.vo.DingTalkLogPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dingtalk.DingTalkMessageLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DingTalkMessageLogMapper extends BaseMapperX<DingTalkMessageLogDO> {
    default DingTalkMessageLogDO selectByRequestAndUser(String requestId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<DingTalkMessageLogDO>()
                .eq(DingTalkMessageLogDO::getTenantId,java.util.Objects.requireNonNullElse(cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),1L))
                .eq(DingTalkMessageLogDO::getRequestId, requestId).eq(DingTalkMessageLogDO::getUserId, userId));
    }
    default PageResult<DingTalkMessageLogDO> selectPage(DingTalkLogPageReqVO req) {
        return selectPage(req, new LambdaQueryWrapperX<DingTalkMessageLogDO>()
                .eq(DingTalkMessageLogDO::getTenantId,java.util.Objects.requireNonNullElse(cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId(),1L))
                .likeIfPresent(DingTalkMessageLogDO::getTemplateCode, req.getTemplateCode())
                .eqIfPresent(DingTalkMessageLogDO::getRequestId, req.getRequestId())
                .eqIfPresent(DingTalkMessageLogDO::getUserId, req.getUserId())
                .eqIfPresent(DingTalkMessageLogDO::getSendStatus, req.getSendStatus())
                .orderByDesc(DingTalkMessageLogDO::getId));
    }
}
