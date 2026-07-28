package com.matlift.user.infrastructure.rest.mapper;

import com.matlift.user.domain.model.RawPassword;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserCommand;
import com.matlift.user.infrastructure.rest.dto.CreateUserRequest;
import com.matlift.user.infrastructure.rest.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    CreateUserCommand toCommand(CreateUserRequest request);

    UserResponse toResponse(User domain);

    default RawPassword toRawPassword(String value) {
        return new RawPassword(value);
    }
}
