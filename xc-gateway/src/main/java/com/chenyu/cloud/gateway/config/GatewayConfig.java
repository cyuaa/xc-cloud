package com.chenyu.cloud.gateway.config;

import com.chenyu.cloud.gateway.filter.AuthGlobalFilter;
import com.chenyu.cloud.gateway.handler.XcGateExceptionHandler;
import com.chenyu.cloud.gateway.properties.IgnoreUrlsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 网关配置类
 * Created by JackyChen on 2021/04/13.
 */
@Configuration
@EnableConfigurationProperties(IgnoreUrlsProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public XcGateExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new XcGateExceptionHandler();
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new AuthGlobalFilter();
    }

}
