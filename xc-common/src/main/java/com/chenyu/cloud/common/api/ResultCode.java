package com.chenyu.cloud.common.api;
/**
 * API状态码-枚举
 * Created by JackyChen on 2021/04/13.
 */
public enum ResultCode {

    SUCCESS(200, "操作成功!"),
    FAILED(500, "操作失败!"),
    VALIDATE_FAILED(404, "参数检验失败!"),
    UNAUTHORIZED(401, "暂未登录或token已经过期!"),
    FORBIDDEN(403, "没有相关权限!"),
    GATEWAY_HIGH(503, "访问量过高,请稍候访问!");
    private Integer code;
    private String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
