package com.matlift.domain.port.in;

import com.matlift.domain.model.SessionCategory;

import java.time.ZonedDateTime;
import java.util.UUID;

public record SaveWorkoutSessionCommand(
        UUID userId,
        ZonedDateTime sessionDate,
        SessionCategory category,
        String activityName,
        int durationMinutes,
        int rpe,
        String notes
) {}