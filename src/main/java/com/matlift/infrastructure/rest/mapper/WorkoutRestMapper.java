package com.matlift.infrastructure.rest.mapper;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutCommand;
import com.matlift.infrastructure.rest.dto.WorkoutRequest;
import com.matlift.infrastructure.rest.dto.WorkoutResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkoutRestMapper {

    SaveWorkoutCommand toCommand(WorkoutRequest request);

    @Mapping(target = "message", constant = "Workout successfully saved")
    WorkoutResponse toResponse(WorkoutSession domain);
}