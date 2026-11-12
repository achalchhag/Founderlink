package com.founderlink.authservice.service;

import com.founderlink.authservice.dto.request.LoginRequest;
import com.founderlink.authservice.dto.request.RegisterUserRequest;
import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.dto.response.UserResponse;
import com.founderlink.authservice.entity.User;
import com.founderlink.authservice.enums.AccountStatus;
import com.founderlink.authservice.exception.DuplicateEmailException;
import com.founderlink.authservice.exception.InvalidCredentialsException;
import com.founderlink.authservice.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository users, PasswordEncoder encoder, JwtTokenService jwtTokenService) {
        this.users = users;
        this.encoder = encoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest r) {
        String email = r.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        User u = users.save(new User(r.fullName().trim(), email, encoder.encode(r.password()), r.role(), AccountStatus.ACTIVE));
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getAccountStatus(), u.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthTokenResponse login(LoginRequest r) {
        String email = r.email().trim().toLowerCase(Locale.ROOT);
        User user = users.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
        if (!encoder.matches(r.password(), user.getPasswordHash()) || user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }
        return jwtTokenService.createAccessToken(user);
    }
}
