package com.matlift.infrastructure.rest.dto;

import java.util.UUID;

public record WorkoutSessionResponse(
        UUID id,
        int internalLoad,
        String message
) {}
