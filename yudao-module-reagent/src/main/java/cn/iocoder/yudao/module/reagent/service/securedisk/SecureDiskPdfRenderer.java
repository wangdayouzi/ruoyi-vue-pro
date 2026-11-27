package cn.iocoder.yudao.module.reagent.service.securedisk;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.aspose.words.Document;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.SaveFormat;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SecureDiskPdfRenderer {
    private static final Map<String, String> INSTRUMENT_CHECKBOX = Map.ofEntries(
            Map.entry("EMR", "EMR"), Map.entry("MSD-001", "MSD_001"), Map.entry("MSD-002", "MSD_002"),
            Map.entry("FCM", "FCM"), Map.entry("INF", "INF"), Map.entry("QPCR", "QPCR"), Map.entry("TSV", "TSV"),
            Map.entry("Flowjo(fcs)", "Flowjo_fcs"), Map.entry("Flowjo(wsp,excel,pdf)", "Flowjo_wsp"),
            Map.entry("CytoFLEX", "CytoFLEX"), Map.entry("DPCR", "DPCR"), Map.entry("DESS", "DESS"),
            Map.entry("SAS", "SAS"), Map.entry("SIM", "SIM"), Map.entry("ESP-001", "ESP"),
            Map.entry("Cytek(zip)", "Cytek_zip"), Map.entry("Cytek(pdf,csv)", "Cytek_pdfcsv"), Map.entry("LUM", "LUM"));
    public byte[] render(byte[] template, String project, String accounts,
                                 String instrument, String path, List<String> permissions) throws Exception {
        Document document = new Document(new ByteArrayInputStream(template));
        replace(document, token("project_id"), project);
        replace(document, token("user_name"), accounts);
        String pathExpression = "paths | join('\\n')";
        replace(document, token(pathExpression), path);
        replace(document, "SD-A-V2-" + token("form_year") + "-[ \u00a0]*", "SD-A-V2-____-______");

        String selected = INSTRUMENT_CHECKBOX.getOrDefault(instrument, "Other");
        for (String key : INSTRUMENT_CHECKBOX.values()) {
            replace(document, token("is_" + key), key.equals(selected) ? "☒" : "☐");
        }
        replace(document, token("is_Other"), selected.equals("Other") ? "☒" : "☐");
        replace(document, token("other"), selected.equals("Other") ? instrument : "");
        fillPermissions(document, permissions, "read", "R", 5);
        fillPermissions(document, permissions, "write", "W", 6);

        document.updatePageLayout();
        if (document.getPageCount() != 1) {
            throw invalid("表单超出一页，请减少目录数量或联系管理员检查模板");
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        document.save(result, SaveFormat.PDF);
        return result.toByteArray();
    }

    private static void fillPermissions(Document document, List<String> permissions,
                                        String key, String label, int count) throws Exception {
        for (int i = 1; i <= count; i++) {
            replace(document, token("is_" + key + "0" + i),
                    permissions.contains(label + i) ? "☒" : "☐");
        }
    }

    private static String token(String expression) {
        return "\\{\\{\\s*" + Pattern.quote(expression) + "\\s*}}";
    }

    private static void replace(Document document, String expression, String value) throws Exception {
        String replacement = value.replace("&", "&&").replace("\n", "&l");
        FindReplaceOptions options = new FindReplaceOptions();
        options.setMatchCase(true);
        if (document.getRange().replace(Pattern.compile(expression), replacement, options) != 1) {
            throw invalid("正式模板缺少必要的填表占位符，或占位符重复");
        }
    }

    private static ServiceException invalid(String message) {
        return new ServiceException(400, message);
    }
}
