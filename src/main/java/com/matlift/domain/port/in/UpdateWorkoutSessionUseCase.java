package com.matlift.domain.port.in;

import com.matlift.domain.model.WorkoutSession;

public interface UpdateWorkoutSessionUseCase {
    WorkoutSession execute(UpdateWorkoutSessionCommand command);
}
