package com.sladamos.pool.function;

import com.sladamos.activity.dto.GetPoolsResponse;
import com.sladamos.pool.model.PoolEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PoolsToResponseFunction implements Function<List<PoolEntity>, GetPoolsResponse> {
    @Override
    public GetPoolsResponse apply(List<PoolEntity> pools) {
        return GetPoolsResponse.builder()
                .pools(
                        pools.stream()
                                .map(pool -> GetPoolsResponse.Pool.builder()
                                        .id(pool.getId())
                                        .name(pool.getName())
                                        .defaultLength(pool.getDefaultLength())
                                        .build())
                                .toList()
                )
                .build();
    }
}
