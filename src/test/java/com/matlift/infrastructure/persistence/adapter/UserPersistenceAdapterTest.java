package com.matlift.infrastructure.persistence.adapter;

import com.matlift.infrastructure.persistence.SpringDataUserRepository;
import com.matlift.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserPersistenceAdapter.class)
class UserPersistenceAdapterTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Test
    void shouldReportWhetherUserExists() {
        UUID existingUserId = UUID.randomUUID();

        springDataUserRepository.save(new UserEntity(existingUserId));

        assertThat(adapter.existsById(existingUserId)).isTrue();
        assertThat(adapter.existsById(UUID.randomUUID())).isFalse();
    }
}
