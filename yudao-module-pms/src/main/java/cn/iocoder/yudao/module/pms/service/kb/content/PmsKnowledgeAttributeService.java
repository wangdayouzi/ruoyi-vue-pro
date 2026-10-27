package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeValueDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeAttributeMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeAttributeValueMapper;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
public class PmsKnowledgeAttributeService {
    private static final Set<String> TYPES = Set.of("text", "textarea", "number", "date", "select", "multiselect", "user");
    @Resource private PmsKnowledgeAttributeMapper mapper;
    @Resource private PmsKnowledgeAttributeValueMapper valueMapper;
    @Resource private PmsKnowledgeLibraryMemberService memberService;
    @Resource private ObjectMapper objectMapper;

    public List<PmsKnowledgeAttributeDO> list(Long libraryId, Long userId) {
        memberService.validateLibraryReadable(libraryId, userId);
        return mapper.listByLibrary(libraryId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveDefinition(PmsKnowledgeAttributeDO input, Long userId) {
        memberService.validateLibraryAdmin(input.getLibraryId(), userId);
        if (input.getFieldCode() == null || !input.getFieldCode().matches("[a-z][a-z0-9_]{1,63}"))
            throw invalidParamException("属性编码须为小写字母、数字或下划线");
        if (input.getFieldName() == null || input.getFieldName().isBlank()) throw invalidParamException("属性名称不能为空");
        if (input.getFieldType() == null || !TYPES.contains(input.getFieldType())) throw invalidParamException("不支持的属性类型");
        if (input.getRequired() == null) input.setRequired(false);
        if (input.getEnabled() == null) input.setEnabled(true);
        if (input.getSort() == null) input.setSort(0);
        if ("select".equals(input.getFieldType()) || "multiselect".equals(input.getFieldType())) {
            if (input.getOptionsJson() == null || parseOptions(input.getOptionsJson()).isEmpty())
                throw invalidParamException("选择类型至少需要一个选项");
        } else if (input.getOptionsJson() != null && !input.getOptionsJson().isBlank()) parseOptions(input.getOptionsJson());
        for (PmsKnowledgeAttributeDO existing : mapper.listByLibrary(input.getLibraryId())) {
            if (existing.getFieldCode().equals(input.getFieldCode()) && !existing.getId().equals(input.getId()))
                throw invalidParamException("属性编码已存在");
        }
        if (input.getId() == null) {
            mapper.insert(input);
            return input.getId();
        }
        PmsKnowledgeAttributeDO old = mapper.selectById(input.getId());
        if (old == null || !old.getLibraryId().equals(input.getLibraryId())) throw invalidParamException("属性不存在");
        if (!old.getFieldCode().equals(input.getFieldCode()) || !old.getFieldType().equals(input.getFieldType()))
            throw invalidParamException("已创建属性的编码和类型不可修改");
        mapper.updateById(input);
        return input.getId();
    }

    public Map<String, String> values(Long documentId, Long libraryId) {
        Map<String, String> result = new HashMap<>();
        for (PmsKnowledgeAttributeValueDO value : valueMapper.listByDocument(documentId)) {
            PmsKnowledgeAttributeDO def = mapper.selectById(value.getAttributeId());
            if (def != null && Boolean.TRUE.equals(def.getEnabled()) && def.getLibraryId().equals(libraryId)
                    && value.getValueText() != null && !value.getValueText().isBlank())
                result.put(def.getFieldCode(), value.getValueText());
        }
        return result;
    }

    /** Keep source-library metadata attached to the document after a cross-library move. */
    public List<Map<String, Object>> carriedValues(Long documentId, Long libraryId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (PmsKnowledgeAttributeValueDO value : valueMapper.listByDocument(documentId)) {
            PmsKnowledgeAttributeDO def = mapper.selectById(value.getAttributeId());
            if (def == null || def.getLibraryId().equals(libraryId)
                    || value.getValueText() == null || value.getValueText().isBlank()) continue;
            result.add(Map.of("fieldName", def.getFieldName(), "fieldCode", def.getFieldCode(),
                    "sourceLibraryId", def.getLibraryId(), "value", value.getValueText()));
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveValues(Long documentId, Long libraryId, Map<String, String> input) {
        Map<String, String> values = input == null ? Map.of() : input;
        List<PmsKnowledgeAttributeDO> defs = mapper.listByLibrary(libraryId);
        for (String key : values.keySet()) {
            if (defs.stream().noneMatch(d -> d.getFieldCode().equals(key) && Boolean.TRUE.equals(d.getEnabled())))
                throw invalidParamException("未知或已停用的属性：" + key);
        }
        Map<Long, PmsKnowledgeAttributeValueDO> existing = new HashMap<>();
        for (PmsKnowledgeAttributeValueDO row : valueMapper.listByDocument(documentId)) existing.put(row.getAttributeId(), row);
        for (PmsKnowledgeAttributeDO def : defs) {
            if (!Boolean.TRUE.equals(def.getEnabled())) continue;
            String value = values.get(def.getFieldCode());
            if (Boolean.TRUE.equals(def.getRequired()) && (value == null || value.isBlank()
                    || "multiselect".equals(def.getFieldType()) && parseOptions(value).isEmpty()))
                throw invalidParamException("请填写属性：" + def.getFieldName());
            if (value != null && !value.isBlank()) validateValue(def, value);
            PmsKnowledgeAttributeValueDO old = existing.get(def.getId());
            if (value == null || value.isBlank()) {
                // Keep the row to avoid colliding with the document/attribute unique key after soft deletion.
                if (old != null) valueMapper.updateById(new PmsKnowledgeAttributeValueDO().setId(old.getId()).setValueText(""));
            } else if (old == null) {
                valueMapper.insert(new PmsKnowledgeAttributeValueDO().setDocumentId(documentId)
                        .setAttributeId(def.getId()).setValueText(value));
            } else {
                valueMapper.updateById(new PmsKnowledgeAttributeValueDO().setId(old.getId()).setValueText(value));
            }
        }
    }

    private List<String> parseOptions(String json) {
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(json);
            if (node == null || !node.isArray()) throw invalidParamException("选项必须是 JSON 字符串数组");
            List<String> options = new ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : node) {
                if (!item.isTextual() || item.asText().isBlank()) throw invalidParamException("选项必须是非空字符串");
                options.add(item.asText());
            }
            return options;
        }
        catch (Exception e) { throw invalidParamException("选项必须是 JSON 字符串数组"); }
    }

    private void validateValue(PmsKnowledgeAttributeDO def, String value) {
        try {
            switch (def.getFieldType()) {
                case "number" -> new BigDecimal(value);
                case "date" -> LocalDate.parse(value);
                case "user" -> Long.parseLong(value);
                case "select" -> {
                    if (!parseOptions(def.getOptionsJson()).contains(value)) throw invalidParamException("选项无效");
                }
                case "multiselect" -> {
                    List<String> selected = parseOptions(value);
                    if (!parseOptions(def.getOptionsJson()).containsAll(selected)) throw invalidParamException("选项无效");
                }
                default -> { if (value.length() > 2000) throw invalidParamException("属性值过长"); }
            }
        } catch (RuntimeException e) {
            throw invalidParamException("属性“" + def.getFieldName() + "”的值不符合类型要求");
        }
    }
}
