package cn.iocoder.yudao.module.reagent.service;

/**
 * 临床方案 / 项目号 基础数据同步 Service 接口
 *
 * <p>数据源：老样本系统 MySQL（数据源 yangben，库 yangben_com）的 think_clinical / think_items；
 * 落地：本地 PostgreSQL 的 reagent_clinical / reagent_item（按 id 幂等 upsert，源表缺失行停用）。
 *
 * @author yudao
 */
public interface ReagentBasicDataSyncService {

    /**
     * 全量同步临床方案 + 项目号，返回结果摘要
     */
    String sync();

}
