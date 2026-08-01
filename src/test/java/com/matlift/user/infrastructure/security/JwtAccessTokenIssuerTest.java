package com.matlift.user.infrastructure.security;

import com.matlift.user.domain.model.AccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAccessTokenIssuerTest {

    private static final String SECRET = "test-only-secret-that-is-long-enough-for-hmac-sha256";
    private static final Duration EXPIRY = Duration.ofHours(24);

    private final JwtAccessTokenIssuer issuer = new JwtAccessTokenIssuer(keyFor(SECRET), EXPIRY);

    private static SecretKey keyFor(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private JwtDecoder decoderFor(String secret) {
        return NimbusJwtDecoder.withSecretKey(keyFor(secret)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Test
    void shouldIssueTokenCarryingTheUserIdAsSubject() {
        UUID userId = UUID.randomUUID();

        AccessToken token = issuer.issue(userId);
        Jwt decoded = decoderFor(SECRET).decode(token.value());

        assertThat(decoded.getSubject()).isEqualTo(userId.toString());
    }

    @Test
    void shouldIssueTokenExpiringAfterTheConfiguredDuration() {
        AccessToken token = issuer.issue(UUID.randomUUID());

        assertThat(token.expiresAt())
                .isAfter(Instant.now().plus(Duration.ofHours(23)))
                .isBefore(Instant.now().plus(Duration.ofHours(25)));

        Jwt decoded = decoderFor(SECRET).decode(token.value());

        assertThat(decoded.getExpiresAt()).isNotNull();
        assertThat(decoded.getIssuedAt()).isNotNull();
        assertThat(decoded.getExpiresAt()).isAfter(decoded.getIssuedAt());
    }

    @Test
    void shouldIssueTokenThatADifferentSecretCannotVerify() {
        String tokenValue = issuer.issue(UUID.randomUUID()).value();

        JwtDecoder foreignDecoder = decoderFor("a-completely-different-secret-also-long-enough-x");

        assertThatThrownBy(() -> foreignDecoder.decode(tokenValue))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldNotExposeTokenValueInToString() {
        AccessToken token = issuer.issue(UUID.randomUUID());

        assertThat(token.toString())
                .doesNotContain(token.value())
                .contains("PROTECTED");
    }

    @Test
    void shouldIssueDistinctTokensForDifferentUsers() {
        AccessToken first = issuer.issue(UUID.randomUUID());
        AccessToken second = issuer.issue(UUID.randomUUID());

        assertThat(first.value()).isNotEqualTo(second.value());
    }
}
