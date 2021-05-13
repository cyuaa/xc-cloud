package com.chenyu.cloud.auth.fallback;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import com.chenyu.cloud.common.exception.ServiceException;
import com.chenyu.cloud.common.response.Result;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Feign异常处理
 * Created by JackyChen on 2021/05/13.
 */
@Slf4j
@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        ServiceException serviceException = null;
        try {
            String errorContent = Util.toString(response.body().asReader());
            Result result = JSONObject.parseObject(errorContent, Result.class);
            serviceException = new ServiceException(result.getCode(), result.getMessage());
        } catch (Exception e) {
            log.error("处理FeignClient 异常错误...");
            e.printStackTrace();
            return new ServiceException(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
        }
        return serviceException;
    }

}
