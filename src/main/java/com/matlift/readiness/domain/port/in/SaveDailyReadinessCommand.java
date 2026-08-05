package com.matlift.readiness.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

public record SaveDailyReadinessCommand(
        UUID userId,
        LocalDate recordDate,
        int sleepScore,
        int sorenessScore,
        int stressScore
) {}
