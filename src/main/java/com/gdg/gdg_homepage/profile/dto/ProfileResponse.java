package com.gdg.gdg_homepage.profile.dto;

import java.util.List;

import com.gdg.gdg_homepage.member.repository.MemberRole;

public record ProfileResponse(
        String name,
        String email,
        String major,
        String bio,
        String imageUrl,

        List<String> stacks,
		MemberRole memberRole
) {
    public static ProfileResponse empty() {
        return new ProfileResponse("", null, "", "", null, List.of(),null);
    }
}