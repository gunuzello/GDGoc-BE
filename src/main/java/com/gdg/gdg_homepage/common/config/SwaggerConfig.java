package com.gdg.gdg_homepage.common.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${spring.profiles.active:local}")
	private String activeProfile;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(
				new Info()
					.title("API Documentation")
					.version("1.0")
					.description("API documentation with JWT authentication"))
			.servers(getServers())
			.components(
				new Components()
					.addSecuritySchemes(
						"accessToken",
						new SecurityScheme()
							.type(SecurityScheme.Type.HTTP)
							.scheme("bearer")
							.bearerFormat("JWT")))
			.addSecurityItem(new SecurityRequirement().addList("accessToken"));
	}

	// 활성화된 프로필에 따라 서버 목록 결정
	private List<Server> getServers() {
		List<Server> servers = new ArrayList<>();

			servers.add(
				new Server().url("http://localhost:8080").description("Local Development Server"));
		servers.add(
			new Server().url("https://port-0-gdgoc-be-mk6fg8a664f60d0d.sel3.cloudtype.app").description("Production Development Server"));


		return servers;
	}

	@Bean
	public OperationCustomizer customOperationCustomizer() {
		return (Operation operation, HandlerMethod handlerMethod) -> {

			Arrays.stream(handlerMethod.getMethodParameters())
				.findFirst()
				.ifPresent(
					param -> {
						operation.addParametersItem(
							new Parameter()
								.in("header")
								.name("Authorization")
								.description("JWT AccessToken")
								.required(true)
								.schema(new StringSchema().example("accesstoken")));
					});
			return operation;
		};
	}
}
