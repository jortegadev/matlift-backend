package com.matlift.infrastructure.rest.dto;

import com.matlift.domain.model.SessionCategory;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record UpdateWorkoutSessionRequest(
        @NotNull(message = "is required") ZonedDateTime sessionDate,
        @NotNull(message = "is required") SessionCategory category,
        @NotNull(message = "is required") String activityName,
        @NotNull(message = "is required") Integer durationMinutes,
        @NotNull(message = "is required") Integer rpe,
        String notes
) {}
