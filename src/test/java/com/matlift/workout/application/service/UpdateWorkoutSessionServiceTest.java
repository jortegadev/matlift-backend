package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.model.SessionCategory;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.in.UpdateWorkoutSessionCommand;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateWorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @InjectMocks
    private UpdateWorkoutSessionService service;

    @Test
    void shouldKeepIdAndOwnerAndRecalculateInternalLoad() {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        WorkoutSession existing = new WorkoutSession(
                id, ownerId, ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ", 90, 8, "original"
        );

        UpdateWorkoutSessionCommand command = new UpdateWorkoutSessionCommand(
                id, ownerId, ZonedDateTime.now(), SessionCategory.CARDIO, "Running", 30, 6, "updated"
        );

        when(repositoryMock.findByIdAndUserId(id, ownerId)).thenReturn(Optional.of(existing));
        when(repositoryMock.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSession result = service.execute(command);

        ArgumentCaptor<WorkoutSession> captor = ArgumentCaptor.forClass(WorkoutSession.class);
        verify(repositoryMock).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getUserId()).isEqualTo(ownerId);

        assertThat(result.getActivityName()).isEqualTo("Running");
        assertThat(result.getCategory()).isEqualTo(SessionCategory.CARDIO);
        assertThat(result.getInternalLoad()).isEqualTo(180);
    }

    @Test
    void shouldThrowAndNotSaveWhenSessionDoesNotExist() {
        UUID unknownId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        UpdateWorkoutSessionCommand command = new UpdateWorkoutSessionCommand(
                unknownId, requesterId, ZonedDateTime.now(), SessionCategory.CARDIO, "Running", 30, 6, null
        );

        when(repositoryMock.findByIdAndUserId(unknownId, requesterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(WorkoutSessionNotFoundException.class);

        verify(repositoryMock, never()).save(any(WorkoutSession.class));
    }
}
