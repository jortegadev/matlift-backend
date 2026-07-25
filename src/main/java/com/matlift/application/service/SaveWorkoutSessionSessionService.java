package com.matlift.application.service;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SaveWorkoutSessionSessionService implements SaveWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;

    public SaveWorkoutSessionSessionService(WorkoutSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkoutSession execute(SaveWorkoutSessionCommand command) {
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