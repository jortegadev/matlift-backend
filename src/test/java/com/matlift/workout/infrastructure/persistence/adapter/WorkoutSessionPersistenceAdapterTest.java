package com.matlift.workout.infrastructure.persistence.adapter;

import com.matlift.AbstractIntegrationTest;
import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.SessionCategory;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.infrastructure.persistence.mapper.WorkoutSessionPersistenceMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({WorkoutSessionPersistenceAdapter.class, WorkoutSessionPersistenceMapperImpl.class})
class WorkoutSessionPersistenceAdapterTest extends AbstractIntegrationTest {

    @Autowired
    private WorkoutSessionPersistenceAdapter adapter;

    @Test
    void shouldSaveAndRetrieveWorkoutSession() {
        UUID userId = createTestUser();

        WorkoutSession sessionToSave = new WorkoutSession(
                null, userId, ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ NoGi", 120, 8, "Open mat session"
        );

        WorkoutSession savedSession = adapter.save(sessionToSave);

        assertThat(savedSession.getId()).isNotNull();

        Optional<WorkoutSession> retrievedSession = adapter.findById(savedSession.getId());

        assertThat(retrievedSession).isPresent();
        assertThat(retrievedSession.get().getActivityName()).isEqualTo("BJJ NoGi");
        assertThat(retrievedSession.get().getInternalLoad()).isEqualTo(960);
    }

    @Test
    void shouldReportWhetherSessionExistsAndDeleteIt() {
        UUID userId = createTestUser();

        WorkoutSession saved = adapter.save(newSession(userId, ZonedDateTime.now()));

        assertThat(adapter.existsById(saved.getId())).isTrue();

        adapter.deleteById(saved.getId());

        assertThat(adapter.existsById(saved.getId())).isFalse();
        assertThat(adapter.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldFindSessionsOfOneUserOnlyMostRecentFirst() {
        UUID userId = createTestUser();
        UUID otherUserId = createTestUser();
        ZonedDateTime now = ZonedDateTime.now();

        adapter.save(newSession(userId, now.minusDays(2)));
        adapter.save(newSession(userId, now));
        adapter.save(newSession(userId, now.minusDays(1)));
        adapter.save(newSession(otherUserId, now));

        PagedResult<WorkoutSession> result = adapter.findByUser(userId, null, null, 0, 20);

        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.content()).hasSize(3);
        assertThat(result.content())
                .extracting(WorkoutSession::getUserId)
                .containsOnly(userId);
        assertThat(result.content())
                .extracting(WorkoutSession::getSessionDate)
                .isSortedAccordingTo(java.util.Comparator.reverseOrder());
    }

    @Test
    void shouldFilterByDateRange() {
        UUID userId = createTestUser();
        ZonedDateTime now = ZonedDateTime.now();

        adapter.save(newSession(userId, now.minusDays(10)));
        adapter.save(newSession(userId, now.minusDays(5)));
        adapter.save(newSession(userId, now));

        PagedResult<WorkoutSession> result =
                adapter.findByUser(userId, now.minusDays(7), now.minusDays(1), 0, 20);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.content()).hasSize(1);
    }

    @Test
    void shouldApplyOpenEndedDateRange() {
        UUID userId = createTestUser();
        ZonedDateTime now = ZonedDateTime.now();

        adapter.save(newSession(userId, now.minusDays(10)));
        adapter.save(newSession(userId, now));

        PagedResult<WorkoutSession> onlyRecent = adapter.findByUser(userId, now.minusDays(1), null, 0, 20);
        PagedResult<WorkoutSession> onlyOld = adapter.findByUser(userId, null, now.minusDays(1), 0, 20);

        assertThat(onlyRecent.totalElements()).isEqualTo(1);
        assertThat(onlyOld.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldPaginateResults() {
        UUID userId = createTestUser();
        ZonedDateTime now = ZonedDateTime.now();

        for (int i = 0; i < 5; i++) {
            adapter.save(newSession(userId, now.minusDays(i)));
        }

        PagedResult<WorkoutSession> firstPage = adapter.findByUser(userId, null, null, 0, 2);

        assertThat(firstPage.content()).hasSize(2);
        assertThat(firstPage.page()).isZero();
        assertThat(firstPage.size()).isEqualTo(2);
        assertThat(firstPage.totalElements()).isEqualTo(5);
        assertThat(firstPage.totalPages()).isEqualTo(3);

        PagedResult<WorkoutSession> lastPage = adapter.findByUser(userId, null, null, 2, 2);

        assertThat(lastPage.content()).hasSize(1);
    }

    private WorkoutSession newSession(UUID userId, ZonedDateTime sessionDate) {
        return new WorkoutSession(
                null, userId, sessionDate, SessionCategory.STRENGTH,
                "Full body", 60, 7, null
        );
    }
}
