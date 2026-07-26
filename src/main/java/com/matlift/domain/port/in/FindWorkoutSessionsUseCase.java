package com.matlift.domain.port.in;

import com.matlift.domain.model.PagedResult;
import com.matlift.domain.model.WorkoutSession;

public interface FindWorkoutSessionsUseCase {
    PagedResult<WorkoutSession> execute(FindWorkoutSessionsQuery query);
}
