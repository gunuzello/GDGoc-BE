package com.gdg.gdg_homepage.member.repository.google;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleLoginManager {
	private final String GOOGLE_TOKEN_URL_HOST = "https://oauth2.googleapis.com";
	private final String GOOGLE_USER_URL_HOST = "https://www.googleapis.com";

	@Value("${social.google.client_id}")
	private String clientId;

	@Value("${social.google.client_secret}")
	private String clientSecret;

	private String getRedirectUri(HttpServletRequest servletRequest) {
		String serverName = servletRequest.getServerName();
		return "http://localhost:3000/auth/social?provider=google";
		// if (serverName.startsWith("localhost")) {
		// } else if (serverName.startsWith("dev")) {
		//
		// } else {
		// 	return "https://dreamai.studio/auth/social?provider=google";
		// }
	}

	public String getAccessToken(String code, HttpServletRequest servletRequest) {
		String redirectUri = getRedirectUri(servletRequest);
		String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
		GoogleTokenResponseDto googleTokenResponseDto =
			WebClient.create(GOOGLE_TOKEN_URL_HOST)
				.post()
				.uri(uriBuilder -> uriBuilder.scheme("https").path("/token").build(true))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(
					BodyInserters.fromFormData("code", decodedCode)
						.with("client_id", clientId)
						.with("client_secret", clientSecret)
						.with("grant_type", "authorization_code")
						.with("redirect_uri", redirectUri))
				.retrieve()
				.bodyToMono(GoogleTokenResponseDto.class)
				.block();

		return googleTokenResponseDto.getAccessToken();
	}

	public GoogleUserInfoResponseDto getUserInfo(String accessToken) {

		return WebClient.create(GOOGLE_USER_URL_HOST)
			.get()
			.uri(uriBuilder -> uriBuilder.scheme("https").path("/userinfo/v2/me").build(true))
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
			.retrieve()
			.bodyToMono(GoogleUserInfoResponseDto.class)
			.block();
	}
}