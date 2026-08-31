package cn.iocoder.yudao.module.infra.convert.toolbox;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolRespVO;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.toolbox.ToolboxToolDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ToolboxToolConvert {

    ToolboxToolConvert INSTANCE = Mappers.getMapper(ToolboxToolConvert.class);

    ToolboxToolDO convert(ToolboxToolSaveReqVO bean);

    ToolboxToolRespVO convert(ToolboxToolDO bean);

    PageResult<ToolboxToolRespVO> convertPage(PageResult<ToolboxToolDO> page);

    List<ToolboxToolRespVO> convertList(List<ToolboxToolDO> list);

}
