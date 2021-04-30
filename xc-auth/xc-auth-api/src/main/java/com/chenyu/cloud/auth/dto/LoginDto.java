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
package com.chenyu.cloud.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录表单
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginDto {

    /** 用户名 */
    @ApiModelProperty(value = "用户名")
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码")
    private String password;

    /** 验证码 */
    @ApiModelProperty(value = "验证码")
    private String captcha;

    /** UUID */
    @ApiModelProperty(value = "UUID")
    private String uuid;

}
