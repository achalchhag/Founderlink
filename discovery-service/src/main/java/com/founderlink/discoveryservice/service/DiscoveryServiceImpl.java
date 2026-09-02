package com.founderlink.discoveryservice.service;

import com.founderlink.discoveryservice.client.ProfileClient;
import com.founderlink.discoveryservice.dto.request.FounderSearchRequest;
import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import com.founderlink.discoveryservice.exception.DownstreamProfileServiceException;
import com.founderlink.discoveryservice.exception.ProfileNotFoundException;
import feign.FeignException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    private final ProfileClient profileClient;

    public DiscoveryServiceImpl(ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    @Override
    public List<ProfileResponse> findFounders(FounderSearchRequest request) {
        try {
            return profileClient.search(request.location(), request.skill(), request.headline());
        } catch (FeignException exception) {
            throw new DownstreamProfileServiceException(exception);
        }
    }

    @Override
    public ProfileResponse findFounderByUserId(UUID userId) {
        try {
            return profileClient.getByUserId(userId);
        } catch (FeignException.NotFound exception) {
            throw new ProfileNotFoundException(userId);
        } catch (FeignException exception) {
            throw new DownstreamProfileServiceException(exception);
        }
    }
}
