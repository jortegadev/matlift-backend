package com.matlift.application.service;

import com.matlift.domain.exception.WorkoutSessionNotFoundException;
import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.domain.port.out.WorkoutSessionRepository;
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
    void shouldReturnSessionWhenItExists() {
        UUID id = UUID.randomUUID();
        WorkoutSession session = new WorkoutSession(
                id, UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ", 90, 8, null
        );

        when(repositoryMock.findById(id)).thenReturn(Optional.of(session));

        assertThat(service.execute(id)).isSameAs(session);
    }

    @Test
    void shouldThrowWhenSessionDoesNotExist() {
        UUID unknownId = UUID.randomUUID();

        when(repositoryMock.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId))
                .isInstanceOf(WorkoutSessionNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
