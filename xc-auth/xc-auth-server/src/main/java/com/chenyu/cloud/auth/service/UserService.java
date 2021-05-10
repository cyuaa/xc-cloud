package com.chenyu.cloud.auth.service;

import com.chenyu.cloud.auth.dto.LoginDto;
import com.chenyu.cloud.auth.dto.UserDto;
import com.chenyu.cloud.auth.dto.UserPasswordDto;
import com.chenyu.cloud.auth.model.*;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.security.util.UserTokenUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户service
 * Created by JackyChen on 2021/04/28.
 */
public interface UserService {

    /**
     * 根据用户名获取后台管理员
     */
    UserModel findByUsername(String username);

    /**
     * 根据id查询用户信息
     */
    UserInfo findInfoById(Integer id);

    /**
     * 根据id查询用户信息
     */
    UserModel findById(Integer id);

    /**
     * 注册功能
     */
    UserModel register(UserDto userDto, HttpServletRequest request);

    /**
     * 登录功能
     * @return 生成的JWT的token
     */
    Result<UserTokenUtil.TokenRet> login(LoginDto loginDto, HttpServletRequest request);

    /**
     * 刷新token的功能
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

    /**
     * 根据用户id获取用户
     */
    UserModel getItem(Integer id);

    /**
     * 根据用户名或昵称分页查询用户
     */
    List<UserModel> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 修改指定用户信息
     */
    int update(Integer id, UserModel admin);

    /**
     * 删除指定用户
     */
    int delete(Integer id);

    /**
     * 修改用户角色关系
     */
    @Transactional
    int updateRole(Integer userId, List<Integer> roleIds);

    /**
     * 获取用户对于角色
     */
    List<RoleModel> getRoleList(Integer userId);

    /**
     * 获取指定用户的可访问资源
     */
    List<UserResourceModel> getResourceList(Integer userId);

    /**
     * 修改用户的+-权限
     */
    @Transactional
    int updatePermission(Integer userId, List<Integer> permissionIds);

    /**
     * 获取用户所有权限（包括角色权限和+-权限）
     */
    List<PermissionModel> getPermissionList(Integer userId);

    /**
     * 修改密码
     */
    int updatePassword(UserPasswordDto userPasswordDto);

}
