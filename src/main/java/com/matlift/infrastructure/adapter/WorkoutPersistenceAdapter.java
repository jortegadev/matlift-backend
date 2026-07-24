package com.matlift.infrastructure.adapter;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import com.matlift.infrastructure.persistence.SpringDataWorkoutRepository;
import com.matlift.infrastructure.persistence.WorkoutEntity;
import com.matlift.infrastructure.persistence.mapper.WorkoutPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WorkoutPersistenceAdapter implements WorkoutSessionRepository {

    private final SpringDataWorkoutRepository repository;
    private final WorkoutPersistenceMapper mapper;

    public WorkoutPersistenceAdapter(SpringDataWorkoutRepository repository, WorkoutPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkoutSession save(WorkoutSession workoutSession) {
        WorkoutEntity entity = mapper.toEntity(workoutSession);
        WorkoutEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<WorkoutSession> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}