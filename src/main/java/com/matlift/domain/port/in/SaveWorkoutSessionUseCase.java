package com.matlift.domain.port.in;

import com.matlift.domain.model.WorkoutSession;

public interface SaveWorkoutSessionUseCase {
    WorkoutSession execute(SaveWorkoutSessionCommand command);
}
