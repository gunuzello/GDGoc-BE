package com.gdg.gdg_homepage.common.exception;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
	private final ErrorCode errorCode;

	public MemberException(ErrorCode errorCode) {
		super(String.format("[%s] %s", errorCode.getCode(), errorCode.getMessage()));
		this.errorCode = errorCode;
	}

	public MemberException(Throwable cause, ErrorCode errorCode) {
		super(String.format("[%s] %s", errorCode.getCode(), errorCode.getMessage()), cause);
		this.errorCode = errorCode;
	}
}
