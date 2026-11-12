package com.founderlink.authservice.dto.response;

import com.founderlink.authservice.enums.AccountStatus;
import com.founderlink.authservice.enums.Role;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        AccountStatus accountStatus,
        Instant createdAt
) {
}
