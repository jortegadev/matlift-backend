package com.matlift.readiness.infrastructure.rest.controller;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessCommand;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessUseCase;
import com.matlift.readiness.infrastructure.rest.mapper.DailyReadinessRestMapperImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DailyReadinessController.class)
@Import(DailyReadinessRestMapperImpl.class)
class DailyReadinessControllerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, Month.JULY, 24);

    private static final String VALID_BODY = """
            {
                "sleepScore": 5,
                "sorenessScore": 1,
                "stressScore": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveDailyReadinessUseCase saveDailyReadinessUseCase;

    @Test
    void shouldReturn200WithTheSavedReadiness() throws Exception {
        UUID userId = UUID.randomUUID();

        when(saveDailyReadinessUseCase.execute(any(SaveDailyReadinessCommand.class)))
                .thenReturn(new DailyReadiness(null, userId, TODAY, 5, 1, 1));

        mockMvc.perform(put("/api/readiness/{date}", TODAY)
                        .principal(principalFor(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recordDate").value("2026-07-24"))
                .andExpect(jsonPath("$.readinessPercentage").value(100));
    }

    @Test
    void shouldNeverExposeTheInternalId() throws Exception {
        when(saveDailyReadinessUseCase.execute(any(SaveDailyReadinessCommand.class)))
                .thenReturn(new DailyReadiness(null, UUID.randomUUID(), TODAY, 3, 3, 3));

        mockMvc.perform(put("/api/readiness/{date}", TODAY)
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void shouldTakeTheOwnerFromTheTokenAndTheDayFromThePath() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDate pathDate = LocalDate.of(2026, Month.MAY, 3);
        ArgumentCaptor<SaveDailyReadinessCommand> commandCaptor =
                ArgumentCaptor.forClass(SaveDailyReadinessCommand.class);

        when(saveDailyReadinessUseCase.execute(any(SaveDailyReadinessCommand.class)))
                .thenReturn(new DailyReadiness(null, userId, pathDate, 5, 1, 1));

        mockMvc.perform(put("/api/readiness/{date}", pathDate)
                        .principal(principalFor(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());

        verify(saveDailyReadinessUseCase).execute(commandCaptor.capture());

        assertThat(commandCaptor.getValue().userId()).isEqualTo(userId);
        assertThat(commandCaptor.getValue().recordDate()).isEqualTo(pathDate);
        assertThat(commandCaptor.getValue().sleepScore()).isEqualTo(5);
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenScoresAreMissing() throws Exception {
        mockMvc.perform(put("/api/readiness/{date}", TODAY)
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.sleepScore").value("is required"))
                .andExpect(jsonPath("$.fields.sorenessScore").value("is required"))
                .andExpect(jsonPath("$.fields.stressScore").value("is required"));

        verify(saveDailyReadinessUseCase, never()).execute(any(SaveDailyReadinessCommand.class));
    }

    @Test
    void shouldReturn400WhenTheDateInThePathIsNotADate() throws Exception {
        mockMvc.perform(put("/api/readiness/{date}", "yesterday")
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parameter 'date' has an invalid value"));

        verify(saveDailyReadinessUseCase, never()).execute(any(SaveDailyReadinessCommand.class));
    }

    @Test
    void shouldReturn400WhenAScoreIsOutOfRange() throws Exception {
        when(saveDailyReadinessUseCase.execute(any(SaveDailyReadinessCommand.class)))
                .thenThrow(new IllegalArgumentException("Sleep score must be between 1 and 5"));

        mockMvc.perform(put("/api/readiness/{date}", TODAY)
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sleepScore": 9, "sorenessScore": 1, "stressScore": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Sleep score must be between 1 and 5"));
    }

    @Test
    void shouldReturn400WhenTheDateIsInTheFuture() throws Exception {
        when(saveDailyReadinessUseCase.execute(any(SaveDailyReadinessCommand.class)))
                .thenThrow(new IllegalArgumentException("Record date cannot be in the future"));

        mockMvc.perform(put("/api/readiness/{date}", TODAY.plusYears(1))
                        .principal(principalFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Record date cannot be in the future"));
    }

    private Principal principalFor(UUID id) {
        return id::toString;
    }
}
