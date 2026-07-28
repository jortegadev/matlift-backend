package com.matlift.user.infrastructure.rest.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email
) {}
