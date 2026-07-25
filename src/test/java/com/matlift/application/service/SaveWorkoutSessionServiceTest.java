package com.matlift.application.service;

import com.matlift.domain.exception.UserNotFoundException;
import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.out.UserRepository;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveWorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private SaveWorkoutSessionService saveWorkoutService;

    @Test
    void shouldExecuteAndSaveWorkout() {
        SaveWorkoutSessionCommand command = new SaveWorkoutSessionCommand(
                UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.STRENGTH,
                "Full body training", 60, 7, ""
        );

        when(userRepositoryMock.existsById(command.userId())).thenReturn(true);
        when(repositoryMock.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSession result = saveWorkoutService.execute(command);

        assertThat(result.getActivityName()).isEqualTo("Full body training");
        assertThat(result.getInternalLoad()).isEqualTo(420); // 60 * 7

        verify(repositoryMock, times(1)).save(any(WorkoutSession.class));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        UUID unknownUserId = UUID.randomUUID();

        SaveWorkoutSessionCommand command = new SaveWorkoutSessionCommand(
                unknownUserId, ZonedDateTime.now(), SessionCategory.CARDIO,
                "Running", 30, 6, null
        );

        when(userRepositoryMock.existsById(unknownUserId)).thenReturn(false);

        assertThatThrownBy(() -> saveWorkoutService.execute(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(unknownUserId.toString());

        verify(repositoryMock, never()).save(any(WorkoutSession.class));
    }
}
