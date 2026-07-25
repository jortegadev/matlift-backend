package com.matlift.infrastructure.persistence;

import com.matlift.domain.model.SessionCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_date", nullable = false)
    private ZonedDateTime sessionDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "category", nullable = false, length = 50)
    private SessionCategory category;

    @Column(name = "activity_name", nullable = false, length = 100)
    private String activityName;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "rpe", nullable = false)
    private int rpe;

    @Column(name = "internal_load", nullable = false)
    private int internalLoad;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;
}
