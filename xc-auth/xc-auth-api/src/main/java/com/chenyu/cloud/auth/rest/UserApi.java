package com.chenyu.cloud.auth.rest;

import com.chenyu.cloud.auth.dto.UserDto;
import com.chenyu.cloud.auth.dto.UserPasswordDto;
import com.chenyu.cloud.auth.model.*;
import com.chenyu.cloud.common.constants.FeignConstants;
import com.chenyu.cloud.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * 用户feign-api
 * Created by JackyChen on 2021/04/29.
 */
@FeignClient(FeignConstants.XC_CLOUD_AUTH)
public interface UserApi {

    /** 标题 */
    String TITLE = "用户管理";
    /** 子标题 */
    String SUB_TITLE = "用户";

    /**
     * 当前登陆用户信息
     * @return Result
     */
    @GetMapping("/user/info")
    Result<UserInfo> getInfo();

    /**
     * 通过用户id查询用户信息
     * @return Result
     */
    @GetMapping("/user/info/{id}")
    Result<UserInfo> findById(@PathVariable(name = "id") Integer userId);

    /**
     * 通过用户id查询用户信息(包含密码)--内部调用
     * @return Result
     */
    @GetMapping("/user/inter/{id}")
    UserModel findByIdInter(@PathVariable(name = "id") Integer userId);

    /**
     * 当前登陆用户信息
     * @return Result
     */
    @GetMapping("/user/org")
    Result<UserOrgRefModel> getOrg();

    /**
     * 当前登陆用户信息
     * @return Result
     */
    @GetMapping("/user/{id}")
    Result<UserOrgRefModel> getOrgByUserId(@PathVariable(name = "id") Integer userId);

    /**
     * 根据 userId 获得用户角色Id集合
     * @param userId 用户Id
     * @return Result
     */
    @GetMapping("/user/role/ids/{userId}")
    Result<List<Integer>> getRoleIdsByUserId(@PathVariable("userId") Integer userId);


    /**
     * 修改密码
     * @return Result
     */
    @PutMapping("/user/password")
    Result<?> updatePassword(@RequestBody UserPasswordDto userPassword);

    /**
     * 重置密码 ID
     * @return Result
     */
    @PutMapping("/user/reset/password/{id}")
    Result<?> resetPasswordById(@PathVariable("id") Integer userId);

    /**
     * 变更账户状态
     *
     * @param userId 用户ID
     * @param enable 启用状态 (0->禁用；1->启用)
     * @return Result
     */
    @PutMapping("/user/status")
    Result<?> updateStatus(@RequestParam("userId") Integer userId, @RequestParam("enable") Integer enable);

    /**
     * 上传头像
     * @param request 文件流 request
     * @return Result
     */
    @PutMapping("/user/icon/{id}")
    Result<?> updateIcon(MultipartHttpServletRequest request, @PathVariable("id") Integer userId);

    /**
     * 用户信息 查询分页
     * @param pageNo 当前页
     * @param pageSize 每页条数
     * @param request request
     * @return Result
     */
    @GetMapping("/user")
    Result<?> page(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            UserOrgRefModel org
    );

    /**
     * 用户信息 新增(注册用户)
     * @param userDto 模型
     * @return Result
     */
    @PostMapping("/user/register")
    Result<?> register(@RequestBody UserDto userDto);

    /**
     * 用户信息 修改
     * @param model 模型
     * @return Result
     */
    @PutMapping("/user")
    Result<?> update(@RequestBody UserModel model);


    /**
     * 用户信息 删除
     * @param id ID
     * @return Result
     */
    @DeleteMapping("/user/{id}")
    Result<?> del(@PathVariable("id") Integer id);

    /**
     * 用户信息 批量删除
     * @param ids ID 数组
     * @return Result
     */
    @PostMapping("/user/batch")
    Result<?> delAll(@RequestBody List<Integer> ids);

    /**
     * 根据 username 获得用户
     * @param username 用户名
     * @return Result
     */
    @GetMapping("/user/name/{username}")
    UserModel findByUsername(@PathVariable("username") String username);

    /**
     * 根据 userId 获得用户角色
     * @param userId 用户Id
     * @return Result
     */
    @GetMapping("/user/roles/{userId}")
    List<RoleModel> getRoleModelsByUserId(Integer userId);

    /**
     * 根据 userId 获得用户权限
     * @param userId 用户Id
     * @return Result
     */
    @GetMapping("/user/perms/{userId}")
    List<String> getAllPerms(@PathVariable("userId") Integer userId);

    /**
     * 根据 userId 获得用户菜单
     * @param userId 用户Id
     * @return Result
     */
    @GetMapping("/user/menus/{userId}")
    List<MenuModel> getMenuListByUserId(@PathVariable("userId") Integer userId);


    /**
     * 当前登陆用户信息
     * @return Result
     */
    @GetMapping("/user/org/{userId}")
    Result<UserOrgRefModel> getOrgInfoByUserId(@PathVariable("userId") Integer userId);

}
