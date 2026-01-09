package com.gdg.gdg_homepage.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {

	@Bean
	public String[] permitAllPaths() {
		return new String[] {
			"/public/**",
			"/",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/member/**",
			"/h2-console/**",
		};
	}
}
