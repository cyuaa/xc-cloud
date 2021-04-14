package com.chenyu.cloud.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chenyu.cloud.filter.AuthGlobalFilter;
import com.chenyu.cloud.handler.XcGateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Set;

/**
 * 网关配置类
 * Created by JackyChen on 2021/04/13.
 */
@Configuration
public class GatewayConfig {

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.extension-configs.group}")
    private String groupId;
    @Value("${spring.cloud.nacos.config.extension-configs.dataId}")
    private String dataId;

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

    @Bean
    public ReadableDataSource<String, Set<GatewayFlowRule>> getReadableDataSource() {
        ReadableDataSource<String, Set<GatewayFlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr,
                groupId,
                dataId,//"sentinel-service",
                source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {
                }));
        GatewayRuleManager.register2Property(flowRuleDataSource.getProperty());
        return flowRuleDataSource;
    }
}
