package com.matlift.workout.application.service;

import com.matlift.workout.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteWorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @InjectMocks
    private DeleteWorkoutSessionService service;

    @Test
    void shouldDeleteWhenSessionBelongsToTheRequester() {
        UUID id = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        when(repositoryMock.existsByIdAndUserId(id, requesterId)).thenReturn(true);

        service.execute(id, requesterId);

        verify(repositoryMock).deleteById(id);
    }

    @Test
    void shouldThrowAndNotDeleteWhenSessionDoesNotExist() {
        UUID unknownId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        when(repositoryMock.existsByIdAndUserId(unknownId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.execute(unknownId, requesterId))
                .isInstanceOf(WorkoutSessionNotFoundException.class);

        verify(repositoryMock, never()).deleteById(unknownId);
    }
}
