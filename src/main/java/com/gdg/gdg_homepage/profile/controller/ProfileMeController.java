package com.gdg.gdg_homepage.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{memberId}")
    public ResponseEntity<ProfileResponse> getMyProfile(@PathVariable Long memberId) {
        return ResponseEntity.ok(profileService.getMyProfile(memberId));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> upsertMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(profileService.updateMyProfile(request.memberId(), request));
    }
}