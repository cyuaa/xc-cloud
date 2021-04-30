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

/**
 * 菜单表
 * Created by JackyChen on 2021/4/29.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MenuModel extends BaseModel {

    /** 父级主键 */
    @ApiModelProperty(value = "父级主键")
    private Integer parentId;

    /** 权限编号 */
    @ApiModelProperty(value = "权限编号")
    private String permissions;

    /** 菜单名称 */
    @ApiModelProperty(value = "名称")
    private String menuName;

    /** 图标 */
    @ApiModelProperty(value = "图标")
    private String icon;

    /** 项目类型: 1-菜单 2-按钮 3-链接 */
    @ApiModelProperty(value = "项目类型")
    private Integer type;

    /** url地址 */
    @ApiModelProperty(value = "url地址")
    private String url;

    /** 组件 - vue 对应组件 */
    @ApiModelProperty(value = "组件")
    private String component;

    /** 重定向 */
    @ApiModelProperty(value = "重定向")
    private String redirect;

    /** 排序 */
    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    /** 是否隐藏 0为否 1为是 */
    @ApiModelProperty(value = "是否隐藏")
    private Integer hidden;

    /** 是否总是显示 0为否 1为是 */
    @ApiModelProperty(value = "是否隐藏")
    private Integer alwaysShow;

}
