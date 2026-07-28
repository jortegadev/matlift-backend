package com.matlift.user.infrastructure.persistence.adapter;

import com.matlift.AbstractIntegrationTest;
import com.matlift.user.domain.model.User;
import com.matlift.user.infrastructure.persistence.mapper.UserPersistenceMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserPersistenceAdapter.class, UserPersistenceMapperImpl.class})
class UserPersistenceAdapterTest extends AbstractIntegrationTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Test
    void shouldReportWhetherUserExists() {
        UUID existingUserId = createTestUser();

        assertThat(adapter.existsById(existingUserId)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    void shouldSaveAndReportUserByEmail() {
        User saved = adapter.save(new User(null, "atleta@matlift.com", "hashed-secret"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("atleta@matlift.com");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed-secret");

        assertThat(adapter.existsByEmail("atleta@matlift.com")).isTrue();
        assertThat(adapter.existsByEmail("otro@matlift.com")).isFalse();
        assertThat(adapter.existsById(saved.getId())).isTrue();
    }

    @Test
    void shouldFindSavedUserByEmail() {
        User saved = adapter.save(new User(null, "buscame@matlift.com", "hashed-secret"));

        assertThat(adapter.findByEmail("buscame@matlift.com"))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getId()).isEqualTo(saved.getId());
                    assertThat(found.getEmail()).isEqualTo("buscame@matlift.com");
                    assertThat(found.getPasswordHash()).isEqualTo("hashed-secret");
                });
    }

    @Test
    void shouldReturnEmptyWhenEmailIsNotRegistered() {
        assertThat(adapter.findByEmail("nadie@matlift.com")).isEmpty();
    }

    @Test
    void shouldNotFindUserWhenEmailCasingDiffers() {
        adapter.save(new User(null, "atleta@matlift.com", "hashed-secret"));

        assertThat(adapter.findByEmail("ATLETA@MatLift.COM")).isEmpty();
    }

    @Test
    void shouldRejectDuplicateEmailAtDatabaseLevel() {
        adapter.save(new User(null, "duplicado@matlift.com", "hash"));
        adapter.save(new User(null, "duplicado@matlift.com", "hash"));

        assertThatThrownBy(() -> adapter.existsByEmail("duplicado@matlift.com"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
