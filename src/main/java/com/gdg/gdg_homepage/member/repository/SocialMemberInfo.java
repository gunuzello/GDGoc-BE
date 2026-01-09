package com.gdg.gdg_homepage.member.repository;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialMemberInfo {
	private final String email;
	private final String imageUrl;

	public static SocialMemberInfo from(String email, String imageUrl) {
		return SocialMemberInfo.builder()
			.email(email)
			.imageUrl(imageUrl)
			.build();
	}
}
