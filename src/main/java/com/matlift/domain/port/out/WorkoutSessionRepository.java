package com.matlift.domain.port.out;

import com.matlift.domain.model.WorkoutSession;

import java.util.Optional;
import java.util.UUID;

public interface WorkoutSessionRepository {
    WorkoutSession save(WorkoutSession session);
    Optional<WorkoutSession> findById(UUID id);
}