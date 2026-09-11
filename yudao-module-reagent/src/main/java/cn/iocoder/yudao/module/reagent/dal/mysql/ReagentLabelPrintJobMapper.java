package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrintJobDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReagentLabelPrintJobMapper extends BaseMapperX<ReagentLabelPrintJobDO> {

    /** 业务页面查看的最近打印历史，固定最多 20 条，避免一次加载全部任务。 */
    default List<ReagentLabelPrintJobDO> selectRecentList() {
        return selectList(new LambdaQueryWrapperX<ReagentLabelPrintJobDO>()
                .orderByDesc(ReagentLabelPrintJobDO::getCreateTime)
                .last("LIMIT 20"));
    }

    default ReagentLabelPrintJobDO selectNextPendingByPrinterIds(List<Long> printerIds) {
        if (printerIds.isEmpty()) {
            return null;
        }
        List<ReagentLabelPrintJobDO> jobs = selectList(new LambdaQueryWrapperX<ReagentLabelPrintJobDO>()
                .in(ReagentLabelPrintJobDO::getPrinterId, printerIds)
                .eq(ReagentLabelPrintJobDO::getStatus, ReagentLabelPrintJobDO.STATUS_PENDING)
                .orderByAsc(ReagentLabelPrintJobDO::getCreateTime)
                .last("LIMIT 1"));
        return jobs.isEmpty() ? null : jobs.get(0);
    }

    /** CAS 领取；两个代理并发时只有一个可把 PENDING 改为 CLAIMED。 */
    default boolean claim(Long jobId, String agentCode, String claimToken) {
        return update(null, new LambdaUpdateWrapper<ReagentLabelPrintJobDO>()
                .set(ReagentLabelPrintJobDO::getStatus, ReagentLabelPrintJobDO.STATUS_CLAIMED)
                .set(ReagentLabelPrintJobDO::getAgentCode, agentCode)
                .set(ReagentLabelPrintJobDO::getClaimToken, claimToken)
                .set(ReagentLabelPrintJobDO::getClaimedTime, java.time.LocalDateTime.now())
                .eq(ReagentLabelPrintJobDO::getId, jobId)
                .eq(ReagentLabelPrintJobDO::getStatus, ReagentLabelPrintJobDO.STATUS_PENDING)) > 0;
    }

    /** 代理进程重启时，将上一进程已领取但未回传的任务标记失败，避免自动重打造成重复标签。 */
    default int failUnfinishedByAgent(String agentCode) {
        return update(null, new LambdaUpdateWrapper<ReagentLabelPrintJobDO>()
                .set(ReagentLabelPrintJobDO::getStatus, ReagentLabelPrintJobDO.STATUS_FAILED)
                .set(ReagentLabelPrintJobDO::getErrorCode, "AGENT_RESTARTED")
                .set(ReagentLabelPrintJobDO::getErrorMessage, "打印代理重启，未确认是否已出纸；请人工确认后重新打印")
                .set(ReagentLabelPrintJobDO::getCompletedTime, java.time.LocalDateTime.now())
                .eq(ReagentLabelPrintJobDO::getAgentCode, agentCode)
                .in(ReagentLabelPrintJobDO::getStatus, ReagentLabelPrintJobDO.STATUS_CLAIMED,
                        ReagentLabelPrintJobDO.STATUS_PRINTING));
    }
}
