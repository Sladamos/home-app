package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PutActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.pool.mapper.PoolSegmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class RequestToActivityFunction implements BiFunction<UUID, PutActivityRequest, ActivityEntity> {

    private final PoolSegmentMapper poolSegmentMapper;

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
                .poolSegments(poolSegmentMapper.toPutPoolSegments(request.getPools()))
                .build();
    }
}
