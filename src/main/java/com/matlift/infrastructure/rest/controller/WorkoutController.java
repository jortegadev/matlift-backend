package com.matlift.infrastructure.rest.controller;

import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutUseCase;
import com.matlift.infrastructure.rest.dto.WorkoutRequest;
import com.matlift.infrastructure.rest.dto.WorkoutResponse;
import com.matlift.infrastructure.rest.mapper.WorkoutRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final SaveWorkoutUseCase saveWorkoutUseCase;
    private final WorkoutRestMapper mapper;

    public WorkoutController(SaveWorkoutUseCase saveWorkoutUseCase, WorkoutRestMapper mapper) {
        this.saveWorkoutUseCase = saveWorkoutUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> saveWorkout(@RequestBody WorkoutRequest request) {
        var command = mapper.toCommand(request);

        WorkoutSession savedSession = saveWorkoutUseCase.execute(command);
        WorkoutResponse response = mapper.toResponse(savedSession);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
