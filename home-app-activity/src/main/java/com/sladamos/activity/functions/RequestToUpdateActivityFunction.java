package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PatchActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityPoolEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.pool.model.PoolEntity;
import com.sladamos.pool.model.SwimmingStyle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class RequestToUpdateActivityFunction implements BiFunction<ActivityEntity, PatchActivityRequest, ActivityEntity> {

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
                .poolSegments(toPoolSegments(patchActivityRequest.getPools()))
                .build();
    }

    private List<ActivityPoolEntity> toPoolSegments(List<PatchActivityRequest.PoolSegmentDto> pools) {
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