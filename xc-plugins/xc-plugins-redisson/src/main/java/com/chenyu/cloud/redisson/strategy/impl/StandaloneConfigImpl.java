package com.chenyu.cloud.redisson.strategy.impl;


import com.chenyu.cloud.common.constants.CoreConstants;
import com.chenyu.cloud.redisson.enums.RedissonType;
import com.chenyu.cloud.redisson.properties.RedissonProperties;
import com.chenyu.cloud.redisson.strategy.RedissonConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.config.Config;

/**
 * 单机部署Redisson配置
 * Created by JackyChen on 2021/04/14.
 */
@Slf4j
public class StandaloneConfigImpl implements RedissonConfigService {

    @Override
    public RedissonType getType() {
        return RedissonType.STANDALONE;
    }


    @Override
    public Config createRedissonConfig(RedissonProperties redissonProperties) {
        Config config = new Config();
        try {
            String address = redissonProperties.getAddress();
            String password = redissonProperties.getPassword();
            int database = redissonProperties.getDatabase();
            String redisAddr = CoreConstants.REDIS_CONNECTION_PREFIX + address;
            config.useSingleServer().setAddress(redisAddr);
            config.useSingleServer().setDatabase(database);
            //密码可以为空
            if (StringUtils.isNotBlank(password)) {
                config.useSingleServer().setPassword(password);
            }
            log.info("初始化[单机部署]方式Config,redisAddress:" + address);
        } catch (Exception e) {
            log.error("单机部署 Redisson init error", e);
        }
        return config;
    }
}
