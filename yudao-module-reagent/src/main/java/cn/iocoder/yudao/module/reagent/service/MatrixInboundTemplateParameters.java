package cn.iocoder.yudao.module.reagent.service;

import cn.iocoder.yudao.module.system.api.dingtalk.DingTalkTemplateParameterProvider;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MatrixInboundTemplateParameters implements DingTalkTemplateParameterProvider {
    public String templateCode() { return "matrix_inbound_ningbo"; }
    @Override
    public boolean supports(String code) {
        return List.of("matrix_inbound_ningbo","matrix_inbound_shanghai","matrix_inbound_completed").contains(code);
    }
    public List<Parameter> parameters() {
        return List.of(
                new Parameter("action","本次事项","新预约提交"),
                new Parameter("site","地区","上海"),
                new Parameter("basNo","BAS 号","SH26BAS0001P"),
                new Parameter("matrixKind","基质类型","混合基质"),
                new Parameter("applicantName","预约人","张三"),
                new Parameter("appointmentDate","预约完成系统入库日期","2026-11-20"),
                new Parameter("limsDesigned","LIMS/线粒 是否design","是"),
                new Parameter("urgent","是否属于加急入库","否"),
                new Parameter("status","入库结果","已入库"),
                new Parameter("registeredName","登记人","李四"),
                new Parameter("registeredTime","登记时间","2026-11-20 15:00:00"),
                new Parameter("detailLink","查看预约入口","请登录系统查看预约")
        );
    }
}
