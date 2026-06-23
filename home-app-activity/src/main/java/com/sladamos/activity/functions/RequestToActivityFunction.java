package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PutActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.SwimmingStyle;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

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
                .poolLength(request.getPoolLength())
                .distanceM(request.getDistanceM())
                .elevationGain(request.getElevationGain())
                .routePoints(request.getRoutePoints())
                .team(request.getTeam())
                .activityDate(request.getActivityDate())
                .swimmingStyle(Optional.ofNullable(request.getSwimmingStyle()).map(SwimmingStyle::valueOf).orElse(SwimmingStyle.FREESTYLE))
                .build();
    }
}
