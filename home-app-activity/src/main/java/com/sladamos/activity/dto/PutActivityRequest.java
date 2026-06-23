package com.sladamos.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutActivityRequest {
    private String activityType;
    private LocalDate activityDate;
    private List<String> team;
    private String comment;
    private String location;
    private Integer distanceM;
    private Integer durationSeconds;
    private Integer poolLength;
    private String swimmingStyle;
    private String routeName;
    private Integer elevationGain;
    private String encodedPolyline;
    private List<String> routePoints;

    public String getName() {
        return activityType;
    }
}
