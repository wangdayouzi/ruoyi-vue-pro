package cn.iocoder.yudao.module.infra.dal.mysql.toolbox;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.controller.admin.toolbox.vo.ToolboxToolPageReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.toolbox.ToolboxToolDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ToolboxToolMapper extends BaseMapperX<ToolboxToolDO> {

    default PageResult<ToolboxToolDO> selectPage(ToolboxToolPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ToolboxToolDO>()
                .likeIfPresent(ToolboxToolDO::getName, reqVO.getName())
                .eqIfPresent(ToolboxToolDO::getCategory, reqVO.getCategory())
                .eqIfPresent(ToolboxToolDO::getStatus, reqVO.getStatus())
                .orderByAsc(ToolboxToolDO::getSort).orderByDesc(ToolboxToolDO::getId));
    }

    /**
     * 获得指定状态下的工具列表，按照 sort 升序、id 降序
     */
    default List<ToolboxToolDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<ToolboxToolDO>()
                .eq(ToolboxToolDO::getStatus, status)
                .orderByAsc(ToolboxToolDO::getSort).orderByDesc(ToolboxToolDO::getId));
    }

    /**
     * 下载次数 +1
     */
    default int updateDownloadCount(Long id) {
        return update(null, new LambdaUpdateWrapper<ToolboxToolDO>()
                .eq(ToolboxToolDO::getId, id)
                .setSql("download_count = download_count + 1"));
    }

}
