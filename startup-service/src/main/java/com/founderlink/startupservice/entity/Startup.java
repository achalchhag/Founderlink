package com.founderlink.startupservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "startups")
public class Startup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 3000)
    private String description;

    @Column(length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Stage stage;

    @Column(name = "looking_for", length = 500)
    private String lookingFor;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Startup() {
    }

    public Startup(UUID userId, String name) {
        this.userId = userId;
        this.name = name;
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

    // Getters

    public UUID getId() { return id; }

    public UUID getUserId() { return userId; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getIndustry() { return industry; }

    public Stage getStage() { return stage; }

    public String getLookingFor() { return lookingFor; }

    public String getWebsiteUrl() { return websiteUrl; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    // Update method

    public void update(String name, String description, String industry, Stage stage,
                       String lookingFor, String websiteUrl) {
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.stage = stage;
        this.lookingFor = lookingFor;
        this.websiteUrl = websiteUrl;
    }

    /** Startup development stage. */
    public enum Stage {
        IDEA,
        MVP,
        EARLY_STAGE,
        GROWTH,
        SCALING
    }
}
