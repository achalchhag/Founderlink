package com.founderlink.profileservice.controller;

import com.founderlink.profileservice.entity.Profile;
import com.founderlink.profileservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "409", description = "Profile already exists")
    })
    public ResponseEntity<Profile> create(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody Profile profile) {
        UUID userId = authenticatedUserId(jwt);
        Profile response = profileService.create(userId, profile);
        return ResponseEntity.created(URI.create("/api/profiles/" + userId)).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<Profile> getMine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getMine(authenticatedUserId(jwt)));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Updates only the profile owned by the authenticated JWT subject.")
    public ResponseEntity<Profile> update(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody Profile updates) {
        return ResponseEntity.ok(profileService.update(authenticatedUserId(jwt), updates));
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
    public ResponseEntity<Profile> getByUserId(
            @Parameter(description = "Auth Service user UUID") @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(profileService.getByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Search founder profiles", description = "Searches profiles by optional location, skill, and headline filters.")
    public ResponseEntity<List<Profile>> search(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "skill", required = false) String skill,
            @RequestParam(value = "headline", required = false) String headline) {
        return ResponseEntity.ok(profileService.search(location, skill, headline));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
