package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PutActivityRequest;
import com.sladamos.activity.model.*;
import com.sladamos.pool.model.PoolEntity;
import com.sladamos.pool.model.SwimmingStyle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class RequestToActivityFunction implements BiFunction<UUID, PutActivityRequest, ActivityEntity> {
    @Override
    public ActivityEntity apply(UUID id, PutActivityRequest request) {
        return ActivityEntity.builder()
                .id(id)
                .comment(request.getComment())
                .location(request.getLocation())
                .routeName(request.getRouteName())
                .encodedPolyline(request.getEncodedPolyline())
                .durationSeconds(request.getDurationSeconds())
                .distanceM(request.getDistanceM())
                .elevationGain(request.getElevationGain())
                .routePoints(request.getRoutePoints())
                .team(request.getTeam())
                .activityDate(request.getActivityDate())
                .creationDate(LocalDateTime.now())
                .activityType(Optional.ofNullable(request.getActivityType()).map(ActivityType::valueOf).orElse(ActivityType.MULTISPORT))
                .poolSegments(toPoolSegments(request.getPools()))
                .build();
    }

    private List<ActivityPoolEntity> toPoolSegments(List<PutActivityRequest.PoolSegmentDto> pools) {
        if (pools == null) return new ArrayList<>();
        return pools.stream().map(dto -> {
            ActivityPoolEntity segment = new ActivityPoolEntity();
            segment.setNumberOfPools(dto.getNumberOfPools());
            segment.setPoolLength(dto.getDefaultLength());
            segment.setSwimmingStyle(Optional.ofNullable(dto.getSwimmingStyle()).map(SwimmingStyle::valueOf).orElse(SwimmingStyle.FREESTYLE));

            PoolEntity pool = new PoolEntity();
            pool.setName(dto.getPoolName());
            pool.setDefaultLength(dto.getDefaultLength());
            segment.setPool(pool);

            return segment;
        }).collect(Collectors.toList());
    }
}
