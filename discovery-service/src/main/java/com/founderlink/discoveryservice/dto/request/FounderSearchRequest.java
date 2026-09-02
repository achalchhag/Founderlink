package com.founderlink.discoveryservice.dto.request;

import jakarta.validation.constraints.Size;

public record FounderSearchRequest(
        @Size(max = 100, message = "location must not exceed 100 characters") String location,
        @Size(max = 100, message = "skill must not exceed 100 characters") String skill,
        @Size(max = 100, message = "headline must not exceed 100 characters") String headline) {
}
