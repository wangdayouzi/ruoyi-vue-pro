package cn.iocoder.yudao.module.pms.service.kb.content;

import cn.iocoder.yudao.module.pms.controller.admin.kb.content.vo.document.PmsKnowledgeDocumentUpdateReqVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentVersionDO;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentMapper;
import cn.iocoder.yudao.module.pms.dal.mysql.kb.content.PmsKnowledgeDocumentVersionMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PmsKnowledgeDocumentRenameTest {
    @Spy @InjectMocks private PmsKnowledgeDocumentServiceImpl service;
    @Mock private PmsKnowledgeDocumentMapper documentMapper;
    @Mock private PmsKnowledgeDocumentVersionMapper documentVersionMapper;
    @Mock private PmsKnowledgeContentPermissionService contentPermissionService;
    @Mock private PmsKnowledgeDocumentLabelService documentLabelService;

    @Test
    void renamingDraftDoesNotCreatePublishedHistory() {
        rename(0);
        verifyNoInteractions(documentVersionMapper);
    }

    @Test
    void renamingPublishedDocumentKeepsVersionHistory() {
        rename(1);
        verify(documentVersionMapper).insert(any(PmsKnowledgeDocumentVersionDO.class));
    }

    private void rename(int status) {
        PmsKnowledgeDocumentDO document = new PmsKnowledgeDocumentDO().setId(1L).setLibraryId(2L)
                .setPermissionId(3L).setTitle("旧名称").setContent("正文").setType(3).setVersionNo(1L).setStatus(status);
        doReturn(document).when(service).getDocument(1L, 7L);
        when(documentMapper.update(any(PmsKnowledgeDocumentDO.class), any(Wrapper.class))).thenReturn(1);
        if (status == 1) when(documentMapper.selectById(1L)).thenReturn(document);
        service.updateDocument(new PmsKnowledgeDocumentUpdateReqVO().setId(1L).setTitle("新名称").setExpectedVersionNo(1L), 7L);
    }
}
