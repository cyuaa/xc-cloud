package com.chenyu.cloud.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Nacos动态路由
 * Created by JackyChen on 2021/04/30.
 */
@Slf4j
@Configuration
public class DynamicRouteConfig implements RouteDefinitionRepository {

    private List<RouteDefinition> routeDefinitions = new ArrayList<RouteDefinition>();

    // 更新路由信息需要的
    private ApplicationEventPublisher publisher;

    // nacos 的配置信息
    private NacosConfigProperties nacosConfigProperties;
    private NacosConfigManager nacosConfigManager;

    // 构造器
    public DynamicRouteConfig(ApplicationEventPublisher publisher, NacosConfigProperties nacosConfigProperties) {
        nacosConfigManager = new NacosConfigManager(nacosConfigProperties);
        this.publisher = publisher;
        this.nacosConfigProperties = nacosConfigProperties;
        addListener();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        try {
            List<NacosConfigProperties.Config> configList = nacosConfigProperties.getSharedConfigs();
            String dataId = configList.get(1).getDataId();
            String content = nacosConfigManager.getConfigService().getConfig(dataId.substring(0, dataId.lastIndexOf(".")), configList.get(1).getGroup(), 5000);
            routeDefinitions = JSONObject.parseArray(content, RouteDefinition.class);
            log.info("Gateway Get RouteDefinitions Finish!!!");
            return Flux.fromIterable(routeDefinitions);
        } catch (NacosException e) {
            log.error("GetRouteDefinitions By Nacos Error", e);
        }
        return Flux.fromIterable(routeDefinitions);
    }

    /**
     * 添加Nacos监听
     */
    private void addListener() {
        try {
            List<NacosConfigProperties.Config> configList = nacosConfigProperties.getSharedConfigs();
            String dataId = configList.get(1).getDataId();
            nacosConfigManager.getConfigService().addListener(dataId.substring(0,dataId.lastIndexOf(".")), configList.get(1).getGroup(), new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("自动更新配置...\r\n{}",configInfo);
                    publisher.publishEvent(new RefreshRoutesEvent(this));
                }
            });
        } catch (NacosException e) {
            log.error("Nacos-AddListener-Error", e);
        }
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
