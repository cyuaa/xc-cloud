package com.chenyu.cloud.test;

/**
 * Feign测试
 * Created by JackyChen on 2021/05/13.
 */

import com.chenyu.cloud.AuthApplication;
import com.chenyu.cloud.auth.api.UserApi;
import com.chenyu.cloud.auth.model.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= AuthApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients(value = "com.chenyu.cloud")
public class FeignTest {

    @Autowired
    private UserApi userApi;

    @Test
    public void test() {
        UserModel model = userApi.findByUsername("admin");
        System.out.println(model);
    }


}
