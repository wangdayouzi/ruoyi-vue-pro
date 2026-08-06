package cn.iocoder.yudao.module.reagent.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

/**
 * 用 Apache POI 生成 JXLS 模板（与 JXLS 运行时同源的 POI，保证兼容）
 * <p>
 * 运行本类 main 方法即可生成模板到 resources/templates/
 *
 * @author yudao
 */
public class GenTemplate {

    public static void main(String[] args) throws Exception {
        String out = "yudao-module-reagent/src/main/resources/templates/reagent-shipment-print.xlsx";
        try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(out)) {
            Sheet s = wb.createSheet("交接单");
            Drawing<?> drawing = s.createDrawingPatriarch();  // 共用同一个 Drawing，避免覆盖

            // 列宽
            int[] widths = {4600, 4200, 3600, 4000, 3600, 4000, 3400, 4000, 4800};
            for (int i = 0; i < widths.length; i++) s.setColumnWidth(i, widths[i]);

            CellStyle title = cs(wb, 16, true, H.CENTER, false);
            CellStyle section = cs(wb, 11, true, H.LEFT, false);
            CellStyle label = cs(wb, 10, true, H.RIGHT, false);
            CellStyle val = cs(wb, 10, false, H.LEFT, false);
            CellStyle valC = cs(wb, 10, false, H.CENTER, false);
            // 试剂表格用带边框的样式
            CellStyle tLabel = cs(wb, 10, true, H.RIGHT, true);
            CellStyle tVal = cs(wb, 10, false, H.LEFT, true);
            CellStyle tValC = cs(wb, 10, false, H.CENTER, true);
            CellStyle hdr = cs(wb, 10, true, H.CENTER, true);
            hdr.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            hdr.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font hdrFont = wb.createFont(); hdrFont.setBold(true); hdrFont.setColor(IndexedColors.WHITE.getIndex());
            hdr.setFont(hdrFont);

            int r = 0;

            // title + jx:area（单个 area 覆盖全部，两个 each 都放在各自区域的左上角 A 列）
            m(s, r, 0, r, 8, "Reagent Handover Sheet 试剂交接单 - 外寄", title);
            addComment(s, r, 0, "jx:area(lastCell=\"I40\")", drawing);
            r += 2;

            // shipper
            m(s, r, 0, r, 8, "以下由发货方填写 (Filled by Shipper)", section); r++;

            inf(s, r++, label, val, "发货日期:", "${shipmentDate}", "项目号:", "${projectNo}");
            inf(s, r++, label, val, "运费结算方式:", "${freightSettlement}", "运输温度:", "${transportTemp}");
            inf(s, r++, label, val, "温度记录仪:", "${hasTempLogger}", "", "");
            r++;

            // address
            m(s, r, 0, r, 8, "收发信息", section); r++;
            c(s, r, 0, "发货单位:", label); m(s, r, 1, r, 4, "${consignorUnit}", val);
            c(s, r, 5, "接收单位:", label); m(s, r, 6, r, 8, "${receiverUnit}", val); r++;
            c(s, r, 0, "发货地址:", label); m(s, r, 1, r, 4, "${consignorAddress}", val);
            c(s, r, 5, "接收地址:", label); m(s, r, 6, r, 8, "${receiverAddress}", val); r++;
            c(s, r, 0, "发货人:", label); c(s, r, 1, "${consignorName}", val);
            c(s, r, 3, "联系方式:", label); c(s, r, 4, "${consignorPhone}", val);
            c(s, r, 5, "接收人:", label); c(s, r, 6, "${receiverName}", val);
            c(s, r, 7, "联系方式:", label); c(s, r, 8, "${receiverPhone}", val); r += 2;

            // reagent detail header
            m(s, r, 0, r, 8, "试剂信息 (Reagent Details)", section); r++;
            String[] hd = {"试剂名称", "BAS ID", "供应商", "规格/浓度", "货号", "批号", "发货数量", "储存温度", "过期日期"};
            for (int i = 0; i < hd.length; i++) c(s, r, i, hd[i], hdr);
            r++;

            // JXLS each 单行区域（items 正常，保持单行）
            String lastCell = "I" + (r + 1);  // 仅当前行
            String cmt = "jx:each(items=\"items\" var=\"item\" lastCell=\"" + lastCell + "\")";
            addComment(s, r, 0, cmt, drawing);

            c(s, r, 0, "${item.reagentName}", tVal);
            c(s, r, 1, "${item.basId}", tVal);
            c(s, r, 2, "${item.vendor}", tVal);
            c(s, r, 3, "${item.content}", tVal);
            c(s, r, 4, "${item.catNo}", tVal);
            c(s, r, 5, "${item.lotNo}", tVal);
            c(s, r, 6, "${item.quantityShipped}", tValC);
            c(s, r, 7, "${item.storageTemp}", tVal);
            c(s, r, 8, "${item.expirationDate}", tVal);
            r += 2;

            // footer（单 area，无独立 area#2）
            c(s, r, 0, "制单人/日期:", label); m(s, r, 1, r, 4, "${creator} / ${creatorTime}", val); r++;
            r++;
            // 物流发货信息：标签在 A，表头在 B-E（原始样式）
            c(s, r, 0, "物流发货信息:", label);
            c(s, r, 1, "发货单号", tLabel); c(s, r, 2, "快递公司", tLabel);
            c(s, r, 3, "快递单号", tLabel); c(s, r, 4, "发货时间", tLabel);
            r++;
            // jx:each：注释在 A 列（空白，但为区域左上角），数据在 B-E
            String shipLastCell = "I" + (r + 1);  // 仅当前行
            addComment(s, r, 0, "jx:each(items=\"shipments\" var=\"ship\" lastCell=\"" + shipLastCell + "\")", drawing);
            c(s, r, 1, "${ship.shipmentNo}", tVal);
            c(s, r, 2, "${ship.expressCompany}", tVal);
            c(s, r, 3, "${ship.trackingNumber}", tVal);
            c(s, r, 4, "${ship.shipmentDate}", tVal);
            r += 2;

            // receiver（在 area#2 内，shipments 插入时会被整体下移）
            c(s, r, 0, "以下由收货方填写 (Filled by Receiver)", section); r++;
            c(s, r, 0, "试剂到达时是否处于合适储存条件？", val);
            c(s, r, 5, "□ 是 (Yes)", valC);
            c(s, r, 7, "□ 否 (No) → 详细说明:", val); r++;
            c(s, r, 0, "试剂到达时是否有损坏或缺失？", val);
            c(s, r, 5, "□ 是 (Yes) → 详细说明:", val);
            c(s, r, 7, "□ 否 (No)", valC); r += 2;
            c(s, r, 0, "接收人签名/日期:", label); c(s, r, 1, "", val); r += 2;
            c(s, r, 0,
              "注: 此表为随货文件，签收后请将此回执的扫描件发送至邮箱: jhsh_sample@accurantbio.com",
              cs(wb, 9, false, H.LEFT));

            // print setup
            s.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            s.setFitToPage(true);
            s.getPrintSetup().setFitWidth((short) 1);

            wb.write(fos);
        }
        System.out.println("Template generated: " + out);
    }

    // helpers
    enum H { LEFT, CENTER, RIGHT }

    /** 创建样式，有无边框 */
    static CellStyle cs(XSSFWorkbook wb, int sz, boolean b, H h, boolean border) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setFontHeightInPoints((short) sz); f.setBold(b); s.setFont(f);
        HorizontalAlignment a = h == H.CENTER ? HorizontalAlignment.CENTER : h == H.RIGHT ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT;
        s.setAlignment(a); s.setVerticalAlignment(VerticalAlignment.CENTER); s.setWrapText(true);
        if (border) {
            s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
            s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        }
        return s;
    }
    static CellStyle cs(XSSFWorkbook wb, int sz, boolean b, H h) { return cs(wb, sz, b, h, true); }

    static Row row(Sheet s, int r) { Row row = s.getRow(r); return row != null ? row : s.createRow(r); }

    static void c(Sheet s, int r, int col, String v, CellStyle st) {
        Cell c = row(s, r).createCell(col); c.setCellValue(v); c.setCellStyle(st);
    }

    static void m(Sheet s, int r1, int c1, int r2, int c2, String v, CellStyle st) {
        s.addMergedRegion(new CellRangeAddress(r1, r2, c1, c2));
        c(s, r1, c1, v, st);
    }

    static void inf(Sheet s, int r, CellStyle l, CellStyle v, String l1, String v1, String l2, String v2) {
        c(s, r, 0, l1, l); m(s, r, 1, r, 3, v1, v);
        c(s, r, 4, l2, l); m(s, r, 5, r, 8, v2, v);
    }

    static void addComment(Sheet s, int r, int col, String text, Drawing<?> drawing) {
        ClientAnchor a = s.getWorkbook().getCreationHelper().createClientAnchor();
        a.setCol1(col); a.setRow1(r);
        a.setCol2(col + 4); a.setRow2(r + 3);
        Comment cmt = drawing.createCellComment(a);
        cmt.setString(s.getWorkbook().getCreationHelper().createRichTextString(text));
        cmt.setVisible(true);
        Cell cell = row(s, r).getCell(col);
        if (cell == null) cell = row(s, r).createCell(col);
        cell.setCellComment(cmt);
    }
}
