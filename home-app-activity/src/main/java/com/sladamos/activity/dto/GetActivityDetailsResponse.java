package com.sladamos.activity.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetActivityDetailsResponse {
    @Data
    @Builder
    public static class ActivityPool {
        private UUID id;
        private String poolName;
        private Integer numberOfPools;
        private Integer poolLength;
        private String swimmingStyle;
    }

    private UUID id;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDate activityDate;
    private String activityType;
    private String comment;
    private String location;
    private String encodedPolyline;
    private String routeName;
    private Integer durationSeconds;
    private Integer elevationGain;
    private Integer distanceM;
    private List<String> team;
    private List<String> routePoints;

    @Singular
    private List<ActivityPool> pools;
}
