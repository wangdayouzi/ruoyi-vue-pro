package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentReceiptReqVO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 生物试剂接收单生成 Service
 *
 * @author yudao
 */
public interface ReagentReceiptService {

    /**
     * 生成生物试剂接收单：读取模板，用试剂基础信息替换占位符，输出 docx
     *
     * @param reqVO    接收单内容
     * @param response HTTP 响应（写入 docx 流）
     */
    void generateReceipt(ReagentReceiptReqVO reqVO, HttpServletResponse response) throws Exception;

}
