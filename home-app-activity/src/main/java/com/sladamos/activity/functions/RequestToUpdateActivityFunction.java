package com.sladamos.activity.functions;

import com.sladamos.activity.dto.PatchActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.SwimmingStyle;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;

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
                .poolLength(Optional.ofNullable(patchActivityRequest.getPoolLength()).orElse(entity.getPoolLength()))
                .distanceM(Optional.ofNullable(patchActivityRequest.getDistanceM()).orElse(entity.getDistanceM()))
                .elevationGain(Optional.ofNullable(patchActivityRequest.getElevationGain()).orElse(entity.getElevationGain()))
                .routePoints(Optional.ofNullable(patchActivityRequest.getRoutePoints()).orElse(entity.getRoutePoints()))
                .team(Optional.ofNullable(patchActivityRequest.getTeam()).orElse(entity.getTeam()))
                .activityDate(Optional.ofNullable(patchActivityRequest.getActivityDate()).orElse(entity.getActivityDate()))
                .swimmingStyle(Optional.ofNullable(patchActivityRequest.getSwimmingStyle()).map(SwimmingStyle::valueOf).orElse(entity.getSwimmingStyle()))
                .build();
    }
}