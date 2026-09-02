package com.founderlink.profileservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(length = 150)
    private String headline;
    @Column(length = 2000)
    private String bio;
    @Column(length = 150)
    private String location;
    @Column(length = 1000)
    private String skills;
    @Column(name = "experience_years")
    private Integer experienceYears;
    @Column(name = "website_url", length = 500)
    private String websiteUrl;
    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;
    @Column(name = "github_url", length = 500)
    private String githubUrl;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Profile() {
    }

    public Profile(UUID userId) {
        this.userId = userId;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getHeadline() { return headline; }
    public String getBio() { return bio; }
    public String getLocation() { return location; }
    public String getSkills() { return skills; }
    public Integer getExperienceYears() { return experienceYears; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String headline, String bio, String location, String skills, Integer experienceYears,
                       String websiteUrl, String linkedinUrl, String githubUrl) {
        this.headline = headline;
        this.bio = bio;
        this.location = location;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.websiteUrl = websiteUrl;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
    }
}
