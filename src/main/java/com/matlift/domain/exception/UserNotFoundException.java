package com.matlift.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
        super("User " + userId + " does not exist");
    }
}
