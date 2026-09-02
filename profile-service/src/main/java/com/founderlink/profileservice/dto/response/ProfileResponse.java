package com.founderlink.profileservice.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProfileResponse(UUID id, UUID userId, String headline, String bio, String location, String skills,
                              Integer experienceYears, String websiteUrl, String linkedinUrl, String githubUrl,
                              Instant createdAt, Instant updatedAt) {
}
