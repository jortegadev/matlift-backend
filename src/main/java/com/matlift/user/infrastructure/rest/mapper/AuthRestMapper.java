package com.matlift.user.infrastructure.rest.mapper;

import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.port.in.AuthenticateUserCommand;
import com.matlift.user.infrastructure.rest.dto.LoginRequest;
import com.matlift.user.infrastructure.rest.dto.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    AuthenticateUserCommand toCommand(LoginRequest request);

    @Mapping(target = "accessToken", source = "value")
    @Mapping(target = "tokenType", constant = "Bearer")
    LoginResponse toResponse(AccessToken accessToken);
}
