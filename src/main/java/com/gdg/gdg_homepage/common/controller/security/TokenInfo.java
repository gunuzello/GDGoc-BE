package com.gdg.gdg_homepage.common.controller.security;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Builder;

@Hidden
@Builder
public record TokenInfo(Long memberId, String role, String tokenType) {

	public static TokenInfo from(Long memberId, String role, String tokenType) {
		return TokenInfo.builder()
			.memberId(memberId)
			.role(role)
			.tokenType(tokenType)
			.build();
	}
}
