package com.gdg.gdg_homepage.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gdg.gdg_homepage.profile.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByMemberId(Long memberId);
}