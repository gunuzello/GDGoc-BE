package com.gdg.gdg_homepage.common.controller.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.gdg.gdg_homepage.common.exception.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {
	private static final String TOKEN_INFO_ATTRIBUTE = "tokenInfo";
	private final JwtTokenManager jwtTokenManager;
	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final PathMatcher pathMatcher = new AntPathMatcher();
	private final String[] permitAllPaths;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return Arrays.stream(permitAllPaths)
			.anyMatch(pattern -> pathMatcher.match(pattern, uri));
	}

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull FilterChain filterChain)
		throws IOException {
		try {
			processAuthentication(request);
			filterChain.doFilter(request, response);
		} catch (AuthException e) {
			handleAuthException(response, e.getErrorCode());
		} catch (Exception e) {
			handleUnexpectedException(response);
		}
	}

	private void processAuthentication(HttpServletRequest request) {
		try {
			String accessToken = jwtTokenManager.resolveAccessToken(request);
			if (accessToken != null) {
				Authentication authentication =
					authenticationManager.authenticate(new JwtAuthenticationToken(accessToken));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				saveTokenInfoInRequest(authentication);
			}
		} catch (AuthException e) {
			throw e;
		} catch (Exception e) {
			throw new AuthException(ErrorCode.JWT_RUNTIME_EXCEPTION);
		}
	}

	private void saveTokenInfoInRequest(Authentication authentication) {
		TokenInfo tokenInfo = (TokenInfo)authentication.getPrincipal();
		RequestContextHolder.currentRequestAttributes()
			.setAttribute(TOKEN_INFO_ATTRIBUTE, tokenInfo, RequestAttributes.SCOPE_REQUEST);
	}

	private void handleAuthException(HttpServletResponse response, ErrorCode errorCode)
		throws IOException {
		ResponseEntity<ErrorResponse> responseEntity =
			ErrorResponse.createErrorResponseEntity(errorCode);

		writeErrorResponse(response, responseEntity);
	}

	private void handleUnexpectedException(HttpServletResponse response) throws IOException {
		SecurityContextHolder.clearContext();
		ResponseEntity<ErrorResponse> responseEntity =
			ErrorResponse.createErrorResponseEntity(ErrorCode.JWT_RUNTIME_EXCEPTION);
		writeErrorResponse(response, responseEntity);
	}

	private void writeErrorResponse(
		HttpServletResponse response, ResponseEntity<ErrorResponse> responseEntity)
		throws IOException {
		response.setStatus(responseEntity.getStatusCode().value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
	}
}
