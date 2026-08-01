package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.port.in.DeleteWorkoutSessionUseCase;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteWorkoutSessionService implements DeleteWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public DeleteWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UUID requesterId) {
        if (!repository.existsByIdAndUserId(id, requesterId)) {
            throw new WorkoutSessionNotFoundException(id);
        }

        repository.deleteById(id);
    }
}
