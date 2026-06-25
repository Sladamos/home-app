package com.sladamos.activity.functions;

import com.sladamos.activity.dto.GetActivitiesResponse;
import com.sladamos.activity.model.ActivityEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class ActivitiesToResponseFunction implements Function<List<ActivityEntity>, GetActivitiesResponse> {
    @Override
    public GetActivitiesResponse apply(List<ActivityEntity> activities) {
        return GetActivitiesResponse.builder()
                .activities(
                        activities.stream()
                                .map(activity -> GetActivitiesResponse.Activity.builder()
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
                                        .build())
                                .toList()
                )
                .build();
    }
}
