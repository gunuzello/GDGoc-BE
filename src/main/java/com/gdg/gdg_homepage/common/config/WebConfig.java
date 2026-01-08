package com.gdg.gdg_homepage.common.config;


import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		CorsConfiguration config = CorsConfig.corsConfiguration();
		registry
			.addMapping("/**")
			.allowedOrigins(Objects.requireNonNull(config.getAllowedOrigins()).toArray(new String[0]))
			.allowedMethods(Objects.requireNonNull(config.getAllowedMethods()).toArray(new String[0]))
			.allowedHeaders(Objects.requireNonNull(config.getAllowedHeaders()).toArray(new String[0]))
			.exposedHeaders(Objects.requireNonNull(config.getExposedHeaders()).toArray(new String[0]))
			.allowCredentials(Boolean.TRUE.equals(config.getAllowCredentials()))
			.allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드 지정
			.allowedHeaders("*") // 모든 헤더 허용
			.exposedHeaders("X-Story-Title")
			.allowCredentials(true) // 자격 증명 허용 여부
			.maxAge(3600); // preflight 요청 캐시 시간
	}
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
