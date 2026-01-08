package com.gdg.gdg_homepage.common.controller.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Authentication authenticate(Authentication authentication) {
		try {

			String token = (String)authentication.getCredentials();

			// 토큰 유효성 검증
			if (!jwtTokenProvider.validateToken(token)) {
				throw new AuthException(ErrorCode.JWT_EXPIRED_TOKEN);
			}

			// 토큰에서 클레임 추출
			JsonObject element = jwtTokenProvider.extractValue(token);
			TokenInfo tokenInfo = TokenInfo.builder()
				.memberId(element.get("id").getAsLong())   // `.getAsLong()` 사용
				.role(element.get("role").getAsString())   // `.getAsString()` 사용
				.tokenType(element.get("tokenType").getAsString())
				.build();

			// 권한 정보 생성
			List<GrantedAuthority> authorities = Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + tokenInfo.role())
			);

			// 인증된 JwtAuthenticationToken 반환
			return new JwtAuthenticationToken(tokenInfo, token, authorities);

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// JwtAuthenticationToken 타입의 인증만 처리
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
