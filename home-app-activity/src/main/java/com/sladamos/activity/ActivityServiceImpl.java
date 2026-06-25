package com.sladamos.activity;

import com.sladamos.activity.handler.ActivityHandler;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    private final Validator validator;

    private final Map<ActivityType, ActivityHandler> handlers;

    public ActivityServiceImpl(ActivityRepository activityRepository, Validator validator, List<ActivityHandler> handlerList) {
        this.activityRepository = activityRepository;
        this.validator = validator;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ActivityHandler::supportedActivity, h -> h));
    }

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
        log.info("Executing handler for activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
        handlers.get(activity.getActivityType()).handle(activity);
        log.info("Validating activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
        Set<ConstraintViolation<ActivityEntity>> violations = validator.validate(activity);
        if (!violations.isEmpty()) {
            log.error("Validation errors occurred for activity: [id: {}, activityType: {}, activityDate: {}]", activity.getId(), activity.getActivityType(), activity.getActivityDate());
            throw new ValidationException(violations);
        }
        return activityRepository.save(activity);
    }

}