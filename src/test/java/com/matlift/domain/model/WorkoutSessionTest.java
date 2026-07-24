package com.matlift.domain.model;

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
}
