package com.gdg.gdg_homepage.profile.dto;

import java.util.List;

import com.gdg.gdg_homepage.member.repository.MemberRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
	Long memberId,
	@NotBlank @Size(max = 50) String name,
	@Size(max = 100) String major,
	@Size(max = 500) String bio,
	@Size(max = 500) String imageUrl,

	@Size(max = 30) List<@Size(max = 50) String> stacks,
	MemberRole memberRole
) {
}