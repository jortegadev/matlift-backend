package com.matlift.workout.domain.exception;

import java.util.UUID;

public class WorkoutSessionNotFoundException extends RuntimeException {

    public WorkoutSessionNotFoundException(UUID workoutSessionId) {
        super("Workout session " + workoutSessionId + " does not exist");
    }
}
