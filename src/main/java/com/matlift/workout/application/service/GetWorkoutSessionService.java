package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.GetWorkoutSessionUseCase;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetWorkoutSessionService implements GetWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public GetWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(UUID id, UUID requesterId) {
        return repository.findByIdAndUserId(id, requesterId)
                .orElseThrow(() -> new WorkoutSessionNotFoundException(id));
    }
}
