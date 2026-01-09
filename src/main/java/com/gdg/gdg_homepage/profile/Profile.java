package com.gdg.gdg_homepage.profile;

import com.gdg.gdg_homepage.common.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "profile",
        uniqueConstraints = @UniqueConstraint(name = "uk_profile_member_id", columnNames = "member_id"))
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String major;

    @Column(length = 500)
    private String bio;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public Profile(Long memberId, String name, String major, String bio, String imageUrl) {
        this.memberId = memberId;
        this.name = name;
        this.major = major;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }

    public void update(String name, String major, String bio, String imageUrl) {
        this.name = name;
        this.major = major;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }
}