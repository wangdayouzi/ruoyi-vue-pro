package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixBasNumber;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import static cn.iocoder.yudao.module.reagent.service.MatrixInboundSupport.*;

@Component
public class MatrixInboundExporter {
    public void writeLedger(HttpServletResponse response, List<MatrixInboundDO> rows) throws IOException {
        List<List<String>> data = rows.stream().map(row -> List.of(row.getCreatedAt().toLocalDate().toString(),
                displayBas(row), formatTime(row.getAppointmentDate()), text(row.getApplicantName()),
                yesNo(row.getLimsDesigned()), statusName(row.getInboundStatus()), text(row.getExceptionRemark()),
                siteName(row.getSite()), yesNo(row.getUrgent()))).toList();
        var columns = List.of("填写日期", "基质BAS号", "预约完成系统入库日期", "预约人", "LIMS/线粒 是否design",
                "状态", "异常情况备注", "地区", "是否属于加急入库");
        // 必须先设置响应头；工作簿写入可能提交响应，此后设置下载名称和类型将不再生效。
        setHeaders(response, "基质入库登记.xlsx");
        FesodSheet.write(response.getOutputStream()).autoCloseStream(false)
                .registerWriteHandler(layout(16, 22, 30, 20, 28, 18, 60, 12, 24))
                .head(columns.stream().map(List::of).toList()).sheet("基质入库登记").doWrite(data);
    }

    public void writeBas(HttpServletResponse response, List<MatrixInboundDO> rows,
                         List<MatrixBasNumber> history) throws IOException {
        var columns = List.of("基质编号", "单据编号", "预约人", "状态", "入库登记人", "入库登记时间", "提交时间");
        var groups = List.of(new ExportGroup("SHANGHAI", "P", "SH混合基质"), new ExportGroup("SHANGHAI", "B", "SH个体基质"),
                new ExportGroup("NINGBO", "P", "NB混合基质"), new ExportGroup("NINGBO", "B", "NB个体基质"));
        setHeaders(response, "基质编号数据.xlsx");
        try (var writer = FesodSheet.write(response.getOutputStream()).autoCloseStream(false).build()) {
            for (int index = 0; index < groups.size(); index++) {
                ExportGroup group = groups.get(index);
                var data = new TreeMap<String, List<String>>();
                history.stream().filter(row -> group.site().equals(row.site()) && group.kind().equals(row.matrixKind()))
                        .forEach(row -> {
                            String number = ("SHANGHAI".equals(row.site()) ? "SH" : "") + row.basNo();
                            data.put(number, List.of(number, "", "", "", "", "", ""));
                        });
                rows.stream().filter(row -> group.site().equals(row.getSite()) && group.kind().equals(row.getMatrixKind()))
                        .forEach(row -> data.put(displayBas(row), List.of(displayBas(row), row.getId().toString(), text(row.getApplicantName()),
                                statusName(row.getInboundStatus()), text(row.getRegisteredName()),
                                formatTime(row.getRegisteredAt()), formatTime(row.getCreatedAt()))));
                var sheet = FesodSheet.writerSheet(index, group.name()).head(columns.stream().map(List::of).toList())
                        .registerWriteHandler(layout(24, 16, 24, 16, 20, 24, 24)).build();
                writer.write(List.copyOf(data.values()), sheet);
            }
        }
    }

    private static void setHeaders(HttpServletResponse response, String filename) {
        response.addHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private static SheetWriteHandler layout(int... widths) {
        return new SheetWriteHandler() {
            @Override
            public void afterSheetCreate(WriteWorkbookHolder workbook, WriteSheetHolder holder) {
                for (int index = 0; index < widths.length; index++) holder.getSheet().setColumnWidth(index, widths[index] * 256);
                holder.getSheet().createFreezePane(0, 1);
            }
        };
    }

    private record ExportGroup(String site, String kind, String name) {}
}
