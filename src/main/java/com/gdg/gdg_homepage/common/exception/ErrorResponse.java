package com.gdg.gdg_homepage.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
	String code,
	String message,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	LocalDateTime timestamp
) {
	public static ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.from(errorCode));
	}

	public static ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode, String message) {
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(ErrorResponse.of(errorCode, message));
	}

	public static ErrorResponse from(ErrorCode errorCode) {
		return new ErrorResponse(
			errorCode.getCode(),
			errorCode.getMessage(),
			LocalDateTime.now()
		);
	}

	public static ErrorResponse of(ErrorCode errorCode, String message) {
		return new ErrorResponse(
			errorCode.getCode(),
			message,
			LocalDateTime.now()
		);
	}
}
