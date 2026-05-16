package com.mindrealm.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: OpenApiConfig
 * @description: OpenAPI/Swagger配置类,配置API文档的标题、描述、版本及JWT认证方案
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.24
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置OpenAPI文档信息,包括项目信息和Bearer Token认证方案
     * @return OpenAPI实例,包含标题、描述、联系人及JWT安全配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("心域 API")
                        .description("青少年心理数字孪生系统 - 心域 (MindRealm)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("心域团队")
                                .email("spring_dev_ljw@163.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("输入JWT Token")));
    }
}
