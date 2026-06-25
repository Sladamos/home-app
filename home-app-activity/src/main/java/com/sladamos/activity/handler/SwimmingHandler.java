package com.sladamos.activity.handler;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.pool.model.PoolSegmentEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.pool.PoolService;
import com.sladamos.pool.model.PoolEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwimmingHandler implements ActivityHandler {

    private final PoolService poolService;

    @Override
    public ActivityType supportedActivity() {
        return ActivityType.SWIMMING;
    }

    @Override
    public void handle(ActivityEntity activity) {
        log.info("Attaching existing pools for activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
        if (activity.getPoolSegments() != null) {
            activity.getPoolSegments().forEach(this.attachActivityToPool(activity));
        }
        calculateTotalDistance(activity);
    }

    private Consumer<PoolSegmentEntity> attachActivityToPool(ActivityEntity activity) {
        return segment -> {
            String poolName = segment.getPoolName();
            if (segment.isSavePool()) {
                PoolEntity managedPool = poolService.getPoolEntityByName(poolName).orElseGet(() -> this.createPoolFromSegment(segment));
                if (segment.getPoolLength() != null && !segment.getPoolLength().equals(managedPool.getDefaultLength()) && segment.isSavePool()) {
                    log.info("Modifying default distance from, to: [from: {}, to: {}]", managedPool.getDefaultLength(), segment.getPoolLength());
                    managedPool.setDefaultLength(segment.getPoolLength());
                }
            } else {
                log.info("Segment is not marked to save pool: [poolName: {}]", poolName);
            }
            segment.setActivity(activity);
        };
    }

    private PoolEntity createPoolFromSegment(PoolSegmentEntity segment) {
        String poolName = segment.getPoolName();
        log.info("Creating new pool for segment: [poolName: {}]", poolName);
        PoolEntity newPool = PoolEntity.builder()
                .name(poolName)
                .defaultLength(segment.getPoolLength())
                .build();
        PoolEntity savedPool = poolService.createPool(newPool);
        log.info("Created new pool: [id: {}, name: {}]", savedPool.getId(), savedPool.getName());
        return savedPool;
    }

    private void calculateTotalDistance(ActivityEntity activity) {
        if (activity.getPoolSegments() != null) {
            int totalDistance = activity.getPoolSegments().stream()
                    .mapToInt(segment -> segment.getPoolLength() != null ? segment.getPoolLength() * segment.getNumberOfPools() : 0)
                    .sum();
            log.info("Calculated total distance in meters for activity: [id: {}, totalDistance: {}]", activity.getId(), totalDistance);
            activity.setDistanceM(totalDistance);
        }
    }
}
