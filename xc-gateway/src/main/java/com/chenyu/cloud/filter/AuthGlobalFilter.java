package com.chenyu.cloud.filter;

import com.chenyu.cloud.common.api.CommonMsg;
import com.chenyu.cloud.common.exception.ServiceException;
import com.chenyu.cloud.config.IgnoreUrlsConfig;
import com.chenyu.cloud.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class AuthGlobalFilter implements GatewayFilter, GlobalFilter, Ordered {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.key.token}")
    private String REDIS_KEY_TOKEN;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //防止 OPTIONS 请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : ignoreUrlsConfig.getUrls()) {
            if (pathMatcher.match("/**" + path, request.getPath().toString())) {
                return chain.filter(exchange);
            }
        }
        // token 验证
        String token = request.getHeaders().getFirst(tokenHeader);
        if (StringUtils.isBlank(token)){
            log.error("token = {}",token);
            throw new ServiceException(CommonMsg.UNAUTHORIZED);
        }
        String username = jwtTokenUtil.getUserNameFromToken(token);
        // 待抽离
        String key = REDIS_DATABASE + ":" + REDIS_KEY_TOKEN + ":" + username;
        String resultToken = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(resultToken)) {
            log.error("resultToken = {}",resultToken);
            throw new ServiceException(CommonMsg.UNAUTHORIZED);
        }
        log.error("resultToken = {}",resultToken);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
