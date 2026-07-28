package com.matlift.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawPasswordTest {

    @Test
    void shouldAcceptPasswordAtMinimumLength() {
        RawPassword password = new RawPassword("12345678");

        assertThat(password.value()).isEqualTo("12345678");
    }

    @Test
    void shouldAcceptLongPasswordWithoutComplexityRules() {
        String phrase = "correct horse battery staple";

        assertThat(new RawPassword(phrase).value()).isEqualTo(phrase);
    }

    @Test
    void shouldPreserveWhitespaceInsidePassword() {
        RawPassword password = new RawPassword("  spaces  matter  ");

        assertThat(password.value()).isEqualTo("  spaces  matter  ");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThatThrownBy(() -> new RawPassword(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        assertThatThrownBy(() -> new RawPassword(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsShorterThanMinimum() {
        assertThatThrownBy(() -> new RawPassword("1234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("8");
    }

    @Test
    void shouldNotExposePasswordInToString() {
        RawPassword password = new RawPassword("super-secret-value");

        assertThat(password.toString())
                .doesNotContain("super-secret-value")
                .isEqualTo("RawPassword[PROTECTED]");
    }

    @Test
    void shouldNotExposePasswordWhenInterpolatedInAMessage() {
        RawPassword password = new RawPassword("super-secret-value");

        assertThat("password was " + password).doesNotContain("super-secret-value");
        assertThat(String.format("password was %s", password)).doesNotContain("super-secret-value");
    }
}
