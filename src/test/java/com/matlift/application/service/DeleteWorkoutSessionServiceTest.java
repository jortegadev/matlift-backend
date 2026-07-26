package com.matlift.application.service;

import com.matlift.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.domain.port.out.WorkoutSessionRepository;
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
    void shouldDeleteWhenSessionExists() {
        UUID id = UUID.randomUUID();

        when(repositoryMock.existsById(id)).thenReturn(true);

        service.execute(id);

        verify(repositoryMock).deleteById(id);
    }

    @Test
    void shouldThrowAndNotDeleteWhenSessionDoesNotExist() {
        UUID unknownId = UUID.randomUUID();

        when(repositoryMock.existsById(unknownId)).thenReturn(false);

        assertThatThrownBy(() -> service.execute(unknownId))
                .isInstanceOf(WorkoutSessionNotFoundException.class);

        verify(repositoryMock, never()).deleteById(unknownId);
    }
}
