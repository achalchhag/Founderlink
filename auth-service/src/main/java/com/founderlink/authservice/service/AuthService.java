package com.founderlink.authservice.service;
import com.founderlink.authservice.dto.request.RegisterUserRequest; import com.founderlink.authservice.dto.response.UserResponse;
public interface AuthService { UserResponse register(RegisterUserRequest request); }
