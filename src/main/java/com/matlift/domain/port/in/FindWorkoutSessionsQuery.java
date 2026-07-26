package com.matlift.domain.port.in;

import java.time.ZonedDateTime;
import java.util.UUID;

public record FindWorkoutSessionsQuery(
        UUID userId,
        ZonedDateTime from,
        ZonedDateTime to,
        int page,
        int size
) {}
