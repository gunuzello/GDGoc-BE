package com.gdg.gdg_homepage.profile;

import com.gdg.gdg_homepage.common.infrastructure.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "profile_tech_stack",
        indexes = @Index(name = "idx_profile_tech_stack_profile_id", columnList = "profile_id"))
public class ProfileTechStack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, length = 50)
    private String name;

    public ProfileTechStack(Profile profile, String name) {
        this.profile = profile;
        this.name = name;
    }
}