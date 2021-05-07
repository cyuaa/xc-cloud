package com.chenyu.cloud.auth.model;

import com.chenyu.cloud.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("角色信息")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RoleModel extends BaseModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "后台用户数量")
    private Integer userCount;
    @ApiModelProperty(value = "启用状态：0->禁用；1->启用")
    private Integer status;
    @ApiModelProperty("排序字段")
    private Integer sort;

}