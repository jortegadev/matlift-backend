package com.matlift.user.application.service;

import com.matlift.user.domain.exception.UserNotFoundException;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserServiceTest {

    @Mock
    private UserRepository repositoryMock;

    @InjectMocks
    private GetCurrentUserService service;

    @Test
    void shouldReturnTheUserWhenItExists() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "atleta@matlift.com", "hash");

        when(repositoryMock.findById(id)).thenReturn(Optional.of(user));

        assertThat(service.execute(id)).isSameAs(user);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        UUID unknownId = UUID.randomUUID();

        when(repositoryMock.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(unknownId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
