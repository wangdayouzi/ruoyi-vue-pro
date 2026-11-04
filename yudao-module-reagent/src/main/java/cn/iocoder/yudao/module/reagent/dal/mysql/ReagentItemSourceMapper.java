package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentItemSourceDTO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 项目号同步源 Mapper（老样本系统 MySQL：think_items）
 *
 * <p>通过 @DS("yangben") 指定外部只读数据源，与本地 PostgreSQL 隔离。
 *
 * @author yudao
 */
@DS("yangben")
@Mapper
public interface ReagentItemSourceMapper {

    /** 全量读取 think_items（left join think_clinical 取方案名称/编号） */
    List<ReagentItemSourceDTO> selectAll();

}
