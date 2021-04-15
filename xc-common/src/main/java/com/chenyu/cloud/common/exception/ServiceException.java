package com.chenyu.cloud.common.exception;

import com.chenyu.cloud.common.response.BaseMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义服务异常
 * Created by JackyChen on 2021/04/13.
 */
@Getter
@Setter
public class ServiceException extends RuntimeException {

    private Integer code;
    private String message;

    public ServiceException(BaseMsg baseMsg) {
        super(baseMsg.getMessage());
        this.code = baseMsg.getCode();
        this.message = baseMsg.getMessage();
    }

    public ServiceException(Integer code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.message = errorMessage;
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
