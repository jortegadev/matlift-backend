package com.matlift.infrastructure.rest.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.infrastructure.rest.dto.UpdateWorkoutSessionRequest;
import com.matlift.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.infrastructure.rest.dto.WorkoutSessionResponse;
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
