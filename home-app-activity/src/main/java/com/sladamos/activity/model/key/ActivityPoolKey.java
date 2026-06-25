package com.sladamos.activity.model.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPoolKey implements Serializable {

    @Column(name = "activity_id")
    private UUID activityId;

    @Column(name = "pool_id")
    private UUID poolId;
}