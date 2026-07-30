package com.matlift.user.domain.port.in;

public record AuthenticateUserCommand(String email, String password) {

    @Override
    @SuppressWarnings({"NullableProblems", "java:S2068"})
    public String toString() {
        return "AuthenticateUserCommand[email=" + email + ", password=PROTECTED]";
    }
}
