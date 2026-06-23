package com.sladamos.activity;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;

import java.util.List;
import java.util.UUID;

public interface ActivityService {
    List<ActivityEntity> getAllActivities();
    List<ActivityEntity> getActivitiesByActivityType(ActivityType activityType);
    ActivityEntity getActivityById(UUID id) throws NotFoundException;
    void createActivity(ActivityEntity book) throws ValidationException;
    void updateActivity(ActivityEntity book) throws ValidationException;
    ActivityEntity duplicateActivity(UUID id) throws NotFoundException, ValidationException, DuplicationException;
    void deleteActivity(UUID id) throws NotFoundException;
}
