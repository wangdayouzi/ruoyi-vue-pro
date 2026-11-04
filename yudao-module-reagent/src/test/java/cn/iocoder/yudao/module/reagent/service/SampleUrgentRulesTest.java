package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.*;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SampleUrgentRulesTest {
    @Test void normalizesHistoricalBasSeparatorsWithoutLosingBlindSuffix() {
        assertEquals("25BAS0076-Blind、25BAS0077-Blind、25BAS0078-Blind",SampleUrgentRules.normalizeBas(
                " 25BAS0076-Blind,25BAS0077-Blind，25BAS0078-Blind；\n25BAS0076-Blind、"));
        assertEquals("",SampleUrgentRules.normalizeBas(null));
    }
    @Test void requiresBasForNewProjectsButAllowsExistingProjectWithoutBas() {
        var req=new SampleUrgentSaveReqVO(); req.setNewProject(true); req.setBasNos("，； ");
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateSave(req));
        req.setNewProject(false);
        assertDoesNotThrow(()->SampleUrgentRules.validateSave(req));
    }
    @Test void historicalArrivalDoesNotBlockReschedulingThroughSimplifiedForm() {
        var req=new SampleUrgentSaveReqVO(); req.setDeadline(LocalDateTime.now()); req.setExpectedArrival(req.getDeadline().plusHours(1));
        assertDoesNotThrow(()->SampleUrgentRules.validateSave(req));
    }
    @Test void partialInboundCanOmitBothScopes() {
        assertDoesNotThrow(() -> SampleUrgentRules.validateProgress(progress(2), row()));
    }
    @Test void exceptionsRequireExplanationAndCannotBeHiddenByCompletion() {
        var req=progress(1); req.setExceptionType("NOT_ARRIVED");
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(req,row()));
        req.setExceptionNote("物流未送达");
        assertDoesNotThrow(()->SampleUrgentRules.validateProgress(req,row()));
        req.setStatus(3); req.setCompletedScope("全部样品"); req.setCompletedTime(LocalDateTime.now());
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(req,row()));
    }
    @Test void completionRejectsRemainingSamplesAndInvalidTimes() {
        var req=progress(3); req.setCompletedScope("全部PK样品"); req.setCompletedTime(LocalDateTime.now().minusMinutes(5));
        req.setRemainingScope("一管待回复");
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(req,row()));
        req.setRemainingScope(""); req.setCompletedTime(LocalDateTime.now().plusDays(1));
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(req,row()));
        req.setCompletedTime(LocalDateTime.now().minusDays(2));
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(req,row()));
        req.setCompletedTime(LocalDateTime.now().minusMinutes(5));
        assertDoesNotThrow(()->SampleUrgentRules.validateProgress(req,row()));
    }
    @Test void partialInboundCannotGoBackToProcessing() {
        var row=row(); row.setStatus(2);
        assertThrows(ServiceException.class,()->SampleUrgentRules.validateProgress(progress(1),row));
    }
    private SampleUrgentDO row() {
        var row=new SampleUrgentDO(); row.setStatus(1); row.setCreateTime(LocalDateTime.now().minusDays(1)); return row;
    }
    private SampleUrgentActionReqVO progress(int status) {
        var req=new SampleUrgentActionReqVO(); req.setStatus(status); req.setExceptionType("NONE"); return req;
    }
}
