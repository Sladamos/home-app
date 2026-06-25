package com.sladamos.activity.functions;

import com.sladamos.activity.dto.GetActivityDetailsResponse;
import com.sladamos.activity.model.ActivityEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class ActivityDetailsToResponseFunction implements Function<ActivityEntity, GetActivityDetailsResponse> {
    @Override
    public GetActivityDetailsResponse apply(ActivityEntity activity) {
        return GetActivityDetailsResponse.builder()
                .id(activity.getId())
                .activityType(Optional.ofNullable(activity.getActivityType()).map(Enum::name).orElse(null))
                .comment(activity.getComment())
                .location(activity.getLocation())
                .routeName(activity.getRouteName())
                .encodedPolyline(activity.getEncodedPolyline())
                .durationSeconds(activity.getDurationSeconds())
                .distanceM(activity.getDistanceM())
                .elevationGain(activity.getElevationGain())
                .routePoints(activity.getRoutePoints())
                .team(activity.getTeam())
                .activityDate(activity.getActivityDate())
                .creationDate(activity.getCreationDate())
                .modificationDate(activity.getModificationDate())
                .pools(
                        activity.getPoolSegments().stream()
                                .map(poolSegment -> GetActivityDetailsResponse.ActivityPool.builder()
                                        .id(poolSegment.getPool().getId())
                                        .poolName(poolSegment.getPool().getName())
                                        .numberOfPools(poolSegment.getNumberOfPools())
                                        .poolLength(poolSegment.getPoolLength())
                                        .swimmingStyle(Optional.ofNullable(poolSegment.getSwimmingStyle()).map(Enum::name).orElse(null))
                                        .build())
                                .toList()
                )
                .build();
    }
}
