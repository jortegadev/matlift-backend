package com.matlift.user.infrastructure.persistence.mapper;

import com.matlift.user.domain.model.User;
import com.matlift.user.infrastructure.persistence.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    UserEntity toEntity(User domain);

    User toDomain(UserEntity entity);
}
