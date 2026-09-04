package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.aspose.core.util.WordTemplateUtils;
import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentReceiptReqVO;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 生物试剂接收单生成 Service 实现：读取「生物试剂接收单模版.docx」，
 * 用试剂基础信息替换 {{xxx}} 占位符后输出 docx 下载。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentReceiptServiceImpl implements ReagentReceiptService {

    private static final String TEMPLATE = "templates/生物试剂接收单模版.docx";

    @Override
    public void generateReceipt(ReagentReceiptReqVO reqVO, HttpServletResponse response) throws Exception {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("{{name}}", nvl(reqVO.getName()));
        values.put("{{basId}}", nvl(reqVO.getBasId()));
        values.put("{{vendor}}", nvl(reqVO.getVendor()));
        values.put("{{brand}}", nvl(reqVO.getBrand()));
        values.put("{{receiveDate}}", nvl(reqVO.getReceiveDate()));
        values.put("{{qty}}", nvl(reqVO.getQty()));
        values.put("{{contentPerUnit}}", nvl(reqVO.getContentPerUnit()));
        values.put("{{lotNo}}", nvl(reqVO.getLotNo()));
        values.put("{{catNo}}", nvl(reqVO.getCatNo()));
        values.put("{{storageLocation}}", nvl(reqVO.getStorageLocation()));
        values.put("{{storageTemp}}", nvl(reqVO.getStorageTemp()));
        values.put("{{expireDate}}", nvl(reqVO.getExpireDate()));
        values.put("{{comment}}", nvl(reqVO.getComment()));

        try (InputStream in = ReagentReceiptServiceImpl.class.getClassLoader().getResourceAsStream(TEMPLATE)) {
            if (in == null) {
                throw new RuntimeException("找不到生物试剂接收单模板: " + TEMPLATE);
            }
            Document doc = WordTemplateUtils.load(in);
            for (Map.Entry<String, String> e : values.entrySet()) {
                WordTemplateUtils.replaceText(doc, e.getKey(), e.getValue());
            }
            String fileName = "生物试剂接收单-" + nvl(reqVO.getBasId()) + ".docx";
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            doc.save(response.getOutputStream(), SaveFormat.DOCX);
            log.info("[generateReceipt] 生成生物试剂接收单 basId={}", reqVO.getBasId());
        }
    }

    private static String nvl(String v) {
        return v == null ? "" : v;
    }

}
