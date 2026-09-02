package com.founderlink.profileservice.mapper;

import com.founderlink.profileservice.dto.response.ProfileResponse;
import com.founderlink.profileservice.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(profile.getId(), profile.getUserId(), profile.getHeadline(), profile.getBio(),
                profile.getLocation(), profile.getSkills(), profile.getExperienceYears(), profile.getWebsiteUrl(),
                profile.getLinkedinUrl(), profile.getGithubUrl(), profile.getCreatedAt(), profile.getUpdatedAt());
    }
}
