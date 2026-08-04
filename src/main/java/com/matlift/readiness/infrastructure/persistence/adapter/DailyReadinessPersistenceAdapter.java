package com.matlift.readiness.infrastructure.persistence.adapter;

import com.matlift.readiness.domain.model.DailyReadiness;
import com.matlift.readiness.domain.port.out.DailyReadinessRepository;
import com.matlift.readiness.infrastructure.persistence.DailyReadinessEntity;
import com.matlift.readiness.infrastructure.persistence.SpringDataDailyReadinessRepository;
import com.matlift.readiness.infrastructure.persistence.mapper.DailyReadinessPersistenceMapper;
import com.matlift.shared.domain.PagedResult;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DailyReadinessPersistenceAdapter implements DailyReadinessRepository {

    private static final String RECORD_DATE_FIELD = "recordDate";

    private final SpringDataDailyReadinessRepository repository;
    private final DailyReadinessPersistenceMapper mapper;

    public DailyReadinessPersistenceAdapter(SpringDataDailyReadinessRepository repository,
                                            DailyReadinessPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public DailyReadiness save(DailyReadiness dailyReadiness) {
        DailyReadinessEntity entity = mapper.toEntity(dailyReadiness);
        DailyReadinessEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<DailyReadiness> findByUserIdAndRecordDate(UUID userId, LocalDate recordDate) {
        return repository.findByUserIdAndRecordDate(userId, recordDate)
                .map(mapper::toDomain);
    }

    @Override
    public PagedResult<DailyReadiness> findByUser(UUID userId, LocalDate from, LocalDate to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, RECORD_DATE_FIELD));
        Page<DailyReadinessEntity> result = repository.findAll(dateRangeOf(userId, from, to), pageable);

        return new PagedResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public boolean deleteByUserIdAndRecordDate(UUID userId, LocalDate recordDate) {
        return repository.deleteByUserIdAndRecordDate(userId, recordDate) > 0;
    }

    private Specification<DailyReadinessEntity> dateRangeOf(UUID userId, LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("userId"), userId));

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(RECORD_DATE_FIELD), from));
            }
            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(RECORD_DATE_FIELD), to));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
