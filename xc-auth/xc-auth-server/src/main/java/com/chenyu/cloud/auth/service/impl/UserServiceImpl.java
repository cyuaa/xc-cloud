package com.chenyu.cloud.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chenyu.cloud.auth.dto.LoginDto;
import com.chenyu.cloud.auth.dto.UserDto;
import com.chenyu.cloud.auth.dto.UserPasswordDto;
import com.chenyu.cloud.auth.mapper.UserMapper;
import com.chenyu.cloud.auth.model.*;
import com.chenyu.cloud.auth.service.UserService;
import com.chenyu.cloud.common.enums.DictType;
import com.chenyu.cloud.common.exception.ServiceException;
import com.chenyu.cloud.common.exception.TokenException;
import com.chenyu.cloud.common.response.CommonMsg;
import com.chenyu.cloud.common.response.Result;
import com.chenyu.cloud.common.response.TokenMsg;
import com.chenyu.cloud.common.thread.refuse.AsyncProcessQueueReFuse;
import com.chenyu.cloud.common.util.IPUtil;
import com.chenyu.cloud.common.properties.GlobalProperties;
import com.chenyu.cloud.core.utils.CaptchaUtil;
import com.chenyu.cloud.core.utils.ValidationUtil;
import com.chenyu.cloud.security.component.XcUserDetailsService;
import com.chenyu.cloud.security.util.TenantUtil;
import com.chenyu.cloud.security.util.UserTokenUtil;
import com.chenyu.cloud.security.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * UmsAdminService实现类
 * Created by macro on 2018/4/26.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private GlobalProperties globalProperties;

    @Resource
    private XcUserDetailsService xcUserDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserModel findByUsername(String username) {
        return null;
    }

    @Override
    public UserInfo findInfoById(Integer id) {
        UserModel user = UserUtil.getUser(id);
        if(user == null){
            throw new TokenException(TokenMsg.EXCEPTION_TOKEN_LOSE_EFFICACY);
        }

        // 个人信息 清除敏感信息
        user.setPassword(null);
        List<Integer> userRolesByUserId = UserUtil.getUserRolesByUserId(user.getId());
        List<String> userAllPermsByUserId = UserUtil.getUserAllPermsByUserId(user.getId());
        UserInfo userInfo = BeanUtil.copyProperties(user, UserInfo.class, "password");
        userInfo.setRoles(userRolesByUserId);
        userInfo.setPerms(userAllPermsByUserId);

        return userInfo;
    }

    @Override
    public UserModel findById(Integer id) {
        UserModel user = UserUtil.getUser(id);
        if(user == null){
            throw new TokenException(TokenMsg.EXCEPTION_TOKEN_LOSE_EFFICACY);
        }

        return userMapper.findById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserModel register(UserDto userDto) {
        Assert.notNull(userDto, "User Data Must Not Be Null!");

        String username = userDto.getUsername();
        // 唯一验证
        Integer count = this.uniqueVerificationByName(username);
        if(count != null && count > 0){
            // 重复
            throw new ServiceException("该用户已存在!");
        }

        // 防止非法操作 - 不允许直接操控到 关键数据
        // 需要注意的是 不要轻易改修改策略
        UserModel model = BeanUtil.copyProperties(userDto, UserModel.class, "");
        model.setLoginIp(null);
        // 默认用户状态为启用
        model.setStatus(DictType.NO_YES_YES.getValue());
        // 设置tenantId
        model.setTenantId(UserUtil.getTenantId());

        // 新增可以直接设置密码
        if(StringUtils.isNotEmpty(model.getPassword())){
            // 处理密码
            model.setPassword(
                    UserUtil.handlePassword(model.getPassword(),
                            globalProperties.getAuth().getToken().getSecret())
            );
        }

        userMapper.insert(model);

        // 新增用户 设置默认角色
        if(null != model) {
            //TODO: 新增用户 设置默认角色
            /*String defRole = null;
            // 获得option 缓存中 角色编号
            OptionsModel optionsModel = OptionsUtil.getOptionByCode("def_role");
            if(optionsModel != null){
                defRole = optionsModel.getOptionValue();
            }

            if(StringUtils.isNotBlank(defRole)){
                QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
                roleQueryWrapper.eq("role_code", defRole);
                roleQueryWrapper.eq(
                        HumpUtil.humpToUnderline(MyBatisConstants.FIELD_DELETE_LOGIC),
                        DictType.NO_YES_NO.getValue());
                SysRole sysRole = iRoleService.getOne(roleQueryWrapper);
                if(sysRole != null){
                    // 设置用户默认角色
                    iUserRoleRefService.setRoles(insertModel.getId(),
                            Convert.toStrArray(sysRole.getId()));
                }
            }*/
        }
        return model;
    }

    @Override
    public Result<UserTokenUtil.TokenRet> login(LoginDto form, HttpServletRequest request) {
        // 非空验证
        if(form == null){
            throw new TokenException(TokenMsg.EXCEPTION_LOGIN_NULL);
        }

        // 验证登录对象
        ValidationUtil.verify(form);

        // 判断账号是否临时锁定
        UserTokenUtil.verifyLockAccount(form.getUsername());

        // 获得当前失败次数
        long slipCount = UserTokenUtil.getSlipCount(form.getUsername());

        // 失败次数超过 验证次数阈值 开启验证码验证
        if(slipCount >= UserTokenUtil.LOGIN_PROPERTIES.getSlipVerifyCount()){
            CaptchaUtil.validate(form.getUuid(), form.getCaptcha());
        }

        // 用户信息
        UserModel user = UserUtil.getUserByUserName(form.getUsername());

        // 账号不存在、密码错误
        if(user == null ||
                !user.getPassword().equals(UserUtil.handlePassword(form.getPassword(), globalProperties.getAuth().getToken().getSecret()))) {
            // 判断是否需要锁定账号 这里没有直接抛异常 而是返回错误信息， 其中包含 是否开启验证码状态
            TokenMsg lockAccountMsg = UserTokenUtil.lockAccount(form.getUsername());
            throw new TokenException(lockAccountMsg);
        }

        // 如果验证成功， 则清除锁定信息
        UserTokenUtil.clearLockAccount(form.getUsername());



        // 如果不是超级管理员
        if(!StringUtils.equals(UserUtil.SUPER_ADMIN, user.getUsername())){
            // 账号锁定验证
            if(null == user.getStatus() ||
                    DictType.NO_YES_NO.getValue().equals(user.getStatus())){
                // 账号已被锁定,请联系管理员
                throw new TokenException(TokenMsg.EXCEPTION_LOGIN_ACCOUNT_LOCKED);
            }

            // 租户启用验证
            TenantModel tenant = TenantUtil.getTenant(user.getTenantId());
            if(tenant == null){
                // 租户未启用，请联系管理员
                throw new TokenException(TokenMsg.EXCEPTION_LOGIN_TENANT_NOT_USABLE);
            }
        }

        // 失败次数超过 验证次数阈值 开启验证码验证
        if(slipCount >= UserTokenUtil.LOGIN_PROPERTIES.getSlipVerifyCount()){
            // 删除验证过后验证码
            CaptchaUtil.delCaptcha(form.getUuid());
        }

        UserDetails userDetails = xcUserDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //生成token，并保存到Redis
        Result<UserTokenUtil.TokenRet> result = UserTokenUtil.createToken(user);
        if(CommonMsg.SUCCESS.getCode().equals(result.getCode())){
            // 异步保存IP
            AsyncProcessQueueReFuse.execute(()->{
                // 保存用户最后登录IP
                String clientIpAddress = IPUtil.getClientIpAddress(request);
                user.setLoginIp(clientIpAddress);
                userMapper.updateLoginIp(user);
            });
        }
        return result;
    }

    @Override
    public String refreshToken(String oldToken) {
        return null;
    }

    @Override
    public UserModel getItem(Integer id) {
        return null;
    }

    @Override
    public List<UserModel> list(String keyword, Integer pageSize, Integer pageNum) {
        return null;
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }

    @Override
    public int updateRole(Integer userId, List<Integer> roleIds) {
        return 0;
    }

    @Override
    public List<RoleModel> getRoleList(Integer userId) {
        return null;
    }

    @Override
    public List<UserResourceModel> getResourceList(Integer userId) {
        return null;
    }

    @Override
    public int updatePermission(Integer userId, List<Integer> permissionIds) {
        return 0;
    }

    @Override
    public List<PermissionModel> getPermissionList(Integer userId) {
        return null;
    }

    @Override
    public int updatePassword(UserPasswordDto userPasswordDto) {
        return 0;
    }

    @Override
    public int update(Integer id, UserModel admin) {
        return 0;
    }

    /**
     * 唯一验证 名称
     * @param username 用户名
     * @return Integer
     */
    @Transactional(readOnly = true)
    public Integer uniqueVerificationByName(String username){
        if(StringUtils.isBlank(username)){
            return null;
        }

        return userMapper.countByUsername(username);
    }

}
