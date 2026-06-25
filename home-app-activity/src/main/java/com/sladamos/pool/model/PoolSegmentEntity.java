package com.sladamos.pool.model;

import com.sladamos.activity.model.ActivityEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "POOL_SEGMENT")
public class PoolSegmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ActivityEntity activity;

    @Column(nullable = false)
    private String poolName;

    @PositiveOrZero(message = "activity.validation.numberOfPools")
    private Integer numberOfPools;

    @PositiveOrZero(message = "activity.validation.poolLength")
    private Integer poolLength;

    @Enumerated(EnumType.STRING)
    private SwimmingStyle swimmingStyle;

    @Transient
    private boolean savePool;
}