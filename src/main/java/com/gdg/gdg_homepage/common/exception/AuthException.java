package com.gdg.gdg_homepage.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
	private final ErrorCode errorCode;

	public AuthException(ErrorCode errorCode) {
		super(String.format("[%s] %s", errorCode.getCode(), errorCode.getMessage()));
		this.errorCode = errorCode;
	}

	public AuthException(Throwable cause, ErrorCode errorCode) {
		super(String.format("[%s] %s", errorCode.getCode(), errorCode.getMessage()), cause);
		this.errorCode = errorCode;
	}
}
