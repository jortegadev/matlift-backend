package com.matlift.readiness.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataDailyReadinessRepository
        extends JpaRepository<DailyReadinessEntity, UUID>, JpaSpecificationExecutor<DailyReadinessEntity> {

    Optional<DailyReadinessEntity> findByUserIdAndRecordDate(UUID userId, LocalDate recordDate);

    @Transactional
    long deleteByUserIdAndRecordDate(UUID userId, LocalDate recordDate);
}
