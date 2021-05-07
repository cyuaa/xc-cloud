package com.chenyu.cloud.redis.config;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.chenyu.cloud.redis.scripts.RedisScriptCache;
import com.chenyu.cloud.redis.scripts.enums.RedisScriptsEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Redis配置
 * Created by JackyChen on 2021/04/14.
 */
@Slf4j
@Configuration
public class RedisConfig {

    private static final FastJsonRedisSerializer<Object> FAST_JSON_REDIS_SERIALIZER = new FastJsonRedisSerializer<>(Object.class);

    @Value("${spring.redis.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis.database:0}")
    private Integer database;
    @Value("${spring.redis.port:6379}")
    private Integer port;
    @Value("${spring.redis.password:null}")
    private String password;

    /**
     * RedisTemplate配置
     * 序列化设置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        // key采用String的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // value序列化方式采用 json
        template.setValueSerializer(FAST_JSON_REDIS_SERIALIZER);
        // hash的value序列化方式采用 json
        template.setHashValueSerializer(FAST_JSON_REDIS_SERIALIZER);

        template.afterPropertiesSet();

        // 开启事务
        //template.setEnableTransactionSupport(true);

        return template;
    }

    @Bean
    @Primary
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        if (StringUtils.isNotBlank(password) && !("null").equals(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
        return factory;
    }

    /**
     * 加载脚本到缓存内
     *
     * 默认开启 全局乐观锁 一劳永逸
     *
     * @return RedisScriptCache
     */
    @Bean
    public RedisScriptCache loadScripts() {
        RedisScriptCache redisScriptCache = new RedisScriptCache();
        RedisScriptsEnum[] scriptEnums = RedisScriptsEnum.values();
        for (RedisScriptsEnum scriptEnum : scriptEnums) {
            String path = scriptEnum.getPath();

            try {
                ClassPathResource resource = new ClassPathResource(path);
                InputStream inputStream = resource.getInputStream();
                String read = IoUtil.read(inputStream, StandardCharsets.UTF_8);
                // 保存脚本到缓存中
                redisScriptCache.putScript(scriptEnum,read);
            } catch (Exception ignored){
                log.error("Save Redis Scripts Failed: {}", ignored.getMessage());
            }
        }
        return redisScriptCache;
    }

}
