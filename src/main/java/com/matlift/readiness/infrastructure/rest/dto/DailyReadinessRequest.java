package com.matlift.readiness.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

public record DailyReadinessRequest(
        @NotNull(message = "is required") Integer sleepScore,
        @NotNull(message = "is required") Integer sorenessScore,
        @NotNull(message = "is required") Integer stressScore
) {}
