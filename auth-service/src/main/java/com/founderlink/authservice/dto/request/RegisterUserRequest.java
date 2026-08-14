package com.founderlink.authservice.dto.request;
import com.founderlink.authservice.enums.Role; import jakarta.validation.constraints.*;
public record RegisterUserRequest(@NotBlank @Size(max=150) String fullName,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(min=12,max=72) String password,@NotNull Role role){}
