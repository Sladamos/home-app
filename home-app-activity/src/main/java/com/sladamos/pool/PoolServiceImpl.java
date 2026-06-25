package com.sladamos.pool;

import com.sladamos.common.exception.RuntimeValidationException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.pool.model.PoolEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class PoolServiceImpl implements PoolService {

    private final PoolRepository poolRepository;

    private final Validator validator;

    @Override
    public List<PoolEntity> getAllPools() {
        log.info("Fetching all pools from the repository");
        return poolRepository.findAll();
    }

    @Override
    public Optional<PoolEntity> getPoolEntityByName(String poolName) {
        log.info("Fetching pool from the repository: [poolName: {}]", poolName);
        return poolRepository.findByName(poolName);
    }

    @Override
    public PoolEntity createPool(PoolEntity pool) throws RuntimeValidationException {
        log.info("Creating pool: [pool: {}]", pool);
        return processAndSavePool(pool);
    }

    private PoolEntity processAndSavePool(PoolEntity pool) throws RuntimeValidationException {
        Set<ConstraintViolation<PoolEntity>> violations = validator.validate(pool);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred for pool: [id: {}, name: {}]", pool.getId(), pool.getName());
            throw new RuntimeValidationException(new ValidationException(violations));
        }
        return poolRepository.saveAndFlush(pool);
    }
}
