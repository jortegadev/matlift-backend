package com.matlift.user.infrastructure.security;

import com.matlift.user.domain.model.RawPassword;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    void shouldProduceHashVerifiableByBCrypt() {
        RawPassword password = new RawPassword("correct horse battery staple");

        String hash = hasher.hash(password);

        assertThat(new BCryptPasswordEncoder().matches(password.value(), hash)).isTrue();
    }

    @Test
    void shouldNotProduceHashVerifiableByADifferentPassword() {
        String hash = hasher.hash(new RawPassword("correct horse battery staple"));

        assertThat(new BCryptPasswordEncoder().matches("another password", hash)).isFalse();
    }

    @Test
    void shouldNeverStoreThePlainTextPassword() {
        RawPassword password = new RawPassword("super-secret-value");

        String hash = hasher.hash(password);

        assertThat(hash).doesNotContain("super-secret-value");
    }

    @Test
    void shouldProduceDifferentHashesForTheSamePassword() {
        RawPassword password = new RawPassword("correct horse battery staple");

        assertThat(hasher.hash(password)).isNotEqualTo(hasher.hash(password));
    }

    @Test
    void shouldProduceHashThatFitsThePasswordHashColumn() {
        String hash = hasher.hash(new RawPassword("correct horse battery staple"));

        assertThat(hash).startsWith("$2a$").hasSizeLessThanOrEqualTo(255);
    }

    @Test
    void shouldMatchTheOriginalPassword() {
        RawPassword password = new RawPassword("correct horse battery staple");

        String hash = hasher.hash(password);

        assertThat(hasher.matches(password.value(), hash)).isTrue();
    }

    @Test
    void shouldNotMatchADifferentPassword() {
        String hash = hasher.hash(new RawPassword("correct horse battery staple"));

        assertThat(hasher.matches("another password", hash)).isFalse();
    }

    @Test
    void shouldNotMatchWhenPasswordDiffersOnlyInCase() {
        String hash = hasher.hash(new RawPassword("CorrectHorseBattery"));

        assertThat(hasher.matches("correcthorsebattery", hash)).isFalse();
    }

    @Test
    void shouldNotMatchWhenHashIsNull() {
        assertThat(hasher.matches("correct horse battery staple", null)).isFalse();
    }

    @Test
    void shouldNotMatchWhenCandidateIsNull() {
        String hash = hasher.hash(new RawPassword("correct horse battery staple"));

        assertThat(hasher.matches(null, hash)).isFalse();
    }

    @Test
    void shouldNotMatchWhenHashIsNotBCrypt() {
        assertThat(hasher.matches("correct horse battery staple", "not-a-bcrypt-hash")).isFalse();
    }

    @Test
    void shouldNotMatchACandidateShorterThanThePasswordPolicy() {
        String hash = hasher.hash(new RawPassword("correct horse battery staple"));

        assertThat(hasher.matches("short", hash)).isFalse();
    }
}
