package cn.iocoder.yudao.module.reagent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentBaseFlatDO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentInboundFlatDTO;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemTypeTreeDTO;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentBaseFlatMapper;
import cn.iocoder.yudao.module.reagent.dal.mysql.ReagentFlatSourceMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 老ERP同步 试剂基础数据(扁平) 同步 Service 实现
 *
 * <p>数据源：**正式库** `mes_pm_inbound_line`（join `mes_pm_inbound`，erpPushJob 已落地，与入库单明细页一致）；
 * 过滤：分类名命中试剂关键词的分类及其子孙（mes_md_item_type 层级，关键词见 REAGENT_KEYWORDS）；
 * 落地：reagent_base_flat（按 src_line_id 幂等 upsert）。
 * 不依赖 staging / 无需先跑 erpSyncJob。
 *
 * @author yudao
 */
@Service
@Slf4j
public class ReagentBaseFlatSyncServiceImpl implements ReagentBaseFlatSyncService {

    /** 从规格文本抠货号："货号：xxx"（到分隔符前） */
    private static final Pattern CAT_NO_PATTERN = Pattern.compile("货号\\s*[:：]\\s*([^，,；;\\s]+)");

    @Resource
    private ReagentFlatSourceMapper reagentFlatSourceMapper; // 正式库源（master）

    @Resource
    private ReagentBaseFlatMapper reagentBaseFlatMapper;

    @Override
    public String sync(String param) {
        Long batch = System.currentTimeMillis();
        // 0) 解析模式+关键词：默认增量（水位过滤）；"full:" 前缀=全量（忽略水位 + 停用缺失行）
        String p = StrUtil.nullToEmpty(param).trim();
        boolean full = StrUtil.startWithIgnoreCase(p, "full:") || p.equalsIgnoreCase("full");
        String keywordParam = p.equalsIgnoreCase("full") ? "" : (full ? StrUtil.removePrefixIgnoreCase(p, "full:") : p);
        String[] keywords = parseKeywords(keywordParam);
        if (keywords.length == 0) {
            log.warn("[reagent-base-flat-sync][{}] 处理参数为空，请填写分类关键词（如 试剂,标准品；全量加 full: 前缀）", batch);
            return "失败: 处理参数为空，请填写分类关键词（如 试剂,标准品；全量加 full: 前缀）";
        }
        // 1) 识别试剂分类键集合：分类名命中关键词的分类 + 其全部子孙（mes_md_item_type 层级）
        List<ReagentItemTypeTreeDTO> tree = reagentFlatSourceMapper.selectItemTypeTree();
        Set<String> catKeys = resolveReagentCategoryKeys(tree, keywords);
        if (CollUtil.isEmpty(catKeys)) {
            log.warn("[reagent-base-flat-sync][{}] 未识别到试剂分类（关键词 {}）中止；现共有分类 {} 个",
                    batch, Arrays.toString(keywords), tree.size());
            return "失败: 正式库 mes_md_item_type 未识别到试剂分类（关键词 " + Arrays.toString(keywords) + "）";
        }
        log.info("[reagent-base-flat-sync][{}] {}：识别试剂分类键 {} 个（关键词 {}）",
                batch, full ? "全量" : "增量", catKeys.size(), Arrays.toString(keywords));
        // 2) 读正式库入库单明细 → 过滤（分类命中）+ 清洗；增量=水位过滤
        LocalDateTime lastSyncTime = full ? null : reagentBaseFlatMapper.selectMaxSyncTime();
        List<ReagentInboundFlatDTO> rows = reagentFlatSourceMapper.selectInboundLineForFlat(lastSyncTime);
        List<ReagentBaseFlatDO> list = new ArrayList<>(rows.size());
        for (ReagentInboundFlatDTO r : rows) {
            String catKey = StrUtil.trimToNull(r.getCategoryKey());
            if (catKey == null || !catKeys.contains(catKey)) {
                continue;
            }
            if (StrUtil.isBlank(r.getSrcLineId())) {
                continue;
            }
            list.add(ReagentBaseFlatDO.builder()
                    .basId(StrUtil.trimToNull(r.getBasId()))
                    .reagentCode(StrUtil.trimToNull(r.getItemCode()))
                    .reagentName(StrUtil.trimToNull(r.getItemName()))
                    .vendor(StrUtil.trimToNull(r.getVendorName()))
                    .warehouse(StrUtil.trimToNull(r.getWarehouseName()))
                    .catNo(cleanCatNo(r.getSpec()))
                    .spec(StrUtil.trimToNull(r.getSpec()))
                    .lotNo(StrUtil.trimToNull(r.getBatchNo()))
                    .expireDate(cleanExpireDate(r.getExpireDate()))
                    .amountLeft(r.getAmountLeft() == null ? null : r.getAmountLeft().toPlainString()) // 剩余量=关联采购订单剩余（与入库单明细页同源）
                    .storageLocation(StrUtil.trimToNull(r.getStorageLocation()))
                    .storageTemp(null)
                    .itemCategory(StrUtil.trimToNull(r.getItemCategory()))
                    .categoryKey(catKey)
                    .srcLineId(r.getSrcLineId())
                    .srcReceiptId(StrUtil.trimToNull(r.getSrcReceiptId()))
                    .status(0)
                    .syncBatch(batch)
                    .syncTime(LocalDateTime.now())
                    .build());
        }
        // 3) 按 src_line_id 分批 upsert
        int upserted = 0;
        for (List<ReagentBaseFlatDO> part : CollUtil.split(list, 500)) {
            upserted += reagentBaseFlatMapper.batchUpsertFlat(part);
        }
        // 4) 全量：停用本次未出现的正常行（源缺失/作废 → status=1）
        int disabled = 0;
        if (full) {
            disabled = reagentBaseFlatMapper.disableMissingBatch(batch);
        }
        log.info("[reagent-base-flat-sync][{}] {}完成：正式库入库单明细 {} 行，命中试剂分类 {} 行，落地 {} 行，停用 {} 行",
                batch, full ? "全量" : "增量", rows.size(), list.size(), upserted, disabled);
        return "试剂同步完成：入库单明细 " + rows.size() + " 行，命中试剂分类 " + list.size()
                + " 行，落地 " + upserted + " 行" + (full ? "，停用源缺失 " + disabled + " 行" : "");
    }

    /** 识别试剂分类键集合：分类名命中任一关键词的节点（含自身）及其全部子孙（mes_md_item_type id/parent_id 层级） */
    private Set<String> resolveReagentCategoryKeys(List<ReagentItemTypeTreeDTO> nodes, String[] keywords) {
        Map<Long, String> codeById = new HashMap<>();
        Map<Long, List<Long>> children = new HashMap<>();
        Set<Long> seeds = new HashSet<>();
        for (ReagentItemTypeTreeDTO n : nodes) {
            if (n.getId() == null || StrUtil.isBlank(n.getCode())) {
                continue;
            }
            codeById.put(n.getId(), n.getCode());
            children.computeIfAbsent(n.getParentId() == null ? 0L : n.getParentId(), k -> new ArrayList<>()).add(n.getId());
            // 分类名命中关键词 → 作为种子，往下一并展开子孙
            if (StrUtil.isNotBlank(n.getName()) && containsKeyword(n.getName(), keywords)) {
                seeds.add(n.getId());
            }
        }
        Set<String> keys = new HashSet<>();
        if (seeds.isEmpty()) {
            return keys;
        }
        Deque<Long> stack = new ArrayDeque<>(seeds);
        Set<Long> visited = new HashSet<>();
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            if (!visited.add(cur)) {
                continue;
            }
            String code = codeById.get(cur);
            if (code != null) {
                keys.add(code);
            }
            List<Long> kids = children.get(cur);
            if (CollUtil.isNotEmpty(kids)) {
                kids.forEach(stack::push);
            }
        }
        return keys;
    }

    /** 分类名是否命中任一关键词（忽略大小写） */
    private boolean containsKeyword(String name, String[] keywords) {
        for (String kw : keywords) {
            if (StrUtil.containsIgnoreCase(name, kw)) {
                return true;
            }
        }
        return false;
    }

    /** 解析任务参数为关键词数组：逗号/顿号/分号/空格分隔；空参返回空数组（由调用方报错） */
    private String[] parseKeywords(String param) {
        if (StrUtil.isBlank(param)) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        for (String p : param.split("[,，、;；\\s]+")) {
            if (StrUtil.isNotBlank(p)) {
                list.add(p.trim());
            }
        }
        return list.toArray(new String[0]);
    }

    /** 从规格文本抠货号："货号：xxx"（到分隔符前）；NA/无/空 → null */
    private String cleanCatNo(String spec) {
        if (StrUtil.isBlank(spec)) {
            return null;
        }
        Matcher m = CAT_NO_PATTERN.matcher(spec);
        if (m.find()) {
            String v = m.group(1).trim();
            if ("NA".equalsIgnoreCase(v) || "无".equals(v) || "-".equals(v)) {
                return null;
            }
            return v;
        }
        return null;
    }

    /** 过期日期清洗：NA/无/空 → null */
    private String cleanExpireDate(String expire) {
        if (StrUtil.isBlank(expire)) {
            return null;
        }
        String t = expire.trim();
        if ("NA".equalsIgnoreCase(t) || "无".equals(t) || "-".equals(t)) {
            return null;
        }
        return t;
    }

}
