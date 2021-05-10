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
package com.chenyu.cloud.gateway.properties;

import com.chenyu.cloud.common.constants.CacheConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 缓存属性配置实体类
 * Created by JackyChen on 2021/04/14.
 */
@Component
@ConfigurationProperties(prefix = CacheConstants.PROP_PREFIX)
@Data
@EqualsAndHashCode(callSuper = false)
public class CacheProperties {

    /** 缓存前缀 */
    private String prefix;

}
