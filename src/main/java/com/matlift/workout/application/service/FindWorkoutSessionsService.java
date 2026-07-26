package com.matlift.workout.application.service;

import com.matlift.user.domain.exception.UserNotFoundException;
import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsQuery;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsUseCase;
import com.matlift.user.domain.port.out.UserRepository;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class FindWorkoutSessionsService implements FindWorkoutSessionsUseCase {

    private final WorkoutSessionRepository repository;
    private final UserRepository userRepository;

    public FindWorkoutSessionsService(WorkoutSessionRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public PagedResult<WorkoutSession> execute(FindWorkoutSessionsQuery query) {
        if (!userRepository.existsById(query.userId())) {
            throw new UserNotFoundException(query.userId());
        }

        return repository.findByUser(query.userId(), query.from(), query.to(), query.page(), query.size());
    }
}
