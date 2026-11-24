package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.job.SampleUrgentPlanJobInstaller;
import cn.iocoder.yudao.module.infra.api.job.JobRegistrationApi;
import cn.iocoder.yudao.module.infra.api.job.dto.JobRegistrationRespDTO;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class SampleUrgentPlanJobInstallerTest {
    @Test void enabledJobGetsStartupCatchUp() {
        var jobs = mock(JobRegistrationApi.class);
        when(jobs.ensureRegistered(any())).thenReturn(new JobRegistrationRespDTO(1L, true));
        new SampleUrgentPlanJobInstaller(jobs).install();
        verify(jobs).triggerIfEnabled(1L);
    }

    @Test void pausedJobDoesNotRunAtStartup() {
        var jobs = mock(JobRegistrationApi.class);
        when(jobs.ensureRegistered(any())).thenReturn(new JobRegistrationRespDTO(1L, false));
        new SampleUrgentPlanJobInstaller(jobs).install();
        verify(jobs, never()).triggerIfEnabled(any());
    }
}
