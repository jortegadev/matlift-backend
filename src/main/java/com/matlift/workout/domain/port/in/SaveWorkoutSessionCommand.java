package com.matlift.workout.domain.port.in;

import com.matlift.workout.domain.model.SessionCategory;

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