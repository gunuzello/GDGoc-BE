package com.gdg.gdg_homepage.profile.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdg.gdg_homepage.profile.Profile;
import com.gdg.gdg_homepage.profile.ProfileTechStack;
import com.gdg.gdg_homepage.profile.dto.ProfileResponse;
import com.gdg.gdg_homepage.profile.dto.ProfileUpdateRequest;
import com.gdg.gdg_homepage.profile.repository.ProfileRepository;
import com.gdg.gdg_homepage.profile.repository.ProfileTechStackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileTechStackRepository profileTechStackRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> {
                    List<String> stacks = profileTechStackRepository.findAll().stream()
                            .filter(s -> s.getProfile().getId().equals(profile.getId()))
                            .map(ProfileTechStack::getName)
                            .toList();

                    // 구글 계정 이메일은 나중에 Member 쪽 repo 정리되면 붙이기
                    String email = null;

                    return new ProfileResponse(
                            profile.getName(),
                            email,
                            profile.getMajor(),
                            profile.getBio(),
                            profile.getImageUrl(),
                            stacks
                    );
                })
                .orElse(ProfileResponse.empty());
    }

    public ProfileResponse updateMyProfile(Long memberId, ProfileUpdateRequest req) {
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> profileRepository.save(
                        new Profile(memberId, req.name(), req.major(), req.bio(), null)
                ));

        // 프로필 기본 정보 업데이트 (dirty checking으로 반영)
        profile.update(req.name(), req.major(), req.bio(), profile.getImageUrl());

        // stacks: 기존 삭제 후 재등록 (처음엔 이게 제일 안전)
        profileTechStackRepository.deleteAll(
                profileTechStackRepository.findAll().stream()
                        .filter(s -> s.getProfile().getId().equals(profile.getId()))
                        .toList()
        );

        if (req.stacks() != null) {
            for (String stack : req.stacks()) {
                if (stack == null || stack.isBlank()) continue;
                profileTechStackRepository.save(new ProfileTechStack(profile, stack.trim()));
            }
        }

        // 구글 계정 이메일은 나중에 Member 쪽 repo 정리되면 붙이기
        String email = null;

        List<String> stacks = profileTechStackRepository.findAll().stream()
                .filter(s -> s.getProfile().getId().equals(profile.getId()))
                .map(ProfileTechStack::getName)
                .toList();

        return new ProfileResponse(
                profile.getName(),
                email,
                profile.getMajor(),
                profile.getBio(),
                profile.getImageUrl(),
                stacks
        );
    }
}