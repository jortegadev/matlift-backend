package com.matlift.infrastructure.persistence.adapter;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import com.matlift.infrastructure.persistence.SpringDataWorkoutSessionRepository;
import com.matlift.infrastructure.persistence.WorkoutSessionEntity;
import com.matlift.infrastructure.persistence.mapper.WorkoutSessionPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WorkoutSessionPersistenceAdapter implements WorkoutSessionRepository {

    private final SpringDataWorkoutSessionRepository repository;
    private final WorkoutSessionPersistenceMapper mapper;

    public WorkoutSessionPersistenceAdapter(SpringDataWorkoutSessionRepository repository, WorkoutSessionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkoutSession save(WorkoutSession workoutSession) {
        WorkoutSessionEntity entity = mapper.toEntity(workoutSession);
        WorkoutSessionEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<WorkoutSession> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}