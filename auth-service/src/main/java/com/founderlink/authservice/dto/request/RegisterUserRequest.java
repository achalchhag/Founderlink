package com.founderlink.authservice.dto.request;

import com.founderlink.authservice.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 6, max = 72, message = "Password must be at least 6 characters") String password,
        Role role
) {
    public RegisterUserRequest {
        if (role == null) {
            role = Role.FOUNDER;
        }
    }
}
