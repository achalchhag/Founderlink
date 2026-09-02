package com.founderlink.discoveryservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.founderlink.discoveryservice.config.SecurityConfiguration;
import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import com.founderlink.discoveryservice.exception.GlobalExceptionHandler;
import com.founderlink.discoveryservice.exception.ProfileNotFoundException;
import com.founderlink.discoveryservice.service.DiscoveryService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DiscoveryController.class)
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {"auth.jwt.issuer=founderlink-auth", "auth.jwt.public-key=unused"})
class DiscoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscoveryService discoveryService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void rejectsUnauthenticatedDiscoveryRequest() throws Exception {
        mockMvc.perform(get("/api/discovery/founders")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void getsFounderForAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        when(discoveryService.findFounderByUserId(userId)).thenReturn(profile(userId));

        mockMvc.perform(get("/api/discovery/founders/{userId}", userId).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(discoveryService).findFounderByUserId(userId);
    }

    @Test
    void returnsNotFoundWhenProfileDoesNotExist() throws Exception {
        UUID userId = UUID.randomUUID();
        when(discoveryService.findFounderByUserId(userId)).thenThrow(new ProfileNotFoundException(userId));

        mockMvc.perform(get("/api/discovery/founders/{userId}", userId).with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void passesSearchFiltersToService() throws Exception {
        when(discoveryService.findFounders(any())).thenReturn(List.of(profile(UUID.randomUUID())));

        mockMvc.perform(get("/api/discovery/founders?location=Mumbai&skill=Java&headline=CTO").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Mumbai"));

        verify(discoveryService).findFounders(eq(new com.founderlink.discoveryservice.dto.request.FounderSearchRequest("Mumbai", "Java", "CTO")));
    }

    private ProfileResponse profile(UUID userId) {
        return new ProfileResponse(UUID.randomUUID(), userId, "CTO", "Bio", "Mumbai", "Java", 5,
                null, null, null, Instant.now(), Instant.now());
    }
}
