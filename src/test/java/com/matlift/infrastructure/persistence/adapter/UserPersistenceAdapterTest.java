package com.matlift.infrastructure.persistence.adapter;

import com.matlift.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserPersistenceAdapter.class)
class UserPersistenceAdapterTest extends AbstractIntegrationTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Test
    void shouldReportWhetherUserExists() {
        UUID existingUserId = createTestUser();

        assertThat(adapter.existsById(existingUserId)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }
}
