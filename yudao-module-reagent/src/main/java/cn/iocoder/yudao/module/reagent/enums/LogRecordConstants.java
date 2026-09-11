package cn.iocoder.yudao.module.reagent.enums;

/**
 * 试剂管理模块操作日志常量。
 *
 * 业务编号统一使用各表的数字主键，以兼容操作日志的业务编号存储规则。
 */
public interface LogRecordConstants {

    String REAGENT_APPLY_TYPE = "试剂申请单";
    String REAGENT_APPLY_CREATE_SUB_TYPE = "创建申请单";
    String REAGENT_APPLY_CREATE_SUCCESS = "创建了试剂申请单【{{#apply.applyNo}}】";
    String REAGENT_APPLY_UPDATE_SUB_TYPE = "修改申请单";
    String REAGENT_APPLY_UPDATE_SUCCESS = "修改了试剂申请单【{{#apply.applyNo}}】: {_DIFF{#updateReqVO}}";
    String REAGENT_APPLY_DELETE_SUB_TYPE = "删除申请单";
    String REAGENT_APPLY_DELETE_SUCCESS = "删除了试剂申请单【{{#apply.applyNo}}】";
    String REAGENT_APPLY_SUBMIT_SUB_TYPE = "提交申请单";
    String REAGENT_APPLY_SUBMIT_SUCCESS = "提交了试剂申请单【{{#apply.applyNo}}】";
    String REAGENT_APPLY_REJECT_SUB_TYPE = "退回申请单";
    String REAGENT_APPLY_REJECT_SUCCESS = "退回了试剂申请单【{{#apply.applyNo}}】，理由：【{{#reqVO.remark}}】";

    String REAGENT_SHIPMENT_TYPE = "试剂发货单";
    String REAGENT_SHIPMENT_CONFIRM_SUB_TYPE = "确认发货";
    String REAGENT_SHIPMENT_CONFIRM_SUCCESS = "确认了申请单【{{#apply.applyNo}}】的发货单【{{#shipment.shipmentNo}}】（{{#shipmentItemCount}} 项）";
    String REAGENT_SHIPMENT_REVOKE_SUB_TYPE = "撤回发货";
    String REAGENT_SHIPMENT_REVOKE_SUCCESS = "撤回了发货单【{{#shipment.shipmentNo}}】";
    String REAGENT_SHIPMENT_UPDATE_LOGISTICS_SUB_TYPE = "修改物流";
    String REAGENT_SHIPMENT_UPDATE_LOGISTICS_SUCCESS = "修改了发货单【{{#shipment.shipmentNo}}】的物流信息：{{#logisticsChange}}";

    String REAGENT_BASE_FLAT_TYPE = "试剂基础数据";
    String REAGENT_BASE_FLAT_UPDATE_SUB_TYPE = "修改基础数据";
    String REAGENT_BASE_FLAT_UPDATE_SUCCESS = "修改了试剂基础数据【{{#baseFlat.reagentName}}】: {_DIFF{#updateReqVO}}";
    String REAGENT_BASE_FLAT_DELETE_SUB_TYPE = "删除基础数据";
    String REAGENT_BASE_FLAT_DELETE_SUCCESS = "删除了试剂基础数据【{{#baseFlat.reagentName}}】";

    String REAGENT_LABEL_PRINT_TYPE = "试剂标签打印";
    String REAGENT_LABEL_PRINT_CREATE_SUB_TYPE = "创建打印任务";
    String REAGENT_LABEL_PRINT_CREATE_SUCCESS = "创建了标签打印任务【{{#job.jobNo}}】（模板【{{#job.templateCode}}】，{{#job.copies}} 份）";

}
