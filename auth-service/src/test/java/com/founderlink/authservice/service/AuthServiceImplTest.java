package com.founderlink.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.founderlink.authservice.dto.request.RegisterUserRequest;
import com.founderlink.authservice.entity.User;
import com.founderlink.authservice.enums.Role;
import com.founderlink.authservice.exception.DuplicateEmailException;
import com.founderlink.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Mock UserRepository users;
  @Test void normalizesEmailHashesPasswordAndActivatesUser() {
    AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12));
    RegisterUserRequest request=new RegisterUserRequest(" Rahul ","Test@Example.com","StrongPassword123!",Role.FOUNDER);
    when(users.save(any(User.class))).thenAnswer(i->i.getArgument(0));
    service.register(request);
    ArgumentCaptor<User> captor=ArgumentCaptor.forClass(User.class); verify(users).save(captor.capture());
    User saved=captor.getValue(); assertThat(saved.getEmail()).isEqualTo("test@example.com"); assertThat(saved.getPasswordHash()).isNotEqualTo(request.password()); assertThat(new BCryptPasswordEncoder().matches(request.password(),saved.getPasswordHash())).isTrue(); assertThat(saved.getAccountStatus().name()).isEqualTo("ACTIVE");
  }
  @Test void rejectsDuplicateEmail(){ when(users.existsByEmail("test@example.com")).thenReturn(true); AuthService service=new AuthServiceImpl(users,new BCryptPasswordEncoder(12)); assertThatThrownBy(()->service.register(new RegisterUserRequest("Test","test@example.com","StrongPassword123!",Role.FOUNDER))).isInstanceOf(DuplicateEmailException.class); verify(users,never()).save(any()); }
}
