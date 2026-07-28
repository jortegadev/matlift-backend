package com.matlift.user.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void shouldGenerateIdWhenNotProvided() {
        User user = new User(null, "atleta@matlift.com", "hash");

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("atleta@matlift.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
    }

    @Test
    void shouldKeepProvidedId() {
        UUID id = UUID.randomUUID();

        User user = new User(id, "atleta@matlift.com", "hash");

        assertThat(user.getId()).isEqualTo(id);
    }

    @Test
    void shouldNormalizeEmailToLowercaseAndTrim() {
        User user = new User(null, "  Atleta@MatLift.COM  ", "hash");

        assertThat(user.getEmail()).isEqualTo("atleta@matlift.com");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> new User(null, null, "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        assertThatThrownBy(() -> new User(null, "   ", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoAtSign() {
        assertThatThrownBy(() -> new User(null, "atleta.matlift.com", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoDomainDot() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldAcceptDotsInLocalPartAndMultiLevelDomain() {
        assertThat(new User(null, "first.last@matlift.com", "hash").getEmail())
                .isEqualTo("first.last@matlift.com");
        assertThat(new User(null, "atleta@mail.matlift.co.uk", "hash").getEmail())
                .isEqualTo("atleta@mail.matlift.co.uk");
    }

    @Test
    void shouldThrowExceptionWhenEmailHasTwoAtSigns() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift@com", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenDomainLabelIsEmpty() {
        assertThatThrownBy(() -> new User(null, "atleta@.com", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenEmailEndsWithDot() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift.", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenDomainHasConsecutiveDots() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift..com", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenMultiLabelDomainEndsWithDot() {
        assertThatThrownBy(() -> new User(null, "atleta@mail.matlift.", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenEmailContainsInnerWhitespace() {
        assertThatThrownBy(() -> new User(null, "atl eta@matlift.com", "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");
    }

    @Test
    void shouldThrowExceptionWhenEmailExceedsMaxLength() {
        String tooLongEmail = "a".repeat(250) + "@matlift.com";

        assertThatThrownBy(() -> new User(null, tooLongEmail, "hash"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
    }

    @Test
    void shouldThrowExceptionWhenPasswordHashIsNull() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password hash is required");
    }

    @Test
    void shouldThrowExceptionWhenPasswordHashIsBlank() {
        assertThatThrownBy(() -> new User(null, "atleta@matlift.com", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password hash is required");
    }
}
