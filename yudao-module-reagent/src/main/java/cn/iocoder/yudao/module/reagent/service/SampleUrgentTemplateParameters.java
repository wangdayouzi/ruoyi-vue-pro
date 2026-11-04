package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SampleUrgentTemplateParameters implements DingTalkTemplateParameterProvider {
    public String templateCode() { return "sample_urgent_notice"; }
    public List<Parameter> parameters() {
        return List.of(
            new Parameter("action","本次事项","登记需求"),
            new Parameter("registrationNo","登记编号","12"),
            new Parameter("clinicalProtocolNo","临床方案编号","ABC-101"),
            new Parameter("testItems","检测项","PK检测管、ADA检测管"),
            new Parameter("basNos","BAS号","26BAS0001-Blind"),
            new Parameter("reservationTime","预约入库时间","2026-11-04 17:30"),
            new Parameter("appointmentNames","预约人","张三、李四"),
            new Parameter("submitterName","提交人","王五"),
            new Parameter("noticeRecipients","通知样品管理员","样品管理组（部门）、赵六"),
            new Parameter("handlerName","处理人","未接手"),
            new Parameter("status","入库状态","待处理"),
            new Parameter("remark","预约备注","样品预计明天收到"),
            new Parameter("completedScope","已完成范围","PK检测管"),
            new Parameter("remainingScope","剩余范围","ADA检测管待回复"),
            new Parameter("exceptionTypeName","异常类型","等待回复/补资料"),
            new Parameter("exceptionNote","异常说明","等待中心回复"),
            new Parameter("actualInboundTime","实际入库时间","2026-11-04 16:30"),
            new Parameter("changeReason","修改原因","调整预约时间")
        );
    }
}
