package com.samsung.portalserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @desc API CORS 정책 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 허용할 API 호출 주소 패턴
                .allowedOrigins("*") // 허용할 Domain(CALLER)
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // 허용할 METHODS
    }
}
