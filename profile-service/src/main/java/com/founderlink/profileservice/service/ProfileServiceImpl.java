package com.founderlink.profileservice.service;

import com.founderlink.profileservice.entity.Profile;
import com.founderlink.profileservice.exception.DuplicateProfileException;
import com.founderlink.profileservice.exception.ProfileNotFoundException;
import com.founderlink.profileservice.repository.ProfileRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profiles;

    public ProfileServiceImpl(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    @Transactional
    public Profile create(UUID userId, Profile input) {
        if (profiles.existsByUserId(userId)) {
            throw new DuplicateProfileException();
        }
        Profile profile = new Profile(userId);
        apply(profile, input.getHeadline(), input.getBio(), input.getLocation(), input.getSkills(), input.getExperienceYears(),
                input.getWebsiteUrl(), input.getLinkedinUrl(), input.getGithubUrl());
        return profiles.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Profile getMine(UUID userId) {
        return find(userId);
    }

    @Override
    @Transactional
    public Profile update(UUID userId, Profile updates) {
        Profile profile = find(userId);
        apply(profile, updates.getHeadline(), updates.getBio(), updates.getLocation(), updates.getSkills(), updates.getExperienceYears(),
                updates.getWebsiteUrl(), updates.getLinkedinUrl(), updates.getGithubUrl());
        return profiles.save(profile);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        profiles.delete(find(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Profile getByUserId(UUID userId) {
        return find(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> search(String location, String skill, String headline) {
        String loc = normalize(location);
        String sk = normalize(skill);
        String hl = normalize(headline);
        if (loc == null && sk == null && hl == null) {
            return profiles.findAllByOrderByCreatedAtDesc();
        }
        return profiles.search(loc, sk, hl);
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
