/**
 * Copyright 2020 OPSLI 快速开发平台 https://www.opsli.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.chenyu.cloud.core.util;

import com.chenyu.cloud.common.constants.OrderConstants;
import com.chenyu.cloud.redisson.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 分布式锁工具类
 * Created by JackyChen on 2021/04/14.
 */
@Slf4j
@Order(OrderConstants.UTIL_ORDER)
@Component
@Lazy(false)
public class DistributedLockUtil {

    /** 锁有效时长为 默认10秒 */
    private static final int LEASE_TIME = 10;

    /** Redisson 分布式锁 */
    private static RedissonLock REDISSON_LOCK;

    /**
     * 分布式 加锁
     * @param lockName 锁名称
     * @return boolean
     */
    public static boolean lock(String lockName){
        boolean isLock = true;
        // 分布式上锁
        if(REDISSON_LOCK != null){
            isLock = REDISSON_LOCK.tryLock(CacheUtil.getPrefixName() + lockName, LEASE_TIME);
        }
        return isLock;
    }

    /**
     * 分布式 释放锁
     * @param lockName 锁名称
     */
    public static void unlock(String lockName){
        // 释放锁
        if(REDISSON_LOCK != null){
            REDISSON_LOCK.unlockByThread(CacheUtil.getPrefixName() + lockName);
        }
    }

    // =============

    /**
     * 初始化
     * @param redissonLock 分布式锁
     */
    @Autowired(required = false)
    public void init(RedissonLock redissonLock){
        DistributedLockUtil.REDISSON_LOCK = redissonLock;
    }


}
