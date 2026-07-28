package com.matlift.user.domain.port.in;

import com.matlift.user.domain.model.User;

public interface CreateUserUseCase {
    User execute(CreateUserCommand command);
}
