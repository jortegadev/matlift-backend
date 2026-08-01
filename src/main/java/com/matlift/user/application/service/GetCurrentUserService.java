package com.matlift.user.application.service;

import com.matlift.user.domain.exception.UserNotFoundException;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.GetCurrentUserUseCase;
import com.matlift.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepository repository;

    public GetCurrentUserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
