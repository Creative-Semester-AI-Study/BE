package com.sejong.aistudyassistant;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "aistudy API 명세서",
                description = "aistudy 서비스 API 명세서",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {
}