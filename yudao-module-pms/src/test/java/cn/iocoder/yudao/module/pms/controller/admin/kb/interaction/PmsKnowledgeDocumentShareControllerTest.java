package cn.iocoder.yudao.module.pms.controller.admin.kb.interaction;

import cn.iocoder.yudao.module.pms.service.kb.content.PmsKnowledgeOfficePreviewService;
import cn.iocoder.yudao.module.pms.controller.admin.kb.interaction.vo.share.PmsKnowledgeDocumentSharePublicRespVO;
import cn.iocoder.yudao.module.pms.dal.dataobject.kb.content.PmsKnowledgeDocumentDO;
import cn.iocoder.yudao.module.pms.enums.kb.content.PmsKnowledgeDocumentTypeEnum;
import cn.iocoder.yudao.module.pms.service.kb.interaction.PmsKnowledgeDocumentShareService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link PmsKnowledgeDocumentShareController} 的单元测试类
 *
 * @author 芋道源码
 */
public class PmsKnowledgeDocumentShareControllerTest {

    @Test
    public void testGetDocumentByToken_fileUsesDefaultPreviewUrl() {
        // mock 数据
        String token = "share-token";
        String rawUrl = "https://file.example.com/private/demo.pdf";
        String previewUrl = "https://file.example.com/private/demo.pdf?signature=temporary";
        PmsKnowledgeDocumentDO document = new PmsKnowledgeDocumentDO().setId(1L).setTitle("测试文件")
                .setType(PmsKnowledgeDocumentTypeEnum.FILE.getType()).setContent(rawUrl).setFileType("pdf");
        PmsKnowledgeDocumentShareService shareService = mock(PmsKnowledgeDocumentShareService.class);
        PmsKnowledgeOfficePreviewService officePreviewService = mock(PmsKnowledgeOfficePreviewService.class);
        when(shareService.getDocumentByShareToken(token, null)).thenReturn(document);
        var share = new cn.iocoder.yudao.module.pms.dal.dataobject.kb.interaction.PmsKnowledgeDocumentShareDO().setToken(token);
        when(shareService.getActiveDocumentShare(document.getId())).thenReturn(share);
        when(officePreviewService.isPreviewAvailable()).thenReturn(true);
        when(officePreviewService.isPreviewSourceRegistered(document)).thenReturn(true);
        when(officePreviewService.buildSharePreviewUrl(document, share)).thenReturn(previewUrl);
        PmsKnowledgeDocumentShareController controller = new PmsKnowledgeDocumentShareController();
        ReflectionTestUtils.setField(controller, "documentShareService", shareService);
        ReflectionTestUtils.setField(controller, "officePreviewService", officePreviewService);

        // 调用
        PmsKnowledgeDocumentSharePublicRespVO result = controller.getDocumentByToken(token, null).getData();

        // 断言
        assertNull(result.getContent());
        assertEquals(previewUrl, result.getPreviewUrl());
        verify(officePreviewService).buildSharePreviewUrl(document, share);
    }

}
