package com.matlift.application.service;

import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.SaveWorkoutSessionCommand;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveWorkoutSessionServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @InjectMocks
    private SaveWorkoutSessionSessionService saveWorkoutService;

    @Test
    void shouldExecuteAndSaveWorkout() {
        SaveWorkoutSessionCommand command = new SaveWorkoutSessionCommand(
                UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.STRENGTH,
                "Full body training", 60, 7, ""
        );

        when(repositoryMock.save(any(WorkoutSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkoutSession result = saveWorkoutService.execute(command);

        assertThat(result.getActivityName()).isEqualTo("Full body training");
        assertThat(result.getInternalLoad()).isEqualTo(420); // 60 * 7

        verify(repositoryMock, times(1)).save(any(WorkoutSession.class));
    }
}
