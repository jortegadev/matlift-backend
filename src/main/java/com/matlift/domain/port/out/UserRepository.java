package com.matlift.domain.port.out;

import java.util.UUID;

public interface UserRepository {
    boolean existsById(UUID id);
}
