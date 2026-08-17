package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintRespVO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentLabelPrintMapper;
import cn.iocoder.yudao.module.reagent.util.PrintUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 试剂标签打印 Service 实现类
 *
 * @author yudao
 */
@Slf4j
@Service
public class ReagentLabelPrintServiceImpl implements ReagentLabelPrintService {

    private static final String TEMPLATE = "reagent-label-print.xlsx";

    @Resource
    private ReagentLabelPrintMapper reagentLabelPrintMapper;

    @Override
    public List<ReagentLabelPrintRespVO> getByBasId(String basId) {
        log.info("[getByBasId] 查询试剂标签信息，basId = {}", basId);
        return reagentLabelPrintMapper.selectByBasId(basId);
    }

    @Override
    public void printLabel(ReagentLabelPrintReqVO reqVO, HttpServletResponse response) {
        Map<String, Object> data = PrintUtil.builder()
                .put("name", reqVO.getName())
                .put("basId", reqVO.getBasId())
                .put("batchNo", reqVO.getBatchNo())
                .put("storageCondition", reqVO.getStorageCondition())
                .put("expireDate", reqVO.getExpireDate())
                .put("receiverName", reqVO.getReceiverName())
                .put("receiveDate", reqVO.getReceiveDate())
                .put("remark", reqVO.getRemark())
                .build();
        String fileName = "试剂标签-" + reqVO.getBasId() + ".xlsx";
        log.info("[printLabel] 生成试剂标签 Excel，basId = {}", reqVO.getBasId());
        PrintUtil.render(response, TEMPLATE, data, fileName);
    }

}
