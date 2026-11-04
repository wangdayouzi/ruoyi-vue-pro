package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalSourceDTO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemSourceDTO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentClinicalMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentClinicalSourceMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentItemMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentItemSourceMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.reagent.enums.ErrorCodeConstants.REAGENT_BASIC_DATA_SOURCE_EMPTY;

/**
 * 临床方案 / 项目号 基础数据同步 Service 实现
 *
 * <p>全量读取老样本系统 MySQL，按 id upsert 到本地 PostgreSQL；源表已不存在的行标记 status=1（停用），
 * 不物理删除，避免历史引用丢失。临床方案的 is_auto_receive 属于本地维护字段，同步不覆盖。
 *
 * <p>注意：不要给该方法加 @Transactional。源库通过 @DS("yangben") 切换数据源，
 * 在事务中动态数据源切换不会生效。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentBasicDataSyncServiceImpl implements ReagentBasicDataSyncService {

    private static final ZoneId ZONE_ASIA_SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final int UPSERT_BATCH_SIZE = 500;

    @Resource
    private ReagentClinicalSourceMapper reagentClinicalSourceMapper;

    @Resource
    private ReagentItemSourceMapper reagentItemSourceMapper;

    @Resource
    private ReagentClinicalMapper reagentClinicalMapper;

    @Resource
    private ReagentItemMapper reagentItemMapper;

    @Override
    public String sync() {
        long batch = System.currentTimeMillis();
        // 1) 先读源库（@DS 切换到 yangben）；读失败直接抛出，本地不受影响
        List<ReagentClinicalSourceDTO> clinicalSources = reagentClinicalSourceMapper.selectAll();
        List<ReagentItemSourceDTO> itemSources = reagentItemSourceMapper.selectAll();
        if (CollUtil.isEmpty(clinicalSources) && CollUtil.isEmpty(itemSources)) {
            log.error("[reagent-basic-data-sync][{}] 源库 think_clinical / think_items 均为空，中止同步", batch);
            throw exception(REAGENT_BASIC_DATA_SOURCE_EMPTY);
        }

        // 2) 临床方案 upsert + 停用源表已删除的行
        int clinicalUpserted = 0;
        int clinicalDisabled = 0;
        List<ReagentClinicalDO> clinicalRows = new ArrayList<>();
        if (CollUtil.isNotEmpty(clinicalSources)) {
            for (ReagentClinicalSourceDTO source : clinicalSources) {
                if (source == null || source.getId() == null) {
                    continue;
                }
                clinicalRows.add(toClinicalDO(source, batch));
            }
        }
        if (CollUtil.isNotEmpty(clinicalRows)) {
            for (List<ReagentClinicalDO> part : CollUtil.split(clinicalRows, UPSERT_BATCH_SIZE)) {
                clinicalUpserted += reagentClinicalMapper.batchUpsertClinical(part);
            }
            clinicalDisabled = reagentClinicalMapper.disableMissingBatch(batch);
        } else {
            log.warn("[reagent-basic-data-sync][{}] 源库 think_clinical 为空，跳过临床方案同步（不移除本地数据）", batch);
        }

        // 3) 项目号 upsert + 停用源表已删除的行
        int itemUpserted = 0;
        int itemDisabled = 0;
        List<ReagentItemDO> itemRows = new ArrayList<>();
        if (CollUtil.isNotEmpty(itemSources)) {
            for (ReagentItemSourceDTO source : itemSources) {
                if (source == null || source.getId() == null) {
                    continue;
                }
                itemRows.add(toItemDO(source, batch));
            }
        }
        if (CollUtil.isNotEmpty(itemRows)) {
            for (List<ReagentItemDO> part : CollUtil.split(itemRows, UPSERT_BATCH_SIZE)) {
                itemUpserted += reagentItemMapper.batchUpsertItem(part);
            }
            itemDisabled = reagentItemMapper.disableMissingBatch(batch);
        } else {
            log.warn("[reagent-basic-data-sync][{}] 源库 think_items 为空，跳过项目号同步（不移除本地数据）", batch);
        }

        log.info("[reagent-basic-data-sync][{}] 完成：临床方案源 {} 行，落地 {} 行，停用 {} 行；项目号源 {} 行，落地 {} 行，停用 {} 行",
                batch, clinicalSources.size(), clinicalUpserted, clinicalDisabled,
                itemSources.size(), itemUpserted, itemDisabled);
        return "同步完成：临床方案 " + clinicalSources.size() + " 行（落地 " + clinicalUpserted
                + "，停用 " + clinicalDisabled + "）；项目号 " + itemSources.size() + " 行（落地 " + itemUpserted
                + "，停用 " + itemDisabled + "）";
    }

    private ReagentClinicalDO toClinicalDO(ReagentClinicalSourceDTO source, long batch) {
        return ReagentClinicalDO.builder()
                .id(source.getId())
                .name(StrUtil.nullToEmpty(source.getName()))
                .number(StrUtil.nullToEmpty(source.getNumber()))
                .sponsorId(source.getSponsorId())
                .isUpload(source.getIsUpload() == null ? 0 : source.getIsUpload())
                .effectiveDate(source.getEffectiveDate())
                .versionNumber(source.getVersionNumber())
                // isAutoReceive 不赋值：新增走库默认 0，更新时 upsert 不写该列，保留本地维护值
                .srcCreateTime(toDateTime(source.getCreateTime()))
                .srcUpdateTime(toDateTime(source.getUpdateTime()))
                .status(0)
                .syncBatch(batch)
                .syncTime(LocalDateTime.now())
                .build();
    }

    private ReagentItemDO toItemDO(ReagentItemSourceDTO source, long batch) {
        return ReagentItemDO.builder()
                .id(source.getId())
                .cityId(source.getCityId())
                .baseId(StrUtil.nullToEmpty(source.getBaseId()))
                .clinicalId(source.getClinicalId())
                .clinicalName(source.getClinicalName())
                .clinicalNumber(source.getClinicalNumber())
                .personId(source.getPersonId())
                .typeId(source.getTypeId())
                .isAging(source.getIsAging() == null ? 1 : source.getIsAging())
                .reviewStatus(source.getReviewStatus() == null ? 0 : source.getReviewStatus())
                .reviewer(source.getReviewer())
                .isReport(source.getIsReport() == null ? 0 : source.getIsReport())
                .isPm(source.getIsPm() == null ? 0 : source.getIsPm())
                .srcCreateTime(toDateTime(source.getCreateTime()))
                .srcUpdateTime(toDateTime(source.getUpdateTime()))
                .status(0)
                .syncBatch(batch)
                .syncTime(LocalDateTime.now())
                .build();
    }

    /** 源表 unix 秒 → Asia/Shanghai 时间；空/0 → null */
    private LocalDateTime toDateTime(Long epochSeconds) {
        if (epochSeconds == null || epochSeconds <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZONE_ASIA_SHANGHAI);
    }

}
