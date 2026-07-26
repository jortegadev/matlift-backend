package com.matlift.workout.infrastructure.rest.controller;

import com.matlift.user.domain.exception.UserNotFoundException;
import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.SessionCategory;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.DeleteWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsQuery;
import com.matlift.workout.domain.port.in.FindWorkoutSessionsUseCase;
import com.matlift.workout.domain.port.in.GetWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.workout.domain.port.in.SaveWorkoutSessionUseCase;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionUseCase;
import com.matlift.workout.infrastructure.rest.mapper.WorkoutSessionRestMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutSessionController.class)
@Import(WorkoutSessionRestMapperImpl.class)
class WorkoutSessionControllerTest {

    private static final String VALID_BODY = """
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveWorkoutSessionUseCase saveWorkoutSessionUseCase;

    @MockitoBean
    private GetWorkoutSessionUseCase getWorkoutSessionUseCase;

    @MockitoBean
    private FindWorkoutSessionsUseCase findWorkoutSessionsUseCase;

    @MockitoBean
    private UpdateWorkoutSessionUseCase updateWorkoutSessionUseCase;

    @MockitoBean
    private DeleteWorkoutSessionUseCase deleteWorkoutSessionUseCase;

    @Test
    void shouldReturn201WithFullRepresentationWhenWorkoutIsValid() throws Exception {
        WorkoutSession saved = session(UUID.randomUUID());

        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class))).thenReturn(saved);

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.userId").value(saved.getUserId().toString()))
                .andExpect(jsonPath("$.category").value("CONTACT_SPORT"))
                .andExpect(jsonPath("$.activityName").value("BJJ Gi"))
                .andExpect(jsonPath("$.durationMinutes").value(90))
                .andExpect(jsonPath("$.rpe").value(8))
                .andExpect(jsonPath("$.internalLoad").value(720))
                .andExpect(jsonPath("$.notes").value("Guard passing"));
    }

    @Test
    void shouldReturn400WhenDomainThrowsException() throws Exception {
        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new IllegalArgumentException("RPE must be between 1 and 10"));

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
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

        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new UserNotFoundException(unknownUserId));

        mockMvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User " + unknownUserId + " does not exist"));
    }

    @Test
    void shouldReturn200WhenGettingExistingWorkout() throws Exception {
        UUID id = UUID.randomUUID();
        WorkoutSession existing = session(id);

        when(getWorkoutSessionUseCase.execute(id)).thenReturn(existing);

        mockMvc.perform(get("/api/workouts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.internalLoad").value(720));
    }

    @Test
    void shouldReturn404WhenGettingUnknownWorkout() throws Exception {
        UUID unknownId = UUID.randomUUID();

        when(getWorkoutSessionUseCase.execute(unknownId))
                .thenThrow(new WorkoutSessionNotFoundException(unknownId));

        mockMvc.perform(get("/api/workouts/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Workout session " + unknownId + " does not exist"));
    }

    @Test
    void shouldReturnPagedListOfWorkouts() throws Exception {
        UUID userId = UUID.randomUUID();
        PagedResult<WorkoutSession> pagedResult =
                new PagedResult<>(List.of(session(UUID.randomUUID())), 0, 20, 1, 1);

        when(findWorkoutSessionsUseCase.execute(any(FindWorkoutSessionsQuery.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/workouts").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].activityName").value("BJJ Gi"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturn400WhenListingWithoutUserId() throws Exception {
        mockMvc.perform(get("/api/workouts"))
                .andExpect(status().isBadRequest());

        verify(findWorkoutSessionsUseCase, never()).execute(any(FindWorkoutSessionsQuery.class));
    }

    @Test
    void shouldReturn200WhenUpdatingExistingWorkout() throws Exception {
        UUID id = UUID.randomUUID();
        WorkoutSession updated = session(id);

        when(updateWorkoutSessionUseCase.execute(any(UpdateWorkoutSessionCommand.class))).thenReturn(updated);

        String updateBody = """
                {
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "CONTACT_SPORT",
                    "activityName": "BJJ Gi",
                    "durationMinutes": 90,
                    "rpe": 8,
                    "notes": "Guard passing"
                }
                """;

        mockMvc.perform(put("/api/workouts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.internalLoad").value(720));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownWorkout() throws Exception {
        UUID unknownId = UUID.randomUUID();

        when(updateWorkoutSessionUseCase.execute(any(UpdateWorkoutSessionCommand.class)))
                .thenThrow(new WorkoutSessionNotFoundException(unknownId));

        String updateBody = """
                {
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "CARDIO",
                    "activityName": "Running",
                    "durationMinutes": 30,
                    "rpe": 6
                }
                """;

        mockMvc.perform(put("/api/workouts/{id}", unknownId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn204WhenDeletingWorkout() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/workouts/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteWorkoutSessionUseCase).execute(id);
    }

    private WorkoutSession session(UUID id) {
        return new WorkoutSession(
                id, UUID.randomUUID(), ZonedDateTime.parse("2026-07-24T18:30:00Z"),
                SessionCategory.CONTACT_SPORT, "BJJ Gi", 90, 8, "Guard passing"
        );
    }
}
