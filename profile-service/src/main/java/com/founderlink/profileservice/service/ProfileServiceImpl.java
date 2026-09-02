package com.founderlink.profileservice.service;

import com.founderlink.profileservice.dto.request.CreateProfileRequest;
import com.founderlink.profileservice.dto.request.UpdateProfileRequest;
import com.founderlink.profileservice.dto.response.ProfileResponse;
import com.founderlink.profileservice.entity.Profile;
import com.founderlink.profileservice.exception.DuplicateProfileException;
import com.founderlink.profileservice.exception.ProfileNotFoundException;
import com.founderlink.profileservice.mapper.ProfileMapper;
import com.founderlink.profileservice.repository.ProfileRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profiles;
    private final ProfileMapper mapper;

    public ProfileServiceImpl(ProfileRepository profiles, ProfileMapper mapper) {
        this.profiles = profiles;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProfileResponse create(UUID userId, CreateProfileRequest request) {
        if (profiles.existsByUserId(userId)) {
            throw new DuplicateProfileException();
        }
        Profile profile = new Profile(userId);
        apply(profile, request.headline(), request.bio(), request.location(), request.skills(), request.experienceYears(),
                request.websiteUrl(), request.linkedinUrl(), request.githubUrl());
        return mapper.toResponse(profiles.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMine(UUID userId) {
        return mapper.toResponse(find(userId));
    }

    @Override
    @Transactional
    public ProfileResponse update(UUID userId, UpdateProfileRequest request) {
        Profile profile = find(userId);
        apply(profile, request.headline(), request.bio(), request.location(), request.skills(), request.experienceYears(),
                request.websiteUrl(), request.linkedinUrl(), request.githubUrl());
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        profiles.delete(find(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getByUserId(UUID userId) {
        return mapper.toResponse(find(userId));
    }

    private Profile find(UUID userId) {
        return profiles.findByUserId(userId).orElseThrow(() -> new ProfileNotFoundException(userId));
    }

    private void apply(Profile profile, String headline, String bio, String location, String skills, Integer experienceYears,
                       String websiteUrl, String linkedinUrl, String githubUrl) {
        profile.update(normalize(headline), normalize(bio), normalize(location), normalize(skills), experienceYears,
                normalize(websiteUrl), normalize(linkedinUrl), normalize(githubUrl));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
