package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentItemPageReqVO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentItemMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目号（老样本系统同步）Service 实现
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentItemServiceImpl implements ReagentItemService {

    @Resource
    private ReagentItemMapper reagentItemMapper;

    @Override
    public PageResult<ReagentItemDO> getItemPage(ReagentItemPageReqVO reqVO) {
        return reagentItemMapper.selectPage(reqVO);
    }

    @Override
    public List<ReagentItemDO> listOptions(Long clinicalId, String keyword) {
        return reagentItemMapper.selectOptions(clinicalId, keyword);
    }

}
