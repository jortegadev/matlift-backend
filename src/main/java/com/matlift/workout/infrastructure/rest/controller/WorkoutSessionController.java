package com.matlift.workout.infrastructure.rest.controller;

import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.DeleteWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsQuery;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsUseCase;
import com.matlift.workout.domain.port.in.GetWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionUseCase;
import com.matlift.shared.infrastructure.rest.dto.PagedResponse;
import com.matlift.workout.infrastructure.rest.dto.UpdateWorkoutSessionRequest;
import com.matlift.workout.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.workout.infrastructure.rest.dto.WorkoutSessionResponse;
import com.matlift.workout.infrastructure.rest.mapper.WorkoutSessionRestMapper;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutSessionController {

    private final SaveWorkoutSessionUseCase saveWorkoutSessionUseCase;
    private final GetWorkoutSessionUseCase getWorkoutSessionUseCase;
    private final FindWorkoutSessionsUseCase findWorkoutSessionsUseCase;
    private final UpdateWorkoutSessionUseCase updateWorkoutSessionUseCase;
    private final DeleteWorkoutSessionUseCase deleteWorkoutSessionUseCase;
    private final WorkoutSessionRestMapper mapper;

    public WorkoutSessionController(SaveWorkoutSessionUseCase saveWorkoutSessionUseCase,
                                    GetWorkoutSessionUseCase getWorkoutSessionUseCase,
                                    FindWorkoutSessionsUseCase findWorkoutSessionsUseCase,
                                    UpdateWorkoutSessionUseCase updateWorkoutSessionUseCase,
                                    DeleteWorkoutSessionUseCase deleteWorkoutSessionUseCase,
                                    WorkoutSessionRestMapper mapper) {
        this.saveWorkoutSessionUseCase = saveWorkoutSessionUseCase;
        this.getWorkoutSessionUseCase = getWorkoutSessionUseCase;
        this.findWorkoutSessionsUseCase = findWorkoutSessionsUseCase;
        this.updateWorkoutSessionUseCase = updateWorkoutSessionUseCase;
        this.deleteWorkoutSessionUseCase = deleteWorkoutSessionUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> saveWorkout(@Valid @RequestBody WorkoutSessionRequest request) {
        WorkoutSession savedSession = saveWorkoutSessionUseCase.execute(mapper.toCommand(request));

        return new ResponseEntity<>(mapper.toResponse(savedSession), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> getWorkout(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getWorkoutSessionUseCase.execute(id)));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<WorkoutSessionResponse>> findWorkouts(
            @RequestParam UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResult<WorkoutSession> result =
                findWorkoutSessionsUseCase.execute(new FindWorkoutSessionsQuery(userId, from, to, page, size));

        List<WorkoutSessionResponse> content = result.content().stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                content, result.page(), result.size(), result.totalElements(), result.totalPages()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> updateWorkout(@PathVariable UUID id,
                                                                @Valid @RequestBody UpdateWorkoutSessionRequest request) {
        WorkoutSession updatedSession = updateWorkoutSessionUseCase.execute(mapper.toUpdateCommand(id, request));

        return ResponseEntity.ok(mapper.toResponse(updatedSession));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable UUID id) {
        deleteWorkoutSessionUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
