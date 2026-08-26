package cn.iocoder.yudao.framework.aspose.core.util;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Aspose.Slides 工具类：PPT/PPTX 基础操作（当前提供 PPTX → PDF 转换）。
 */
public class SlidesUtils {

    /**
     * PPTX → PDF
     */
    public static void pptxToPdf(InputStream pptxIn, OutputStream pdfOut) throws Exception {
        Presentation pres = null;
        try {
            pres = new Presentation(pptxIn);
            pres.save(pdfOut, SaveFormat.Pdf);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }

}
