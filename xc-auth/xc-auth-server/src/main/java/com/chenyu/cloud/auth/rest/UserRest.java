package com.chenyu.cloud.auth.rest;

import com.chenyu.cloud.auth.api.UserApi;
import com.chenyu.cloud.auth.dto.LoginDto;
import com.chenyu.cloud.auth.dto.UserDto;
import com.chenyu.cloud.auth.dto.UserPasswordDto;
import com.chenyu.cloud.auth.model.*;
import com.chenyu.cloud.auth.service.UserService;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.security.util.UserTokenUtil;
import com.chenyu.cloud.security.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户登录Rest
 * Created by JackyChen on 2021/04/28.
 */
@Api(tags = UserApi.TITLE)
@RestController
@RequestMapping("/user")
public class UserRest {

    @Autowired
    private UserService userService;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<UserTokenUtil.TokenRet> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        return userService.login(loginDto, request);
    }

    @ApiOperation("当前登陆用户信息")
    @GetMapping("/info")
    public Result<UserInfo> getInfo() {
        UserModel user = UserUtil.getUser();
        return Result.success(userService.findInfoById(user.getId()));
    }

    @ApiOperation(value = "通过id查询用户信息", notes = "通过id查询用户信息")
    public Result<UserInfo> findById(Integer userId) {
        return Result.success(userService.findInfoById(userId));
    }

    @ApiOperation(value = "通过id查询用户信息(系统内部调用)", notes = "通过id查询用户信息(系统内部调用)")
    public UserModel findByIdInter(Integer userId) {
        return userService.findById(userId);
    }

    public Result<UserOrgRefModel> getOrg() {
        return null;
    }

    public Result<UserOrgRefModel> getOrgByUserId(Integer userId) {
        return null;
    }

    public Result<List<Integer>> getRoleIdsByUserId(Integer userId) {
        return null;
    }

    public Result<?> updatePassword(UserPasswordDto userPassword) {
        return null;
    }

    public Result<?> resetPasswordById(Integer userId) {
        return null;
    }

    public Result<?> updateStatus(Integer userId, Integer enable) {
        return null;
    }

    public Result<?> updateIcon(MultipartHttpServletRequest request, Integer userId) {
        return null;
    }

    public Result<?> page(Integer pageNo, Integer pageSize, UserOrgRefModel org) {
        return null;
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<?> register(UserDto userDto) {
        return Result.success(userService.register(userDto));
    }

    public Result<?> update(UserModel model) {
        return null;
    }

    public Result<?> del(Integer id) {
        return null;
    }

    public Result<?> delAll(List<Integer> ids) {
        return null;
    }

    public UserModel findByUsername(String username) {
        return userService.findByUsername(username);
    }

    public List<RoleModel> getRoleModelsByUserId(Integer userId) {
        return null;
    }

    public List<String> getAllPerms(Integer userId) {
        return null;
    }

    public List<MenuModel> getMenuListByUserId(Integer userId) {
        return null;
    }

    public Result<UserOrgRefModel> getOrgInfoByUserId(Integer userId) {
        return null;
    }
}
