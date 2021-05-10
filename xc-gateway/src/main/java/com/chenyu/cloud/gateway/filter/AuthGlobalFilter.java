package com.chenyu.cloud.gateway.filter;

import com.chenyu.cloud.common.exception.ServiceException;
import com.chenyu.cloud.common.properties.GlobalProperties;
import com.chenyu.cloud.common.response.CommonMsg;
import com.chenyu.cloud.gateway.properties.IgnoreUrlsProperties;
import com.chenyu.cloud.gateway.util.UserTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局认证过滤器
 * Created by JackyChen on 2021/04/13.
 */
@Configuration
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private IgnoreUrlsProperties ignoreUrlsProperties;

    @Autowired
    private GlobalProperties globalProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String pathStr = request.getURI().getPath();
        // 内部服务接口，不允许外部访问
        if(antPathMatcher.match("/**/inner/**", pathStr)) {
            throw new ServiceException(CommonMsg.FORBIDDEN);
        }
        //防止 OPTIONS 请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : ignoreUrlsProperties.getUrls()) {
            if (pathMatcher.match("/**" + path, request.getPath().toString())) {
                return chain.filter(exchange);
            }
        }
        // token 验证
        String token = request.getHeaders().getFirst(globalProperties.getAuth().getToken().getTokenHeader());
        if (StringUtils.isBlank(token)){
            log.error("token = {}",token);
            throw new ServiceException(CommonMsg.UNAUTHORIZED);
        }
        // token 验证
        boolean verify = UserTokenUtil.verify(token);
        if (!verify) {
            log.error("token = {}, 无效!", token);
            throw new ServiceException(CommonMsg.UNAUTHORIZED);
        }
        String username = UserTokenUtil.getUserNameByToken(token);
        if (StringUtils.isBlank(username)) {
            log.error("用户不存在! : {}", token);
            throw new ServiceException(CommonMsg.UNAUTHORIZED);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
