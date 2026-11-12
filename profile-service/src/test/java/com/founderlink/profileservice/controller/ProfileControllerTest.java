package com.founderlink.profileservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.founderlink.profileservice.config.SecurityConfiguration;
import com.founderlink.profileservice.entity.Profile;
import com.founderlink.profileservice.service.ProfileService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfiguration.class)
@TestPropertySource(properties = {
        "auth.jwt.issuer=founderlink-auth",
        "auth.jwt.public-key=unused"
})
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void rejectsUnauthenticatedProfileCreation() throws Exception {
        mockMvc.perform(post("/api/profiles").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void derivesProfileOwnerFromJwtInsteadOfRequestBody() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID attemptedUserId = UUID.randomUUID();
        Profile response = new Profile(authenticatedUserId);
        response.setHeadline("Founder");
        response.setExperienceYears(3);

        when(profileService.create(eq(authenticatedUserId), any(Profile.class))).thenReturn(response);

        mockMvc.perform(post("/api/profiles")
                        .with(jwt().jwt(jwt -> jwt.subject(authenticatedUserId.toString())))
                        .contentType("application/json")
                        .content("{\"userId\":\"" + attemptedUserId + "\",\"headline\":\"Founder\",\"experienceYears\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(authenticatedUserId.toString()));
    }

    @Test
    void rejectsNegativeExperienceYears() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();

        mockMvc.perform(post("/api/profiles")
                        .with(jwt().jwt(jwt -> jwt.subject(authenticatedUserId.toString())))
                        .contentType("application/json")
                        .content("{\"experienceYears\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.experienceYears").exists());
    }
}
