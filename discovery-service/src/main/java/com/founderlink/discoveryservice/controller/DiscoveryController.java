package com.founderlink.discoveryservice.controller;

import com.founderlink.discoveryservice.dto.request.FounderSearchRequest;
import com.founderlink.discoveryservice.dto.response.ProfileResponse;
import com.founderlink.discoveryservice.service.DiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discovery/founders")
@Tag(name = "Founder Discovery", description = "Authenticated founder profile discovery")
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    public DiscoveryController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping
    @Operation(summary = "Find founder profiles", description = "Filters founder profiles by optional location, skill, and headline.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Matching profiles returned"), @ApiResponse(responseCode = "401", description = "Authentication required")})
    public ResponseEntity<List<ProfileResponse>> findFounders(@Valid @ModelAttribute FounderSearchRequest request) {
        return ResponseEntity.ok(discoveryService.findFounders(request));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a founder profile by user ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profile returned"), @ApiResponse(responseCode = "404", description = "Profile not found"), @ApiResponse(responseCode = "401", description = "Authentication required")})
    public ResponseEntity<ProfileResponse> findFounderByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(discoveryService.findFounderByUserId(userId));
    }
}
