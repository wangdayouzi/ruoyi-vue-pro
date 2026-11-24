package cn.iocoder.yudao.module.infra.api.job;

import cn.iocoder.yudao.module.infra.api.job.dto.JobRegistrationReqDTO;
import cn.iocoder.yudao.module.infra.api.job.dto.JobRegistrationRespDTO;
import jakarta.validation.Valid;

public interface JobRegistrationApi {
    /** 并发安全地登记调度，保留管理员已有 cron 和暂停状态。 */
    JobRegistrationRespDTO ensureRegistered(@Valid JobRegistrationReqDTO request);
    void triggerIfEnabled(Long id);
}
