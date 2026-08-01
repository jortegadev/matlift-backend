package com.matlift.workout.domain.port.in;

import com.matlift.workout.domain.model.WorkoutSession;

import java.util.UUID;

public interface GetWorkoutSessionUseCase {
    WorkoutSession execute(UUID id, UUID requesterId);
}
