package com.matlift.user.domain.port.in;

import com.matlift.user.domain.model.RawPassword;

public record CreateUserCommand(String email, RawPassword password) {}
