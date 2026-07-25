package com.matlift.infrastructure.rest.controller;

import com.matlift.domain.exception.UserNotFoundException;
import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.infrastructure.rest.dto.WorkoutSessionRequest;
import com.matlift.infrastructure.rest.dto.WorkoutSessionResponse;
import com.matlift.infrastructure.rest.mapper.WorkoutSessionRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutSessionController.class)
class WorkoutSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveWorkoutSessionUseCase saveWorkoutSessionUseCase;

    @MockitoBean
    private WorkoutSessionRestMapper mapper;

    @Test
    void shouldReturn201WhenWorkoutIsValid() throws Exception {
        UUID mockId = UUID.randomUUID();

        SaveWorkoutSessionCommand mockCommand = new SaveWorkoutSessionCommand(
                UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.STRENGTH, "Pesas", 60, 7, "Test"
        );

        WorkoutSession mockSession = new WorkoutSession(
                mockId, mockCommand.userId(), mockCommand.sessionDate(),
                mockCommand.category(), mockCommand.activityName(),
                mockCommand.durationMinutes(), mockCommand.rpe(), mockCommand.notes()
        );

        WorkoutSessionResponse mockResponse = new WorkoutSessionResponse(mockId, 420);

        when(mapper.toCommand(any(WorkoutSessionRequest.class))).thenReturn(mockCommand);
        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class))).thenReturn(mockSession);
        when(mapper.toResponse(any(WorkoutSession.class))).thenReturn(mockResponse);

        String jsonRequest = """
                {
                    "userId": "123e4567-e89b-12d3-a456-426614174000",
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "STRENGTH",
                    "activityName": "Workout",
                    "durationMinutes": 60,
                    "rpe": 7,
                    "notes": "Test"
                }
                """;

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.internalLoad").value(420));
    }

    @Test
    void shouldReturn400WhenDomainThrowsException() throws Exception {
        SaveWorkoutSessionCommand mockCommand = new SaveWorkoutSessionCommand(
                UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.STRENGTH, "Workout", 60, 15, null
        );

        when(mapper.toCommand(any(WorkoutSessionRequest.class))).thenReturn(mockCommand);
        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new IllegalArgumentException("RPE must be between 1 and 10"));

        String jsonConRpeInvalido = """
                {
                    "userId": "123e4567-e89b-12d3-a456-426614174000",
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "STRENGTH",
                    "activityName": "Workout",
                    "durationMinutes": 60,
                    "rpe": 15
                }
                """;

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConRpeInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("RPE must be between 1 and 10"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenRequiredFieldsAreMissing() throws Exception {
        String jsonWithoutRpeAndDuration = """
                {
                    "userId": "123e4567-e89b-12d3-a456-426614174000",
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "STRENGTH",
                    "activityName": "Workout"
                }
                """;

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutRpeAndDuration))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.rpe").value("is required"))
                .andExpect(jsonPath("$.fields.durationMinutes").value("is required"));

        verify(saveWorkoutSessionUseCase, never()).execute(any(SaveWorkoutSessionCommand.class));
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        UUID unknownUserId = UUID.randomUUID();

        SaveWorkoutSessionCommand mockCommand = new SaveWorkoutSessionCommand(
                unknownUserId, ZonedDateTime.now(), SessionCategory.STRENGTH, "Workout", 60, 7, null
        );

        when(mapper.toCommand(any(WorkoutSessionRequest.class))).thenReturn(mockCommand);
        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new UserNotFoundException(unknownUserId));

        String jsonRequest = """
                {
                    "userId": "%s",
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "STRENGTH",
                    "activityName": "Workout",
                    "durationMinutes": 60,
                    "rpe": 7
                }
                """.formatted(unknownUserId);

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User " + unknownUserId + " does not exist"));
    }
}
