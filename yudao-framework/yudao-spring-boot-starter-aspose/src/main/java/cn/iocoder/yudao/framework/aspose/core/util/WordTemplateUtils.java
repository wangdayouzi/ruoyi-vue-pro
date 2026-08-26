package cn.iocoder.yudao.framework.aspose.core.util;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FindReplaceOptions;
import com.aspose.words.IReplacingCallback;
import com.aspose.words.NodeType;
import com.aspose.words.ReplaceAction;
import com.aspose.words.ReplacingArgs;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspose.Words 工具类：Word 模板占位符替换、页码/总页数域、图片/二维码插入、导出 PDF。
 *
 * <p>从 xiningweb/ruoyi-print 的 SysPrintFileServiceImpl 抽离，去掉业务依赖，仅保留 Aspose 核心能力。</p>
 */
public class WordTemplateUtils {

    /**
     * 加载 Word 文档
     */
    public static Document load(InputStream in) throws Exception {
        return new Document(in);
    }

    /**
     * 解除文档保护（无密码/空密码传 null 或空串）
     */
    public static void unprotect(Document doc, String password) throws Exception {
        doc.unprotect(password);
    }

    /**
     * 普通文本占位符替换（如 {companyName}）
     */
    public static void replaceText(Document doc, String oldValue, String newValue) throws Exception {
        doc.getRange().replace(oldValue, newValue != null ? newValue : "");
    }

    /**
     * 将 _PG_ 标记替换为 PAGE 域、_TP_ 标记替换为 NUMPAGES 域。
     * 使用 FindReplaceOptions + IReplacingCallback，由 Aspose 引擎安全遍历，
     * 天然支持跨 Run 边界的占位符匹配。
     */
    public static void replaceWithPageFields(Document doc) throws Exception {
        replaceMarkerWithField(doc, "_PG_", "PAGE");
        replaceMarkerWithField(doc, "_TP_", "NUMPAGES");
    }

    /**
     * 将文档中的占位符替换为图片（如二维码）。
     * 注意：必须先调用本方法插入 PAGE/NUMPAGES 域，再 updateFields() + updatePageLayout()，
     * 顺序反了导出 PDF 时会残留 _PG_/_TP_ 字面量。
     */
    public static void replaceTextWithImage(Document doc, String placeholder, byte[] imageBytes,
                                            int width, int height) throws Exception {
        FindReplaceOptions options = new FindReplaceOptions();
        options.setReplacingCallback(new IReplacingCallback() {
            @Override
            public int replacing(ReplacingArgs args) throws Exception {
                // 从当前匹配节点动态获取所属 Document，斩断对外部 doc 变量的闭包引用
                @SuppressWarnings("unchecked")
                Document currentDoc = (Document) args.getMatchNode().getDocument();
                DocumentBuilder builder = new DocumentBuilder(currentDoc);
                // 必须将光标移到匹配节点位置，否则图片插入到文档开头
                builder.moveTo(args.getMatchNode());
                // 指定图片尺寸，防止大图撑破表格单元格
                builder.insertImage(imageBytes, width, height);
                return ReplaceAction.REPLACE;
            }
        });
        doc.getRange().replace(placeholder, "", options);
    }

    /**
     * 生成二维码 PNG 字节数组（ZXing）
     */
    public static byte[] generateQRCode(String content, int width, int height) throws Exception {
        com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
        Map<com.google.zxing.EncodeHintType, Object> hints = new HashMap<>();
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 0); // 设置二维码边距为 0
        com.google.zxing.common.BitMatrix bitMatrix =
                qrCodeWriter.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(bitMatrix);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }

    /**
     * 将文档中的占位符替换为二维码图片
     */
    public static void replaceTextWithQRCode(Document doc, String placeholder, String qrContent,
                                             int qrSize, int insertWidth, int insertHeight) throws Exception {
        byte[] qrImageBytes = generateQRCode(qrContent, qrSize, qrSize);
        replaceTextWithImage(doc, placeholder, qrImageBytes, insertWidth, insertHeight);
    }

    /**
     * 接受所有修订并删除批注（避免导出 PDF 时右侧出现批注栏）
     */
    public static void acceptRevisionsAndClearComments(Document doc) throws Exception {
        doc.acceptAllRevisions();
        try {
            doc.getChildNodes(NodeType.COMMENT, true).clear();
        } catch (Exception ignored) {
        }
    }

    /**
     * 更新域并重新分页布局（必须用在插入 PAGE/NUMPAGES 域之后）
     */
    public static void updateFieldsAndLayout(Document doc) throws Exception {
        doc.updateFields();
        doc.updatePageLayout();
    }

    /**
     * 保存为 PDF 到输出流
     */
    public static void saveAsPdf(Document doc, OutputStream out) throws Exception {
        doc.save(out, SaveFormat.PDF);
    }

    /**
     * 保存为 PDF 到文件路径
     */
    public static void saveAsPdf(Document doc, String filePath) throws Exception {
        doc.save(filePath, SaveFormat.PDF);
    }

    /**
     * 将标记替换为 PAGE / NUMPAGES 域（支持跨 Run 匹配）
     */
    private static void replaceMarkerWithField(Document doc, String marker, String fieldCode) throws Exception {
        FindReplaceOptions options = new FindReplaceOptions();
        options.setReplacingCallback(new IReplacingCallback() {
            @Override
            public int replacing(ReplacingArgs args) throws Exception {
                Run matchRun = (Run) args.getMatchNode();
                String fullText = matchRun.getText();
                int idx = fullText.indexOf(marker);
                if (idx < 0) {
                    return ReplaceAction.SKIP;
                }
                String leftText = fullText.substring(0, idx);
                String rightText = fullText.substring(idx + marker.length());

                @SuppressWarnings("unchecked")
                Document currentDoc = (Document) matchRun.getDocument();
                DocumentBuilder builder = new DocumentBuilder(currentDoc);

                if (!rightText.isEmpty()) {
                    matchRun.setText(leftText);
                    // deepClone 保留原始 Run 的全部字体格式，避免右侧文字退化
                    Run rightRun = (Run) matchRun.deepClone(true);
                    rightRun.setText(rightText);
                    matchRun.getParentNode().insertAfter(rightRun, matchRun);
                    builder.moveTo(rightRun);
                } else {
                    matchRun.setText(leftText);
                    if (!leftText.isEmpty()) {
                        Run endMarker = (Run) matchRun.deepClone(true);
                        endMarker.setText("");
                        matchRun.getParentNode().insertAfter(endMarker, matchRun);
                        builder.moveTo(endMarker);
                    } else {
                        builder.moveTo(matchRun);
                    }
                }

                builder.insertField(fieldCode);
                // 已在回调中手动裁剪 marker 文本，返回 SKIP 避免引擎二次修改
                return ReplaceAction.SKIP;
            }
        });
        doc.getRange().replace(marker, "", options);
    }

}
