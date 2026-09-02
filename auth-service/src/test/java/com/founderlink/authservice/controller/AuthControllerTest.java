package com.founderlink.authservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.dto.response.UserResponse;
import com.founderlink.authservice.enums.AccountStatus;
import com.founderlink.authservice.enums.Role;
import com.founderlink.authservice.exception.GlobalExceptionHandler;
import com.founderlink.authservice.exception.InvalidCredentialsException;
import com.founderlink.authservice.service.AuthService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = Mockito.mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registersValidRequestAndReturnsSafeResponse() throws Exception {
        when(authService.register(any())).thenReturn(new UserResponse(
                UUID.randomUUID(), "Rahul Sharma", "rahul@example.com", Role.FOUNDER,
                AccountStatus.ACTIVE, Instant.parse("2026-08-14T00:00:00Z")
        ));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("rahul@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void logsInAndReturnsOnlyTokenData() throws Exception {
        when(authService.login(any())).thenReturn(new AuthTokenResponse("signed.jwt.token", "Bearer", 900));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"rahul@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void returnsUnauthorizedForInvalidCredentials() throws Exception {
        when(authService.login(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"rahul@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"))
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void returnsUnauthorizedWhenInvalidCredentialsAreWrapped() throws Exception {
        when(authService.login(any())).thenThrow(new RuntimeException(new InvalidCredentialsException()));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"rahul@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void rejectsInvalidLoginEmail() throws Exception {
        assertLoginValidationError("{\"email\":\"invalid-email\",\"password\":\"StrongPassword123!\"}");
    }

    @Test
    void rejectsBlankLoginPassword() throws Exception {
        assertLoginValidationError("{\"email\":\"rahul@example.com\",\"password\":\" \"}");
    }

    @Test
    void rejectsInvalidEmail() throws Exception { assertValidationError("{\"fullName\":\"Rahul Sharma\",\"email\":\"invalid-email\",\"password\":\"StrongPassword123!\",\"role\":\"FOUNDER\"}"); }
    @Test
    void rejectsBlankFullName() throws Exception { assertValidationError("{\"fullName\":\" \",\"email\":\"rahul@example.com\",\"password\":\"StrongPassword123!\",\"role\":\"FOUNDER\"}"); }
    @Test
    void rejectsShortPassword() throws Exception { assertValidationError("{\"fullName\":\"Rahul Sharma\",\"email\":\"rahul@example.com\",\"password\":\"short\",\"role\":\"FOUNDER\"}"); }
    @Test
    void rejectsNullRole() throws Exception { assertValidationError("{\"fullName\":\"Rahul Sharma\",\"email\":\"rahul@example.com\",\"password\":\"StrongPassword123!\",\"role\":null}"); }
    @Test
    void rejectsBlankEmail() throws Exception { assertValidationError("{\"fullName\":\"Rahul Sharma\",\"email\":\" \",\"password\":\"StrongPassword123!\",\"role\":\"FOUNDER\"}"); }

    private void assertValidationError(String content) throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    private void assertLoginValidationError(String content) throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    private String validRequest() {
        return "{\"fullName\":\"Rahul Sharma\",\"email\":\"rahul@example.com\",\"password\":\"StrongPassword123!\",\"role\":\"FOUNDER\"}";
    }
}
