package com.matlift.workout.infrastructure.rest.controller;

import com.jayway.jsonpath.JsonPath;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WorkoutSessionOwnershipTest extends AbstractIntegrationTest {

    private static final String WORKOUT_BODY = """
            {
                "sessionDate": "2026-07-24T18:30:00Z",
                "category": "CONTACT_SPORT",
                "activityName": "BJJ Gi",
                "durationMinutes": 90,
                "rpe": 8
            }
            """;

    private static final String UPDATE_BODY = """
            {
                "sessionDate": "2026-07-24T18:30:00Z",
                "category": "CARDIO",
                "activityName": "Hijacked",
                "durationMinutes": 30,
                "rpe": 5
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessTokenIssuer accessTokenIssuer;

    private String registerAndTokenFor(String email) throws Exception {
        String body = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"supersecret\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID userId = UUID.fromString(JsonPath.read(body, "$.id"));

        return "Bearer " + accessTokenIssuer.issue(userId).value();
    }

    private String createWorkoutFor(String token) throws Exception {
        String body = mockMvc.perform(post("/api/workouts")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WORKOUT_BODY))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(body, "$.id");
    }

    @Test
    void shouldNotLetAUserReadAnotherUsersSession() throws Exception {
        String ownerToken = registerAndTokenFor("owner-read@matlift.com");
        String intruderToken = registerAndTokenFor("intruder-read@matlift.com");

        String sessionId = createWorkoutFor(ownerToken);

        mockMvc.perform(get("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, intruderToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityName").value("BJJ Gi"));
    }

    @Test
    void shouldNotLetAUserUpdateAnotherUsersSession() throws Exception {
        String ownerToken = registerAndTokenFor("owner-update@matlift.com");
        String intruderToken = registerAndTokenFor("intruder-update@matlift.com");

        String sessionId = createWorkoutFor(ownerToken);

        mockMvc.perform(put("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, intruderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityName").value("BJJ Gi"));
    }

    @Test
    void shouldNotLetAUserDeleteAnotherUsersSession() throws Exception {
        String ownerToken = registerAndTokenFor("owner-delete@matlift.com");
        String intruderToken = registerAndTokenFor("intruder-delete@matlift.com");

        String sessionId = createWorkoutFor(ownerToken);

        mockMvc.perform(delete("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, intruderToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldOnlyListTheAuthenticatedUsersOwnSessions() throws Exception {
        String ownerToken = registerAndTokenFor("owner-list@matlift.com");
        String intruderToken = registerAndTokenFor("intruder-list@matlift.com");

        createWorkoutFor(ownerToken);

        mockMvc.perform(get("/api/workouts").header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get("/api/workouts").header(HttpHeaders.AUTHORIZATION, intruderToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldLetTheOwnerDeleteItsOwnSession() throws Exception {
        String ownerToken = registerAndTokenFor("owner-own-delete@matlift.com");

        String sessionId = createWorkoutFor(ownerToken);

        mockMvc.perform(delete("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/workouts/{id}", sessionId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isNotFound());
    }
}
