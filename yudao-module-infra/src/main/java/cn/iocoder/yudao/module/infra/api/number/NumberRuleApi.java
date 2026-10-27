package cn.iocoder.yudao.module.infra.api.number;

public interface NumberRuleApi {
    /** Issues a number for a saved business object. The caller's transaction must include the object creation. */
    String issue(Long ruleId, String bizType, Long bizId);

    boolean isEnabled(Long ruleId);
}
