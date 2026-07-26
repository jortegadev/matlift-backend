package com.matlift.domain.port.out;

import com.matlift.domain.model.PagedResult;
import com.matlift.domain.model.WorkoutSession;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutSessionRepository {
    WorkoutSession save(WorkoutSession session);

    Optional<WorkoutSession> findById(UUID id);

    PagedResult<WorkoutSession> findByUser(UUID userId, ZonedDateTime from, ZonedDateTime to, int page, int size);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
