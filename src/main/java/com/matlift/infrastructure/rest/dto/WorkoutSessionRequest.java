package com.matlift.infrastructure.rest.dto;

import com.matlift.domain.model.SessionCategory;

import java.time.ZonedDateTime;
import java.util.UUID;

public record WorkoutSessionRequest(
        UUID userId,
        ZonedDateTime sessionDate,
        SessionCategory category,
        String activityName,
        int durationMinutes,
        int rpe,
        String notes
) {}