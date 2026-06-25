package com.sladamos.activity.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPoolsResponse {

    @Data
    @Builder
    public static class Pool {
        private UUID id;
        private String name;
        private Integer defaultLength;
    }

    @Singular
    private List<Pool> pools;
}
