package com.matlift.shared.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtConfigTest {

    private static final String VALID_SECRET = "test-only-secret-that-is-long-enough-for-hmac-sha256";

    private final JwtConfig config = new JwtConfig();

    @Test
    void shouldBuildSigningKeyFromASecretOfSufficientLength() {
        SecretKey key = config.jwtSigningKey(VALID_SECRET);

        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
        assertThat(key.getEncoded()).hasSizeGreaterThanOrEqualTo(32);
    }

    @Test
    void shouldRejectSecretShorterThanRequiredByHmacSha256() {
        String shortSecret = "too-short";

        assertThatThrownBy(() -> config.jwtSigningKey(shortSecret))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }

    @Test
    void shouldBuildDecoderFromTheSigningKey() {
        JwtDecoder decoder = config.jwtDecoder(config.jwtSigningKey(VALID_SECRET));

        assertThat(decoder).isNotNull();
    }
}
