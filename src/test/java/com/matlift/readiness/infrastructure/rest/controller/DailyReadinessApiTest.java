package com.matlift.readiness.infrastructure.rest.controller;

import com.jayway.jsonpath.JsonPath;
import com.matlift.AbstractIntegrationTest;
import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.out.DailyReadinessRepository;
import com.matlift.shared.domain.PagedResult;
import com.matlift.user.domain.port.out.AccessTokenIssuer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DailyReadinessApiTest extends AbstractIntegrationTest {

    private static final LocalDate A_DAY = LocalDate.of(2026, Month.MAY, 3);

    private static final String GREAT_DAY = """
            {"sleepScore": 5, "sorenessScore": 1, "stressScore": 1}
            """;

    private static final String TERRIBLE_DAY = """
            {"sleepScore": 1, "sorenessScore": 5, "stressScore": 5}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessTokenIssuer accessTokenIssuer;

    @Autowired
    private DailyReadinessRepository readinessRepository;

    @Test
    void shouldStoreTheReadinessOfADay() throws Exception {
        UUID userId = registerUser("readiness-store@matlift.com");

        mockMvc.perform(putReadiness(userId, A_DAY, GREAT_DAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recordDate").value("2026-05-03"))
                .andExpect(jsonPath("$.readinessPercentage").value(100));
    }

    @Test
    void shouldReplaceTheRecordWhenTheSameDayIsSentTwice() throws Exception {
        UUID userId = registerUser("readiness-upsert@matlift.com");

        mockMvc.perform(putReadiness(userId, A_DAY, GREAT_DAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readinessPercentage").value(100));

        mockMvc.perform(putReadiness(userId, A_DAY, TERRIBLE_DAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readinessPercentage").value(0));

        PagedResult<DailyReadiness> stored = readinessRepository.findByUser(userId, null, null, 0, 20);

        assertThat(stored.totalElements()).isEqualTo(1);
        assertThat(stored.content().getFirst().getSleepScore()).isEqualTo(1);
    }

    @Test
    void shouldKeepTheSameRecordIdAcrossReplacements() throws Exception {
        UUID userId = registerUser("readiness-stable-id@matlift.com");

        mockMvc.perform(putReadiness(userId, A_DAY, GREAT_DAY)).andExpect(status().isOk());

        UUID idAfterFirstWrite = readinessRepository.findByUserIdAndRecordDate(userId, A_DAY)
                .orElseThrow()
                .getId();

        mockMvc.perform(putReadiness(userId, A_DAY, TERRIBLE_DAY)).andExpect(status().isOk());

        UUID idAfterSecondWrite = readinessRepository.findByUserIdAndRecordDate(userId, A_DAY)
                .orElseThrow()
                .getId();

        assertThat(idAfterSecondWrite).isEqualTo(idAfterFirstWrite);
    }

    @Test
    void shouldLetTwoUsersRecordTheSameDay() throws Exception {
        UUID firstUserId = registerUser("readiness-first@matlift.com");
        UUID secondUserId = registerUser("readiness-second@matlift.com");

        mockMvc.perform(putReadiness(firstUserId, A_DAY, GREAT_DAY)).andExpect(status().isOk());
        mockMvc.perform(putReadiness(secondUserId, A_DAY, TERRIBLE_DAY)).andExpect(status().isOk());

        assertThat(readinessRepository.findByUserIdAndRecordDate(firstUserId, A_DAY))
                .get()
                .extracting(DailyReadiness::getReadinessPercentage)
                .isEqualTo(100);
        assertThat(readinessRepository.findByUserIdAndRecordDate(secondUserId, A_DAY))
                .get()
                .extracting(DailyReadiness::getReadinessPercentage)
                .isEqualTo(0);
    }

    @Test
    void shouldRejectADayTooFarInTheFuture() throws Exception {
        UUID userId = registerUser("readiness-future@matlift.com");

        mockMvc.perform(putReadiness(userId, LocalDate.now().plusDays(30), GREAT_DAY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Record date cannot be in the future"));

        assertThat(readinessRepository.findByUser(userId, null, null, 0, 20).totalElements()).isZero();
    }

    @Test
    void shouldAcceptTomorrowSoClientsAheadOfUtcAreNotBlocked() throws Exception {
        UUID userId = registerUser("readiness-tomorrow@matlift.com");

        mockMvc.perform(putReadiness(userId, LocalDate.now().plusDays(1), GREAT_DAY))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAScoreOutsideTheAllowedRange() throws Exception {
        UUID userId = registerUser("readiness-range@matlift.com");

        mockMvc.perform(putReadiness(userId, A_DAY, """
                        {"sleepScore": 7, "sorenessScore": 1, "stressScore": 1}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Sleep score must be between 1 and 5"));

        assertThat(readinessRepository.findByUser(userId, null, null, 0, 20).totalElements()).isZero();
    }

    private UUID registerUser(String email) throws Exception {
        String body = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"supersecret\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private org.springframework.test.web.servlet.RequestBuilder putReadiness(UUID userId, LocalDate date, String body) {
        return put("/api/readiness/{date}", date)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenIssuer.issue(userId).value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
