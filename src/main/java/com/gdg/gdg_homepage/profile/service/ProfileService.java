package com.gdg.gdg_homepage.profile.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;
import com.gdg.gdg_homepage.member.repository.Member;
import com.gdg.gdg_homepage.member.repository.MemberRepository;
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
	private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long memberId) {
		Optional<Member> findMember = memberRepository.findById(memberId);
		if (findMember.isEmpty()) {throw new AuthException(ErrorCode.MEMBER_NOT_FOUND);
		}
		String email = findMember.get().getUsername();
		return profileRepository.findByMemberId(memberId)
                .map(profile -> {
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
                            stacks,
						findMember.get().getMemberRole()
                    );
                })
                .orElse(ProfileResponse.empty());
    }
	@Transactional
    public ProfileResponse updateMyProfile(Long memberId, ProfileUpdateRequest req) {
		Optional<Member> findMember = memberRepository.findById(memberId);
		if (findMember.isEmpty()) {throw new AuthException(ErrorCode.MEMBER_NOT_FOUND);
		}
		String email = findMember.get().getUsername();
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> profileRepository.save(
                        new Profile(memberId, req.name(), req.major(), req.bio(), req.imageUrl())
                ));

        // 프로필 기본 정보 업데이트 (dirty checking으로 반영)
        profile.update(req.name(), req.major(), req.bio(), req.imageUrl());

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

        List<String> stacks = profileTechStackRepository.findAll().stream()
                .filter(s -> s.getProfile().getId().equals(profile.getId()))
                .map(ProfileTechStack::getName)
                .toList();
		Member savedMember = memberRepository.save(findMember.get().changeMemberRole(req.memberRole()));

		return new ProfileResponse(
                profile.getName(),
                email,
                profile.getMajor(),
                profile.getBio(),
                profile.getImageUrl(),
                stacks,
			savedMember.getMemberRole()
        );
    }
}