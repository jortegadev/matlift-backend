package com.matlift.application.service;

import com.matlift.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.domain.port.in.UpdateWorkoutSessionUseCase;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateWorkoutSessionService implements UpdateWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public UpdateWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(UpdateWorkoutSessionCommand command) {
        WorkoutSession existing = repository.findById(command.id())
                .orElseThrow(() -> new WorkoutSessionNotFoundException(command.id()));

        WorkoutSession updated = new WorkoutSession(
                existing.getId(),
                existing.getUserId(),
                command.sessionDate(),
                command.category(),
                command.activityName(),
                command.durationMinutes(),
                command.rpe(),
                command.notes()
        );

        return repository.save(updated);
    }
}
