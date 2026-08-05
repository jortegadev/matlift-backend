package com.matlift.readiness.application.service;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessCommand;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessUseCase;
import com.matlift.readiness.domain.port.out.DailyReadinessRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class SaveDailyReadinessService implements SaveDailyReadinessUseCase {

    private static final int TIME_ZONE_TOLERANCE_DAYS = 1;

    private final DailyReadinessRepository repository;
    private final Clock clock;

    public SaveDailyReadinessService(DailyReadinessRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public DailyReadiness execute(SaveDailyReadinessCommand command) {
        requireNotInTheFuture(command.recordDate());

        UUID existingId = repository.findByUserIdAndRecordDate(command.userId(), command.recordDate())
                .map(DailyReadiness::getId)
                .orElse(null);

        return repository.save(new DailyReadiness(
                existingId,
                command.userId(),
                command.recordDate(),
                command.sleepScore(),
                command.sorenessScore(),
                command.stressScore()
        ));
    }

    private void requireNotInTheFuture(LocalDate recordDate) {
        LocalDate latestAcceptedDate = LocalDate.now(clock).plusDays(TIME_ZONE_TOLERANCE_DAYS);

        if (recordDate != null && recordDate.isAfter(latestAcceptedDate)) {
            throw new IllegalArgumentException("Record date cannot be in the future");
        }
    }
}
