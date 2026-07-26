package com.matlift.application.service;

import com.matlift.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.GetWorkoutSessionUseCase;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetWorkoutSessionService implements GetWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public GetWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new WorkoutSessionNotFoundException(id));
    }
}
