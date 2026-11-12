package com.founderlink.authservice.controller;

import com.founderlink.authservice.dto.request.LoginRequest;
import com.founderlink.authservice.dto.request.RegisterUserRequest;
import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.dto.response.UserResponse;
import com.founderlink.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "FounderLink account registration")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Register a FounderLink account",
            description = "Creates an active account. Passwords are stored only as BCrypt hashes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account registered"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @Operation(summary = "Sign in to FounderLink",
            description = "Authenticates an active account and issues a signed JWT access token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token issued"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}
