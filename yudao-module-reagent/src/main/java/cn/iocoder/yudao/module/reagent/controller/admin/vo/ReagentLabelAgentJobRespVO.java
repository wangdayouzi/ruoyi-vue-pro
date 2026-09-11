package cn.iocoder.yudao.module.reagent.controller.admin.vo;

import lombok.Data;

/** Windows 打印代理领取任务后的最小执行载荷。 */
@Data
public class ReagentLabelAgentJobRespVO {
    private Long jobId;
    private String jobNo;
    private String claimToken;
    private String systemPrinterName;
    private String templateCode;
    private String dataSnapshot;
    private Integer copies;
}
