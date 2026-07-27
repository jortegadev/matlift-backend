package com.matlift.user.infrastructure.security;

import com.matlift.user.domain.model.RawPassword;
import com.matlift.user.domain.port.out.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(RawPassword rawPassword) {
        return encoder.encode(rawPassword.value());
    }
}
