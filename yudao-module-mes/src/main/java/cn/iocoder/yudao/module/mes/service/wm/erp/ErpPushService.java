package cn.iocoder.yudao.module.mes.service.wm.erp;

/**
 * 老 ERP → MES 推送服务（阶段2）
 *
 * 读取 staging 中间库未推送行，写入 MES 仓库模块（字典/批次/流水/库存台账）。
 * 手动触发，按批处理，已推送行标记，可重复安全执行。
 *
 * @author yudao
 */
public interface ErpPushService {

    /**
     * 执行推送
     *
     * @param limit 每类单据本次最多处理行数（0=不限）
     * @return 结果摘要
     */
    String push(int limit);

}
