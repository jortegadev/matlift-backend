package com.matlift.readiness.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DailyReadinessTest {

    private static final LocalDate TODAY = LocalDate.of(2026, Month.JULY, 24);

    @Test
    void shouldReportFullReadinessWhenEverythingIsAtItsBest() {
        DailyReadiness readiness = newReadiness(5, 1, 1);

        assertThat(readiness.getReadinessPercentage()).isEqualTo(100);
    }

    @Test
    void shouldReportNoReadinessWhenEverythingIsAtItsWorst() {
        DailyReadiness readiness = newReadiness(1, 5, 5);

        assertThat(readiness.getReadinessPercentage()).isZero();
    }

    @Test
    void shouldReportHalfReadinessWhenEveryScoreIsInTheMiddle() {
        DailyReadiness readiness = newReadiness(3, 3, 3);

        assertThat(readiness.getReadinessPercentage()).isEqualTo(50);
    }

    @Test
    void shouldLowerReadinessAsSorenessGrows() {
        DailyReadiness fresh = newReadiness(5, 1, 1);
        DailyReadiness sore = newReadiness(5, 5, 1);

        assertThat(sore.getReadinessPercentage()).isLessThan(fresh.getReadinessPercentage());
    }

    @Test
    void shouldLowerReadinessAsStressGrows() {
        DailyReadiness calm = newReadiness(5, 1, 1);
        DailyReadiness stressed = newReadiness(5, 1, 5);

        assertThat(stressed.getReadinessPercentage()).isLessThan(calm.getReadinessPercentage());
    }

    @Test
    void shouldRaiseReadinessAsSleepGrows() {
        DailyReadiness rested = newReadiness(5, 3, 3);
        DailyReadiness sleepless = newReadiness(1, 3, 3);

        assertThat(rested.getReadinessPercentage()).isGreaterThan(sleepless.getReadinessPercentage());
    }

    @Test
    void shouldWeighEveryScoreEqually() {
        DailyReadiness badSleep = newReadiness(1, 1, 1);
        DailyReadiness verySore = newReadiness(5, 5, 1);
        DailyReadiness veryStressed = newReadiness(5, 1, 5);

        assertThat(badSleep.getReadinessPercentage())
                .isEqualTo(verySore.getReadinessPercentage())
                .isEqualTo(veryStressed.getReadinessPercentage());
    }

    @Test
    void shouldAlwaysProduceAPercentageTheDatabaseAccepts() {
        List<Integer> percentages = new ArrayList<>();

        for (int sleep = 1; sleep <= 5; sleep++) {
            for (int soreness = 1; soreness <= 5; soreness++) {
                for (int stress = 1; stress <= 5; stress++) {
                    percentages.add(newReadiness(sleep, soreness, stress).getReadinessPercentage());
                }
            }
        }

        assertThat(percentages)
                .hasSize(125)
                .allMatch(percentage -> percentage >= 0 && percentage <= 100);
    }

    @Test
    void shouldGenerateIdWhenNotProvided() {
        assertThat(newReadiness(3, 3, 3).getId()).isNotNull();
    }

    @Test
    void shouldKeepTheProvidedId() {
        UUID id = UUID.randomUUID();

        DailyReadiness readiness = new DailyReadiness(id, UUID.randomUUID(), TODAY, 3, 3, 3);

        assertThat(readiness.getId()).isEqualTo(id);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThatThrownBy(() -> new DailyReadiness(null, null, TODAY, 3, 3, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User id");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenRecordDateIsNull() {
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> new DailyReadiness(null, userId, null, 3, 3, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Record date");
    }

    @Test
    void shouldThrowExceptionWhenSleepScoreIsOutOfRange() {
        assertThatThrownBy(() -> newReadiness(0, 3, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sleep score");
    }

    @Test
    void shouldThrowExceptionWhenSorenessScoreIsOutOfRange() {
        assertThatThrownBy(() -> newReadiness(3, 6, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Soreness score");
    }

    @Test
    void shouldThrowExceptionWhenStressScoreIsOutOfRange() {
        assertThatThrownBy(() -> newReadiness(3, 3, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stress score");
    }

    private DailyReadiness newReadiness(int sleepScore, int sorenessScore, int stressScore) {
        return new DailyReadiness(null, UUID.randomUUID(), TODAY, sleepScore, sorenessScore, stressScore);
    }
}
