package com.matlift.user.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull(message = "is required") String email,
        @NotNull(message = "is required") String password
) {

    @Override
    @SuppressWarnings({"NullableProblems", "java:S2068"})
    public String toString() {
        return "CreateUserRequest[email=" + email + ", password=PROTECTED]";
    }
}
