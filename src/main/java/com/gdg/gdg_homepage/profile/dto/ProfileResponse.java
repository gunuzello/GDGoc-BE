package com.gdg.gdg_homepage.profile.dto;

import java.util.List;

public record ProfileResponse(
        String name,
        String email,
        String major,
        String bio,
        String imageUrl,
        List<String> stacks
) {
    public static ProfileResponse empty() {
        return new ProfileResponse("", null, "", "", null, List.of());
    }
}