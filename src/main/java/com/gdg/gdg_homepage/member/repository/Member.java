package com.gdg.gdg_homepage.member.repository;

import com.gdg.gdg_homepage.common.infrastructure.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity

public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private MemberRole memberRole;

	private String imageUrl;
	@Builder
	private Member(String username, MemberRole memberRole, String imageUrl) {
		this.username = username;
		this.memberRole = memberRole;
		this.imageUrl = imageUrl;
	}
	public static Member create(SocialMemberInfo memberInfo) {
		return Member.builder().username(memberInfo.getEmail())
			.memberRole(null)
			.imageUrl(memberInfo.getImageUrl())
			.build();
	}
}