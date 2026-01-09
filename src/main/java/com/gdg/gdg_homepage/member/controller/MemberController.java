package com.gdg.gdg_homepage.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gdg.gdg_homepage.member.controller.request.MemberSocialLoginRequestDto;
import com.gdg.gdg_homepage.member.controller.response.LoginSuccessResponseDto;
import com.gdg.gdg_homepage.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	@PostMapping("/login")
	public LoginSuccessResponseDto socialLogin(
		@RequestBody MemberSocialLoginRequestDto memberSocialLoginRequestDto,
		HttpServletResponse response, HttpServletRequest request) {
		return memberService.socialLogin(memberSocialLoginRequestDto, response, request);
	}
}
