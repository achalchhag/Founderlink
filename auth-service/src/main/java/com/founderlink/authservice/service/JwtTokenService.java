package com.founderlink.authservice.service;

import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.entity.User;

public interface JwtTokenService {
    AuthTokenResponse createAccessToken(User user);
}
