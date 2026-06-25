package com.sladamos.activity.model;

import com.sladamos.activity.model.key.ActivityPoolKey;
import com.sladamos.pool.model.PoolEntity;
import com.sladamos.pool.model.SwimmingStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "ACTIVITY_POOL")
public class ActivityPoolEntity {

    @EmbeddedId
    private ActivityPoolKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("activityId")
    @JoinColumn(name = "activity_id")
    private ActivityEntity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("poolId")
    @JoinColumn(name = "pool_id")
    private PoolEntity pool;

    @PositiveOrZero(message = "activity.validation.numberOfPools")
    private Integer numberOfPools;

    @PositiveOrZero(message = "activity.validation.poolLength")
    private Integer poolLength;

    @Enumerated(EnumType.STRING)
    private SwimmingStyle swimmingStyle;
}