package com.matlift.user.infrastructure.rest.controller;

import com.matlift.user.domain.exception.InvalidCredentialsException;
import com.matlift.user.domain.model.AccessToken;
import com.matlift.user.domain.port.in.AuthenticateUserCommand;
import com.matlift.user.domain.port.in.AuthenticateUserUseCase;
import com.matlift.user.infrastructure.rest.mapper.AuthRestMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(AuthRestMapperImpl.class)
class AuthControllerTest {

    private static final String VALID_BODY = """
            {
                "email": "atleta@matlift.com",
                "password": "supersecret"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    void shouldReturn200WithAccessToken() throws Exception {
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
                .thenReturn(new AccessToken("jwt-token-value", expiresAt));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token-value"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void shouldNeverEchoThePasswordBackInTheResponse() throws Exception {
        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
                .thenReturn(new AccessToken("jwt-token-value", Instant.now().plus(24, ChronoUnit.HOURS)));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("supersecret"))));
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void shouldReturnTheSameResponseForUnknownEmailAndWrongPassword() throws Exception {
        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        MvcResult unknownEmail = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "nadie@matlift.com", "password": "supersecret"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MvcResult wrongPassword = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "atleta@matlift.com", "password": "wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(unknownEmail.getResponse().getContentAsString())
                .isEqualTo(wrongPassword.getResponse().getContentAsString());
    }

    @Test
    void shouldReturn401AndNotBadRequestWhenPasswordIsShorterThanThePolicy() throws Exception {
        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "atleta@matlift.com", "password": "short"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.email").value("is required"))
                .andExpect(jsonPath("$.fields.password").value("is required"));

        verify(authenticateUserUseCase, never()).execute(any(AuthenticateUserCommand.class));
    }
}
