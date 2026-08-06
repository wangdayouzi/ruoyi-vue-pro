package cn.iocoder.yudao.module.reagent.util;

import lombok.extern.slf4j.Slf4j;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JXLS 3.x Excel 打印工具类
 *
 * @author yudao
 */
@Slf4j
public class PrintUtil {

    private static final String TEMPLATE_BASE = "templates/";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 渲染模板 → 输出 Excel
     */
    public static void render(HttpServletResponse response,
                              String template,
                              Map<String, Object> data,
                              String outputName) {
        String templatePath = TEMPLATE_BASE + template;
        try (InputStream is = loadTemplate(templatePath);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            if (data != null) {
                log.info("[print] context keys: {}", data.keySet());
                Object items = data.get("items");
                if (items instanceof List) {
                    log.info("[print] items count: {}", ((List<?>) items).size());
                }
                Object shipments = data.get("shipments");
                if (shipments instanceof List) {
                    log.info("[print] shipments count: {}", ((List<?>) shipments).size());
                }
            }

            // JXLS 3.x 新 API：JxlsPoiTemplateFillerBuilder
            JxlsPoiTemplateFillerBuilder.newInstance()
                    .withTemplate(is)
                    .buildAndFill(data, () -> bos);

            writeResponse(response, bos.toByteArray(), outputName);

        } catch (Exception e) {
            log.error("[print] JXLS 渲染失败 template={}", template, e);
            throw new RuntimeException("打印失败: " + e.getMessage(), e);
        }
    }

    // ==================== 数据构建辅助 ====================

    public static DataBuilder builder() {
        return new DataBuilder();
    }

    public static String fmtDate(Object dateObj) {
        if (dateObj == null) return "";
        try {
            if (dateObj instanceof java.time.LocalDateTime ldt) return ldt.format(DATE_FMT);
            if (dateObj instanceof java.time.LocalDate ld) return ld.format(DATE_FMT);
            if (dateObj instanceof java.util.Date d)
                return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
            if (dateObj instanceof Number n)
                return java.time.Instant.ofEpochMilli(n.longValue())
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
            return dateObj.toString();
        } catch (Exception e) { return dateObj.toString(); }
    }

    public static String fmtDateTime(Object dateObj) {
        if (dateObj == null) return "";
        try {
            if (dateObj instanceof java.time.LocalDateTime ldt) return ldt.format(DATETIME_FMT);
            if (dateObj instanceof java.time.LocalDate ld) return ld.atStartOfDay().format(DATETIME_FMT);
            if (dateObj instanceof java.util.Date d)
                return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(DATETIME_FMT);
            if (dateObj instanceof Number n)
                return java.time.Instant.ofEpochMilli(n.longValue())
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(DATETIME_FMT);
            return dateObj.toString();
        } catch (Exception e) { return dateObj.toString(); }
    }

    public static String fmtYorN(Boolean b) { return Boolean.TRUE.equals(b) ? "是" : "否"; }
    public static String fmtYorN(Integer i) { return Integer.valueOf(1).equals(i) ? "有" : "无"; }
    public static String fmtBlk(Object obj) { return obj == null ? "" : obj.toString(); }

    // ==================== 内部 ====================

    private static InputStream loadTemplate(String path) {
        InputStream is = PrintUtil.class.getClassLoader().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("模板文件不存在: " + path);
        return new BufferedInputStream(is);
    }

    private static void writeResponse(HttpServletResponse response, byte[] data, String fileName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentLength(data.length);
            response.getOutputStream().write(data);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("写入响应失败", e);
        }
    }

    // ==================== 内部类 ====================

    public static class DataBuilder {
        private final Map<String, Object> map = new LinkedHashMap<>();
        public DataBuilder put(String key, Object value) { map.put(key, value != null ? value : ""); return this; }
        public DataBuilder putAll(Map<String, Object> others) { if (others != null) map.putAll(others); return this; }
        public Map<String, Object> build() { return map; }
    }
}
