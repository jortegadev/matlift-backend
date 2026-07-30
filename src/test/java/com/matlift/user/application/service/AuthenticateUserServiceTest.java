package com.matlift.user.application.service;

import com.matlift.user.domain.exception.InvalidCredentialsException;
import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.AuthenticateUserCommand;
import com.matlift.user.domain.port.out.AccessTokenIssuer;
import com.matlift.user.domain.port.out.PasswordHasher;
import com.matlift.user.domain.port.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    private static final String EMAIL = "atleta@matlift.com";
    private static final String HASH = "bcrypt-hash";

    @Mock
    private UserRepository repositoryMock;

    @Mock
    private PasswordHasher passwordHasherMock;

    @Mock
    private AccessTokenIssuer accessTokenIssuerMock;

    @InjectMocks
    private AuthenticateUserService service;

    @Test
    void shouldIssueTokenForTheAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        AccessToken expectedToken = new AccessToken("jwt-value", Instant.now().plus(24, ChronoUnit.HOURS));

        when(repositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(new User(userId, EMAIL, HASH)));
        when(passwordHasherMock.matches("supersecret", HASH)).thenReturn(true);
        when(accessTokenIssuerMock.issue(userId)).thenReturn(expectedToken);

        AccessToken result = service.execute(new AuthenticateUserCommand(EMAIL, "supersecret"));

        assertThat(result).isSameAs(expectedToken);
    }

    @Test
    void shouldAuthenticateRegardlessOfEmailCasingAndSpacing() {
        UUID userId = UUID.randomUUID();
        AccessToken expectedToken = new AccessToken("jwt-value", Instant.now().plus(24, ChronoUnit.HOURS));

        when(repositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(new User(userId, EMAIL, HASH)));
        when(passwordHasherMock.matches("supersecret", HASH)).thenReturn(true);
        when(accessTokenIssuerMock.issue(userId)).thenReturn(expectedToken);

        AccessToken result = service.execute(
                new AuthenticateUserCommand("  ATLETA@MatLift.COM  ", "supersecret"));

        assertThat(result).isSameAs(expectedToken);
    }

    @Test
    void shouldRejectUnknownEmailWithoutIssuingToken() {
        when(repositoryMock.findByEmail("nadie@matlift.com")).thenReturn(Optional.empty());

        AuthenticateUserCommand command = new AuthenticateUserCommand("nadie@matlift.com", "supersecret");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(accessTokenIssuerMock, never()).issue(any(UUID.class));
    }

    @Test
    void shouldRejectWrongPasswordWithoutIssuingToken() {
        when(repositoryMock.findByEmail(EMAIL))
                .thenReturn(Optional.of(new User(UUID.randomUUID(), EMAIL, HASH)));
        when(passwordHasherMock.matches(anyString(), anyString())).thenReturn(false);

        AuthenticateUserCommand command = new AuthenticateUserCommand(EMAIL, "wrong-password");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(accessTokenIssuerMock, never()).issue(any(UUID.class));
    }

    @Test
    void shouldNotRevealWhetherTheEmailExists() {
        when(repositoryMock.findByEmail("nadie@matlift.com")).thenReturn(Optional.empty());
        when(repositoryMock.findByEmail(EMAIL))
                .thenReturn(Optional.of(new User(UUID.randomUUID(), EMAIL, HASH)));
        when(passwordHasherMock.matches(anyString(), anyString())).thenReturn(false);

        AuthenticateUserCommand unknownEmail = new AuthenticateUserCommand("nadie@matlift.com", "supersecret");
        AuthenticateUserCommand wrongPassword = new AuthenticateUserCommand(EMAIL, "wrong-password");

        Throwable unknownEmailFailure = catchThrowable(() -> service.execute(unknownEmail));
        Throwable wrongPasswordFailure = catchThrowable(() -> service.execute(wrongPassword));

        assertThat(unknownEmailFailure)
                .hasSameClassAs(wrongPasswordFailure)
                .hasMessage(wrongPasswordFailure.getMessage());
    }

    @Test
    void shouldNotExposeThePasswordInTheCommandToString() {
        AuthenticateUserCommand command = new AuthenticateUserCommand(EMAIL, "supersecret");

        assertThat(command.toString())
                .doesNotContain("supersecret")
                .contains("PROTECTED");
    }
}
