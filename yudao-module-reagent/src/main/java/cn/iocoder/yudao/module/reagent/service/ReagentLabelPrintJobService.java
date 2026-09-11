package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrintJobDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrinterDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelTemplateDO;

import java.util.List;

/** 试剂标签打印任务服务。 */
public interface ReagentLabelPrintJobService {

    List<ReagentLabelPrinterDO> getEnabledPrinters();

    List<ReagentLabelTemplateDO> getEnabledTemplates();

    Long createJob(ReagentLabelPrintJobCreateReqVO reqVO);

    ReagentLabelPrintJobDO getJob(Long id);

    /** 获得最近 20 条标签打印任务，用于业务页面历史查看。 */
    List<ReagentLabelPrintJobDO> getRecentJobs();

    /** 验证代理并领取一条属于该代理打印机的任务；无任务返回 null。 */
    ReagentLabelAgentJobRespVO claimNext(String agentCode, String agentToken);

    void heartbeat(String agentCode, String agentToken);

    /** 代理启动时清理上一进程未完成的领取任务。 */
    void recoverAfterRestart(String agentCode, String agentToken);

    void reportResult(String agentCode, String agentToken, ReagentLabelAgentResultReqVO reqVO);
}
