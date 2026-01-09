package com.gdg.gdg_homepage.common.controller.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.gdg.gdg_homepage.member.repository.Member;
import com.google.gson.JsonObject;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 관련 비즈니스 로직 및 HTTP 통합을 담당하는 서비스
 * - HTTP 요청/응답과의 통합
 * - 토큰 생성, 갱신, 검증 등의 비즈니스 로직
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {

	private final JwtTokenProvider jwtTokenProvider;
	@Value("${jwt.accessExpiration}")
	private long accessTokenValidTime;
	@Value("${jwt.refreshExpiration}")
	private long refreshTokenValidTime;

	/**
	 * 헤더에 토큰 설정
	 *
	 * @param member   회원 정보
	 * @param response HTTP 응답
	 */
	public void setTokenInHeader(Member member, HttpServletResponse response) {
		String accessToken = createToken(member, accessTokenValidTime, "access");
		String refreshToken = createToken(member, refreshTokenValidTime, "refresh");
		setTokenHeaders(response, accessToken, refreshToken);
	}

	/**
	 * 헤더와 쿠키에 토큰 설정
	 *
	 * @param member   회원 정보
	 * @param response HTTP 응답
	 */
	public void setTokenInHeaderWithCookie(Member member, HttpServletResponse response) {
		String accessToken = createToken(member, accessTokenValidTime, "access");
		String refreshToken = createToken(member, refreshTokenValidTime, "refresh");

		setTokenHeaders(response, accessToken, refreshToken);
		setTokenCookies(response, accessToken, refreshToken);
	}

	/**
	 * 리프레시 토큰 검증
	 *
	 * @param request HTTP 요청
	 * @return 토큰 유효 여부
	 */
	public Boolean validateRefreshToken(HttpServletRequest request) {
		log.info("Validating refresh token");

		try {
			String refreshToken = resolveRefreshToken(request);
			return jwtTokenProvider.validateToken(refreshToken);
		}
		 catch (Exception e) {
			log.error("Unexpected error during token validation: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 액세스 토큰 검증
	 *
	 * @param request HTTP 요청
	 * @return 토큰 유효 여부
	 */
	public Boolean validateAccessToken(HttpServletRequest request) {
		String accessToken = resolveAccessToken(request);
		if (accessToken == null) {
			return false;
		}
		return jwtTokenProvider.validateToken(accessToken);
	}

	/**
	 * 요청에서 액세스 토큰 단순 추출
	 *
	 * @param request HTTP 요청
	 * @return 액세스 토큰
	 */
	public String resolveAccessToken(HttpServletRequest request) {
		String token = extractTokenFromHeader(request, "Authorization", "Bearer ");
		if (token != null) {
			return token;
		}
		return extractTokenFromCookie(request, "access");
	}

	/**
	 * 요청에서 리프레시 토큰 추출
	 *
	 * @param request HTTP 요청
	 * @return 리프레시 토큰
	 */
	public String resolveRefreshToken(HttpServletRequest request) {
		log.info("Resolving refresh token from request");

		try {
			String token = extractTokenFromHeader(request, "Refresh-Token", "Bearer ");

			if (token == null) {
				token = extractTokenFromCookie(request, "refresh");
			}

			if (token == null) {
				throw new AuthException(ErrorCode.JWT_NOT_FOUND);
			}

			String tokenType = jwtTokenProvider.extractTokenType(token);
			if (!tokenType.equals("refresh")) {
				throw new AuthException(ErrorCode.JWT_INVALID_FORMAT);
			}

			return token;
		} catch (AuthException e) {
			log.error("Failed to resolve refresh token: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error during token resolution: {}", e.getMessage());
			throw new AuthException(ErrorCode.JWT_NOT_FOUND);
		}
	}

	/**
	 * 토큰 생성
	 *
	 * @param member         회원 정보
	 * @param tokenValidTime 토큰 유효 시간
	 * @param tokenType      토큰 타입
	 * @return 생성된 토큰
	 */
	private String createToken(Member member, long tokenValidTime, String tokenType) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", member.getId());
		jsonObject.addProperty("role", member.getMemberRole().name());
		jsonObject.addProperty("tokenType", tokenType);

		String encryptedSubject = jwtTokenProvider.encrypt(jsonObject.toString());
		return jwtTokenProvider.createToken(encryptedSubject, tokenValidTime);
	}

	/**
	 * 헤더에 토큰 설정
	 *
	 * @param response     HTTP 응답
	 * @param accessToken  액세스 토큰
	 * @param refreshToken 리프레시 토큰
	 */
	private void setTokenHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setHeader("Authorization", "Bearer " + accessToken);
		response.setHeader("Refresh-Token", "Bearer " + refreshToken);
	}

	/**
	 * 쿠키에 토큰 설정
	 *
	 * @param response     HTTP 응답
	 * @param accessToken  액세스 토큰
	 * @param refreshToken 리프레시 토큰
	 */
	private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
		int oneDay = 86400; // 24시간 (초 단위)

		addSecureCookie(response, "access", accessToken, oneDay);
		addSecureCookie(response, "refresh", refreshToken, oneDay);
	}

	/**
	 * 보안 쿠키 추가
	 *
	 * @param response HTTP 응답
	 * @param name     쿠키 이름
	 * @param value    쿠키 값
	 * @param maxAge   쿠키 수명
	 */
	private void addSecureCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true); // JS에서 접근 불가
		cookie.setMaxAge(maxAge);
		cookie.setSecure(true);
		response.addCookie(cookie);
	}

	/**
	 * 헤더에서 토큰 추출
	 *
	 * @param request    HTTP 요청
	 * @param headerName 헤더 이름
	 * @param prefix     접두사
	 * @return 추출된 토큰
	 */
	private String extractTokenFromHeader(HttpServletRequest request, String headerName, String prefix) {
		String header = request.getHeader(headerName);
		if (header != null && header.startsWith(prefix)) {
			return header.substring(prefix.length()).trim();
		}
		return null;
	}

	/**
	 * 쿠키에서 토큰 추출
	 *
	 * @param request    HTTP 요청
	 * @param cookieName 쿠키 이름
	 * @return 추출된 토큰
	 */
	private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookieName.equals(cookie.getName()))
				.findFirst()
				.map(Cookie::getValue)
				.orElse(null);
		}
		return null;
	}
}
