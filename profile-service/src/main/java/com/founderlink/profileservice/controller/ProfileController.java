package com.founderlink.profileservice.controller;

import com.founderlink.profileservice.dto.request.CreateProfileRequest;
import com.founderlink.profileservice.dto.request.UpdateProfileRequest;
import com.founderlink.profileservice.dto.request.ProfileSearchRequest;
import com.founderlink.profileservice.dto.response.ProfileResponse;
import com.founderlink.profileservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profiles", description = "Authenticated FounderLink user profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @Operation(summary = "Create my profile", description = "Creates a profile for the authenticated JWT subject.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "409", description = "Profile already exists")})
    public ResponseEntity<ProfileResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                   @Valid @RequestBody CreateProfileRequest request) {
        UUID userId = authenticatedUserId(jwt);
        ProfileResponse response = profileService.create(userId, request);
        return ResponseEntity.created(URI.create("/api/profiles/" + userId)).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ProfileResponse> getMine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getMine(authenticatedUserId(jwt)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Updates only the profile owned by the authenticated JWT subject.")
    public ResponseEntity<ProfileResponse> update(@AuthenticationPrincipal Jwt jwt,
                                                   @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.update(authenticatedUserId(jwt), request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete my profile")
    @ApiResponse(responseCode = "204", description = "Profile deleted")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        profileService.delete(authenticatedUserId(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by user ID", description = "Requires an authenticated access token.")
    public ResponseEntity<ProfileResponse> getByUserId(@Parameter(description = "Auth Service user UUID") @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Search founder profiles", description = "Searches profiles by optional location, skill, and headline filters.")
    public ResponseEntity<List<ProfileResponse>> search(@Valid @ModelAttribute ProfileSearchRequest request) {
        return ResponseEntity.ok(profileService.search(request));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
