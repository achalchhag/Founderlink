package com.founderlink.matchingservice.controller;

import com.founderlink.matchingservice.entity.MatchRecord;
import com.founderlink.matchingservice.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
@Tag(name = "Matching", description = "AI matching algorithm and recommendations for co-founders and startups")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping
    @Operation(summary = "Create a match record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match record created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MatchRecord> createMatch(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MatchRecord matchRecord) {
        UUID userId = authenticatedUserId(jwt);
        MatchRecord created = matchingService.createMatch(userId, matchRecord);
        return ResponseEntity.created(URI.create("/api/matching/" + created.getId())).body(created);
    }

    @GetMapping
    @Operation(summary = "Get user's match recommendations", description = "Returns persisted matches and AI recommendations for the authenticated user.")
    public ResponseEntity<List<MatchRecord>> getMyMatches(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "type", required = false) String matchType) {
        UUID userId = authenticatedUserId(jwt);
        if (matchType != null && !matchType.isBlank()) {
            return ResponseEntity.ok(matchingService.getMatchesByType(userId, matchType.trim().toUpperCase()));
        }
        return ResponseEntity.ok(matchingService.getMyMatches(userId));
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get match record by ID")
    public ResponseEntity<MatchRecord> getById(@PathVariable UUID matchId) {
        return ResponseEntity.ok(matchingService.getById(matchId));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate and save recommended matches", description = "Computes and saves curated recommendations in database.")
    public ResponseEntity<List<MatchRecord>> generateRecommendations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "skills", required = false, defaultValue = "") String skills) {
        UUID userId = authenticatedUserId(jwt);
        return ResponseEntity.ok(matchingService.generateRecommendations(userId, role, skills));
    }

    @PatchMapping("/{matchId}/status")
    @Operation(summary = "Update match status", description = "Update status (e.g. ACCEPTED, REJECTED, PENDING).")
    public ResponseEntity<MatchRecord> updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID matchId,
            @RequestBody Map<String, String> statusBody) {
        UUID userId = authenticatedUserId(jwt);
        String status = statusBody.getOrDefault("status", "PENDING");
        return ResponseEntity.ok(matchingService.updateStatus(userId, matchId, status));
    }

    @DeleteMapping("/{matchId}")
    @Operation(summary = "Delete match record")
    public ResponseEntity<Void> deleteMatch(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID matchId) {
        UUID userId = authenticatedUserId(jwt);
        matchingService.deleteMatch(userId, matchId);
        return ResponseEntity.noContent().build();
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
