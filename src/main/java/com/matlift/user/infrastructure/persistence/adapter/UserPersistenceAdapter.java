package com.matlift.user.infrastructure.persistence.adapter;

import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.out.UserRepository;
import com.matlift.user.infrastructure.persistence.SpringDataUserRepository;
import com.matlift.user.infrastructure.persistence.UserEntity;
import com.matlift.user.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(SpringDataUserRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }
}
