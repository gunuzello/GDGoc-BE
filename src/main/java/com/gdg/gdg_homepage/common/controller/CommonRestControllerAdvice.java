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


	@ExceptionHandler(HttpServerErrorException.GatewayTimeout.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleGatewayTimeout(
		HttpServerErrorException e, HandlerMethod method) {
		log.error(
			"GatewayTimeout 발생 method: {} ,\n{}", method.getMethod().getName(), e.getMessage(), e);

		String detailMessage =
			"이미지 생성 시간이 초과되었습니다\n" + "죄송합니다. 현재 서버가 혼잡하여 이미지 생성 요청이 완료되지 않았습니다. 잠시 후 다시 시도해 주시기 바랍니다.";

		return ErrorResponse.createErrorResponseEntity(ErrorCode.SYSTEM_ERROR, detailMessage);
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
