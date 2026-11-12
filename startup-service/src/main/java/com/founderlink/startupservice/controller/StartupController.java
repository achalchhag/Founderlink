package com.founderlink.startupservice.controller;

import com.founderlink.startupservice.entity.Startup;
import com.founderlink.startupservice.service.StartupService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/startups")
@Tag(name = "Startups", description = "Startup idea management")
public class StartupController {

    private final StartupService startupService;

    public StartupController(StartupService startupService) {
        this.startupService = startupService;
    }

    @PostMapping
    @Operation(summary = "Create a startup idea",
            description = "Creates a new startup idea for the authenticated user. A user can have multiple startups.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Startup created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Startup> create(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody Startup startup) {
        UUID userId = authenticatedUserId(jwt);
        Startup created = startupService.create(userId, startup);
        return ResponseEntity.created(URI.create("/api/startups/" + created.getId())).body(created);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my startups", description = "Returns all startups owned by the authenticated user.")
    public ResponseEntity<List<Startup>> getMyStartups(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(startupService.getMyStartups(authenticatedUserId(jwt)));
    }

    @GetMapping("/{startupId}")
    @Operation(summary = "Get a startup by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Startup returned"),
            @ApiResponse(responseCode = "404", description = "Startup not found")
    })
    public ResponseEntity<Startup> getById(@PathVariable UUID startupId) {
        return ResponseEntity.ok(startupService.getById(startupId));
    }

    @PutMapping("/{startupId}")
    @Operation(summary = "Update a startup", description = "Only the owner can update their startup.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Startup updated"),
            @ApiResponse(responseCode = "403", description = "Not the owner"),
            @ApiResponse(responseCode = "404", description = "Startup not found")
    })
    public ResponseEntity<Startup> update(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable UUID startupId,
                                          @Valid @RequestBody Startup updates) {
        return ResponseEntity.ok(startupService.update(authenticatedUserId(jwt), startupId, updates));
    }

    @DeleteMapping("/{startupId}")
    @Operation(summary = "Delete a startup", description = "Only the owner can delete their startup.")
    @ApiResponse(responseCode = "204", description = "Startup deleted")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable UUID startupId) {
        startupService.delete(authenticatedUserId(jwt), startupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Find startups by industry",
            description = "Returns startups filtered by industry. Requires authentication.")
    public ResponseEntity<List<Startup>> findByIndustry(
            @RequestParam(value = "industry", required = false) String industry) {
        if (industry == null || industry.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(startupService.findByIndustry(industry.trim()));
    }

    private UUID authenticatedUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
