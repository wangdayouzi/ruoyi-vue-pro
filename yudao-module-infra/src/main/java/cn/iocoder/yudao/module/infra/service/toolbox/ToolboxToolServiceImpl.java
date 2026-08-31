package cn.iocoder.yudao.module.infra.service.toolbox;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolSaveReqVO;
import cn.iocoder.yudao.module.infra.convert.toolbox.ToolboxToolConvert;
import cn.iocoder.yudao.module.infra.dal.dataobject.toolbox.ToolboxToolDO;
import cn.iocoder.yudao.module.infra.dal.mysql.toolbox.ToolboxToolMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.TOOLBOX_TOOL_NOT_EXISTS;

/**
 * IT 工具箱工具 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
@Validated
public class ToolboxToolServiceImpl implements ToolboxToolService {

    @Resource
    private ToolboxToolMapper toolboxToolMapper;

    @Override
    public Long createToolboxTool(ToolboxToolSaveReqVO createReqVO) {
        ToolboxToolDO toolboxTool = ToolboxToolConvert.INSTANCE.convert(createReqVO);
        if (toolboxTool.getDownloadCount() == null) {
            toolboxTool.setDownloadCount(0);
        }
        toolboxToolMapper.insert(toolboxTool);
        return toolboxTool.getId();
    }

    @Override
    public void updateToolboxTool(ToolboxToolSaveReqVO updateReqVO) {
        validateToolboxToolExists(updateReqVO.getId());
        ToolboxToolDO updateObj = ToolboxToolConvert.INSTANCE.convert(updateReqVO);
        toolboxToolMapper.updateById(updateObj);
    }

    @Override
    public void deleteToolboxTool(Long id) {
        validateToolboxToolExists(id);
        toolboxToolMapper.deleteById(id);
    }

    @Override
    public ToolboxToolDO getToolboxTool(Long id) {
        return toolboxToolMapper.selectById(id);
    }

    @Override
    public PageResult<ToolboxToolDO> getToolboxToolPage(ToolboxToolPageReqVO pageReqVO) {
        return toolboxToolMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ToolboxToolDO> getEnabledToolboxToolList() {
        return toolboxToolMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public void recordToolboxToolDownload(Long id) {
        toolboxToolMapper.updateDownloadCount(id);
    }

    private void validateToolboxToolExists(Long id) {
        if (toolboxToolMapper.selectById(id) == null) {
            throw exception(TOOLBOX_TOOL_NOT_EXISTS);
        }
    }

}
