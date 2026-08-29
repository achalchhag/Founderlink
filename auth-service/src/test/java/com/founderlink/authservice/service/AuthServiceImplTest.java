package com.founderlink.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.founderlink.authservice.dto.request.RegisterUserRequest;
import com.founderlink.authservice.dto.request.LoginRequest;
import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.entity.User;
import com.founderlink.authservice.enums.AccountStatus;
import com.founderlink.authservice.enums.Role;
import com.founderlink.authservice.exception.DuplicateEmailException;
import com.founderlink.authservice.exception.InvalidCredentialsException;
import com.founderlink.authservice.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Mock UserRepository users;
  @Mock JwtTokenService jwtTokenService;
  @Test void normalizesEmailHashesPasswordAndActivatesUser() {
    AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12),jwtTokenService);
    RegisterUserRequest request=new RegisterUserRequest(" Rahul ","Test@Example.com","StrongPassword123!",Role.FOUNDER);
    when(users.save(any(User.class))).thenAnswer(i->i.getArgument(0));
    service.register(request);
    ArgumentCaptor<User> captor=ArgumentCaptor.forClass(User.class); verify(users).save(captor.capture());
    User saved=captor.getValue(); assertThat(saved.getEmail()).isEqualTo("test@example.com"); assertThat(saved.getPasswordHash()).isNotEqualTo(request.password()); assertThat(new BCryptPasswordEncoder().matches(request.password(),saved.getPasswordHash())).isTrue(); assertThat(saved.getAccountStatus().name()).isEqualTo("ACTIVE");
  }
  @Test void rejectsDuplicateEmail(){ when(users.existsByEmail("test@example.com")).thenReturn(true); AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12),jwtTokenService); assertThatThrownBy(()->service.register(new RegisterUserRequest("Test","test@example.com","StrongPassword123!",Role.FOUNDER))).isInstanceOf(DuplicateEmailException.class); verify(users,never()).save(any()); }
  @Test void logsInWithNormalizedEmailAndIssuesToken(){ User user=user(AccountStatus.ACTIVE); AuthTokenResponse token=new AuthTokenResponse("token","Bearer",900); when(users.findByEmail("test@example.com")).thenReturn(Optional.of(user)); when(jwtTokenService.createAccessToken(user)).thenReturn(token); AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12),jwtTokenService); assertThat(service.login(new LoginRequest(" Test@Example.com ","StrongPassword123!"))).isEqualTo(token); verify(users).findByEmail("test@example.com"); verify(jwtTokenService).createAccessToken(user); }
  @Test void rejectsUnknownLoginEmail(){ when(users.findByEmail("unknown@example.com")).thenReturn(Optional.empty()); assertInvalidLogin(new LoginRequest("unknown@example.com","StrongPassword123!")); }
  @Test void rejectsIncorrectPassword(){ User user=user(AccountStatus.ACTIVE); when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); assertInvalidLogin(new LoginRequest(user.getEmail(),"incorrect-password")); }
  @Test void rejectsInactiveAccount(){ User user=user(AccountStatus.INACTIVE); when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); assertInvalidLogin(new LoginRequest(user.getEmail(),"StrongPassword123!")); }
  @Test void rejectsLockedAccount(){ User user=user(AccountStatus.LOCKED); when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user)); assertInvalidLogin(new LoginRequest(user.getEmail(),"StrongPassword123!")); }
  private void assertInvalidLogin(LoginRequest request){ AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12),jwtTokenService); assertThatThrownBy(()->service.login(request)).isInstanceOf(InvalidCredentialsException.class); verify(jwtTokenService,never()).createAccessToken(any()); }
  private User user(AccountStatus status){ User user=new User("Test User","test@example.com",new BCryptPasswordEncoder(12).encode("StrongPassword123!"),Role.FOUNDER,status); ReflectionTestUtils.setField(user,"id",UUID.randomUUID()); return user; }
}
