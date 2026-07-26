package com.matlift.workout.infrastructure.rest.dto;

import com.matlift.workout.domain.model.SessionCategory;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WorkoutSessionResponse(
        UUID id,
        UUID userId,
        ZonedDateTime sessionDate,
        SessionCategory category,
        String activityName,
        int durationMinutes,
        int rpe,
        int internalLoad,
        String notes
) {}
