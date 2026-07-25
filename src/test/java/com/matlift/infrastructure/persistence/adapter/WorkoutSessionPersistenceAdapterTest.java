package com.matlift.infrastructure.persistence.adapter;

import com.matlift.domain.model.SessionCategory;
import com.matlift.domain.model.WorkoutSession;
import com.matlift.infrastructure.persistence.mapper.WorkoutSessionPersistenceMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({WorkoutSessionPersistenceAdapter.class, WorkoutSessionPersistenceMapperImpl.class})
class WorkoutSessionPersistenceAdapterTest {

    @Autowired
    private WorkoutSessionPersistenceAdapter adapter;

    @Test
    void shouldSaveAndRetrieveWorkoutSession() {
        WorkoutSession sessionToSave = new WorkoutSession(
                null, UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ NoGi", 120, 8, "Open mat session"
        );

        WorkoutSession savedSession = adapter.save(sessionToSave);

        assertThat(savedSession.getId()).isNotNull();

        Optional<WorkoutSession> retrievedSession = adapter.findById(savedSession.getId());

        assertThat(retrievedSession).isPresent();
        assertThat(retrievedSession.get().getActivityName()).isEqualTo("BJJ NoGi");
        assertThat(retrievedSession.get().getInternalLoad()).isEqualTo(960);
    }
}
