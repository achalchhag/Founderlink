package com.founderlink.discoveryservice.dto.response;

import java.time.Instant;
import java.util.UUID;

/** Profile Service response contract used at the Discovery Service boundary. */
public record ProfileResponse(UUID id, UUID userId, String headline, String bio, String location, String skills,
                              Integer experienceYears, String websiteUrl, String linkedinUrl, String githubUrl,
                              Instant createdAt, Instant updatedAt) {
}
