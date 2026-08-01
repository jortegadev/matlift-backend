package com.matlift.user.domain.port.out;

import com.matlift.user.domain.model.AccessToken;

import java.util.UUID;

public interface AccessTokenIssuer {
    AccessToken issue(UUID userId);
}
