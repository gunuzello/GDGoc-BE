package com.gdg.gdg_homepage.member.service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdg.gdg_homepage.common.controller.security.JwtTokenManager;
import com.gdg.gdg_homepage.member.controller.request.MemberSocialLoginRequestDto;
import com.gdg.gdg_homepage.member.controller.response.LoginSuccessResponseDto;
import com.gdg.gdg_homepage.member.repository.Member;
import com.gdg.gdg_homepage.member.repository.MemberRepository;
import com.gdg.gdg_homepage.member.repository.SocialMemberInfo;
import com.gdg.gdg_homepage.member.repository.google.GoogleLoginProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;
	private final JwtTokenManager jwtTokenManager;
	private final GoogleLoginProvider googleLoginProvider;


	@Transactional
	public LoginSuccessResponseDto socialLogin(
		MemberSocialLoginRequestDto memberSocialLoginRequestDto, HttpServletResponse response,
		HttpServletRequest request) {
		SocialMemberInfo userInfo = googleLoginProvider.getMemberInfo(memberSocialLoginRequestDto, request);
		return processSocialLogin(userInfo, response);
	}

	private LoginSuccessResponseDto processSocialLogin(
		SocialMemberInfo memberInfo, HttpServletResponse response) {
		Optional<Member> findMember = memberRepository.findByUsername(memberInfo.getEmail());
		return findMember.map(member -> processExistingMember(member, response))
			.orElseGet(() -> processNewSocialMember(memberInfo,response));
	}

	private LoginSuccessResponseDto processExistingMember(
		Member member, HttpServletResponse response) {
			jwtTokenManager.setTokenInHeader(member, response);
			return LoginSuccessResponseDto.fromDomain(
				member
			);
	}
	private LoginSuccessResponseDto processNewSocialMember(SocialMemberInfo memberInfo,HttpServletResponse response) {
		Member newMember = Member.create(memberInfo);
		Member savedMember = memberRepository.save(newMember);
		jwtTokenManager.setTokenInHeader(savedMember, response);
		return LoginSuccessResponseDto.fromDomain(
			savedMember
		);
	}

	private String generateRandomPassword() {
		// UUID를 사용하여 랜덤 문자열 생성
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");

		// 추가적인 복잡성을 위해 랜덤 문자 추가
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		StringBuilder sb = new StringBuilder(uuid.substring(0, 10));

		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 6; i++) {
			int index = random.nextInt(chars.length());
			sb.append(chars.charAt(index));
		}
		return sb.toString();
	}
}
