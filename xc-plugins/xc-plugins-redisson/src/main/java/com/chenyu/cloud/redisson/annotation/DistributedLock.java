package com.chenyu.cloud.redisson.annotation;

import com.chenyu.cloud.common.constants.RedissonConstants;

import java.lang.annotation.*;

/**
 * 基于注解的分布式式锁
 * Created by JackyChen on 2021/04/14.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {

    /**
     * 锁的名称
     */
    String value() default RedissonConstants.LOCK_PREFIX;

    /**
     * 锁的有效时间
     */
    int leaseTime() default 10;
}


