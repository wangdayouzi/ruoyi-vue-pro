package cn.iocoder.yudao.module.infra.service.number;

import cn.iocoder.yudao.module.infra.api.number.NumberRuleApi;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberIssueDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.number.NumberRuleDO;
import cn.iocoder.yudao.module.infra.dal.mysql.number.NumberIssueMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.number.NumberRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;

@Service
public class NumberRuleService implements NumberRuleApi {
    @Resource private NumberRuleMapper ruleMapper;
    @Resource private NumberIssueMapper issueMapper;

    public List<NumberRuleDO> list() {
        return ruleMapper.selectList();
    }

    public NumberRuleDO get(Long id) {
        return ruleMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(NumberRuleDO input) {
        if (input.getCode() == null || !input.getCode().matches("[A-Z][A-Z0-9_]{1,63}"))
            throw invalidParamException("规则编码只能使用大写字母、数字和下划线");
        if (input.getName() == null || input.getName().isBlank()) throw invalidParamException("规则名称不能为空");
        if (input.getDigits() == null || input.getDigits() < 1 || input.getDigits() > 16)
            throw invalidParamException("流水号位数须在 1 到 16 之间");
        if (input.getStartNo() == null || input.getStartNo() < 1)
            throw invalidParamException("起始值必须大于 0");
        if (String.valueOf(input.getStartNo()).length() > input.getDigits())
            throw invalidParamException("起始值不能超过流水号位数");
        if (input.getPrefix() == null) input.setPrefix("");
        if (input.getPrefix().length() > 64) throw invalidParamException("固定前缀过长");
        if (input.getYearlyReset() == null) input.setYearlyReset(false);
        if (input.getStatus() == null) input.setStatus(0);
        if (input.getStatus() != 0 && input.getStatus() != 1) throw invalidParamException("规则状态无效");
        NumberRuleDO sameCode = ruleMapper.selectByCode(input.getCode());
        if (sameCode != null && !sameCode.getId().equals(input.getId()))
            throw invalidParamException("规则编码已存在");
        if (input.getId() == null) {
            input.setLastNo(0L).setLastYear(null);
            ruleMapper.insert(input);
            return input.getId();
        }
        NumberRuleDO existing = ruleMapper.selectById(input.getId());
        if (existing == null) throw invalidParamException("编号规则不存在");
        if (existing.getLastNo() != null && existing.getLastNo() > 0
                && (!existing.getCode().equals(input.getCode()) || !existing.getPrefix().equals(input.getPrefix())
                || !existing.getDigits().equals(input.getDigits())
                || !existing.getStartNo().equals(input.getStartNo())
                || !existing.getYearlyReset().equals(input.getYearlyReset())))
            throw invalidParamException("已使用规则只能修改名称和状态");
        input.setLastNo(null).setLastYear(null);
        ruleMapper.updateById(input);
        return input.getId();
    }

    @Override
    public boolean isEnabled(Long ruleId) {
        NumberRuleDO rule = ruleMapper.selectById(ruleId);
        return rule != null && Integer.valueOf(0).equals(rule.getStatus());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public String issue(Long ruleId, String bizType, Long bizId) {
        NumberRuleDO rule = ruleMapper.selectForUpdate(ruleId);
        if (rule == null || !Integer.valueOf(0).equals(rule.getStatus()))
            throw invalidParamException("编号规则不存在或已停用");
        int year = Year.now().getValue();
        long next = Boolean.TRUE.equals(rule.getYearlyReset()) && !Integer.valueOf(year).equals(rule.getLastYear())
                ? rule.getStartNo() : Math.max(rule.getStartNo(), rule.getLastNo() + 1);
        if (String.valueOf(next).length() > rule.getDigits())
            throw invalidParamException("编号流水号已超出配置位数");
        String value = rule.getPrefix() + (Boolean.TRUE.equals(rule.getYearlyReset()) ? year : "")
                + String.format("%0" + rule.getDigits() + "d", next);
        ruleMapper.updateById(new NumberRuleDO().setId(ruleId).setLastNo(next).setLastYear(year));
        issueMapper.insert(new NumberIssueDO().setRuleId(ruleId).setNumberValue(value)
                .setBizType(bizType).setBizId(bizId));
        return value;
    }
}
