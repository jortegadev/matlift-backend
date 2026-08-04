package com.matlift.readiness.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_readiness")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReadinessEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "sleep_score", nullable = false)
    private int sleepScore;

    @Column(name = "soreness_score", nullable = false)
    private int sorenessScore;

    @Column(name = "stress_score", nullable = false)
    private int stressScore;

    @Column(name = "readiness_percentage", nullable = false)
    private int readinessPercentage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
