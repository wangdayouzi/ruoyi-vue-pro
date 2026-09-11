package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentLabelPrinterDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReagentLabelPrinterMapper extends BaseMapperX<ReagentLabelPrinterDO> {

    default List<ReagentLabelPrinterDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<ReagentLabelPrinterDO>()
                .eq(ReagentLabelPrinterDO::getStatus, 1)
                .orderByAsc(ReagentLabelPrinterDO::getName));
    }

    default List<ReagentLabelPrinterDO> selectByAgentCode(String agentCode) {
        return selectList(new LambdaQueryWrapperX<ReagentLabelPrinterDO>()
                .eq(ReagentLabelPrinterDO::getAgentCode, agentCode)
                .eq(ReagentLabelPrinterDO::getStatus, 1));
    }
}
