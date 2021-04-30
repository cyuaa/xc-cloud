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
import com.chenyu.cloud.core.annotation.ValidationArgs;
import com.chenyu.cloud.core.annotation.ValidationArgsLenMax;
import com.chenyu.cloud.core.enums.ValiArgsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 租户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TenantModel extends BaseModel {


    /** 租户名称 */
    @ApiModelProperty(value = "租户名称")
    @ValidationArgs({ValiArgsType.IS_NOT_NULL,ValiArgsType.IS_GENERAL_WITH_CHINESE})
    @ValidationArgsLenMax(50)
    private String name;

    /** 是否启用 0是  1否*/
    @ApiModelProperty(value = "是否启用")
    @ValidationArgs({ValiArgsType.IS_NOT_NULL})
    @ValidationArgsLenMax(1)
    private Integer status;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    @ValidationArgsLenMax(255)
    private String remark;


}
