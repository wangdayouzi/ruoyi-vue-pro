package cn.iocoder.yudao.framework.aspose.core.util;

import com.aspose.pdf.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Aspose.PDF 工具类：PDF 基础操作（页数统计等）。
 *
 * <p>从 xiningweb/ruoyi-print 的 SysPrintFileServiceImpl 抽离（getPages().size()）。</p>
 */
public class PdfUtils {

    /**
     * 统计 PDF 字节数组的页数
     */
    public static int getPageCount(byte[] pdfBytes) throws Exception {
        try (InputStream in = new ByteArrayInputStream(pdfBytes)) {
            return getPageCount(in);
        }
    }

    /**
     * 统计 PDF 输入流的页数
     */
    public static int getPageCount(InputStream in) throws Exception {
        Document doc = new Document(in);
        try {
            return doc.getPages().size();
        } finally {
            doc.close();
        }
    }

    /**
     * 统计 PDF 文件路径的页数
     */
    public static int getPageCount(String filePath) throws Exception {
        Document doc = new Document(filePath);
        try {
            return doc.getPages().size();
        } finally {
            doc.close();
        }
    }

}
