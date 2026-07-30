package com.matlift.user.application.service;

import com.matlift.user.domain.exception.InvalidCredentialsException;
import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.AuthenticateUserCommand;
import com.matlift.user.domain.port.in.AuthenticateUserUseCase;
import com.matlift.user.domain.port.out.AccessTokenIssuer;
import com.matlift.user.domain.port.out.PasswordHasher;
import com.matlift.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenIssuer accessTokenIssuer;

    public AuthenticateUserService(UserRepository repository,
                                   PasswordHasher passwordHasher,
                                   AccessTokenIssuer accessTokenIssuer) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.accessTokenIssuer = accessTokenIssuer;
    }

    @Override
    public AccessToken execute(AuthenticateUserCommand command) {
        Optional<User> user = repository.findByEmail(User.normalizeEmail(command.email()));

        if (user.isEmpty() || !passwordHasher.matches(command.password(), user.get().getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return accessTokenIssuer.issue(user.get().getId());
    }
}
