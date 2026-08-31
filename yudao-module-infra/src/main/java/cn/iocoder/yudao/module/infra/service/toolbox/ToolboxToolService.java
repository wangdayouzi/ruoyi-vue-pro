package cn.iocoder.yudao.module.infra.service.toolbox;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolPageReqVO;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.toolbox.ToolboxToolDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * IT 工具箱工具 Service 接口
 *
 * @author 芋道源码
 */
public interface ToolboxToolService {

    /**
     * 创建工具
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createToolboxTool(@Valid ToolboxToolSaveReqVO createReqVO);

    /**
     * 更新工具
     *
     * @param updateReqVO 更新信息
     */
    void updateToolboxTool(@Valid ToolboxToolSaveReqVO updateReqVO);

    /**
     * 删除工具
     *
     * @param id 编号
     */
    void deleteToolboxTool(Long id);

    /**
     * 获得工具
     *
     * @param id 编号
     * @return 工具
     */
    ToolboxToolDO getToolboxTool(Long id);

    /**
     * 获得工具分页
     *
     * @param pageReqVO 分页查询
     * @return 工具分页
     */
    PageResult<ToolboxToolDO> getToolboxToolPage(ToolboxToolPageReqVO pageReqVO);

    /**
     * 获得启用中的工具列表（首页展示用）
     *
     * @return 工具列表
     */
    List<ToolboxToolDO> getEnabledToolboxToolList();

    /**
     * 记录下载次数 +1
     *
     * @param id 编号
     */
    void recordToolboxToolDownload(Long id);

}
