package cn.iocoder.yudao.module.reagent.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 试剂交接单 Excel 模板生成器
 * <p>
 * 运行本类 main 方法即可生成模板文件，放在 resources/templates/ 下供 poi-tl 使用。
 * 生成后本类可删除，模板独立维护。
 *
 * @author yudao
 */
public class ReagentTemplateGenerator {

    private static final String OUTPUT_PATH = "yudao-module-reagent/src/main/resources/templates/reagent-shipment-print.xlsx";

    public static void main(String[] args) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("交接单");

            // 列宽
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 3200);
            sheet.setColumnWidth(2, 3000);
            sheet.setColumnWidth(3, 3000);
            sheet.setColumnWidth(4, 3200);
            sheet.setColumnWidth(5, 3200);
            sheet.setColumnWidth(6, 2600);
            sheet.setColumnWidth(7, 3200);
            sheet.setColumnWidth(8, 4400);

            CellStyle titleStyle = createStyle(wb, 16, true, HorizontalAlignment.CENTER);
            CellStyle subtitleStyle = createStyle(wb, 10, false, HorizontalAlignment.LEFT);
            CellStyle labelStyle = createStyle(wb, 10, true, HorizontalAlignment.RIGHT);
            CellStyle valueStyle = createStyle(wb, 10, false, HorizontalAlignment.LEFT);
            CellStyle headerStyle = createStyle(wb, 10, true, HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle dataStyle = createStyle(wb, 10, false, HorizontalAlignment.CENTER);
            CellStyle sectionStyle = createStyle(wb, 11, true, HorizontalAlignment.LEFT);

            int rowIdx = 0;

            // ========== 标题 ==========
            setCell(sheet, rowIdx, 0, titleStyle, "Reagent Handover Sheet 试剂交接单 - 外寄");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));
            rowIdx += 2;

            // ========== 发货信息 ==========
            setCell(sheet, rowIdx, 0, sectionStyle, "以下由发货方填写 (Filled by Shipper)");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));
            rowIdx++;

            addInfoRow(sheet, rowIdx++, labelStyle, valueStyle,
                    "发货单号:", "{{shipmentNo}}", "发货日期:", "{{shipmentDate}}");
            addInfoRow(sheet, rowIdx++, labelStyle, valueStyle,
                    "快递公司:", "{{expressCompany}}", "快递单号:", "{{trackingNumber}}");
            addInfoRow(sheet, rowIdx++, labelStyle, valueStyle,
                    "项目号:", "{{projectNo}}", "运输温度:", "{{transportTemp}}");
            addInfoRow(sheet, rowIdx++, labelStyle, valueStyle,
                    "运费结算方式:", "{{freightSettlement}}", "温度记录仪:", "{{hasTempLogger}}");
            rowIdx++;

            // ========== 收发方信息 ==========
            setCell(sheet, rowIdx, 0, sectionStyle, "收发信息");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));
            rowIdx++;

            // 发货方
            setCell(sheet, rowIdx, 0, labelStyle, "发货单位:");
            setCell(sheet, rowIdx, 1, valueStyle, "{{consignorUnit}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
            setCell(sheet, rowIdx, 5, labelStyle, "接收单位:");
            setCell(sheet, rowIdx, 6, valueStyle, "{{receiverUnit}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 6, 8));
            rowIdx++;

            setCell(sheet, rowIdx, 0, labelStyle, "发货地址:");
            setCell(sheet, rowIdx, 1, valueStyle, "{{consignorAddress}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
            setCell(sheet, rowIdx, 5, labelStyle, "接收地址:");
            setCell(sheet, rowIdx, 6, valueStyle, "{{receiverAddress}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 6, 8));
            rowIdx++;

            setCell(sheet, rowIdx, 0, labelStyle, "发货人:");
            setCell(sheet, rowIdx, 1, valueStyle, "{{consignorName}}");
            setCell(sheet, rowIdx, 3, labelStyle, "联系方式:");
            setCell(sheet, rowIdx, 4, valueStyle, "{{consignorPhone}}");
            setCell(sheet, rowIdx, 5, labelStyle, "接收人:");
            setCell(sheet, rowIdx, 6, valueStyle, "{{receiverName}}");
            setCell(sheet, rowIdx, 8, labelStyle, "联系方式:");
            setCell(sheet, rowIdx, 8, valueStyle, "{{receiverPhone}}");
            rowIdx += 2;

            // ========== 试剂明细表 ==========
            setCell(sheet, rowIdx, 0, sectionStyle, "试剂信息 (Reagent Details)");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));
            rowIdx++;

            String[] headers = {"试剂名称", "BAS ID", "供应商", "规格/浓度", "货号", "批号", "发货数量", "储存温度", "过期日期"};
            for (int i = 0; i < headers.length; i++) {
                setCell(sheet, rowIdx, i, headerStyle, headers[i]);
            }
            rowIdx++;

            // poi-tl 表格循环标记
            setCell(sheet, rowIdx, 0, dataStyle, "{{#items}}");
            setCell(sheet, rowIdx, 1, dataStyle, "{{reagentName}}");
            setCell(sheet, rowIdx, 2, dataStyle, "{{basId}}");
            setCell(sheet, rowIdx, 3, dataStyle, "{{vendor}}");
            setCell(sheet, rowIdx, 4, dataStyle, "{{content}}");
            setCell(sheet, rowIdx, 5, dataStyle, "{{catNo}}");
            setCell(sheet, rowIdx, 6, dataStyle, "{{lotNo}}");
            setCell(sheet, rowIdx, 7, dataStyle, "{{quantityShipped}}");
            setCell(sheet, rowIdx, 8, dataStyle, "{{storageTemp}}");
            rowIdx++;

            setCell(sheet, rowIdx, 0, dataStyle, "{{/items}}");
            rowIdx += 2;

            // ========== 底部签章 ==========
            setCell(sheet, rowIdx, 0, labelStyle, "制单人/日期:");
            setCell(sheet, rowIdx, 2, valueStyle, "{{creator}} / {{creatorTime}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 5));
            rowIdx++;

            setCell(sheet, rowIdx, 0, labelStyle, "物流发货信息:");
            setCell(sheet, rowIdx, 2, valueStyle, "{{logisticsInfo}}");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 8));
            rowIdx += 2;

            // ========== 收货方确认区 ==========
            setCell(sheet, rowIdx, 0, sectionStyle, "以下由收货方填写 (Filled by Receiver)");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));
            rowIdx++;

            setCell(sheet, rowIdx, 0, valueStyle, "试剂到达时是否处于合适储存条件？");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 3));
            setCell(sheet, rowIdx, 4, valueStyle, "□ 是 (Yes)");
            setCell(sheet, rowIdx, 6, valueStyle, "□ 否 (No) → 详细说明:");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 6, 8));
            rowIdx++;

            setCell(sheet, rowIdx, 0, valueStyle, "试剂到达时是否有损坏或缺失？");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 3));
            setCell(sheet, rowIdx, 4, valueStyle, "□ 是 (Yes) → 详细说明:");
            setCell(sheet, rowIdx, 6, valueStyle, "□ 否 (No)");
            rowIdx += 2;

            setCell(sheet, rowIdx, 0, labelStyle, "接收人签名/日期:");
            setCell(sheet, rowIdx, 3, valueStyle, "__________________");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 3, 8));
            rowIdx += 2;

            setCell(sheet, rowIdx, 0, valueStyle, "注: 此表为随货文件，签收后请将此回执的扫描件发送至邮箱: jhsh_sample@accurantbio.com");
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 8));

            // 打印设置
            sheet.setPrintGridlines(false);
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(false);
            sheet.setFitToPage(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);

            try (FileOutputStream fos = new FileOutputStream(OUTPUT_PATH)) {
                wb.write(fos);
            }
            System.out.println("模板已生成: " + OUTPUT_PATH);
        }
    }

    // ==================== 辅助方法 ====================

    private static void addInfoRow(Sheet sheet, int rowIdx,
                                    CellStyle labelStyle, CellStyle valueStyle,
                                    String label1, String val1, String label2, String val2) {
        setCell(sheet, rowIdx, 0, labelStyle, label1);
        setCell(sheet, rowIdx, 1, valueStyle, val1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 3));
        setCell(sheet, rowIdx, 4, labelStyle, label2);
        setCell(sheet, rowIdx, 5, valueStyle, val2);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 5, 8));
    }

    private static CellStyle createStyle(XSSFWorkbook wb, int fontSize, boolean bold,
                                          HorizontalAlignment align) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private static void setCell(Sheet sheet, int rowIdx, int colIdx, CellStyle style, String value) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) row = sheet.createRow(rowIdx);
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
