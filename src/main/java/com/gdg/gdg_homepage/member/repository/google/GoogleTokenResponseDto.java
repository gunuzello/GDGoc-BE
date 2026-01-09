package com.gdg.gdg_homepage.member.repository.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTokenResponseDto {
	@JsonProperty("token_type")
	public String tokenType;

	@JsonProperty("access_token")
	public String accessToken;

	@JsonProperty("expires_in")
	public Integer expiresIn;

	@JsonProperty("scope")
	public String scope;

	@JsonProperty("id_token")
	public String idToken;
}
