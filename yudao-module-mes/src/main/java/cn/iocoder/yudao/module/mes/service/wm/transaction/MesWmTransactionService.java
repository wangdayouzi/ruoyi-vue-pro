package cn.iocoder.yudao.module.mes.service.wm.transaction;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mes.controller.admin.wm.transaction.vo.MesWmTransactionPageReqVO;
import cn.iocoder.yudao.module.mes.dal.dataobject.wm.transaction.MesWmTransactionDO;
import cn.iocoder.yudao.module.mes.service.wm.transaction.dto.MesWmTransactionSaveReqDTO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 库存事务流水 Service 接口
 *
 * 校验 → 库存台账更新 → 事务流水插入。
 */
public interface MesWmTransactionService {

    /**
     * 创建库存事务（含校验 + 库存更新 + 插入流水）
     *
     * @param reqDTO 事务数据
     * @return 事务流水编号
     */
    Long createTransaction(@Valid MesWmTransactionSaveReqDTO reqDTO);

    /**
     * 批量创建库存事务
     *
     * @param reqDTOs 事务数据列表
     */
    void createTransactionList(List<MesWmTransactionSaveReqDTO> reqDTOs);

    /**
     * 获得库存事务流水
     *
     * @param id 流水编号
     * @return 流水
     */
    MesWmTransactionDO getTransaction(Long id);

    /**
     * 获得库存事务流水分页
     *
     * @param pageReqVO 分页查询
     * @return 流水分页
     */
    PageResult<MesWmTransactionDO> getTransactionPage(MesWmTransactionPageReqVO pageReqVO);

}
