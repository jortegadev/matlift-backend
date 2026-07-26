package com.matlift.workout.domain.port.in;

import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.WorkoutSession;

public interface FindWorkoutSessionsUseCase {
    PagedResult<WorkoutSession> execute(FindWorkoutSessionsQuery query);
}
