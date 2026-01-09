package com.gdg.gdg_homepage.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gdg.gdg_homepage.common.util.SecurityUtil;
import com.gdg.gdg_homepage.profile.dto.ProfileResponse;
import com.gdg.gdg_homepage.profile.dto.ProfileUpdateRequest;
import com.gdg.gdg_homepage.profile.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me/profile")
public class ProfileMeController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(profileService.getMyProfile(memberId));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> upsertMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(profileService.updateMyProfile(memberId, request));
    }
}