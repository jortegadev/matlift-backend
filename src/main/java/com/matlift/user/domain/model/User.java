package com.matlift.user.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public class User {

    private static final int MAX_EMAIL_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s.]+(?:\\.[^@\\s.]+){1,10}$");

    private final UUID id;
    private final String email;
    private final String passwordHash;

    @Builder
    public User(UUID id, String email, String passwordHash) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (normalizedEmail.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Email must be at most " + MAX_EMAIL_LENGTH + " characters");
        }
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Email format is invalid");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.email = normalizedEmail;
        this.passwordHash = passwordHash;
    }
}
