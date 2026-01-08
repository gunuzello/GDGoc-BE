package com.gdg.gdg_homepage.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.gdg_homepage.common.controller.security.JwtAuthenticationProvider;
import com.gdg.gdg_homepage.common.controller.security.JwtAuthorizationTokenFilter;
import com.gdg.gdg_homepage.common.controller.security.JwtTokenManager;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationProvider jwtAuthenticationProvider;
	private final JwtTokenManager jwtTokenManager;
	private final ObjectMapper objectMapper;
	private final String[] permitAllPaths;

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(jwtAuthenticationProvider);
	}

	@Bean
	public JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter() {
		return new JwtAuthorizationTokenFilter(jwtTokenManager, authenticationManager(), objectMapper, permitAllPaths);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.headers(
				httpSecurityHeadersConfigurer ->
					httpSecurityHeadersConfigurer.frameOptions(FrameOptionsConfig::disable))
			// CSRF 비활성화
			.csrf(AbstractHttpConfigurer::disable)
			// 세션 비활성화 (JWT 사용)
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// 기본 로그인 방식들 비활성화
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			// 요청 인가 규칙 설정
			.authorizeHttpRequests(
				auth -> auth.requestMatchers(permitAllPaths).permitAll().anyRequest().authenticated())

			// 인증 실패 시 401 에러
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					response.sendError(401, "Unauthorized");
				})
			)
			// CORS 설정
			.cors(cors -> cors.configure(http))

			// Authentication Manager 설정
			.authenticationManager(authenticationManager())

			// JWT 필터 추가
			.addFilterBefore(jwtAuthorizationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
