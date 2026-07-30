package com.matlift.user.infrastructure.security;

import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.port.out.AccessTokenIssuer;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private static final int MIN_SECRET_BYTES = 32;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final JwtEncoder encoder;
    private final Duration expiry;

    public JwtAccessTokenIssuer(@Value("${matlift.security.jwt.secret}") String secret,
                                @Value("${matlift.security.jwt.expiry}") Duration expiry) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "matlift.security.jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes for HMAC-SHA256");
        }

        SecretKey key = new SecretKeySpec(secretBytes, HMAC_ALGORITHM);

        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.expiry = expiry;
    }

    @Override
    public AccessToken issue(UUID userId) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expiry);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AccessToken(value, expiresAt);
    }
}
