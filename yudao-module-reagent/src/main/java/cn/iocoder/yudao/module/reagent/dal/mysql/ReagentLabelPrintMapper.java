package cn.iocoder.yudao.module.reagent.dal.mysql;

import cn.iocoder.yudao.module.reagent.controller.admin.vo.ReagentLabelPrintRespVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试剂标签打印 Mapper
 *
 * 查询的是外部只读 SQL Server 数据源（PM 系统），通过 @DS 指定数据源
 *
 * @author yudao
 */
@DS("sqlserver")
@Mapper
public interface ReagentLabelPrintMapper {

    Long selectCountByBasId(@Param("basId") String basId);

    List<ReagentLabelPrintRespVO> selectPageByBasId(@Param("basId") String basId,
                                                     @Param("offset") Integer offset,
                                                     @Param("pageSize") Integer pageSize);

}
