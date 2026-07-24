package com.matlift.application.service;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutCommand;
import com.matlift.domain.port.in.SaveWorkoutUseCase;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SaveWorkoutService implements SaveWorkoutUseCase {

    private final WorkoutSessionRepository repository;

    public SaveWorkoutService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(SaveWorkoutCommand command) {
        WorkoutSession session = new WorkoutSession(
                null,
                command.userId(),
                command.sessionDate(),
                command.category(),
                command.activityName(),
                command.durationMinutes(),
                command.rpe(),
                command.notes()
        );
        return repository.save(session);
    }
}