package com.founderlink.authservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.JWSAlgorithm;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {

    @Bean
    JwtEncoder jwtEncoder(JwtProperties properties) {
        RSAPublicKey publicKey = (RSAPublicKey) decodePublicKey(properties.publicKey());
        RSAPrivateKey privateKey = (RSAPrivateKey) decodePrivateKey(properties.privateKey());
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("auth-service")
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    private PrivateKey decodePrivateKey(String encodedKey) {
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKey)));
        } catch (Exception exception) {
            throw new IllegalStateException("AUTH_JWT_PRIVATE_KEY must be a Base64-encoded PKCS#8 RSA private key", exception);
        }
    }

    private PublicKey decodePublicKey(String encodedKey) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(encodedKey)));
        } catch (Exception exception) {
            throw new IllegalStateException("AUTH_JWT_PUBLIC_KEY must be a Base64-encoded X.509 RSA public key", exception);
        }
    }
}
