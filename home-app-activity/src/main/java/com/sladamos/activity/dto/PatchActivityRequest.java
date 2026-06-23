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
public class PatchActivityRequest {
    private String activityType;
    private String comment;
    private String location;
    private String swimmingStyle;
    private String routeName;
    private String encodedPolyline;
    private Integer durationSeconds;
    private Integer poolLength;
    private Integer distanceM;
    private Integer elevationGain;
    private List<String> routePoints;
    private List<String> team;
    private LocalDate activityDate;
}
