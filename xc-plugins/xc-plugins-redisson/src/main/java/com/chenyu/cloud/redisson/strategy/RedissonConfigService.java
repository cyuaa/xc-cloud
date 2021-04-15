package com.chenyu.cloud.redisson.strategy;


import com.chenyu.cloud.redisson.enums.RedissonType;
import com.chenyu.cloud.redisson.properties.RedissonProperties;
import org.redisson.config.Config;

/**
 * Redisson配置构建接口
 * Created by JackyChen on 2021/04/14.
 */
public interface RedissonConfigService {

    /**
     * 获得类型
     * @return type
     */
    RedissonType getType();

    /**
     * 根据不同的Redis配置策略创建对应的Config
     * @param redissonProperties
     * @return Config
     */
    Config createRedissonConfig(RedissonProperties redissonProperties);
}
