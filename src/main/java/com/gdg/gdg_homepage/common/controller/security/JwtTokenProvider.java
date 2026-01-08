package com.gdg.gdg_homepage.common.controller.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰의 생성, 파싱, 검증을 담당하는 컴포넌트
 * - 순수 JWT 관련 기능만 담당
 * - HTTP 요청/응답 처리는 포함하지 않음
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.aesKey}")
	private String aesKey;

	/**
	 * JWT 토큰 생성
	 * @param subject 암호화된 토큰 정보
	 * @param expirationTime 토큰 만료 시간(밀리초)
	 * @return 생성된 JWT 토큰
	 */
	public String createToken(String subject, long expirationTime) {
		Claims claims = Jwts.claims().subject(subject).build();
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationTime);

		return Jwts.builder()
			.claims(claims)
			.issuedAt(now)
			.expiration(expiration)
			.signWith(getSigningKey())
			.compact();
	}

	/**
	 * 토큰에서 모든 클레임 추출
	 * @param token JWT 토큰
	 * @return 클레임
	 */
	public Claims extractAllClaims(String token) {
		return getParser()
			.parseSignedClaims(token)
			.getPayload();
	}

	/**
	 * 토큰에서 값 추출
	 * @param token JWT 토큰
	 * @return 토큰에서 추출한 정보
	 */
	public JsonObject extractValue(String token) {
		String subject = extractAllClaims(token).getSubject();
		String decrypted = decrypt(subject);
		return new Gson().fromJson(decrypted, JsonObject.class);
	}

	/**
	 * 토큰에서 토큰 타입 추출
	 * @param token JWT 토큰
	 * @return 토큰 타입 (access 또는 refresh)
	 */
	public String extractTokenType(String token) {
		JsonElement tokenType = extractValue(token).get("tokenType");
		return tokenType.getAsString();
	}

	/**
	 * 토큰 유효성 검증
	 * @param token JWT 토큰
	 * @return 토큰 유효 여부
	 * @throws AuthException 토큰 검증 실패 시
	 */
	public boolean validateToken(String token) {
		try {
			Claims claims = extractAllClaims(token);
			return !claims.getExpiration().before(new Date());
		}
	catch (Exception e) {
			throw new AuthException(ErrorCode.JWT_RUNTIME_EXCEPTION);
		}
	}

	/**
	 * 평문 데이터 암호화
	 * @param plainText 평문
	 * @return 암호화된 텍스트
	 */
	@SneakyThrows
	public String encrypt(String plainText) {
		SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
		IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0, 16).getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV);

		byte[] encryptionByte = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

		return Hex.encodeHexString(encryptionByte);
	}

	/**
	 * 암호화된 데이터 복호화
	 * @param encodeText 암호화된 텍스트
	 * @return 복호화된 평문
	 */
	@SneakyThrows
	private String decrypt(String encodeText) {
		SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
		IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0, 16).getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IV);

		byte[] decodeByte = Hex.decodeHex(encodeText);

		return new String(cipher.doFinal(decodeByte), StandardCharsets.UTF_8);
	}

	/**
	 * 서명 키 생성
	 * @return 서명 키
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * JWT 파서 생성
	 * @return JWT 파서
	 */
	private JwtParser getParser() {
		return Jwts.parser()
			.verifyWith(this.getSigningKey())
			.build();
	}
}
