package com.sladamos.activity.handler;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityPoolEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.activity.model.key.ActivityPoolKey;
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
    }

    private Consumer<ActivityPoolEntity> attachActivityToPool(ActivityEntity activity) {
        return segment -> {
            PoolEntity pool = segment.getPool();
            PoolEntity managedPool = poolService.getPoolEntityByName(pool.getName()).orElseGet(() -> poolService.createPool(pool));
            if (pool.getDefaultLength() != null && !pool.getDefaultLength().equals(managedPool.getDefaultLength())) {
                log.info("Modifying default distance from, to: [from: {}, to: {}]", managedPool.getDefaultLength(), pool.getDefaultLength());
                managedPool.setDefaultLength(pool.getDefaultLength());
            }

            log.info("Attaching pool to segment: [id: {}, poolName: {}]", managedPool.getId(), managedPool.getName());
            segment.setPool(managedPool);
            segment.setActivity(activity);

            if (activity.getId() != null && managedPool.getId() != null) {
                log.info("Attaching ActivityPoolKey: [activityId: {}, poolId: {}]", activity.getId(), managedPool.getId());
                segment.setId(new ActivityPoolKey(activity.getId(), managedPool.getId()));
            }
        };
    }
}
