package com.matlift.readiness.infrastructure.persistence.mapper;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.infrastructure.persistence.DailyReadinessEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DailyReadinessPersistenceMapper {

    @Mapping(target = "createdAt", ignore = true)
    DailyReadinessEntity toEntity(DailyReadiness domain);

    DailyReadiness toDomain(DailyReadinessEntity entity);
}
