package com.founderlink.authservice.repository;
import com.founderlink.authservice.entity.User; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,UUID>{ Optional<User> findByEmail(String email); boolean existsByEmail(String email); }
