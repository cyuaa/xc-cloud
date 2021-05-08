package com.chenyu.cloud.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.chenyu.cloud.gateway.filter.AuthGlobalFilter;
import com.chenyu.cloud.gateway.handler.XcGateExceptionHandler;
import com.chenyu.cloud.gateway.properties.IgnoreUrlsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

/**
 * Sentinel配置类
 * Created by JackyChen on 2021/04/13.
 */
@Configuration
@EnableConfigurationProperties(IgnoreUrlsProperties.class)
public class SentinelConfig {

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    // nacos 的配置信息
    @Autowired
    private NacosConfigProperties nacosConfigProperties;

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

    // if change to true, should be config NACOS_NAMESPACE_ID
    private static boolean isDemoNamespace = false;
    // fill your namespace id,if you want to use namespace. for example:
    // 0f5c7314-4983-4022-ad5a-347de1d1057d,you can get it on nacos's console

    @PostConstruct
    public void initRules() {
        if (isDemoNamespace) {
            loadMyNamespaceRules();
        } else {
            loadRules();
        }
    }

    private void loadRules() {
        List<NacosConfigProperties.Config> configList = nacosConfigProperties.getSharedConfigs();
        String dataId = configList.get(1).getDataId();
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr, configList.get(1).getGroup(),
                dataId.substring(0, dataId.lastIndexOf(".")), source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    private void loadMyNamespaceRules() {
        List<NacosConfigProperties.Config> configList = nacosConfigProperties.getSharedConfigs();
        String dataId = configList.get(1).getDataId();

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, "");

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(properties, configList.get(1).getGroup(),
                dataId.substring(0, dataId.lastIndexOf(".")), source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

}
