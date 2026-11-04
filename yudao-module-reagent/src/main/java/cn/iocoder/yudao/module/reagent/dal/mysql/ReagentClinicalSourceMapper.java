package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentClinicalSourceDTO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 临床方案同步源 Mapper（老样本系统 MySQL：think_clinical）
 *
 * <p>通过 @DS("yangben") 指定外部只读数据源，与本地 PostgreSQL 隔离。
 *
 * @author yudao
 */
@DS("yangben")
@Mapper
public interface ReagentClinicalSourceMapper {

    /** 全量读取 think_clinical（源表约千行，每晚全量同步 + 本地 upsert） */
    List<ReagentClinicalSourceDTO> selectAll();

}
