package com.matlift.application.service;

import com.matlift.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.domain.port.in.DeleteWorkoutSessionUseCase;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteWorkoutSessionService implements DeleteWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public DeleteWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        if (!repository.existsById(id)) {
            throw new WorkoutSessionNotFoundException(id);
        }

        repository.deleteById(id);
    }
}
