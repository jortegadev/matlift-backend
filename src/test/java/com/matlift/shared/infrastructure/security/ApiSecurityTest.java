package com.matlift.shared.infrastructure.security;

import com.matlift.AbstractIntegrationTest;
import com.matlift.user.domain.port.out.AccessTokenIssuer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessTokenIssuer accessTokenIssuer;

    private String bearerTokenFor(UUID userId) {
        return "Bearer " + accessTokenIssuer.issue(userId).value();
    }

    @Test
    void shouldAllowRegistrationWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "publico@matlift.com", "password": "supersecret"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldAllowLoginWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "nadie@matlift.com", "password": "supersecret"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }

    @Test
    void shouldRejectWorkoutRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/workouts/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void shouldRejectWorkoutRequestsWithAMalformedToken() throws Exception {
        mockMvc.perform(get("/api/workouts/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectWorkoutRequestsWithATokenSignedByAnotherSecret() throws Exception {
        String foreignToken = "eyJhbGciOiJIUzI1NiJ9"
                + ".eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAifQ"
                + ".Zm9yZ2VkLXNpZ25hdHVyZS10aGF0LWlzLW5vdC12YWxpZA";

        mockMvc.perform(get("/api/workouts/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + foreignToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectCurrentUserRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication required"));
    }

    @Test
    void shouldAcceptWorkoutRequestsCarryingAValidToken() throws Exception {
        mockMvc.perform(get("/api/workouts/{id}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearerTokenFor(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }
}
