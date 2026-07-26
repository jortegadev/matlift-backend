package com.matlift.application.service;

import com.matlift.domain.exception.UserNotFoundException;
import com.matlift.domain.model.PagedResult;
import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.in.FindWorkoutSessionsQuery;
import com.matlift.domain.port.out.UserRepository;
import com.matlift.domain.port.out.WorkoutSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindWorkoutSessionsServiceTest {

    @Mock
    private WorkoutSessionRepository repositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private FindWorkoutSessionsService service;

    @Test
    void shouldReturnPagedSessionsForExistingUser() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime from = ZonedDateTime.now().minusDays(7);
        ZonedDateTime to = ZonedDateTime.now();

        WorkoutSession session = new WorkoutSession(
                UUID.randomUUID(), userId, to, SessionCategory.STRENGTH, "Full body", 60, 7, null
        );
        PagedResult<WorkoutSession> expected = new PagedResult<>(List.of(session), 0, 20, 1, 1);

        when(userRepositoryMock.existsById(userId)).thenReturn(true);
        when(repositoryMock.findByUser(userId, from, to, 0, 20)).thenReturn(expected);

        PagedResult<WorkoutSession> result =
                service.execute(new FindWorkoutSessionsQuery(userId, from, to, 0, 20));

        assertThat(result).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        UUID unknownUserId = UUID.randomUUID();

        when(userRepositoryMock.existsById(unknownUserId)).thenReturn(false);

        FindWorkoutSessionsQuery query = new FindWorkoutSessionsQuery(unknownUserId, null, null, 0, 20);

        assertThatThrownBy(() -> service.execute(query))
                .isInstanceOf(UserNotFoundException.class);

        verify(repositoryMock, never()).findByUser(eq(unknownUserId), any(), any(), anyInt(), anyInt());
    }
}
