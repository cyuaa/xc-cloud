package com.chenyu.cloud.auth.model;


import com.chenyu.cloud.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资源信息
 */
@ApiModel("资源信息")
@Data
public class UserResourceModel extends BaseModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源名称")
    private String name; // 资源名称
    @ApiModelProperty(value = "资源URL")
    private String url; // 资源URL
    @ApiModelProperty(value = "描述")
    private String description; // 描述
    @ApiModelProperty(value = "资源分类ID")
    private Long categoryId; // 资源分类ID

}