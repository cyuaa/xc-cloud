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
package com.chenyu.cloud.redis.scripts.enums;


/**
 * Redis 脚本枚举
 * Created by JackyChen on 2021/04/14.
 */
public enum RedisScriptsEnum {

    /** Redis加锁脚本 */
    REDIS_LOCK("/lua/redis_lock.lua"),
    /** Redis解锁脚本 */
    REDIS_UN_LOCK("/lua/redis_unlock.lua");

    /** 脚本路径 */
    private final String path;

    RedisScriptsEnum(String path){
        this.path = path;
    }

    /**
     * 获得路径
     * @return path
     */
    public String getPath() {
        return path;
    }
}