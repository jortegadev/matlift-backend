package com.matlift.infrastructure.rest.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.infrastructure.rest.dto.WorkoutSessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutSessionRestMapper {

    SaveWorkoutSessionCommand toCommand(WorkoutSessionRequest request);

    @Mapping(target = "message", constant = "Workout successfully saved")
    WorkoutSessionResponse toResponse(WorkoutSession domain);
}