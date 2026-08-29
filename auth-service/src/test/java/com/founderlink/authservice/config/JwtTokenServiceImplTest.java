package com.founderlink.authservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.founderlink.authservice.dto.response.AuthTokenResponse;
import com.founderlink.authservice.entity.User;
import com.founderlink.authservice.enums.AccountStatus;
import com.founderlink.authservice.enums.Role;
import com.founderlink.authservice.service.JwtTokenServiceImpl;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenServiceImplTest {

    @Test
    void createsVerifiableRs256TokenWithRequiredClaims() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        JwtProperties properties = new JwtProperties(
                "https://auth.founderlink.test",
                900,
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())
        );
        JwtEncoder encoder = new JwtConfiguration().jwtEncoder(properties);
        User user = new User("Rahul Sharma", "rahul@example.com", "bcrypt-hash", Role.FOUNDER, AccountStatus.ACTIVE);
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);

        AuthTokenResponse response = new JwtTokenServiceImpl(encoder, properties).createAccessToken(user);
        Jwt jwt = NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build().decode(response.accessToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);
        assertThat(jwt.getSubject()).isEqualTo(userId.toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo("rahul@example.com");
        assertThat(jwt.getClaimAsString("role")).isEqualTo("FOUNDER");
        assertThat(jwt.getIssuer().toString()).isEqualTo("https://auth.founderlink.test");
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
        assertThat(jwt.getClaims()).doesNotContainKeys("password", "passwordHash");
    }
}
