package com.sladamos.pool;

import com.sladamos.common.exception.RuntimeValidationException;
import com.sladamos.pool.model.PoolEntity;

import java.util.Optional;
import java.util.UUID;

public interface PoolService {
    Optional<PoolEntity> getPoolEntityById(UUID id);

    Optional<PoolEntity> getPoolEntityByName(String poolName);

    PoolEntity createPool(PoolEntity pool) throws RuntimeValidationException;
}
