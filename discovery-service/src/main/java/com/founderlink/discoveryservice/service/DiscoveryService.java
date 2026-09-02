package com.founderlink.discoveryservice.service;

import com.founderlink.discoveryservice.dto.request.FounderSearchRequest;
import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import java.util.List;
import java.util.UUID;

public interface DiscoveryService {
    List<ProfileResponse> findFounders(FounderSearchRequest request);
    ProfileResponse findFounderByUserId(UUID userId);
}
