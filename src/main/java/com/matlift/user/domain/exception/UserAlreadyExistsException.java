package com.matlift.user.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("Email " + email + " is already registered");
    }
}
