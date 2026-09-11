package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintReqVO;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintRespVO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 试剂标签打印 Service 接口
 *
 * @author yudao
 */
public interface ReagentLabelPrintService {

    /**
     * 根据 BASID 查询试剂标签信息（只读 SQL Server）
     *
     * @param basId 试剂编号（必填）
     * @param pageNo 页码，从 1 开始；每页固定 5 条
     * @return 匹配的试剂标签信息分页结果
     */
    PageResult<ReagentLabelPrintRespVO> getPageByBasId(String basId, Integer pageNo);

    /**
     * 生成试剂标签打印 Excel（键值对表格）
     *
     * @param reqVO    标签信息（含接收人/接收日期/备注等）
     * @param response HTTP 响应，直接写回 Excel 文件
     */
    void printLabel(ReagentLabelPrintReqVO reqVO, HttpServletResponse response);

}
