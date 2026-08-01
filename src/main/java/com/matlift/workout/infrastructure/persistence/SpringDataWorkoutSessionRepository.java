package com.matlift.workout.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataWorkoutSessionRepository
        extends JpaRepository<WorkoutSessionEntity, UUID>, JpaSpecificationExecutor<WorkoutSessionEntity> {

    Optional<WorkoutSessionEntity> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
