package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.quartz.core.scheduler.SchedulerManager;
import cn.iocoder.yudao.module.infra.dal.dataobject.job.JobDO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import cn.iocoder.yudao.module.reagent.job.SampleUrgentPlanJobInstaller;
import org.junit.jupiter.api.*;
import org.quartz.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SampleUrgentPlanJobInstallerTest {
    private static final String HANDLER = "sampleUrgentPlanGenerateJob";
    private final JobMapper jobs = mock(JobMapper.class);
    private final JobService service = mock(JobService.class);
    private final Scheduler scheduler = mock(Scheduler.class);
    private final SchedulerManager manager = mock(SchedulerManager.class);
    private final PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
    private final SampleUrgentPlanJobInstaller installer = new SampleUrgentPlanJobInstaller(
            jobs, service, scheduler, manager, mock(JdbcTemplate.class), transactions);
    @BeforeEach void setup() {
        when(transactions.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
    }
    private JobDO row(int status) {
        return JobDO.builder().id(1L).name("周期计划").status(status).handlerName(HANDLER).handlerParam("")
                .cronExpression("0 0 8 * * ?").retryCount(0).retryInterval(0).monitorTimeout(0).build();
    }
    @Test void sqlSeedGetsRealQuartzScheduleAndStartupCatchUp() throws Exception {
        var row = row(0); when(jobs.selectByHandlerName(HANDLER)).thenReturn(row);
        installer.install();
        verify(manager).addJob(1L, HANDLER, "", "0 0 8 * * ?", 0, 0);
        assertEquals(1, row.getStatus()); verify(jobs).updateById(row);
        verify(service).triggerJob(1L);
    }
    @Test void administratorPausedTaskStaysPausedAndDoesNotRunAtStartup() throws Exception {
        when(jobs.selectByHandlerName(HANDLER)).thenReturn(row(2));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.checkExists(any(TriggerKey.class))).thenReturn(true);
        installer.install();
        verifyNoInteractions(manager, service);
        verify(jobs, never()).updateById(any(JobDO.class));
    }
    @Test void missingQuartzTriggerIsRepairedWhilePreservingPausedState() throws Exception {
        when(jobs.selectByHandlerName(HANDLER)).thenReturn(row(2));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        installer.install();
        verify(manager).deleteJob(HANDLER);
        verify(manager).addJob(1L, HANDLER, "", "0 0 8 * * ?", 0, 0);
        verify(manager).pauseJob(HANDLER);
        verifyNoInteractions(service);
    }
}
