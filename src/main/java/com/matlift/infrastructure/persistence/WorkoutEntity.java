package com.matlift.infrastructure.persistence;

import com.matlift.domain.model.SessionCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_date", nullable = false)
    private ZonedDateTime sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SessionCategory category;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "rpe", nullable = false)
    private int rpe;

    @Column(name = "internal_load", nullable = false)
    private int internalLoad;

    @Column(name = "notes")
    private String notes;

}
