package com.matlift.readiness.infrastructure.rest.controller;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessUseCase;
import com.matlift.readiness.infrastructure.rest.dto.DailyReadinessRequest;
import com.matlift.readiness.infrastructure.rest.dto.DailyReadinessResponse;
import com.matlift.readiness.infrastructure.rest.mapper.DailyReadinessRestMapper;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/readiness")
public class DailyReadinessController {

    private final SaveDailyReadinessUseCase saveDailyReadinessUseCase;
    private final DailyReadinessRestMapper mapper;

    public DailyReadinessController(SaveDailyReadinessUseCase saveDailyReadinessUseCase,
                                    DailyReadinessRestMapper mapper) {
        this.saveDailyReadinessUseCase = saveDailyReadinessUseCase;
        this.mapper = mapper;
    }

    @PutMapping("/{date}")
    public ResponseEntity<DailyReadinessResponse> saveReadiness(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody DailyReadinessRequest request,
            Principal principal) {

        DailyReadiness savedReadiness =
                saveDailyReadinessUseCase.execute(mapper.toCommand(request, requesterId(principal), date));

        return ResponseEntity.ok(mapper.toResponse(savedReadiness));
    }

    private UUID requesterId(Principal principal) {
        return UUID.fromString(principal.getName());
    }
}
