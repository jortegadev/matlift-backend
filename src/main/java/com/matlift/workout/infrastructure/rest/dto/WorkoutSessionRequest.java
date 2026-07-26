package com.matlift.workout.infrastructure.rest.dto;

import com.matlift.workout.domain.model.SessionCategory;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WorkoutSessionRequest(
        @NotNull(message = "is required") UUID userId,
        @NotNull(message = "is required") ZonedDateTime sessionDate,
        @NotNull(message = "is required") SessionCategory category,
        @NotNull(message = "is required") String activityName,
        @NotNull(message = "is required") Integer durationMinutes,
        @NotNull(message = "is required") Integer rpe,
        String notes
) {}
