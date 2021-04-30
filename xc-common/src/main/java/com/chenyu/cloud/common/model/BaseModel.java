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
package com.chenyu.cloud.common.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Version;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * Model 基础类
 *
 * 尽量 与本地 服务的 entity 保持一致（除去不想要暴露给 web的字段）
 *
 * api层级的 wrapper 也是对于数据安全性的一次包装
 *
 * Entity 增加的 deleted 字段， 不需要同步更新到 Wrapper的Model中
 * Wrapper的Model 只是用于 对外展示
 *
 * Created by JackyChen on 2021/4/28.
 *
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public abstract class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ID */
	// @ApiModelProperty(value = "ID")
	private Integer id;

	/** 创建人 */
	// @ApiModelProperty(value = "创建人")
	private Integer createBy;

	/** 创建时间 */
	// @ApiModelProperty(value = "创建时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/** 更新人 */
	// @ApiModelProperty(value = "修改人")
	private Integer updateBy;

	/** 更新时间 */
	// @ApiModelProperty(value = "修改时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	/** 乐观锁 版本 */
	// @ApiModelProperty(value = "版本")
	@Version
	private Integer version;

	/** 租户id */
	private Integer tenantId;
}
