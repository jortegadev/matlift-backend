package com.matlift.workout.infrastructure.rest.mapper;

import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.workout.infrastructure.rest.dto.UpdateWorkoutSessionRequest;
import com.matlift.workout.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.workout.infrastructure.rest.dto.WorkoutSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WorkoutSessionRestMapper {

    SaveWorkoutSessionCommand toCommand(WorkoutSessionRequest request);

    @Mapping(target = "id", source = "id")
    UpdateWorkoutSessionCommand toUpdateCommand(UUID id, UpdateWorkoutSessionRequest request);

    WorkoutSessionResponse toResponse(WorkoutSession domain);
}
