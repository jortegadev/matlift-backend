package com.matlift.user.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "is required") String email,
        @NotNull(message = "is required") String password
) {

    @Override
    @SuppressWarnings({"NullableProblems", "java:S2068"})
    public String toString() {
        return "LoginRequest[email=" + email + ", password=PROTECTED]";
    }
}
