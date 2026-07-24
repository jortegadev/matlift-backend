package com.matlift.domain.port.in;

import com.matlift.domain.model.WorkoutSession;

public interface SaveWorkoutUseCase {
    WorkoutSession execute(SaveWorkoutCommand command);
}
