package cn.iocoder.yudao.module.reagent.service;

/**
 * 老ERP同步 试剂基础数据(扁平) 同步 Service 接口
 *
 * <p>数据源：正式库 mes_pm_inbound_line；过滤：分类名命中关键词（param，含子孙）；落地：reagent_base_flat（按 src_line_id 幂等）。
 *
 * @author yudao
 */
public interface ReagentBaseFlatSyncService {

    /**
     * 同步试剂基础数据（正式库入库单明细，分类名命中 param 关键词 + 子孙展开），返回结果摘要
     *
     * @param param 分类关键词（必填），逗号/顿号/分号/空格分隔，如 "试剂,标准品"；为空则中止
     */
    String sync(String param);

}
