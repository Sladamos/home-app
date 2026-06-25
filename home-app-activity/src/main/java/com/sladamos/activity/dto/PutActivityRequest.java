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
    private String routeName;
    private Integer elevationGain;
    private String encodedPolyline;
    private List<String> routePoints;
    private List<PoolSegmentDto> pools;

    @Data
    public static class PoolSegmentDto {
        private String poolName;
        private Integer defaultLength;
        private Integer numberOfPools;
        private String swimmingStyle;
        private boolean savePool;
    }
}
