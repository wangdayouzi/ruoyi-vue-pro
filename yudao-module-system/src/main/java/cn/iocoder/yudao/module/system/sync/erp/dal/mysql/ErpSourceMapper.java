package cn.iocoder.yudao.module.system.sync.erp.dal.mysql;

import cn.iocoder.yudao.module.system.sync.erp.dto.ErpInboundDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpItemCategoryDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpItemDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpPoLineDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpRequisitionDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnInDTO;
import cn.iocoder.yudao.module.system.sync.erp.dto.ErpReturnOutDTO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老 ERP(PM) 源数据 Mapper —— 只读 SQL Server
 *
 * 所有查询：全表 WITH (NOLOCK)、按日期窗口走索引、批量一次性拉取，避免反复查卡源库。
 *
 * @author yudao
 */
@DS("sqlserver")
@InterceptorIgnore(tenantLine = "true")
@Mapper
public interface ErpSourceMapper {

    /**
     * 按年月窗口拉取入库明细行（含冗余名称）
     *
     * @param beginYm 开始年月（yyyy-MM-dd）
     * @param endYm   结束年月（yyyy-MM-dd）
     */
    List<ErpInboundDTO> selectInbound(@Param("beginYm") String beginYm, @Param("endYm") String endYm);

    /**
     * 按年月窗口拉取领料明细行（含冗余名称）
     *
     * @param beginYm 开始年月（yyyy-MM-dd）
     * @param endYm   结束年月（yyyy-MM-dd）
     */
    List<ErpRequisitionDTO> selectRequisition(@Param("beginYm") String beginYm, @Param("endYm") String endYm);

    /**
     * 按年月窗口拉取项目退料明细行（含冗余名称，pm02419=1 已审核）
     */
    List<ErpReturnInDTO> selectReturnIn(@Param("beginYm") String beginYm, @Param("endYm") String endYm);

    /**
     * 按年月窗口拉取采购退货明细行（含冗余名称，pm02217=1 采购退货出库）
     */
    List<ErpReturnOutDTO> selectReturnOut(@Param("beginYm") String beginYm, @Param("endYm") String endYm);

    /**
     * 全量拉取物料分类（sdpm001，行数 ~160 很小，整表拉；pm00103=父分类键）
     */
    List<ErpItemCategoryDTO> selectItemCategory();

    /**
     * 按订单日期窗口拉取采购订单明细（sdpm014→sdpm013.pm01302 订单日期；剩余=采购+替代−已领用）
     */
    List<ErpPoLineDTO> selectPoLine(@Param("beginYm") String beginYm, @Param("endYm") String endYm);

    /**
     * 物料主档按编码点查（sdpm002，基础数据按需补缺用；只查缺失编码，量小）
     *
     * @param codes 物料编码集合（pm00201）
     */
    List<ErpItemDTO> selectItemsByCodes(@Param("codes") List<String> codes);

}
