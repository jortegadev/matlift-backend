package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.model.SessionCategory;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @InjectMocks
    private GetWorkoutSessionService service;

    @Test
    void shouldReturnSessionWhenItBelongsToTheRequester() {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        WorkoutSession session = new WorkoutSession(
                id, ownerId, ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ", 90, 8, null
        );

        when(repositoryMock.findByIdAndUserId(id, ownerId)).thenReturn(Optional.of(session));

        assertThat(service.execute(id, ownerId)).isSameAs(session);
    }

    @Test
    void shouldThrowWhenSessionDoesNotExist() {
        UUID unknownId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        when(repositoryMock.findByIdAndUserId(unknownId, requesterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId, requesterId))
                .isInstanceOf(WorkoutSessionNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
