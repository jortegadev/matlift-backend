package com.matlift.application.service;

import com.matlift.domain.exception.UserNotFoundException;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.domain.port.out.UserRepository;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SaveWorkoutSessionService implements SaveWorkoutSessionUseCase {

    private final WorkoutSessionRepository repository;
    private final UserRepository userRepository;

    public SaveWorkoutSessionService(WorkoutSessionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

        if (!userRepository.existsById(session.getUserId())) {
            throw new UserNotFoundException(session.getUserId());
        }

        return repository.save(session);
    }
}