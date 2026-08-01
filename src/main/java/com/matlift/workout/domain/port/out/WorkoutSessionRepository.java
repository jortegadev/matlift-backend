package com.matlift.workout.domain.port.out;

import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.WorkoutSession;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutSessionRepository {
    WorkoutSession save(WorkoutSession session);

    Optional<WorkoutSession> findByIdAndUserId(UUID id, UUID userId);

    PagedResult<WorkoutSession> findByUser(UUID userId, ZonedDateTime from, ZonedDateTime to, int page, int size);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    void deleteById(UUID id);
}
