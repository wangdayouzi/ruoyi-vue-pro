package cn.iocoder.yudao.module.system.sync.erp.dal.mysql;

import cn.iocoder.yudao.module.system.sync.erp.dto.ErpInboundDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpItemCategoryDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineQtyDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpRequisitionDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnInDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnOutDTO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * staging 中间库 Mapper —— 独立库 yudao_stg_pm
 *
 * 写入：批量 upsert（ON CONFLICT DO UPDATE），按源唯一键去重。
 *
 * @author yudao
 */
@DS("stgpm")
@InterceptorIgnore(tenantLine = "true")
@Mapper
public interface StgPmMapper {

    /** 批量 upsert 入库行 */
    int batchUpsertInbound(@Param("list") List<ErpInboundDTO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 领料行 */
    int batchUpsertRequisition(@Param("list") List<ErpRequisitionDTO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 退料行 */
    int batchUpsertReturnIn(@Param("list") List<ErpReturnInDTO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 采购退货行 */
    int batchUpsertReturnOut(@Param("list") List<ErpReturnOutDTO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 物料分类（sdpm001 全量） */
    int batchUpsertItemCategory(@Param("list") List<ErpItemCategoryDTO> list, @Param("syncBatch") Long syncBatch);

    /** 批量 upsert 采购订单明细（sdpm014 全量） */
    int batchUpsertPoLine(@Param("list") List<ErpPoLineDTO> list, @Param("syncBatch") Long syncBatch);

    /** 清空采购订单明细（窗口快照，先清再写） */
    int deleteAllPoLine();

    /** 读取全部采购订单明细（落地采购订单正式表用，幂等） */
    List<ErpPoLineDTO> selectPoLineAll();

    /** 按订单行聚合 领用数量（staging：领料→入库行→订单行） */
    List<ErpPoLineQtyDTO> selectRequisitionQtyByPoLine();

    /** 按订单行聚合 退料数量（staging：退料→领料行→入库行→订单行） */
    List<ErpPoLineQtyDTO> selectReturnInQtyByPoLine();

    /** 按订单行聚合 采购退货数量（staging：采购退货→入库行→订单行） */
    List<ErpPoLineQtyDTO> selectReturnOutQtyByPoLine();

    /* ---------------- 阶段2 推送读取/标记 ---------------- */

    /** 读取待推送分类（pushed=0） */
    List<ErpItemCategoryDTO> selectItemCategoryForPush(@Param("limit") int limit);

    /** 标记分类已推送 */
    int markItemCategoryPushed(@Param("ids") List<Long> ids, @Param("pushBatch") Long pushBatch);

    /** 读取待推送入库行（pushed=0） */
    List<ErpInboundDTO> selectInboundForPush(@Param("limit") int limit);

    /** 读取全部入库行（落地采购入库单正式表用，幂等） */
    List<ErpInboundDTO> selectInboundAll();

    /** 物料→分类 映射（去重，用于给已推送物料回填分类） */
    List<ErpInboundDTO> selectItemCategoryDistinct();

    /** 读取待推送领料行（pushed=0） */
    List<ErpRequisitionDTO> selectRequisitionForPush(@Param("limit") int limit);

    /** 读取待推送退料行（pushed=0） */
    List<ErpReturnInDTO> selectReturnInForPush(@Param("limit") int limit);

    /** 读取待推送采购退货行（pushed=0） */
    List<ErpReturnOutDTO> selectReturnOutForPush(@Param("limit") int limit);

    /** 标记入库行已推送 */
    int markInboundPushed(@Param("ids") List<Long> ids, @Param("pushBatch") Long pushBatch);

    /** 标记领料行已推送 */
    int markRequisitionPushed(@Param("ids") List<Long> ids, @Param("pushBatch") Long pushBatch);

    /** 标记退料行已推送 */
    int markReturnInPushed(@Param("ids") List<Long> ids, @Param("pushBatch") Long pushBatch);

    /** 标记采购退货行已推送 */
    int markReturnOutPushed(@Param("ids") List<Long> ids, @Param("pushBatch") Long pushBatch);

    /* ---------------- 推送日志 ---------------- */

    int insertPushLog(@Param("pushBatch") Long pushBatch);

    int updatePushLogSuccess(@Param("pushBatch") Long pushBatch, @Param("result") String result);

    int updatePushLogFail(@Param("pushBatch") Long pushBatch, @Param("errorMsg") String errorMsg);

    /** 记录同步开始 */
    int insertSyncLog(@Param("syncBatch") Long syncBatch, @Param("backfillDays") Integer backfillDays);

    /** 同步成功收尾 */
    int updateSyncLogSuccess(@Param("syncBatch") Long syncBatch, @Param("result") String result);

    /** 同步失败收尾 */
    int updateSyncLogFail(@Param("syncBatch") Long syncBatch, @Param("errorMsg") String errorMsg);

}
