package com.gdg.gdg_homepage.member.controller.response;

import com.gdg.gdg_homepage.member.repository.Member;
import com.gdg.gdg_homepage.member.repository.MemberRole;

public record LoginSuccessResponseDto(
	String username,
	MemberRole memberRole,
	String imageUrl,
	Long memberId
) {
	public static LoginSuccessResponseDto fromDomain(Member member) {
		return new LoginSuccessResponseDto(
			member.getUsername(),
			member.getMemberRole(),
			member.getImageUrl(),
			member.getId()
		);
	}
}
