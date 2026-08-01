package com.matlift.user.domain.port.in;

import com.matlift.user.domain.model.User;

import java.util.UUID;

public interface GetCurrentUserUseCase {
    User execute(UUID id);
}
