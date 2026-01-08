package com.gdg.gdg_homepage.member;


import com.gdg.gdg_homepage.common.infrastructure.BaseEntity;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member extends BaseEntity {

	private final Long id;

	private String nickname;
	private String username;
	private String tel;
	private String email;

	private boolean isDeleted;

	private String password;

	private String organization;

	private Long organizationId;

	private MemberRole memberRole;

	private MemberType memberType;

	//
	// public static Member createMemberFromSignup(MemberSignupDto signupDto, String encodedPassword) {
	// 	return Member.builder()
	// 		.nickname(signupDto.nickname())
	// 		.username(signupDto.username())
	// 		.password(encodedPassword)
	// 		.email(signupDto.email())
	// 		.memberRole(signupDto.memberRole())
	// 		.memberType(signupDto.memberType())
	// 		.isMarketingAgreed(signupDto.isMarketingAgreed())
	// 		.organization(signupDto.organization() != null ? signupDto.organization() : null)
	// 		.organizationId(signupDto.organizationId() != null ? signupDto.organizationId() : null)
	// 		.isDeleted(false)
	// 		.build();
	// }
	//
	// public static Member createPrinterMember(String tel, String encodedPassword) {
	// 	return Member.builder()
	// 		.tel(tel)
	// 		.password(encodedPassword)
	// 		.memberRole(MemberRole.PRINTER)
	// 		.isDeleted(false)
	// 		.build();
	// }
	//
	// public static Member createClassroomStudent(
	// 	String username, Classroom classroom, String password) {
	// 	return Member.builder()
	// 		.username(username)
	// 		.nickname(username)
	// 		.memberRole(MemberRole.STUDENT)
	// 		.memberType(MemberType.CLASS)
	// 		.isDeleted(false)
	// 		.password(password)
	// 		.classroom(classroom)
	// 		.build();
	// }
	//
	// public void changeNameInfo(String newName) {
	// 	this.username = newName;
	// 	this.nickname = newName;
	// }
	//
	// public void updatePassword(String password) {
	// 	this.password = password;
	// }
	//
	// public Boolean isMemberTypeMatched(MemberType memberType) {
	// 	return Objects.equals(this.memberType, memberType);
	// }
	//
	// public Member softDelete() {
	// 	if (!isDeleted) {
	// 		isDeleted = true;
	// 	}
	// 	return this;
	// }
	//
	// public Member updateMember(MemberUpdateRequestDto request) {
	// 	if (request.nickname() != null) {
	// 		this.nickname = request.nickname();
	// 	}
	// 	if (request.username() != null) {
	// 		this.username = request.username();
	// 	}
	// 	if (request.tel() != null) {
	// 		this.tel = request.tel();
	// 	}
	// 	if (request.email() != null) {
	// 		this.email = request.email();
	// 	}
	// 	if (request.isMarketingAgreed() != null) {
	// 		this.isMarketingAgreed = request.isMarketingAgreed();
	// 	}
	// 	return this;
	// }
	//
	// public Member updateMemberRole(MemberRole memberRole) {
	// 	this.memberRole = memberRole;
	// 	return this;
	// }
}
