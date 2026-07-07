package cn.iocoder.yudao.module.amf.controller.admin;

import cn.iocoder.yudao.module.amf.service.AmfOnlyOfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Map;

/**
 * OnlyOffice 回调接口
 *
 * @author yudao
 */
@Tag(name = "管理后台 - OnlyOffice 回调（无需权限校验）")
@RestController
@RequestMapping("/amf/onlyoffice")
@Slf4j
public class AmfOnlyOfficeCallbackController {

    @Resource
    private AmfOnlyOfficeService amfOnlyOfficeService;

    /**
     * OnlyOffice 编辑保存回调
     *
     * OnlyOffice 编辑完成后会回调此接口，请求体包含保存的文件下载URL
     */
    @PostMapping("/callback")
    @Operation(summary = "OnlyOffice 保存回调")
    public Map<String, Object> callback(@RequestBody Map<String, Object> body,
                                         @RequestParam("versionId") Long versionId) {
        log.info("OnlyOffice callback received: versionId={}, body={}", versionId, body);

        // 获取回调状态
        int status = (int) body.getOrDefault("status", -1);
        // status 2: 文档已编辑完成（关闭编辑窗口），需要获取最新文件
        // status 6: 正在编辑中（强制保存）
        if (status == 2 || status == 6) {
            String downloadUrl = (String) body.get("url");
            amfOnlyOfficeService.handleSaveCallback(versionId, downloadUrl);
        }

        // 返回 error: 0 表示回调处理成功
        return Map.of("error", 0);
    }

    /**
     * OnlyOffice 文档下载接口
     *
     * OnlyOffice 通过此接口获取原始文件内容
     */
    @GetMapping("/download")
    @Operation(summary = "OnlyOffice 下载文件")
    public void download(@RequestParam("versionId") Long versionId,
                          HttpServletResponse response) {
        try {
            byte[] fileBytes = amfOnlyOfficeService.getFileBytes(versionId);

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment");
            response.setContentLength(fileBytes.length);

            try (OutputStream os = response.getOutputStream()) {
                os.write(fileBytes);
                os.flush();
            }
        } catch (Exception e) {
            log.error("OnlyOffice 下载文件失败: versionId={}", versionId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
