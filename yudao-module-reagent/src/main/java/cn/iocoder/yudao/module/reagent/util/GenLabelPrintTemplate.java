package cn.iocoder.yudao.module.reagent.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

/**
 * 试剂标签打印 Excel 模板生成器（JXLS）
 * <p>
 * 运行本类 main 方法即可生成模板到 resources/templates/ ，生成后模板独立维护。
 * 布局：两列（键列 A + 值列 B），每行一个键值对，键和值各占一个单元格。
 *
 * @author yudao
 */
public class GenLabelPrintTemplate {

    public static void main(String[] args) throws Exception {
        String out = "yudao-module-reagent/src/main/resources/templates/reagent-label-print.xlsx";
        try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(out)) {
            Sheet s = wb.createSheet("试剂标签");
            Drawing<?> drawing = s.createDrawingPatriarch();

            // 列宽：A 键列 / B 值列（可自行调整）
            s.setColumnWidth(0, 7000);
            s.setColumnWidth(1, 12000);

            CellStyle title = cs(wb, 16, true, HorizontalAlignment.CENTER);
            CellStyle label = cs(wb, 11, true, HorizontalAlignment.RIGHT);
            CellStyle val = cs(wb, 11, false, HorizontalAlignment.LEFT);

            int r = 0;

            // 标题
            m(s, r, 0, r, 1, "Reagent Label", title);
            addComment(s, r, 0, "jx:area(lastCell=\"B10\")", drawing);
            r += 2;

            // 键值对（每行：键 1 格 + 值 1 格，与标签模板一致）
            kv(s, r++, label, val, "Name:", "${name}");
            kv(s, r++, label, val, "BASID:", "${basId}");
            kv(s, r++, label, val, "LOT:", "${batchNo}");
            kv(s, r++, label, val, "Storage condition(unopen):", "${storageCondition}");
            kv(s, r++, label, val, "Received date:", "${receiveDate}");
            kv(s, r++, label, val, "Received by:", "${receiverName}");
            kv(s, r++, label, val, "Exp.Date(unopen):", "${expireDate}");
            kv(s, r, label, val, "Remark:", "${remark}");

            // 打印设置：A4 竖版，宽度缩放到一页
            s.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            s.setFitToPage(true);
            s.getPrintSetup().setFitWidth((short) 1);

            wb.write(fos);
        }
        System.out.println("Template generated: " + out);
    }

    // ==================== helpers ====================

    static CellStyle cs(XSSFWorkbook wb, int sz, boolean b, HorizontalAlignment h) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) sz);
        f.setBold(b);
        s.setFont(f);
        s.setAlignment(h);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setWrapText(true);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    static Row row(Sheet s, int r) {
        Row row = s.getRow(r);
        return row != null ? row : s.createRow(r);
    }

    static void c(Sheet s, int r, int col, String v, CellStyle st) {
        Cell cell = row(s, r).createCell(col);
        cell.setCellValue(v);
        cell.setCellStyle(st);
    }

    static void m(Sheet s, int r1, int c1, int r2, int c2, String v, CellStyle st) {
        s.addMergedRegion(new CellRangeAddress(r1, r2, c1, c2));
        c(s, r1, c1, v, st);
    }

    /** 键值对行：键占 A 列，值占 B 列 */
    static void kv(Sheet s, int r, CellStyle l, CellStyle v, String labelText, String valueExpr) {
        c(s, r, 0, labelText, l);
        c(s, r, 1, valueExpr, v);
    }

    static void addComment(Sheet s, int r, int col, String text, Drawing<?> drawing) {
        ClientAnchor a = s.getWorkbook().getCreationHelper().createClientAnchor();
        a.setCol1(col);
        a.setRow1(r);
        a.setCol2(col + 4);
        a.setRow2(r + 3);
        Comment cmt = drawing.createCellComment(a);
        cmt.setString(s.getWorkbook().getCreationHelper().createRichTextString(text));
        cmt.setVisible(true);
        Cell cell = row(s, r).getCell(col);
        if (cell == null) {
            cell = row(s, r).createCell(col);
        }
        cell.setCellComment(cmt);
    }
}
