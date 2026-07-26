package com.matlift.workout.infrastructure.persistence.adapter;

import com.matlift.shared.domain.PagedResult;
import com.matlift.workout.domain.model.WorkoutSession;
import com.matlift.workout.domain.port.out.WorkoutSessionRepository;
import com.matlift.workout.infrastructure.persistence.SpringDataWorkoutSessionRepository;
import com.matlift.workout.infrastructure.persistence.WorkoutSessionEntity;
import com.matlift.workout.infrastructure.persistence.mapper.WorkoutSessionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkoutSessionPersistenceAdapter implements WorkoutSessionRepository {

    private static final String SESSION_DATE_FIELD = "sessionDate";

    private final SpringDataWorkoutSessionRepository repository;
    private final WorkoutSessionPersistenceMapper mapper;

    public WorkoutSessionPersistenceAdapter(SpringDataWorkoutSessionRepository repository, WorkoutSessionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public WorkoutSession save(WorkoutSession workoutSession) {
        WorkoutSessionEntity entity = mapper.toEntity(workoutSession);
        WorkoutSessionEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<WorkoutSession> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PagedResult<WorkoutSession> findByUser(UUID userId, ZonedDateTime from, ZonedDateTime to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, SESSION_DATE_FIELD));
        Page<WorkoutSessionEntity> result = repository.findAll(dateRangeOf(userId, from, to), pageable);

        return new PagedResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Specification<WorkoutSessionEntity> dateRangeOf(UUID userId, ZonedDateTime from, ZonedDateTime to) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("userId"), userId));

            if (from != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(SESSION_DATE_FIELD), from));
            }
            if (to != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(SESSION_DATE_FIELD), to));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
