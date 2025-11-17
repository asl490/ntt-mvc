package com.ntt.prueba.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("NTT Prueba MVC")
                                                .version("1.0")
                                                .description("NTT Prueba MVC"))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }

        @Bean
        public OperationCustomizer customOperationIdCustomizer() {
                return (Operation operation, HandlerMethod handlerMethod) -> {
                        String serviceName = operation.getTags() != null && !operation.getTags().isEmpty()
                                        ? operation.getTags().getFirst()
                                        : "DefaultService";
                        serviceName = serviceName.replace("-controller", "");
                        String methodName = handlerMethod.getMethod().getName();
                        String operationId = serviceName + capitalize(methodName);

                        operation.setOperationId(operationId);

                        return operation;
                };
        }

        private String capitalize(String str) {
                if (str == null || str.isEmpty())
                        return str;
                return str.substring(0, 1).toUpperCase() + str.substring(1);
        }

}