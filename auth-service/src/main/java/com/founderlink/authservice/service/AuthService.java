package com.founderlink.authservice.service;

import com.founderlink.authservice.dto.request.LoginRequest;
import com.founderlink.authservice.dto.request.RegisterUserRequest;
import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterUserRequest request);

    AuthTokenResponse login(LoginRequest request);
}
