package com.matlift.user.infrastructure.persistence.adapter;

import com.matlift.user.domain.port.out.UserRepository;
import com.matlift.user.infrastructure.persistence.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}
