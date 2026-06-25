package com.sladamos.activity;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityPoolEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.activity.model.key.ActivityPoolKey;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.RuntimeValidationException;
import com.sladamos.common.exception.ValidationException;
import com.sladamos.pool.PoolService;
import com.sladamos.pool.model.PoolEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    private final PoolService poolService;

    private final Validator validator;

    @Override
    public List<ActivityEntity> getAllActivities() {
        log.info("Fetching all activities from the repository");
        return activityRepository.findAll();
    }

    @Override
    public List<ActivityEntity> getActivitiesByActivityType(ActivityType activityType) {
        log.info("Fetching activities from the repository: [activityType: {}]", activityType);
        return activityRepository.findAllByActivityType(activityType);
    }

    @Override
    public ActivityEntity getActivityById(UUID id) throws NotFoundException {
        log.info("Fetching activity: [id: {}]", id);
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found with id: " + id));
    }

    @Override
    @Transactional
    public void createActivity(ActivityEntity activity) throws ValidationException {
        log.info("Creating activity: [activity: {}]", activity);
        processAndSaveActivity(activity);
    }

    @Override
    @Transactional
    public void updateActivity(ActivityEntity activity) throws ValidationException {
        log.info("Updating activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
        processAndSaveActivity(activity);
    }

    @Override
    @Transactional
    public ActivityEntity duplicateActivity(UUID id) throws NotFoundException, ValidationException {
        ActivityEntity sourceActivity = getActivityById(id);
        log.info("Duplicating activity: [id: {}, activityType: {}, activityDate: {}]", sourceActivity.getId(), sourceActivity.getActivityType(), sourceActivity.getActivityDate());
        ActivityEntity duplicatedActivity = sourceActivity.toBuilder()
                .id(UUID.randomUUID())
                .build();
        return processAndSaveActivity(duplicatedActivity);
    }

    @Override
    @Transactional
    public void deleteActivity(UUID id) throws NotFoundException {
        log.info("Deleting activity with id: {}", id);
        ActivityEntity activity = getActivityById(id);
        activityRepository.delete(activity);
        log.info("Activity successfully deleted: [id: {}]", id);
    }

    private ActivityEntity processAndSaveActivity(ActivityEntity activity) throws ValidationException {
        attachExistingPools(activity);
        Set<ConstraintViolation<ActivityEntity>> violations = validator.validate(activity);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred for activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
            throw new ValidationException(violations);
        }
        return activityRepository.save(activity);
    }

    private void attachExistingPools(ActivityEntity activity) throws RuntimeValidationException {
        if (activity.getActivityType() == ActivityType.SWIMMING) {
            log.info("Attaching existing pools for activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
            if (activity.getPoolSegments() != null) {
                activity.getPoolSegments().forEach(this.attachActivityToPool(activity));
            }
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