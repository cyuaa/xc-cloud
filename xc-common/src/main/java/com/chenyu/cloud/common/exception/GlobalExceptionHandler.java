package com.chenyu.cloud.common.exception;

import com.chenyu.cloud.common.api.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 * Created by JackyChen on 2021/04/13.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    public Result<?> handle(ServiceException e) {
        if (e.getResultCode() != null) {
            return Result.error(e.getResultCode());
        }
        return Result.error(e.getMessage());
    }

}
