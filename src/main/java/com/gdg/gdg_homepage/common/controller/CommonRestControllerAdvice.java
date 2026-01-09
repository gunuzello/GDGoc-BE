package com.gdg.gdg_homepage.common.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.gdg.gdg_homepage.common.exception.ErrorResponse;
import com.gdg.gdg_homepage.common.exception.MemberException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CommonRestControllerAdvice {


	@ExceptionHandler(AuthException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> authException(AuthException e) {
		log.error("AuthException 발생: {}", e.getErrorCode(), e);
		return ErrorResponse.createErrorResponseEntity(e.getErrorCode());
	}


	@ExceptionHandler(MemberException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> memberException(MemberException e) {
		log.error("MemberException 발생: {}", e.getErrorCode(), e);
		return ErrorResponse.createErrorResponseEntity(e.getErrorCode());
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException 발생: {}", e.getMessage(), e);

		String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		return ErrorResponse.createErrorResponseEntity(ErrorCode.VALIDATION_ERROR, errorMessage);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleValidationException(
		HandlerMethodValidationException e) {
		List<String> errors =
			e.getAllValidationResults().stream()
				.flatMap(result -> result.getResolvableErrors().stream())
				.map(MessageSourceResolvable::getDefaultMessage)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		String errorMessage = String.join(", ", errors);
		log.error("valid 검증 에러가 발생하였습니다 : {}", errorMessage);

		return ErrorResponse.createErrorResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
	}

}
