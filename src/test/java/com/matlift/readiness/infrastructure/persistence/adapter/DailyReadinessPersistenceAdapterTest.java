package com.matlift.readiness.infrastructure.persistence.adapter;

import com.matlift.AbstractIntegrationTest;
import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.infrastructure.persistence.mapper.DailyReadinessPersistenceMapperImpl;
import com.matlift.shared.domain.PagedResult;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DailyReadinessPersistenceAdapter.class, DailyReadinessPersistenceMapperImpl.class})
class DailyReadinessPersistenceAdapterTest extends AbstractIntegrationTest {

    private static final LocalDate TODAY = LocalDate.of(2026, Month.JULY, 24);

    @Autowired
    private DailyReadinessPersistenceAdapter adapter;

    @Autowired
    private EntityManager testEntityManager;

    @Test
    void shouldSaveAndRetrieveReadinessByDate() {
        UUID userId = createTestUser();

        DailyReadiness saved = adapter.save(newReadiness(userId, TODAY, 5, 1, 1));

        assertThat(saved.getId()).isNotNull();

        Optional<DailyReadiness> retrieved = adapter.findByUserIdAndRecordDate(userId, TODAY);

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getSleepScore()).isEqualTo(5);
        assertThat(retrieved.get().getReadinessPercentage()).isEqualTo(100);
    }

    @Test
    void shouldReplaceTheRecordWhenSavingTheSameIdAgain() {
        UUID userId = createTestUser();

        DailyReadiness saved = adapter.save(newReadiness(userId, TODAY, 5, 1, 1));

        adapter.save(new DailyReadiness(saved.getId(), userId, TODAY, 1, 5, 5));

        PagedResult<DailyReadiness> all = adapter.findByUser(userId, null, null, 0, 20);

        assertThat(all.totalElements()).isEqualTo(1);
        assertThat(all.content().getFirst().getReadinessPercentage()).isZero();
    }

    @Test
    void shouldRejectASecondRecordForTheSameUserAndDate() {
        UUID userId = createTestUser();

        adapter.save(newReadiness(userId, TODAY, 5, 1, 1));
        adapter.save(newReadiness(userId, TODAY, 1, 5, 5));

        assertThatThrownBy(testEntityManager::flush)
                .rootCause()
                .hasMessageContaining("unique_user_daily_readiness");
    }

    @Test
    void shouldNotRetrieveAnotherUsersRecord() {
        UUID ownerId = createTestUser();
        UUID someoneElseId = createTestUser();

        adapter.save(newReadiness(ownerId, TODAY, 5, 1, 1));

        assertThat(adapter.findByUserIdAndRecordDate(someoneElseId, TODAY)).isEmpty();
        assertThat(adapter.findByUserIdAndRecordDate(ownerId, TODAY)).isPresent();
    }

    @Test
    void shouldFindRecordsOfOneUserOnlyMostRecentFirst() {
        UUID userId = createTestUser();
        UUID otherUserId = createTestUser();

        adapter.save(newReadiness(userId, TODAY.minusDays(2), 3, 3, 3));
        adapter.save(newReadiness(userId, TODAY, 3, 3, 3));
        adapter.save(newReadiness(userId, TODAY.minusDays(1), 3, 3, 3));
        adapter.save(newReadiness(otherUserId, TODAY, 3, 3, 3));

        PagedResult<DailyReadiness> result = adapter.findByUser(userId, null, null, 0, 20);

        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.content())
                .extracting(DailyReadiness::getUserId)
                .containsOnly(userId);
        assertThat(result.content())
                .extracting(DailyReadiness::getRecordDate)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void shouldFilterByInclusiveDateRange() {
        UUID userId = createTestUser();

        adapter.save(newReadiness(userId, TODAY.minusDays(10), 3, 3, 3));
        adapter.save(newReadiness(userId, TODAY.minusDays(5), 3, 3, 3));
        adapter.save(newReadiness(userId, TODAY, 3, 3, 3));

        PagedResult<DailyReadiness> result =
                adapter.findByUser(userId, TODAY.minusDays(5), TODAY, 0, 20);

        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    void shouldApplyOpenEndedDateRange() {
        UUID userId = createTestUser();

        adapter.save(newReadiness(userId, TODAY.minusDays(10), 3, 3, 3));
        adapter.save(newReadiness(userId, TODAY, 3, 3, 3));

        PagedResult<DailyReadiness> onlyRecent = adapter.findByUser(userId, TODAY.minusDays(1), null, 0, 20);
        PagedResult<DailyReadiness> onlyOld = adapter.findByUser(userId, null, TODAY.minusDays(1), 0, 20);

        assertThat(onlyRecent.totalElements()).isEqualTo(1);
        assertThat(onlyOld.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldPaginateResults() {
        UUID userId = createTestUser();

        for (int day = 0; day < 5; day++) {
            adapter.save(newReadiness(userId, TODAY.minusDays(day), 3, 3, 3));
        }

        PagedResult<DailyReadiness> firstPage = adapter.findByUser(userId, null, null, 0, 2);

        assertThat(firstPage.content()).hasSize(2);
        assertThat(firstPage.page()).isZero();
        assertThat(firstPage.size()).isEqualTo(2);
        assertThat(firstPage.totalElements()).isEqualTo(5);
        assertThat(firstPage.totalPages()).isEqualTo(3);
    }

    @Test
    void shouldDeleteTheRecordOfADayAndReportIt() {
        UUID userId = createTestUser();

        adapter.save(newReadiness(userId, TODAY, 3, 3, 3));

        assertThat(adapter.deleteByUserIdAndRecordDate(userId, TODAY)).isTrue();
        assertThat(adapter.findByUserIdAndRecordDate(userId, TODAY)).isEmpty();
    }

    @Test
    void shouldReportNothingDeletedWhenTheDayHasNoRecord() {
        UUID userId = createTestUser();

        assertThat(adapter.deleteByUserIdAndRecordDate(userId, TODAY)).isFalse();
    }

    @Test
    void shouldNotDeleteAnotherUsersRecord() {
        UUID ownerId = createTestUser();
        UUID someoneElseId = createTestUser();

        adapter.save(newReadiness(ownerId, TODAY, 3, 3, 3));

        assertThat(adapter.deleteByUserIdAndRecordDate(someoneElseId, TODAY)).isFalse();
        assertThat(adapter.findByUserIdAndRecordDate(ownerId, TODAY)).isPresent();
    }

    private DailyReadiness newReadiness(UUID userId, LocalDate recordDate,
                                        int sleepScore, int sorenessScore, int stressScore) {
        return new DailyReadiness(null, userId, recordDate, sleepScore, sorenessScore, stressScore);
    }
}
