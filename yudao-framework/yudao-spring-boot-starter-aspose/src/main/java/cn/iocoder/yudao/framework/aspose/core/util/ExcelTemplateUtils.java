package cn.iocoder.yudao.framework.aspose.core.util;

import com.aspose.cells.PageOrientationType;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Aspose.Cells 工具类：Excel 相关处理（当前提供 Excel → PDF 转换）。
 *
 * <p>从 xiningweb/ruoyi-print 的 SysPrintHistoryExportService 抽离（convertExportExcelToPdf）。</p>
 */
public class ExcelTemplateUtils {

    /**
     * 将 Excel 字节数组转换为 PDF 字节数组。
     *
     * @param excelBytes                  Excel 文件字节（可由 EasyExcel/JXLS 模板填充后得到）
     * @param landscape                   是否横向打印
     * @param allColumnsInOnePagePerSheet 是否每页收缩所有列（压缩宽表列）
     */
    public static byte[] excelToPdf(byte[] excelBytes, boolean landscape,
                                    boolean allColumnsInOnePagePerSheet) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);
             ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream()) {
            Workbook workbook = new Workbook(inputStream);
            if (landscape) {
                for (int i = 0; i < workbook.getWorksheets().getCount(); i++) {
                    workbook.getWorksheets().get(i).getPageSetup()
                            .setOrientation(PageOrientationType.LANDSCAPE);
                }
            }
            PdfSaveOptions saveOptions = new PdfSaveOptions();
            saveOptions.setAllColumnsInOnePagePerSheet(allColumnsInOnePagePerSheet);
            workbook.save(pdfOutput, saveOptions);
            return pdfOutput.toByteArray();
        }
    }

}
