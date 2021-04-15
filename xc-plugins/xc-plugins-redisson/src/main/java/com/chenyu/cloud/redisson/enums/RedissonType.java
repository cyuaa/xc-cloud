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
package com.chenyu.cloud.redisson.enums;


import lombok.Getter;

/**
 * Redisson 类型
 * Created by JackyChen on 2021/04/14.
 */
@Getter
public enum RedissonType {

    /** 类型 */
    STANDALONE("standalone", "单节点部署方式"),
    SENTINEL("sentinel", "哨兵部署方式"),
    CLUSTER("cluster", "集群方式"),
    MASTER_SLAVE("master_slave", "主从部署方式"),

    ;


    private final String type;
    private final String desc;

    public static RedissonType getType(String type) {
        RedissonType[] var1 = values();
        int var2 = var1.length;

        for (RedissonType e : var1) {
            if (e.type.equalsIgnoreCase(type)) {
                return e;
            }
        }

        return null;
    }

    // ================

    RedissonType(final String type, final String desc) {
        this.type = type;
        this.desc = desc;
    }

}
