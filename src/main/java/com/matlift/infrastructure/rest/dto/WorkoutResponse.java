package com.matlift.infrastructure.rest.dto;

import java.util.UUID;

public record WorkoutResponse(
        UUID id,
        int internalLoad,
        String message
) {}
