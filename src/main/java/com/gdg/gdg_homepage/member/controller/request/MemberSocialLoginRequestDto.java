package com.gdg.gdg_homepage.member.controller.request;


import jakarta.annotation.Nullable;

public record MemberSocialLoginRequestDto(
	String code, @Nullable String state) {}
