package com.chenyu.cloud.auth.model;

import com.chenyu.cloud.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户model
 * Created by JackyChen on 2021/04/28.
 */
@ApiModel("用户信息")
@Data
public class UserModel extends BaseModel {
    @ApiModelProperty("用户名")
    private String username; // 用户名
    @ApiModelProperty("密码")
    private String password; // 密码
    @ApiModelProperty(value = "头像")
    private String icon; // 头像
    @ApiModelProperty(value = "手机")
    private String mobile;
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    @ApiModelProperty(value = "邮箱")
    private String email; // 邮箱
    @ApiModelProperty(value = "昵称")
    private String nickName; // 昵称
    @ApiModelProperty(value = "备注信息")
    private String note; // 备注信息
    @ApiModelProperty(value = "最后登陆IP")
    private String loginIp;
    @ApiModelProperty(value = "最后登录时间")
    private Date loginTime; // 创建时间
    @ApiModelProperty(value = "签名")
    private String sign;
    @ApiModelProperty(value = "帐号启用状态：0->禁用；1->启用")
    private Integer status; // 帐号启用状态：0->禁用；1->启用
}
