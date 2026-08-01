package com.matlift.user.domain.port.out;

import com.matlift.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsById(UUID id);

    boolean existsByEmail(String email);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    User save(User user);
}
