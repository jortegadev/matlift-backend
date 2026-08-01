package com.matlift.user.infrastructure.rest.controller;

import com.matlift.user.domain.exception.UserAlreadyExistsException;
import com.matlift.user.domain.exception.UserNotFoundException;
import com.matlift.user.domain.model.User;
import com.matlift.user.domain.port.in.CreateUserCommand;
import com.matlift.user.domain.port.in.CreateUserUseCase;
import com.matlift.user.domain.port.in.GetCurrentUserUseCase;
import com.matlift.user.infrastructure.rest.mapper.UserRestMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserRestMapperImpl.class)
class UserControllerTest {

    private static final String VALID_BODY = """
            {
                "email": "atleta@matlift.com",
                "password": "supersecret"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @Test
    void shouldReturn201WithCreatedUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(createUserUseCase.execute(any(CreateUserCommand.class)))
                .thenReturn(new User(id, "atleta@matlift.com", "bcrypt-hash"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("atleta@matlift.com"));
    }

    @Test
    void shouldNeverExposeAnyPasswordFieldInTheResponse() throws Exception {
        when(createUserUseCase.execute(any(CreateUserCommand.class)))
                .thenReturn(new User(UUID.randomUUID(), "atleta@matlift.com", "bcrypt-hash"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("supersecret"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("bcrypt-hash"))));
    }

    @Test
    void shouldReturn409WhenEmailIsAlreadyRegistered() throws Exception {
        when(createUserUseCase.execute(any(CreateUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException("atleta@matlift.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email atleta@matlift.com is already registered"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.email").value("is required"))
                .andExpect(jsonPath("$.fields.password").value("is required"));

        verify(createUserUseCase, never()).execute(any(CreateUserCommand.class));
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        String shortPassword = """
                {
                    "email": "atleta@matlift.com",
                    "password": "corta"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shortPassword))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password must be at least 8 characters"));

        verify(createUserUseCase, never()).execute(any(CreateUserCommand.class));
    }

    @Test
    void shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
        String invalidEmail = """
                {
                    "email": "no-es-un-email",
                    "password": "supersecret"
                }
                """;

        when(createUserUseCase.execute(any(CreateUserCommand.class)))
                .thenThrow(new IllegalArgumentException("Email format is invalid"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email format is invalid"));
    }

    @Test
    void shouldReturn200WithTheAuthenticatedUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(getCurrentUserUseCase.execute(id)).thenReturn(new User(id, "atleta@matlift.com", "bcrypt-hash"));

        mockMvc.perform(get("/api/users/me").principal(principalFor(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("atleta@matlift.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturn404WhenAuthenticatedUserNoLongerExists() throws Exception {
        UUID id = UUID.randomUUID();

        when(getCurrentUserUseCase.execute(id)).thenThrow(new UserNotFoundException(id));

        mockMvc.perform(get("/api/users/me").principal(principalFor(id)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User " + id + " does not exist"));
    }

    private Principal principalFor(UUID id) {
        return id::toString;
    }
}
