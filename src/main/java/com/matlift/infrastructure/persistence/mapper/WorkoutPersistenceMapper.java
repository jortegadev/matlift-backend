package com.matlift.infrastructure.persistence.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.infrastructure.persistence.WorkoutEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkoutPersistenceMapper {

    WorkoutEntity toEntity(WorkoutSession domain);

    WorkoutSession toDomain(WorkoutEntity entity);
}
