package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeAttributeValueDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeAttributeMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeAttributeValueMapper;
import cn.iocoder.yudao.module.pms.service.kb.library.PmsKnowledgeLibraryMemberService;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmsKnowledgeAttributeServiceTest {
    @InjectMocks private PmsKnowledgeAttributeService service;
    @Mock private PmsKnowledgeAttributeMapper attributeMapper;
    @Mock private PmsKnowledgeAttributeValueMapper valueMapper;
    @Mock private PmsKnowledgeLibraryMemberService memberService;
    @Spy private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void invalidSelectOptionsAndMissingTypeReturnBusinessErrors() {
        for (String options : List.of("null", "{}", "[1]")) {
            ServiceException error = assertThrows(ServiceException.class, () -> service.saveDefinition(
                    new PmsKnowledgeAttributeDO().setLibraryId(10L).setFieldCode("test_field")
                            .setFieldName("选项").setFieldType("select").setOptionsJson(options), 7L));
            assertEquals(400, error.getCode());
        }
        assertEquals(400, assertThrows(ServiceException.class, () -> service.saveDefinition(
                new PmsKnowledgeAttributeDO().setLibraryId(10L).setFieldCode("test_field").setFieldName("选项"), 7L)).getCode());
    }

    @Test
    void crossLibraryMoveKeepsSourceValuesReadOnlyAndSeparatesTargetFields() {
        when(valueMapper.listByDocument(9L)).thenReturn(List.of(
                new PmsKnowledgeAttributeValueDO().setAttributeId(1L).setValueText("EXT-001"),
                new PmsKnowledgeAttributeValueDO().setAttributeId(2L).setValueText("2026-10-23")));
        when(attributeMapper.selectById(1L)).thenReturn(new PmsKnowledgeAttributeDO().setId(1L)
                .setLibraryId(10L).setFieldCode("source_ref").setFieldName("来源编号").setEnabled(true));
        when(attributeMapper.selectById(2L)).thenReturn(new PmsKnowledgeAttributeDO().setId(2L)
                .setLibraryId(20L).setFieldCode("received_on").setFieldName("接收日期").setEnabled(true));

        assertEquals("2026-10-23", service.values(9L, 20L).get("received_on"));
        assertEquals(1, service.values(9L, 20L).size());
        assertEquals("EXT-001", service.carriedValues(9L, 20L).get(0).get("value"));
        assertEquals("来源编号", service.carriedValues(9L, 20L).get(0).get("fieldName"));
    }

    @Test
    void searchReturnsOnlyEnabledFieldsForRequestedDocuments() {
        when(attributeMapper.listByLibrary(10L)).thenReturn(List.of(
                new PmsKnowledgeAttributeDO().setId(1L).setFieldCode("issuer")
                        .setEnabled(true),
                new PmsKnowledgeAttributeDO().setId(3L).setFieldCode("old_issuer")
                        .setEnabled(false)));
        Set<Long> documentIds = Set.of(8L, 9L, 11L);
        when(valueMapper.listByDocumentsAndAttributes(documentIds, Set.of(1L))).thenReturn(List.of(
                new PmsKnowledgeAttributeValueDO().setDocumentId(8L).setAttributeId(1L).setValueText("发行单位甲"),
                new PmsKnowledgeAttributeValueDO().setDocumentId(9L).setAttributeId(1L).setValueText("发行单位乙"),
                new PmsKnowledgeAttributeValueDO().setDocumentId(11L).setAttributeId(1L).setValueText(" ")));

        assertEquals(Map.of(8L, Map.of("issuer", "发行单位甲"), 9L, Map.of("issuer", "发行单位乙")),
                service.searchValues(10L, documentIds));
    }

    @Test
    void allLibrariesSearchDoesNotLoadAttributes() {
        assertEquals(Map.of(), service.searchValues(null, Set.of(8L)));
        verifyNoInteractions(attributeMapper, valueMapper);
    }
}
