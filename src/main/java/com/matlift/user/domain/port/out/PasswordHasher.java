package com.matlift.user.domain.port.out;

import com.matlift.user.domain.model.RawPassword;

public interface PasswordHasher {
    String hash(RawPassword rawPassword);

    boolean matches(String candidatePassword, String passwordHash);
}
