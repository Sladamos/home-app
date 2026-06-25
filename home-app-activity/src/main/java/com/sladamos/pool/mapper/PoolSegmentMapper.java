package com.sladamos.pool.mapper;

import com.sladamos.activity.dto.PatchActivityRequest;
import com.sladamos.activity.dto.PutActivityRequest;
import com.sladamos.pool.model.PoolSegmentEntity;
import com.sladamos.pool.model.SwimmingStyle;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PoolSegmentMapper {

    public List<PoolSegmentEntity> toPatchPoolSegments(List<PatchActivityRequest.PoolSegmentDto> pools) {
        if (pools == null) return new ArrayList<>();
        return pools.stream().map(dto -> createPoolSegmentEntity(dto.getNumberOfPools(),
                dto.getDefaultLength(), dto.getSwimmingStyle(), dto.isSavePool(), dto.getPoolName())).collect(Collectors.toList());
    }

    public List<PoolSegmentEntity> toPutPoolSegments(List<PutActivityRequest.PoolSegmentDto> pools) {
        if (pools == null) return new ArrayList<>();
        return pools.stream().map(dto -> createPoolSegmentEntity(dto.getNumberOfPools(),
                dto.getDefaultLength(), dto.getSwimmingStyle(), dto.isSavePool(), dto.getPoolName())).collect(Collectors.toList());
    }

    private static @NonNull PoolSegmentEntity createPoolSegmentEntity(Integer numberOfPools, Integer defaultLength, String swimmingStyle, boolean savePool, String poolName) {
        PoolSegmentEntity segment = new PoolSegmentEntity();
        segment.setNumberOfPools(numberOfPools);
        segment.setPoolLength(defaultLength);
        segment.setSwimmingStyle(Optional.ofNullable(swimmingStyle).map(SwimmingStyle::valueOf).orElse(SwimmingStyle.FREESTYLE));
        segment.setSavePool(savePool);
        segment.setPoolName(poolName);
        return segment;
    }
}
