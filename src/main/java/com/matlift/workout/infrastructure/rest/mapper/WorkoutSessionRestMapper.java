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

    @Mapping(target = "userId", source = "userId")
    SaveWorkoutSessionCommand toCommand(WorkoutSessionRequest request, UUID userId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "requesterId", source = "requesterId")
    UpdateWorkoutSessionCommand toUpdateCommand(UUID id, UUID requesterId, UpdateWorkoutSessionRequest request);

    WorkoutSessionResponse toResponse(WorkoutSession domain);
}
