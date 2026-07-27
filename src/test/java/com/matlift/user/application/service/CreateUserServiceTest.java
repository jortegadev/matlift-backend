package com.matlift.user.application.service;

import com.matlift.user.domain.exception.UserAlreadyExistsException;
import com.matlift.user.domain.model.RawPassword;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserCommand;
import com.matlift.user.domain.port.out.PasswordHasher;
import com.matlift.user.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepository repositoryMock;

    @Mock
    private PasswordHasher passwordHasherMock;

    @InjectMocks
    private CreateUserService service;

    @Test
    void shouldStoreHashedPasswordAndNeverTheRawOne() {
        RawPassword rawPassword = new RawPassword("supersecret");
        CreateUserCommand command = new CreateUserCommand("atleta@matlift.com", rawPassword);

        when(passwordHasherMock.hash(rawPassword)).thenReturn("bcrypt-hash");
        when(repositoryMock.existsByEmail("atleta@matlift.com")).thenReturn(false);
        when(repositoryMock.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.execute(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repositoryMock).save(captor.capture());

        assertThat(captor.getValue().getPasswordHash()).isEqualTo("bcrypt-hash");
        assertThat(captor.getValue().getPasswordHash()).isNotEqualTo("supersecret");
        assertThat(result.getEmail()).isEqualTo("atleta@matlift.com");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void shouldThrowAndNotSaveWhenEmailIsAlreadyRegistered() {
        CreateUserCommand command =
                new CreateUserCommand("atleta@matlift.com", new RawPassword("supersecret"));

        when(passwordHasherMock.hash(any(RawPassword.class))).thenReturn("bcrypt-hash");
        when(repositoryMock.existsByEmail("atleta@matlift.com")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("atleta@matlift.com");

        verify(repositoryMock, never()).save(any(User.class));
    }

    @Test
    void shouldDetectDuplicateRegardlessOfEmailCasingAndSpacing() {
        CreateUserCommand command =
                new CreateUserCommand("  ATLETA@MatLift.COM  ", new RawPassword("supersecret"));

        when(passwordHasherMock.hash(any(RawPassword.class))).thenReturn("bcrypt-hash");
        when(repositoryMock.existsByEmail("atleta@matlift.com")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(repositoryMock, never()).save(any(User.class));
    }

    @Test
    void shouldRejectInvalidEmailBeforeReachingPersistence() {
        CreateUserCommand command =
                new CreateUserCommand("no-es-un-email", new RawPassword("supersecret"));

        when(passwordHasherMock.hash(any(RawPassword.class))).thenReturn("bcrypt-hash");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email format is invalid");

        verify(repositoryMock, never()).save(any(User.class));
    }
}
