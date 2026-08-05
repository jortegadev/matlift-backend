package com.matlift.readiness.application.service;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessCommand;
import com.matlift.readiness.domain.port.out.DailyReadinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveDailyReadinessServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, Month.JULY, 24);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-07-24T10:00:00Z"), ZoneOffset.UTC);

    @Mock
    private DailyReadinessRepository repositoryMock;

    @Captor
    private ArgumentCaptor<DailyReadiness> readinessCaptor;

    private SaveDailyReadinessService service;

    @BeforeEach
    void setUp() {
        service = new SaveDailyReadinessService(repositoryMock, FIXED_CLOCK);
    }

    @Test
    void shouldCreateANewRecordWhenTheDayHasNoneYet() {
        UUID userId = UUID.randomUUID();

        when(repositoryMock.findByUserIdAndRecordDate(userId, TODAY)).thenReturn(Optional.empty());
        when(repositoryMock.save(any(DailyReadiness.class))).thenAnswer(call -> call.getArgument(0));

        DailyReadiness saved = service.execute(commandFor(userId, TODAY, 5, 1, 1));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getReadinessPercentage()).isEqualTo(100);
    }

    @Test
    void shouldReuseTheExistingIdWhenTheDayAlreadyHasARecord() {
        UUID userId = UUID.randomUUID();
        DailyReadiness existing = new DailyReadiness(null, userId, TODAY, 5, 1, 1);

        when(repositoryMock.findByUserIdAndRecordDate(userId, TODAY)).thenReturn(Optional.of(existing));
        when(repositoryMock.save(any(DailyReadiness.class))).thenAnswer(call -> call.getArgument(0));

        service.execute(commandFor(userId, TODAY, 1, 5, 5));

        verify(repositoryMock).save(readinessCaptor.capture());

        assertThat(readinessCaptor.getValue().getId()).isEqualTo(existing.getId());
        assertThat(readinessCaptor.getValue().getReadinessPercentage()).isZero();
    }

    @Test
    void shouldAcceptTodaysDate() {
        UUID userId = UUID.randomUUID();

        when(repositoryMock.findByUserIdAndRecordDate(userId, TODAY)).thenReturn(Optional.empty());
        when(repositoryMock.save(any(DailyReadiness.class))).thenAnswer(call -> call.getArgument(0));

        assertThat(service.execute(commandFor(userId, TODAY, 3, 3, 3))).isNotNull();
    }

    @Test
    void shouldAcceptPastDates() {
        UUID userId = UUID.randomUUID();
        LocalDate lastMonth = TODAY.minusMonths(1);

        when(repositoryMock.findByUserIdAndRecordDate(userId, lastMonth)).thenReturn(Optional.empty());
        when(repositoryMock.save(any(DailyReadiness.class))).thenAnswer(call -> call.getArgument(0));

        assertThat(service.execute(commandFor(userId, lastMonth, 3, 3, 3))).isNotNull();
    }

    @Test
    void shouldAcceptTomorrowToToleratePositiveTimeZoneOffsets() {
        UUID userId = UUID.randomUUID();
        LocalDate tomorrow = TODAY.plusDays(1);

        when(repositoryMock.findByUserIdAndRecordDate(userId, tomorrow)).thenReturn(Optional.empty());
        when(repositoryMock.save(any(DailyReadiness.class))).thenAnswer(call -> call.getArgument(0));

        assertThat(service.execute(commandFor(userId, tomorrow, 3, 3, 3))).isNotNull();
    }

    @Test
    void shouldRejectDatesBeyondTheTimeZoneTolerance() {
        SaveDailyReadinessCommand command = commandFor(UUID.randomUUID(), TODAY.plusDays(2), 3, 3, 3);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");

        verify(repositoryMock, never()).save(any(DailyReadiness.class));
    }

    @Test
    void shouldNotEvenLookUpTheDayWhenTheDateIsRejected() {
        SaveDailyReadinessCommand command = commandFor(UUID.randomUUID(), TODAY.plusYears(180), 3, 3, 3);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repositoryMock, never()).findByUserIdAndRecordDate(any(UUID.class), any(LocalDate.class));
    }

    private SaveDailyReadinessCommand commandFor(UUID userId, LocalDate recordDate,
                                                 int sleepScore, int sorenessScore, int stressScore) {
        return new SaveDailyReadinessCommand(userId, recordDate, sleepScore, sorenessScore, stressScore);
    }
}
