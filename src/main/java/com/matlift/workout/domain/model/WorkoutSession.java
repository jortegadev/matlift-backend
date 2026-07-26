package com.matlift.workout.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;


@Getter
public class WorkoutSession {

    private static final int MAX_NOTES_LENGTH = 1000;

    private final UUID id;
    private final UUID userId;

    private final ZonedDateTime sessionDate;

    private final SessionCategory category;
    private final String activityName;

    private final int durationMinutes;
    private final int rpe;
    private final int internalLoad;

    private final String notes;

    @Builder
    public WorkoutSession(UUID id, UUID userId, ZonedDateTime sessionDate,
                          SessionCategory category, String activityName,
                          int durationMinutes, int rpe, String notes) {

        if (rpe < 1 || rpe > 10) throw new IllegalArgumentException("RPE must be between 1 and 10");
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be greater than 0");
        if (activityName == null || activityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity name is required");
        }
        if (userId == null) throw new IllegalArgumentException("User id is required");
        if (sessionDate == null) throw new IllegalArgumentException("Session date is required");
        if (category == null) throw new IllegalArgumentException("Category is required");
        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("Notes must be at most " + MAX_NOTES_LENGTH + " characters");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.sessionDate = sessionDate;
        this.category = category;
        this.activityName = activityName;
        this.durationMinutes = durationMinutes;
        this.rpe = rpe;
        this.internalLoad = durationMinutes * rpe;
        this.notes = notes;
    }
}
