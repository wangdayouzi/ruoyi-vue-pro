package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.SampleUrgentPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.SampleUrgentDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.SampleUrgentMapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SampleUrgentPageQueryTest {
    @BeforeAll static void metadata() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SampleUrgentDO.class);
    }

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapperX<SampleUrgentDO> query(SampleUrgentPageReqVO req) {
        var mapper = mock(SampleUrgentMapper.class, CALLS_REAL_METHODS);
        var captured = new AtomicReference<LambdaQueryWrapperX<SampleUrgentDO>>();
        doAnswer(invocation -> {
            captured.set(invocation.getArgument(1));
            return new PageResult<SampleUrgentDO>(List.of(), 0L);
        }).when(mapper).selectPage(any(PageParam.class), any(Wrapper.class));
        mapper.page(req, 1L);
        return captured.get();
    }

    @Test void defaultsKeepUnfinishedOverdueFirst() {
        String sql = query(new SampleUrgentPageReqVO()).getSqlSegment();
        assertTrue(sql.contains("CASE WHEN status IN (0,1,2) AND deadline < CURRENT_TIMESTAMP"));
        assertTrue(sql.endsWith("id DESC"));
    }

    @Test void bothTimeColumnsSortAcrossPagesWithStableTieBreaker() {
        for (String field : List.of("deadline", "createTime")) {
            for (String order : List.of("asc", "desc")) {
                var req = new SampleUrgentPageReqVO();
                req.setPageNo(2);
                req.setSortField(field);
                req.setSortOrder(order);
                String sql = query(req).getSqlSegment();
                String column = "createTime".equals(field) ? "create_time" : "deadline";
                assertTrue(sql.endsWith("ORDER BY " + column + " " + order.toUpperCase() + ",id DESC"), sql);
                assertFalse(sql.contains("CASE WHEN"));
            }
        }
    }

    @Test void timeRangesCombineWithExistingSiteStatusAndOverdueFilters() {
        var req = new SampleUrgentPageReqVO();
        var start = LocalDateTime.of(2026, 11, 1, 0, 0);
        var end = start.plusDays(2).minusSeconds(1);
        req.setDeadline(new LocalDateTime[]{start, end});
        req.setCreateTime(new LocalDateTime[]{start.minusDays(5), start.minusDays(1)});
        req.setInboundSite("SHANGHAI");
        req.setStatus(1);
        req.setView("OVERDUE");
        var wrapper = query(req);
        String sql = wrapper.getSqlSegment();
        assertTrue(sql.contains("deadline BETWEEN"), sql);
        assertTrue(sql.contains("create_time BETWEEN"), sql);
        assertTrue(sql.contains("inbound_site ="), sql);
        assertTrue(sql.contains("status ="), sql);
        assertTrue(sql.contains("deadline <"), sql);
        assertTrue(wrapper.getParamNameValuePairs().containsValue(start));
        assertTrue(wrapper.getParamNameValuePairs().containsValue(end));
        assertTrue(wrapper.getParamNameValuePairs().containsValue(start.minusDays(5)));
    }

    @Test void unknownSortInputCannotEnterSql() {
        var req = new SampleUrgentPageReqVO();
        req.setSortField("deadline; DROP TABLE sample_urgent");
        req.setSortOrder("asc");
        String sql = query(req).getSqlSegment();
        assertFalse(sql.contains("DROP"));
        assertTrue(sql.contains("CASE WHEN"));
    }
}
