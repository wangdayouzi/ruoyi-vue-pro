package cn.iocoder.yudao.module.bpm.api.task;

import jakarta.validation.constraints.NotEmpty;

/**
 * 流程任务 Api 接口
 *
 * @author jason
 */
public interface BpmProcessTaskApi {

    /**
     * 触发流程任务的执行
     *
     * @param processInstanceId 流程实例编号
     * @param taskDefineKey 任务 Key
     */
    void triggerTask(@NotEmpty(message = "流程实例的编号不能为空") String processInstanceId,
                     @NotEmpty(message = "任务 Key 不能为空") String taskDefineKey);

    /**
     * 完成指定流程实例 + 任务 Key 的用户任务（自动同意）
     *
     * 适用于业务侧在外部操作完成后，自动通过该节点，从而触发 complete 事件监听器（如：业务表单发货完成后自动通过审核节点）
     *
     * @param processInstanceId 流程实例编号
     * @param taskDefineKey     任务 Key
     * @param reason            同意理由
     * @param userId            当前操作人（任务无办理人时的兜底）
     */
    void completeTaskByKey(@NotEmpty(message = "流程实例的编号不能为空") String processInstanceId,
                           @NotEmpty(message = "任务 Key 不能为空") String taskDefineKey,
                           String reason,
                           Long userId);

}
