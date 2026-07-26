package com.matlift.workout.infrastructure.persistence.mapper;

import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.infrastructure.persistence.WorkoutSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutSessionPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    WorkoutSessionEntity toEntity(WorkoutSession domain);

    WorkoutSession toDomain(WorkoutSessionEntity entity);
}
