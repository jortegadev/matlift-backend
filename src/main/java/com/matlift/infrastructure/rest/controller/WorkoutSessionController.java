package com.matlift.infrastructure.rest.controller;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.infrastructure.rest.dto.WorkoutSessionResponse;
import com.matlift.infrastructure.rest.mapper.WorkoutSessionRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutSessionController {

    private final SaveWorkoutSessionUseCase saveWorkoutSessionUseCase;
    private final WorkoutSessionRestMapper mapper;

    public WorkoutSessionController(SaveWorkoutSessionUseCase saveWorkoutSessionUseCase, WorkoutSessionRestMapper mapper) {
        this.saveWorkoutSessionUseCase = saveWorkoutSessionUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> saveWorkout(@RequestBody WorkoutSessionRequest request) {
        var command = mapper.toCommand(request);

        WorkoutSession savedSession = saveWorkoutSessionUseCase.execute(command);
        WorkoutSessionResponse response = mapper.toResponse(savedSession);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
