package com.matlift.user.application.service;

import com.matlift.user.domain.exception.UserAlreadyExistsException;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserCommand;
import com.matlift.user.domain.port.in.CreateUserUseCase;
import com.matlift.user.domain.port.out.PasswordHasher;
import com.matlift.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;

    public CreateUserService(UserRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User execute(CreateUserCommand command) {
        User user = new User(null, command.email(), passwordHasher.hash(command.password()));

        if (repository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail());
        }

        return repository.save(user);
    }
}
