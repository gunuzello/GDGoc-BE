package com.gdg.gdg_homepage.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// JWT 관련 에러 코드
	JWT_INVALID_FORMAT("JWT_001", 401, "잘못된 JWT 형식입니다"),
	JWT_INVALID_VALUE("JWT_003", 400, "유효하지 않은 값입니다"),
	JWT_RUNTIME_EXCEPTION("JWT_004", 500, "런타임 오류가 발생했습니다"),
	JWT_EXPIRED_TOKEN("JWT_005", 401, "만료된 토큰입니다"),
	JWT_NOT_FOUND("JWT_006", 401, "JWT 토큰이 없거나 유효하지 않습니다"),

	// ChatGPT 관련 에러 코드


	// 페이지 파싱 관련 에러

	// 책(Story) 관련 에러 코드

	// 이미지 생성 에러 코드

	// 권한 에러
	INVALID_MEMBER_ROLE("AUTH_004", 400, "권한이 없습니다."),
	AUTH_SOCIAL_INVALID_PARAMETER("AUTH_003", 400, "로그인 중 에러가 발생하였습니다. 잠시 후 다시 시도해주세요."),
	AUTH_KAKAO_SERVER_ERROR("AUTH_004", 400, "구글 서버에 오류가 발생했습니다."),
	MEMBER_PASSWORD_NOT_FOUND("AUTH_007", 400, "비밀번호 입력은 필수입니다"),
	MEMBER_NOT_FOUND("MEMBER_002", 404, "회원을 찾을 수 없습니다"),
	MEMBER_PASSWORD_MISMATCH("MEMBER_004", 400, "비밀번호가 일치하지 않습니다"),
	MEMBER_USERNAME_ALREADY_EXIST("MEMBER_005", 400, "이미 존재하는 아이디입니다."),
	MEMBER_EMAIL_ALREADY_EXIST("MEMBER_006", 400, "이미 존재하는 이메일입니다."),
	MEMBER_SOFT_DELETED("MEMBER_008", 400, "탈퇴 대기중인 회원입니다."),
	SYSTEM_ERROR("E901", 500, "시스템 오류가 발생했습니다"),
	INVALID_INPUT_VALUE("VAL_001", 400, "입력값이 올바르지 않습니다"),
	VALIDATION_ERROR("VALID_001", 400, "잘못된 입력입니다.");



	private final String code;
	private final int status;
	private final String message;

	ErrorCode(String code, int status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}
}
