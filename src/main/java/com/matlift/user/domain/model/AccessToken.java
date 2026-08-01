package com.matlift.user.domain.model;

import java.time.Instant;

public record AccessToken(String value, Instant expiresAt) {

    public AccessToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Access token value is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Access token expiry is required");
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "AccessToken[value=PROTECTED, expiresAt=" + expiresAt + "]";
    }
}
