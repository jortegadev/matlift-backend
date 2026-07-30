package com.matlift.user.domain.port.in;

import com.matlift.user.domain.model.AccessToken;

public interface AuthenticateUserUseCase {
    AccessToken execute(AuthenticateUserCommand command);
}
