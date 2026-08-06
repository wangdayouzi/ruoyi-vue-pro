package cn.iocoder.yudao.module.reagent.dal.redis;

/**
 * Key 定义常量类
 */
public interface RedisKeyConstants {

    /**
     * 序号生成 KEY
     * <p>
     * KEY 格式：reagent_no:{prefix}{yyyyMMdd}
     */
    String REAGENT_NO = "reagent_no:";

}
