package com.matlift.workout.domain.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkoutSessionTest {

    @Test
    void shouldCreateSessionAndCalculateInternalLoad() {
        UUID userId = UUID.randomUUID();

        WorkoutSession session = new WorkoutSession(
                null, userId, ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ", 90, 8, "Good session!"
        );

        assertThat(session.getId()).isNotNull();
        assertThat(session.getInternalLoad()).isEqualTo(720);
    }

    @Test
    void shouldThrowExceptionWhenRpeIsGreaterThan10() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime date = ZonedDateTime.now();

        assertThatThrownBy(() ->
                new WorkoutSession(
                        null, userId, date, SessionCategory.CONTACT_SPORT,
                        "BJJ", 90, 15, "RPE too high"
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10");
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        ZonedDateTime date = ZonedDateTime.now();

        assertThatThrownBy(() ->
                new WorkoutSession(
                        null, null, date, SessionCategory.CONTACT_SPORT,
                        "BJJ", 90, 8, null
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User id");
    }

    @Test
    void shouldThrowExceptionWhenSessionDateIsNull() {
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() ->
                new WorkoutSession(
                        null, userId, null, SessionCategory.CONTACT_SPORT,
                        "BJJ", 90, 8, null
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Session date");
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime date = ZonedDateTime.now();

        assertThatThrownBy(() ->
                new WorkoutSession(
                        null, userId, date, null,
                        "BJJ", 90, 8, null
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category");
    }

    @Test
    void shouldThrowExceptionWhenNotesExceedMaxLength() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime date = ZonedDateTime.now();
        String tooLongNotes = "x".repeat(1001);

        assertThatThrownBy(() ->
                new WorkoutSession(
                        null, userId, date, SessionCategory.CONTACT_SPORT,
                        "BJJ", 90, 8, tooLongNotes
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000");
    }

    @Test
    void shouldAcceptNotesAtMaxLength() {
        String notesAtLimit = "x".repeat(1000);

        WorkoutSession session = new WorkoutSession(
                null, UUID.randomUUID(), ZonedDateTime.now(), SessionCategory.CONTACT_SPORT,
                "BJJ", 90, 8, notesAtLimit
        );

        assertThat(session.getNotes()).hasSize(1000);
    }
}
