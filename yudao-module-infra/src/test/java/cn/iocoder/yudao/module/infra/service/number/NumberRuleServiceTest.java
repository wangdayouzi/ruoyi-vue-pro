package cn.iocoder.yudao.module.infra.service.number;

import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberIssueDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberRuleDO;
import cn.iocoder.yudao.module.infra.dal.mysql.number.NumberIssueMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.number.NumberRuleMapper;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NumberRuleServiceTest {
    @InjectMocks private NumberRuleService service;
    @Mock private NumberRuleMapper ruleMapper;
    @Mock private NumberIssueMapper issueMapper;

    @Test
    void issueFormatsAndRecordsBusinessNumber() {
        when(ruleMapper.selectForUpdate(1L)).thenReturn(new NumberRuleDO().setId(1L).setStatus(0)
                .setPrefix("Q200-").setDigits(4).setStartNo(1L).setLastNo(7L).setYearlyReset(false));
        assertEquals("Q200-0008", service.issue(1L, "PMS_KB_DOCUMENT", 55L));
        ArgumentCaptor<NumberIssueDO> issue = ArgumentCaptor.forClass(NumberIssueDO.class);
        verify(issueMapper).insert(issue.capture());
        assertEquals("Q200-0008", issue.getValue().getNumberValue());
        assertEquals(55L, issue.getValue().getBizId());
        ArgumentCaptor<NumberRuleDO> update = ArgumentCaptor.forClass(NumberRuleDO.class);
        verify(ruleMapper).updateById(update.capture());
        assertEquals(8L, update.getValue().getLastNo());
    }

    @Test
    void issueRejectsExhaustedRule() {
        when(ruleMapper.selectForUpdate(1L)).thenReturn(new NumberRuleDO().setId(1L).setStatus(0)
                .setPrefix("Q-").setDigits(2).setStartNo(1L).setLastNo(99L).setYearlyReset(false));
        ServiceException error = assertThrows(ServiceException.class, () -> service.issue(1L, "PMS_KB_DOCUMENT", 55L));
        assertEquals(400, error.getCode());
        assertEquals("编号流水号已超出配置位数", error.getMessage());
    }

    @Test
    void startingNumberMustFitConfiguredDigits() {
        ServiceException error = assertThrows(ServiceException.class, () -> service.save(new NumberRuleDO()
                .setCode("TEST").setName("测试").setDigits(2).setStartNo(100L)));
        assertEquals(400, error.getCode());
    }
}
