package com.founderlink.profileservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 150) String headline,
        @Size(max = 2000) String bio,
        @Size(max = 150) String location,
        @Size(max = 1000) String skills,
        @Min(0) Integer experienceYears,
        @Size(max = 500) String websiteUrl,
        @Size(max = 500) String linkedinUrl,
        @Size(max = 500) String githubUrl
) {
}
