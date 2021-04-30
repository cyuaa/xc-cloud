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
package com.chenyu.cloud.auth.model;

import com.chenyu.cloud.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户信息表
 * Created by JackyChen on 2021/4/29.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserInfo extends BaseModel {


    /** 登录账户 */
    @ApiModelProperty(value = "登录账户")
    private String username;

    /** 真实姓名 */
    @ApiModelProperty(value = "真实姓名")
    private String realName;

    /** 手机 */
    @ApiModelProperty(value = "手机")
    private String mobile;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /** 工号 */
    @ApiModelProperty(value = "工号")
    private String no;

    /** 头像 */
    @ApiModelProperty(value = "头像")
    private String icon;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 签名 */
    @ApiModelProperty(value = "签名")
    private String sign;

    /** 角色列表 */
    @ApiModelProperty(value = "角色列表")
    private List<Integer> roles;

    /** 权限列表 */
    @ApiModelProperty(value = "权限列表")
    private List<String> perms;

}
