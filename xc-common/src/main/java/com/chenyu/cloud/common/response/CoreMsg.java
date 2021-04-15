package com.chenyu.cloud.common.response;
/**
 * 核心消息类
 * Created by JackyChen on 2021/04/15.
 */
public enum CoreMsg implements BaseMsg {

    /**
     * Redis
     */
    REDIS_EXCEPTION_PUSH_SUB(10200,"Redis 订阅通道失败!"),
    REDIS_EXCEPTION_LOCK(10201,"无法申领分布式锁!");

    private final Integer code;
    private final String message;

    CoreMsg(Integer code, String message){
        this.code = code;
        this.message = message;
    };


    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
