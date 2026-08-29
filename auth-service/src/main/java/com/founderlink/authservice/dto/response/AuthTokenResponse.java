package com.founderlink.authservice.dto.response;

public record AuthTokenResponse(String accessToken, String tokenType, long expiresIn) {
}
