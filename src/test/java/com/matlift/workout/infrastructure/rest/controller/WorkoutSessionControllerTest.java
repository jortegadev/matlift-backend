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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
        UUID requesterId = UUID.randomUUID();
        WorkoutSession saved = session(UUID.randomUUID(), requesterId);

        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class))).thenReturn(saved);

        mockMvc.perform(post("/api/workouts")
                        .principal(principalFor(requesterId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.userId").value(requesterId.toString()))
                .andExpect(jsonPath("$.category").value("CONTACT_SPORT"))
                .andExpect(jsonPath("$.activityName").value("BJJ Gi"))
                .andExpect(jsonPath("$.durationMinutes").value(90))
                .andExpect(jsonPath("$.rpe").value(8))
                .andExpect(jsonPath("$.internalLoad").value(720))
                .andExpect(jsonPath("$.notes").value("Guard passing"));
    }

    @Test
    void shouldTakeTheOwnerFromTheTokenNotFromTheBody() throws Exception {
        UUID requesterId = UUID.randomUUID();

        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenReturn(session(UUID.randomUUID(), requesterId));

        mockMvc.perform(post("/api/workouts")
                        .principal(principalFor(requesterId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());

        ArgumentCaptor<SaveWorkoutSessionCommand> captor = ArgumentCaptor.forClass(SaveWorkoutSessionCommand.class);
        verify(saveWorkoutSessionUseCase).execute(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(requesterId);
    }

    @Test
    void shouldReturn400WhenDomainThrowsException() throws Exception {
        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new IllegalArgumentException("RPE must be between 1 and 10"));

        mockMvc.perform(post("/api/workouts")
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("RPE must be between 1 and 10"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenRequiredFieldsAreMissing() throws Exception {
        String jsonWithoutRpeAndDuration = """
                {
                    "sessionDate": "2026-07-24T18:30:00Z",
                    "category": "STRENGTH",
                    "activityName": "Workout"
                }
                """;

        mockMvc.perform(post("/api/workouts")
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutRpeAndDuration))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.rpe").value("is required"))
                .andExpect(jsonPath("$.fields.durationMinutes").value("is required"));

        verify(saveWorkoutSessionUseCase, never()).execute(any(SaveWorkoutSessionCommand.class));
    }

    @Test
    void shouldReturn404WhenAuthenticatedUserNoLongerExists() throws Exception {
        UUID requesterId = UUID.randomUUID();

        when(saveWorkoutSessionUseCase.execute(any(SaveWorkoutSessionCommand.class)))
                .thenThrow(new UserNotFoundException(requesterId));

        mockMvc.perform(post("/api/workouts")
                        .principal(principalFor(requesterId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User " + requesterId + " does not exist"));
    }

    @Test
    void shouldReturn200WhenGettingExistingWorkout() throws Exception {
        UUID id = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkoutSession existing = session(id, requesterId);

        when(getWorkoutSessionUseCase.execute(id, requesterId)).thenReturn(existing);

        mockMvc.perform(get("/api/workouts/{id}", id).principal(principalFor(requesterId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.internalLoad").value(720));
    }

    @Test
    void shouldReturn404WhenGettingUnknownOrSomeoneElsesWorkout() throws Exception {
        UUID unknownId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        when(getWorkoutSessionUseCase.execute(unknownId, requesterId))
                .thenThrow(new WorkoutSessionNotFoundException(unknownId));

        mockMvc.perform(get("/api/workouts/{id}", unknownId).principal(principalFor(requesterId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Workout session " + unknownId + " does not exist"));
    }

    @Test
    void shouldReturnPagedListOfWorkoutsForTheAuthenticatedUser() throws Exception {
        UUID requesterId = UUID.randomUUID();
        PagedResult<WorkoutSession> pagedResult =
                new PagedResult<>(List.of(session(UUID.randomUUID(), requesterId)), 0, 20, 1, 1);

        when(findWorkoutSessionsUseCase.execute(any(FindWorkoutSessionsQuery.class))).thenReturn(pagedResult);

        mockMvc.perform(get("/api/workouts").principal(principalFor(requesterId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].activityName").value("BJJ Gi"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        ArgumentCaptor<FindWorkoutSessionsQuery> captor = ArgumentCaptor.forClass(FindWorkoutSessionsQuery.class);
        verify(findWorkoutSessionsUseCase).execute(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(requesterId);
    }

    @Test
    void shouldReturn200WhenUpdatingExistingWorkout() throws Exception {
        UUID id = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkoutSession updated = session(id, requesterId);

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
                        .principal(principalFor(requesterId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.internalLoad").value(720));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownOrSomeoneElsesWorkout() throws Exception {
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
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn204WhenDeletingWorkout() throws Exception {
        UUID id = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        mockMvc.perform(delete("/api/workouts/{id}", id).principal(principalFor(requesterId)))
                .andExpect(status().isNoContent());

        verify(deleteWorkoutSessionUseCase).execute(id, requesterId);
    }

    private WorkoutSession session(UUID id, UUID userId) {
        return new WorkoutSession(
                id, userId, ZonedDateTime.parse("2026-07-24T18:30:00Z"),
                SessionCategory.CONTACT_SPORT, "BJJ Gi", 90, 8, "Guard passing"
        );
    }

    private Principal principalFor(UUID id) {
        return id::toString;
    }
}
