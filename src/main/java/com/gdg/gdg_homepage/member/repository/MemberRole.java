package com.gdg.gdg_homepage.member.repository;

import lombok.Getter;

@Getter
public enum MemberRole {

    MEMBER("member"),
    CORE("core"),
    ORGANIZER("organizer");

    private final String role;
    MemberRole(String role) {
        this.role = role;
    }
}
