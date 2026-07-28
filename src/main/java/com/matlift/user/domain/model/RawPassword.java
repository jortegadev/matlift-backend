package com.matlift.user.domain.model;

public record RawPassword(String value) {

    private static final int MIN_LENGTH = 8;

    public RawPassword {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters");
        }
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "RawPassword[PROTECTED]";
    }
}
