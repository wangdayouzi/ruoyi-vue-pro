package cn.iocoder.yudao.module.system.service.dingtalk;

import cn.iocoder.yudao.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.job.JobDO;
import cn.iocoder.yudao.module.infra.dal.mysql.job.JobMapper;
import cn.iocoder.yudao.module.infra.service.job.JobService;
import cn.iocoder.yudao.module.system.job.dingtalk.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkNotificationJobTest {
    @Test void installsThroughExistingJobServiceWithoutOverwritingPausedJobs() throws Exception {
        var mapper=mock(JobMapper.class); var service=mock(JobService.class); var jdbc=mock(JdbcTemplate.class);
        var manager=mock(PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        var installer=new DingTalkNotificationJobInstaller(mapper,service,jdbc,manager);
        installer.install();
        var req=ArgumentCaptor.forClass(JobSaveReqVO.class); verify(service).createJob(req.capture());
        assertEquals("dingTalkNotificationSendJob",req.getValue().getHandlerName());
        assertEquals("0/30 * * * * ?",req.getValue().getCronExpression()); assertEquals(0,req.getValue().getRetryCount());
        when(mapper.selectByHandlerName(anyString())).thenReturn(JobDO.builder().status(2).build());
        installer.install(); verify(service,times(1)).createJob(any()); verify(service,never()).updateJobStatus(anyLong(),anyInt());
    }
    @Test void handlerReturnsSummaryAndPropagatesFailuresToTaskLogs() {
        var queue=mock(DingTalkNotificationQueueService.class);
        var job=new DingTalkNotificationSendJob(queue);
        when(queue.dispatch()).thenReturn("本次处理2条"); assertEquals("本次处理2条",job.execute(""));
        when(queue.dispatch()).thenThrow(new IllegalStateException("数据库不可用"));
        assertThrows(IllegalStateException.class,()->job.execute(""));
    }
}
