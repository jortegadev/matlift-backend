package com.matlift.infrastructure.persistence.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.infrastructure.persistence.WorkoutSessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkoutSessionPersistenceMapper {

    WorkoutSessionEntity toEntity(WorkoutSession domain);

    WorkoutSession toDomain(WorkoutSessionEntity entity);
}
