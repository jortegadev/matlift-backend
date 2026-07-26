package com.matlift.user.domain.port.out;

import com.matlift.user.domain.model.User;

import java.util.UUID;

public interface UserRepository {
    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    User save(User user);
}
