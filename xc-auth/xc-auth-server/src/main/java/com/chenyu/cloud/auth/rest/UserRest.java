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
public class UserRest implements UserApi {

    @Autowired
    private UserService userService;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<UserTokenUtil.TokenRet> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        return userService.login(loginDto, request);
    }

    @ApiOperation("当前登陆用户信息")
    @GetMapping("/info")
    @Override
    public Result<UserInfo> getInfo() {
        UserModel user = UserUtil.getUser();
        return Result.success(userService.findInfoById(user.getId()));
    }

    @ApiOperation(value = "通过id查询用户信息", notes = "通过id查询用户信息")
    @Override
    public Result<UserInfo> findById(Integer userId) {
        return Result.success(userService.findInfoById(userId));
    }

    @ApiOperation(value = "通过id查询用户信息(系统内部调用)", notes = "通过id查询用户信息(系统内部调用)")
    @Override
    public UserModel findByIdInter(Integer userId) {
        return userService.findById(userId);
    }

    @Override
    public Result<UserOrgRefModel> getOrg() {
        return null;
    }

    @Override
    public Result<UserOrgRefModel> getOrgByUserId(Integer userId) {
        return null;
    }

    @Override
    public Result<List<Integer>> getRoleIdsByUserId(Integer userId) {
        return null;
    }

    @Override
    public Result<?> updatePassword(UserPasswordDto userPassword) {
        return null;
    }

    @Override
    public Result<?> resetPasswordById(Integer userId) {
        return null;
    }

    @Override
    public Result<?> updateStatus(Integer userId, Integer enable) {
        return null;
    }

    @Override
    public Result<?> updateIcon(MultipartHttpServletRequest request, Integer userId) {
        return null;
    }

    @Override
    public Result<?> page(Integer pageNo, Integer pageSize, UserOrgRefModel org) {
        return null;
    }

    /**
     * 用户信息 新增(注册用户)
     * @param userDto 模型
     * @return Result
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<?> register(UserDto userDto, HttpServletRequest request) {
        return Result.success(userService.register(userDto, request));
    }

    @Override
    public Result<?> update(UserModel model) {
        return null;
    }

    @Override
    public Result<?> del(Integer id) {
        return null;
    }

    @Override
    public Result<?> delAll(List<Integer> ids) {
        return null;
    }

    @ApiOperation("通过用户名查询用户")
    @GetMapping("/name/{username}")
    @Override
    public UserModel findByUsername(String username) {
        return userService.findByUsername(username);
    }

    @Override
    public List<RoleModel> getRoleModelsByUserId(Integer userId) {
        return null;
    }

    @Override
    public List<String> getAllPerms(Integer userId) {
        return null;
    }

    @Override
    public List<MenuModel> getMenuListByUserId(Integer userId) {
        return null;
    }

    @Override
    public Result<UserOrgRefModel> getOrgInfoByUserId(Integer userId) {
        return null;
    }
}
