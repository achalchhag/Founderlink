package com.founderlink.profileservice.service;

import com.founderlink.profileservice.dto.request.CreateProfileRequest;
import com.founderlink.profileservice.dto.request.UpdateProfileRequest;
import com.founderlink.profileservice.dto.request.ProfileSearchRequest;
import com.founderlink.profileservice.dto.response.ProfileResponse;
import java.util.List;
import java.util.UUID;

public interface ProfileService {
    ProfileResponse create(UUID userId, CreateProfileRequest request);
    ProfileResponse getMine(UUID userId);
    ProfileResponse update(UUID userId, UpdateProfileRequest request);
    void delete(UUID userId);
    ProfileResponse getByUserId(UUID userId);
    List<ProfileResponse> search(ProfileSearchRequest request);
}
