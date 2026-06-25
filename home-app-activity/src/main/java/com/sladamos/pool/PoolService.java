package com.sladamos.pool;

import com.sladamos.common.exception.RuntimeValidationException;
import com.sladamos.pool.model.PoolEntity;

import java.util.List;
import java.util.Optional;

public interface PoolService {
    List<PoolEntity> getAllPools();

    Optional<PoolEntity> getPoolEntityByName(String poolName);

    PoolEntity createPool(PoolEntity pool) throws RuntimeValidationException;
}
