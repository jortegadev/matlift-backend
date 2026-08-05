package com.matlift.readiness.infrastructure.rest.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DailyReadinessResponse(
        UUID userId,
        LocalDate recordDate,
        int sleepScore,
        int sorenessScore,
        int stressScore,
        int readinessPercentage
) {}
