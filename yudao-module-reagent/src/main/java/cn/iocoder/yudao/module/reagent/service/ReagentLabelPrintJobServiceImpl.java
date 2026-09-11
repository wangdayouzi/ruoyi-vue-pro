package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrintJobDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrinterDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelTemplateDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentLabelPrintJobMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentLabelPrinterMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentLabelTemplateMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.reagent.enums.LogRecordConstants.*;

@Service
@Slf4j
public class ReagentLabelPrintJobServiceImpl implements ReagentLabelPrintJobService {

    /** 代理默认每 30 秒心跳一次；连续 3 次未收到心跳即视为离线。 */
    private static final long AGENT_HEARTBEAT_TIMEOUT_SECONDS = 90;

    @Resource
    private ReagentLabelPrinterMapper printerMapper;
    @Resource
    private ReagentLabelPrintJobMapper labelPrintJobMapper;
    @Resource
    private ReagentLabelTemplateMapper templateMapper;

    @Override
    public List<ReagentLabelPrinterDO> getEnabledPrinters() {
        List<ReagentLabelPrinterDO> printers = printerMapper.selectEnabledList();
        LocalDateTime now = LocalDateTime.now();
        printers.forEach(printer -> printer.setOnlineStatus(isAgentOnline(printer, now) ? 1 : 0));
        return printers;
    }

    @Override
    public List<ReagentLabelTemplateDO> getEnabledTemplates() {
        return templateMapper.selectEnabledList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = REAGENT_LABEL_PRINT_TYPE, subType = REAGENT_LABEL_PRINT_CREATE_SUB_TYPE, bizNo = "{{#job.id}}",
            success = REAGENT_LABEL_PRINT_CREATE_SUCCESS)
    public Long createJob(ReagentLabelPrintJobCreateReqVO reqVO) {
        ReagentLabelPrinterDO printer = printerMapper.selectById(reqVO.getPrinterId());
        if (printer == null || !Integer.valueOf(1).equals(printer.getStatus())
                || !isAgentOnline(printer, LocalDateTime.now())) {
            throw exception(REAGENT_LABEL_PRINTER_NOT_AVAILABLE);
        }
        if (templateMapper.selectEnabledByCode(reqVO.getTemplateCode()) == null) {
            throw exception(REAGENT_LABEL_TEMPLATE_NOT_AVAILABLE);
        }
        ReagentLabelPrintJobDO job = new ReagentLabelPrintJobDO();
        job.setPrinterId(printer.getId());
        job.setTemplateCode(reqVO.getTemplateCode());
        job.setDataSnapshot(JSONUtil.toJsonStr(reqVO.getLabel()));
        job.setCopies(reqVO.getCopies());
        job.setPrintedCount(0);
        job.setStatus(ReagentLabelPrintJobDO.STATUS_PENDING);
        // 任务编号在入库前生成，兼容 job_no 设为 NOT NULL 的 PostgreSQL / MySQL 表结构。
        job.setJobNo("RLP" + IdUtil.getSnowflakeNextIdStr());
        labelPrintJobMapper.insert(job);
        LogRecordContext.putVariable("job", job);
        return job.getId();
    }

    @Override
    public ReagentLabelPrintJobDO getJob(Long id) {
        ReagentLabelPrintJobDO job = labelPrintJobMapper.selectById(id);
        if (job == null) {
            throw exception(REAGENT_LABEL_JOB_NOT_EXISTS);
        }
        return job;
    }

    @Override
    public List<ReagentLabelPrintJobDO> getRecentJobs() {
        return labelPrintJobMapper.selectRecentList();
    }

    @Override
    public ReagentLabelAgentJobRespVO claimNext(String agentCode, String agentToken) {
        List<ReagentLabelPrinterDO> printers = validateAgent(agentCode, agentToken);
        List<Long> printerIds = printers.stream().map(ReagentLabelPrinterDO::getId).toList();
        // CAS 领取失败说明被另一并发请求抢走，重新查询一次即可。
        for (int i = 0; i < 2; i++) {
            ReagentLabelPrintJobDO job = labelPrintJobMapper.selectNextPendingByPrinterIds(printerIds);
            if (job == null) {
                return null;
            }
            String claimToken = IdUtil.fastSimpleUUID();
            if (!labelPrintJobMapper.claim(job.getId(), agentCode, claimToken)) {
                continue;
            }
            ReagentLabelPrinterDO printer = printers.stream()
                    .filter(item -> item.getId().equals(job.getPrinterId())).findFirst().orElseThrow();
            ReagentLabelAgentJobRespVO respVO = new ReagentLabelAgentJobRespVO();
            respVO.setJobId(job.getId());
            respVO.setJobNo(job.getJobNo());
            respVO.setClaimToken(claimToken);
            respVO.setSystemPrinterName(printer.getSystemPrinterName());
            respVO.setTemplateCode(job.getTemplateCode());
            respVO.setDataSnapshot(job.getDataSnapshot());
            respVO.setCopies(job.getCopies());
            return respVO;
        }
        return null;
    }

    @Override
    public void heartbeat(String agentCode, String agentToken) {
        List<ReagentLabelPrinterDO> printers = validateAgent(agentCode, agentToken);
        LocalDateTime now = LocalDateTime.now();
        printers.forEach(printer -> {
            ReagentLabelPrinterDO update = new ReagentLabelPrinterDO();
            update.setId(printer.getId());
            update.setOnlineStatus(1);
            update.setLastHeartbeatTime(now);
            printerMapper.updateById(update);
        });
    }

    /**
     * 在线仅表示代理已在时限内成功连接后端；不代表 Windows 打印机、纸带或模板已经通过真机检测。
     */
    private boolean isAgentOnline(ReagentLabelPrinterDO printer, LocalDateTime now) {
        return printer.getLastHeartbeatTime() != null
                && !printer.getLastHeartbeatTime().isBefore(now.minusSeconds(AGENT_HEARTBEAT_TIMEOUT_SECONDS));
    }

    @Override
    public void recoverAfterRestart(String agentCode, String agentToken) {
        validateAgent(agentCode, agentToken);
        int count = labelPrintJobMapper.failUnfinishedByAgent(agentCode);
        if (count > 0) {
            log.warn("[label-print] agent={} restarted; {} unfinished job(s) marked failed", agentCode, count);
        }
    }

    @Override
    public void reportResult(String agentCode, String agentToken, ReagentLabelAgentResultReqVO reqVO) {
        validateAgent(agentCode, agentToken);
        ReagentLabelPrintJobDO job = getJob(reqVO.getJobId());
        if (!agentCode.equals(job.getAgentCode()) || !reqVO.getClaimToken().equals(job.getClaimToken())) {
            throw exception(REAGENT_LABEL_AGENT_UNAUTHORIZED);
        }
        if (reqVO.getStatus() != ReagentLabelPrintJobDO.STATUS_SUCCESS
                && reqVO.getStatus() != ReagentLabelPrintJobDO.STATUS_FAILED) {
            throw exception(REAGENT_LABEL_JOB_STATUS_INVALID);
        }
        if (job.getStatus() == ReagentLabelPrintJobDO.STATUS_SUCCESS || job.getStatus() == ReagentLabelPrintJobDO.STATUS_FAILED) {
            return; // 代理网络重试时幂等返回
        }
        ReagentLabelPrintJobDO update = new ReagentLabelPrintJobDO();
        update.setId(job.getId());
        update.setStatus(reqVO.getStatus());
        update.setPrintedCount(reqVO.getPrintedCount() == null ? 0 : reqVO.getPrintedCount());
        update.setErrorCode(reqVO.getErrorCode());
        update.setErrorMessage(reqVO.getErrorMessage());
        update.setCompletedTime(LocalDateTime.now());
        labelPrintJobMapper.updateById(update);
        log.info("[label-print] job={} status={} printed={}", job.getJobNo(), reqVO.getStatus(), update.getPrintedCount());
    }

    private List<ReagentLabelPrinterDO> validateAgent(String agentCode, String agentToken) {
        List<ReagentLabelPrinterDO> printers = printerMapper.selectByAgentCode(agentCode);
        byte[] supplied = sha256(agentToken);
        boolean authorized = printers.stream()
                .map(ReagentLabelPrinterDO::getAgentTokenHash)
                .filter(java.util.Objects::nonNull)
                .map(hash -> hash.getBytes(StandardCharsets.UTF_8))
                .anyMatch(expected -> MessageDigest.isEqual(expected, supplied));
        if (printers.isEmpty() || !authorized) {
            throw exception(REAGENT_LABEL_AGENT_UNAUTHORIZED);
        }
        return printers;
    }

    /** 存库的是十六进制 SHA-256 字符串，避免机器密钥以明文落库。 */
    private static byte[] sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().getBytes(StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JDK 缺少 SHA-256", exception);
        }
    }
}
