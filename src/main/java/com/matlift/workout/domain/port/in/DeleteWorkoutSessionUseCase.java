package com.matlift.workout.domain.port.in;

import java.util.UUID;

public interface DeleteWorkoutSessionUseCase {
    void execute(UUID id);
}
