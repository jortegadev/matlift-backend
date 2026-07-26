package com.matlift.domain.port.in;

import com.matlift.domain.model.WorkoutSession;

import java.util.UUID;

public interface GetWorkoutSessionUseCase {
    WorkoutSession execute(UUID id);
}
