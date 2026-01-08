package com.gdg.gdg_homepage.profile.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import com.gdg.gdg_homepage.common.util.SecurityUtil;
import com.gdg.gdg_homepage.profile.Profile;
import com.gdg.gdg_homepage.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me/profile")
public class ProfileImageController {

    private final ProfileRepository profileRepository;

    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("image") MultipartFile image) throws IOException {

        Long memberId = SecurityUtil.getCurrentMemberId();

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 파일이 없습니다.");
        }

        String contentType = image.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            return ResponseEntity.badRequest().body("JPG 또는 PNG 파일만 업로드 가능합니다.");
        }

        // 저장 디렉토리 생성
        File uploadDir = new File("uploads/profile");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일명 생성
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;

        File savedFile = new File(uploadDir, savedFileName);
        image.transferTo(savedFile);

        String imageUrl = "/uploads/profile/" + savedFileName;

        // 프로필 조회 (없으면 생성)
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseGet(() -> profileRepository.save(
                        new Profile(memberId, null, null, null, imageUrl)
                ));

        profile.update(
                profile.getName(),
                profile.getMajor(),
                profile.getBio(),
                imageUrl
        );

        return ResponseEntity.ok(
                java.util.Map.of("imageUrl", imageUrl)
        );
    }
}