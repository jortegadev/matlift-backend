package com.matlift.readiness.infrastructure.rest.mapper;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.in.SaveDailyReadinessCommand;
import com.matlift.readiness.infrastructure.rest.dto.DailyReadinessRequest;
import com.matlift.readiness.infrastructure.rest.dto.DailyReadinessResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DailyReadinessRestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "recordDate", source = "recordDate")
    SaveDailyReadinessCommand toCommand(DailyReadinessRequest request, UUID userId, LocalDate recordDate);

    DailyReadinessResponse toResponse(DailyReadiness domain);
}
