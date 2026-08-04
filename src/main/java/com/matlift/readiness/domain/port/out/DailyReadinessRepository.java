package com.matlift.readiness.domain.port.out;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.shared.domain.PagedResult;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyReadinessRepository {

    DailyReadiness save(DailyReadiness dailyReadiness);

    Optional<DailyReadiness> findByUserIdAndRecordDate(UUID userId, LocalDate recordDate);

    PagedResult<DailyReadiness> findByUser(UUID userId, LocalDate from, LocalDate to, int page, int size);

    boolean deleteByUserIdAndRecordDate(UUID userId, LocalDate recordDate);
}
