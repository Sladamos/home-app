package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PatchActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.pool.mapper.PoolSegmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class RequestToUpdateActivityFunction implements BiFunction<ActivityEntity, PatchActivityRequest, ActivityEntity> {

    private final PoolSegmentMapper poolSegmentMapper;

    @Override
    public ActivityEntity apply(ActivityEntity entity, PatchActivityRequest patchActivityRequest) {
        return ActivityEntity.builder()
                .id(entity.getId())
                .comment(Optional.ofNullable(patchActivityRequest.getComment()).orElse(entity.getComment()))
                .location(Optional.ofNullable(patchActivityRequest.getLocation()).orElse(entity.getLocation()))
                .routeName(Optional.ofNullable(patchActivityRequest.getRouteName()).orElse(entity.getRouteName()))
                .encodedPolyline(Optional.ofNullable(patchActivityRequest.getEncodedPolyline()).orElse(entity.getEncodedPolyline()))
                .durationSeconds(Optional.ofNullable(patchActivityRequest.getDurationSeconds()).orElse(entity.getDurationSeconds()))
                .distanceM(Optional.ofNullable(patchActivityRequest.getDistanceM()).orElse(entity.getDistanceM()))
                .elevationGain(Optional.ofNullable(patchActivityRequest.getElevationGain()).orElse(entity.getElevationGain()))
                .routePoints(Optional.ofNullable(patchActivityRequest.getRoutePoints()).orElse(entity.getRoutePoints()))
                .team(Optional.ofNullable(patchActivityRequest.getTeam()).orElse(entity.getTeam()))
                .activityDate(Optional.ofNullable(patchActivityRequest.getActivityDate()).orElse(entity.getActivityDate()))
                .activityType(Optional.ofNullable(patchActivityRequest.getActivityType()).map(ActivityType::valueOf).orElse(entity.getActivityType()))
                .creationDate(entity.getCreationDate())
                .poolSegments(poolSegmentMapper.toPatchPoolSegments(patchActivityRequest.getPools()))
                .build();
    }
}