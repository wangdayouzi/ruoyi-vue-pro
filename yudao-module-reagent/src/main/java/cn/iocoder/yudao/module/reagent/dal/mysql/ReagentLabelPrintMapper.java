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

    /**
     * 根据 BASID 查询试剂标签信息
     *
     * @param basId 试剂编号（必填）
     * @return 匹配的试剂标签信息列表（一个 BASID 可能对应多个批号）
     */
    List<ReagentLabelPrintRespVO> selectByBasId(@Param("basId") String basId);

}
