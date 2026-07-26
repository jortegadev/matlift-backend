package com.matlift.workout.domain.port.in;

import com.matlift.workout.domain.model.WorkoutSession;

public interface UpdateWorkoutSessionUseCase {
    WorkoutSession execute(UpdateWorkoutSessionCommand command);
}
