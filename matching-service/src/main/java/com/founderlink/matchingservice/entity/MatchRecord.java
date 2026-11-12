package com.founderlink.matchingservice.entity;

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
@Table(name = "match_records")
public class MatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "match_type", nullable = false, length = 50)
    private String matchType; // "FOUNDER" or "STARTUP"

    @Column(name = "target_name", nullable = false, length = 200)
    private String targetName;

    @Column(name = "target_role", length = 200)
    private String targetRole;

    @Column(name = "target_bio", columnDefinition = "TEXT")
    private String targetBio;

    @Column(name = "target_industry", length = 100)
    private String targetIndustry;

    @Column(name = "match_score", nullable = false)
    private int matchScore;

    @Column(name = "synergy_summary", columnDefinition = "TEXT")
    private String synergySummary;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // "PENDING", "ACCEPTED", "REJECTED"

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public MatchRecord() {
    }

    public MatchRecord(UUID userId, UUID targetId, String matchType, String targetName,
                       String targetRole, String targetBio, String targetIndustry,
                       int matchScore, String synergySummary) {
        this.userId = userId;
        this.targetId = targetId;
        this.matchType = matchType;
        this.targetName = targetName;
        this.targetRole = targetRole;
        this.targetBio = targetBio;
        this.targetIndustry = targetIndustry;
        this.matchScore = matchScore;
        this.synergySummary = synergySummary;
        this.status = "PENDING";
    }

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = "PENDING";
        }
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTargetBio() {
        return targetBio;
    }

    public void setTargetBio(String targetBio) {
        this.targetBio = targetBio;
    }

    public String getTargetIndustry() {
        return targetIndustry;
    }

    public void setTargetIndustry(String targetIndustry) {
        this.targetIndustry = targetIndustry;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getSynergySummary() {
        return synergySummary;
    }

    public void setSynergySummary(String synergySummary) {
        this.synergySummary = synergySummary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
