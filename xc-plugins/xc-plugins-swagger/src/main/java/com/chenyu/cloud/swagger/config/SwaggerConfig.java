package com.chenyu.cloud.swagger.config;

import com.chenyu.cloud.common.constants.CoreConstants;
import com.chenyu.cloud.common.properties.GlobalProperties;
import com.fasterxml.classmate.TypeResolver;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * SwaggerConfig
 * Created by JackyChen on 2021/04/14.
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
@Slf4j
public class SwaggerConfig {

    private final TypeResolver typeResolver;

    @Autowired
    private GlobalProperties globalProperties;

    @Autowired
    public SwaggerConfig(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    // ========================= Swagger =========================

    /**
     * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     *
     * @return Docket
     */
    @Bean
    public Docket createRestApi() {
        log.info("=====> Swagger Init Success!!! <=====");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("Xc-Cloud 1.0")
                .select()
                //此包路径下的类，才生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.chenyu.cloud"))
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.<SecurityScheme>newArrayList(apiKey()));
    }


    /**
     * api文档的详细信息函数,注意这里的注解引用的是哪个
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // //大标题
                .title(CoreConstants.PLATFORM_NAME + " 服务API接口文档")
                // 版本号
                .version("1.0")
                // 描述
                .description("后台API接口")
                // 作者
                .contact(new Contact("JackyChen", "https://cloud.chenyu.com", "chenyudalao@gmail.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }


    /**
     * 安全模块
     * @return
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    /**
     * jwt token
     * @return
     */
    private List<Parameter> defaultToken() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameters= Lists.newArrayList();
        parameterBuilder.name(globalProperties.getAuth().getToken().getTokenHeader())
                .description("Token 令牌")
                .modelRef(new ModelRef("String"))
                .parameterType("header")
                .required(false).build();
        parameters.add(parameterBuilder.build());
        return parameters;
    }

    /**
     * oauth2 授权
     * @return
     */
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference(globalProperties.getAuth().getToken().getTokenHeader(), authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey(globalProperties.getAuth().getToken().getTokenHeader(), globalProperties.getAuth().getToken().getTokenHeader(), "header");
    }


}
