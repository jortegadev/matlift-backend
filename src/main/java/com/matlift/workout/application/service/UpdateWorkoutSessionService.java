package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionUseCase;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateWorkoutSessionService implements UpdateWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public UpdateWorkoutSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(UpdateWorkoutSessionCommand command) {
        WorkoutSession existing = repository.findByIdAndUserId(command.id(), command.requesterId())
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
