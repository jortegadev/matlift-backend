package com.matlift.infrastructure.persistence.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.infrastructure.persistence.WorkoutSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutSessionPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    WorkoutSessionEntity toEntity(WorkoutSession domain);

    WorkoutSession toDomain(WorkoutSessionEntity entity);
}
