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
package com.chenyu.cloud.common.enums;

/**
 * 缓存类型
 * Created by JackyChen on 2021/04/14.
 */
public enum CacheType {

    /** 缓存类型 */
    TIMED("timed", "时控数据"),
    EDEN("eden", "永久数据"),
    EDEN_HASH("edenhash", "永久Hash数据")

    ;

    private final String name;
    private final String desc;

    public static CacheType getCacheType(String cacheType) {
        CacheType[] var1 = values();
        for (CacheType type : var1) {
            if (type.name.equalsIgnoreCase(cacheType)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    // =================

    CacheType(final String name, final String desc) {
        this.name = name;
        this.desc = desc;
    }
}