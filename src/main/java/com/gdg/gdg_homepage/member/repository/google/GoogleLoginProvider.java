package com.gdg.gdg_homepage.member.repository.google;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.gdg.gdg_homepage.member.controller.request.MemberSocialLoginRequestDto;
import com.gdg.gdg_homepage.member.repository.SocialMemberInfo;

@Component
@RequiredArgsConstructor
public class GoogleLoginProvider {
	private final GoogleLoginManager googleLoginManager;


	public SocialMemberInfo getMemberInfo(MemberSocialLoginRequestDto request,
		HttpServletRequest servletRequest) {
		String token = googleLoginManager.getAccessToken(request.code(),servletRequest);
		GoogleUserInfoResponseDto userInfoDto = googleLoginManager.getUserInfo(token);
		return SocialMemberInfo.from(userInfoDto.getEmail(), userInfoDto.getPicture());
	}
}
