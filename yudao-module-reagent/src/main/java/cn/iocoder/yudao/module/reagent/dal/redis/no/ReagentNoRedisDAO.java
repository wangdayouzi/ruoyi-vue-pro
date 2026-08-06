package cn.iocoder.yudao.module.reagent.dal.redis.no;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.module.reagent.dal.redis.RedisKeyConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 生物试剂序号生成的 Redis DAO（参考 ErpNoRedisDAO 的标准方案）
 *
 * 序号规则：前缀 + 日期(yyMMdd) + 当日自增序号(5位)，如 APL26080600001
 */
@Repository
public class ReagentNoRedisDAO {

    /**
     * 日期格式：yyMMdd，如 260806
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    /**
     * 申请单号 {@link cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentApplyDO}
     */
    public static final String APPLY_NO_PREFIX = "APL";
    /**
     * 发货单号 {@link cn.iocoder.yudao.module.reagent.dal.dataobject.ReagentShipmentDO}
     */
    public static final String SHIPMENT_NO_PREFIX = "SHIP";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成序号
     *
     * @param prefix 前缀
     * @return 序号
     */
    public String generate(String prefix) {
        // 递增序号：前缀 + 日期(yyMMdd)
        String noPrefix = prefix + LocalDateTime.now().format(DATE_FORMATTER);
        String key = RedisKeyConstants.REAGENT_NO + noPrefix;
        Long no = stringRedisTemplate.opsForValue().increment(key);
        // 按天自增，key 保留 2 天，防止跨天边界计数丢失
        stringRedisTemplate.expire(key, Duration.ofDays(2L));
        return noPrefix + String.format("%05d", no);
    }

}
