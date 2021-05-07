package com.chenyu.cloud.auth.mapper;


import com.chenyu.cloud.auth.model.UserModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int countByUsername(@Param("username") String username);

//    int deleteByExample(UmsAdminExample example);

    int deleteById(Long id);

    int insert(UserModel model);

//    int insertSelective(UserModel record);

    List<UserModel> findAll();

    UserModel findById(Integer id);

    UserModel findByUsername(String userName);

//    int updateByExampleSelective(@Param("record") UserModel record, @Param("example") UmsAdminExample example);

//    int updateByExample(@Param("record") UserModel record, @Param("example") UmsAdminExample example);

//    int updateByPrimaryKeySelective(UserModel record);

//    int updateByPrimaryKey(UserModel record);

    void updateLoginIp(UserModel model);

}