package com.sladamos.pool;

import com.sladamos.pool.model.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PoolRepository extends JpaRepository<PoolEntity, UUID> {

    Optional<PoolEntity> findByName(String poolName);
}
