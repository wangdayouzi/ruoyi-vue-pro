package cn.iocoder.yudao.module.reagent.service.listener;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.HashMap;
import java.util.Map;

/**
 * 试剂邮件模板参数组装工具
 *
 * 把申请单表单里的业务字段填充到邮件模板参数，供模板占位符使用。
 * 说明：按业务需求，邮件模板不携带单号（申请单号/发货单号/快递单号等），仅带接收单位等表单字段。
 *
 * @author yudao
 */
final class ReagentMailParamsHelper {

    private ReagentMailParamsHelper() {
    }

    /**
     * 模板参数白名单：仅业务字段，避免把全部流程变量塞进 system_mail_log.template_params（varchar(255)）导致超长
     */
    private static final String[] TEMPLATE_PARAM_KEYS = {
            "applyNo", "receiverUnit", "receiverName", "receiverPhone", "receiverAddress"
    };

    /**
     * 从流程变量中组装邮件模板参数（仅白名单字段，缺失填空串）
     *
     * @param delegateTask 任务
     * @return 模板参数
     */
    static Map<String, Object> buildTemplateParams(DelegateTask delegateTask) {
        Map<String, Object> params = new HashMap<>();
        for (String key : TEMPLATE_PARAM_KEYS) {
            Object value = delegateTask.getVariable(key);
            params.put(key, value == null ? "" : value);
        }
        return params;
    }

    /**
     * 将申请单的"接收单位"表单字段填充到模板参数
     *
     * 缺失字段填充为空串，避免模板参数校验（MAIL_SEND_TEMPLATE_PARAM_MISS）失败导致邮件发送报错
     *
     * @param params 模板参数
     * @param apply 申请单；为 null 时不填充
     */
    static void putApplyFormFields(Map<String, Object> params, ReagentApplyDO apply) {
        if (apply == null) {
            return;
        }
        params.put("applyNo", StrUtil.nullToEmpty(apply.getApplyNo()));               // 申请单号
        params.put("receiverUnit", StrUtil.nullToEmpty(apply.getReceiverUnit()));       // 接收单位
        params.put("receiverName", StrUtil.nullToEmpty(apply.getReceiverName()));       // 接收联系人
        params.put("receiverPhone", StrUtil.nullToEmpty(apply.getReceiverPhone()));     // 联系电话
        params.put("receiverAddress", StrUtil.nullToEmpty(apply.getReceiverAddress())); // 接收地址
    }

}
