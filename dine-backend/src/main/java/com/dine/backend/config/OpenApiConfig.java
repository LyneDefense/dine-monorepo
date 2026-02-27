package com.dine.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DialNDine API")
                        .version("1.0.0")
                        .description("AI-Powered Restaurant Phone Ordering Platform API Documentation")
                        .contact(new Contact()
                                .name("DialNDine Team")
                                .email("support@dialndine.com")))
                .servers(List.of(
                        new Server().url("/").description("Current Server")))
                .tags(List.of(
                        new Tag().name("Authentication").description("用户认证相关接口"),
                        new Tag().name("Restaurant").description("餐厅管理接口"),
                        new Tag().name("Menu Category").description("菜单分类管理接口"),
                        new Tag().name("Menu Item").description("菜单项管理接口"),
                        new Tag().name("Combo").description("套餐管理接口"),
                        new Tag().name("Order").description("订单管理接口"),
                        new Tag().name("Dining Section").description("餐区管理接口"),
                        new Tag().name("Dining Table").description("餐桌管理接口"),
                        new Tag().name("AI Phone Agent").description("AI电话代理接口"),
                        new Tag().name("Account").description("账户管理接口")))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Token Authentication")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }
}
