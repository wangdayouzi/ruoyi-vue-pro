package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.reagent.dal.dataobject.MatrixInboundDO;
import cn.iocoder.yudao.module.reagent.enums.MatrixInboundStatusEnum;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

final class MatrixInboundSupport {
    static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    static final String QUERY_PERMISSION = "reagent:matrix-inbound:query";
    static final String REGISTER_PERMISSION = "reagent:matrix-inbound:register";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MatrixInboundSupport() {}

    static long tenantId() {
        // 本系统关闭多租户时没有请求租户上下文；沿用系统通知模块的内部默认值。
        return Objects.requireNonNullElse(TenantContextHolder.getTenantId(), 1L);
    }
    static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }
    static String displayBas(MatrixInboundDO row) {
        return ("SHANGHAI".equals(row.getSite()) ? "SH" : "") + row.getBasNo();
    }
    static String siteName(String site) {
        return "SHANGHAI".equals(site) ? "上海" : "宁波";
    }
    static String templateCode(String site) {
        return "matrix_inbound_" + site.toLowerCase(Locale.ROOT);
    }
    static String statusName(Integer status) {
        return MatrixInboundStatusEnum.of(status).getLabel();
    }
    static String text(String value) {
        return Objects.requireNonNullElse(value, "");
    }
    static String yesNo(Boolean value) {
        return value == null ? "" : value ? "是" : "否";
    }
    static String formatTime(LocalDateTime value) {
        return value == null ? "" : TIME_FORMAT.format(value);
    }
}
