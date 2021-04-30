package com.chenyu.cloud.gateway.handler;

import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.fastjson.JSON;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.common.response.CommonMsg;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关异常处理器
 * Created by JackyChen on 2021/04/13.
 */
public class XcGateExceptionHandler implements WebExceptionHandler {


	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		exchange.getResponse().setStatusCode(HttpStatus.OK);
		byte[] bytes = JSON.toJSONString(buildErrorResult(ex)).getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
		exchange.getResponse().getHeaders().set("Content-Type", "application/json;charset=UTF-8");
		return exchange.getResponse().writeWith(Flux.just(buffer));
		
	}


	private Result<?> buildErrorResult(Throwable ex) {
		if(ex instanceof FlowException) {
			return Result.error(CommonMsg.GATEWAY_HIGH);
		}else {
			return Result.error(CommonMsg.FAILED.getCode(), ex.getClass().getSimpleName() + ":" + ex.getMessage());
		}
	}

}
