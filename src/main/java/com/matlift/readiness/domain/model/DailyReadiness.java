package com.matlift.readiness.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class DailyReadiness {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;
    private static final int INVERSION_BASE = MAX_SCORE + 1;
    private static final int MIN_RAW_SCORE = 3;
    private static final int RAW_SCORE_RANGE = 12;

    private final UUID id;
    private final UUID userId;

    private final LocalDate recordDate;

    private final int sleepScore;
    private final int sorenessScore;
    private final int stressScore;

    private final int readinessPercentage;

    @Builder
    public DailyReadiness(UUID id, UUID userId, LocalDate recordDate,
                          int sleepScore, int sorenessScore, int stressScore) {

        if (userId == null) throw new IllegalArgumentException("User id is required");
        if (recordDate == null) throw new IllegalArgumentException("Record date is required");
        requireValidScore(sleepScore, "Sleep score");
        requireValidScore(sorenessScore, "Soreness score");
        requireValidScore(stressScore, "Stress score");

        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.recordDate = recordDate;
        this.sleepScore = sleepScore;
        this.sorenessScore = sorenessScore;
        this.stressScore = stressScore;
        this.readinessPercentage = calculateReadinessPercentage(sleepScore, sorenessScore, stressScore);
    }

    private static void requireValidScore(int score, String scoreName) {
        if (score < MIN_SCORE || score > MAX_SCORE) {
            throw new IllegalArgumentException(
                    scoreName + " must be between " + MIN_SCORE + " and " + MAX_SCORE);
        }
    }

    private static int calculateReadinessPercentage(int sleepScore, int sorenessScore, int stressScore) {
        int rawScore = sleepScore + inverted(sorenessScore) + inverted(stressScore);

        return Math.round((rawScore - MIN_RAW_SCORE) * 100f / RAW_SCORE_RANGE);
    }

    private static int inverted(int score) {
        return INVERSION_BASE - score;
    }
}
