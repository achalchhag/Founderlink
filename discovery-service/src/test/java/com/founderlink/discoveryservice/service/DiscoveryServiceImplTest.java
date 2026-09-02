package com.founderlink.discoveryservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.founderlink.discoveryservice.client.ProfileClient;
import com.founderlink.discoveryservice.dto.request.FounderSearchRequest;
import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import com.founderlink.discoveryservice.exception.ProfileNotFoundException;
import feign.FeignException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscoveryServiceImplTest {

    @Mock
    private ProfileClient profileClient;

    @Test
    void retrievesProfileThroughFeignClient() {
        UUID userId = UUID.randomUUID();
        ProfileResponse expected = profile(userId);
        when(profileClient.getByUserId(userId)).thenReturn(expected);

        ProfileResponse actual = new DiscoveryServiceImpl(profileClient).findFounderByUserId(userId);

        assertThat(actual).isEqualTo(expected);
        verify(profileClient).getByUserId(userId);
    }

    @Test
    void passesSearchFiltersToFeignClient() {
        FounderSearchRequest request = new FounderSearchRequest("Mumbai", "Java", "CTO");
        new DiscoveryServiceImpl(profileClient).findFounders(request);

        verify(profileClient).search("Mumbai", "Java", "CTO");
    }

    @Test
    void mapsFeignNotFoundToProfileNotFound() {
        UUID userId = UUID.randomUUID();
        when(profileClient.getByUserId(userId)).thenThrow(FeignException.errorStatus("getByUserId", feign.Response.builder().status(404).reason("Not Found").request(feign.Request.create(feign.Request.HttpMethod.GET, "/api/profiles/" + userId, java.util.Map.of(), null, null, null)).build()));

        assertThatThrownBy(() -> new DiscoveryServiceImpl(profileClient).findFounderByUserId(userId)).isInstanceOf(ProfileNotFoundException.class);
    }

    private ProfileResponse profile(UUID userId) {
        return new ProfileResponse(UUID.randomUUID(), userId, "CTO", "Bio", "Mumbai", "Java", 5,
                null, null, null, Instant.now(), Instant.now());
    }
}
