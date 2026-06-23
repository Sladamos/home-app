package com.sladamos.activity;

import com.sladamos.activity.dto.GetActivitiesResponse;
import com.sladamos.activity.dto.PatchActivityRequest;
import com.sladamos.activity.dto.PutActivityRequest;
import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.functions.ActivitiesToResponseFunction;
import com.sladamos.activity.functions.RequestToActivityFunction;
import com.sladamos.activity.functions.RequestToUpdateActivityFunction;
import com.sladamos.common.exception.DuplicationException;
import com.sladamos.common.exception.NotFoundException;
import com.sladamos.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    private final ActivitiesToResponseFunction activitiesToResponse;

    private final RequestToActivityFunction requestToActivity;

    private final RequestToUpdateActivityFunction requestToUpdateActivity;

    @GetMapping
    public GetActivitiesResponse getActivities() {
        log.info("Request fetching all activities");
        GetActivitiesResponse response = activitiesToResponse.apply(service.getAllActivities());
        log.info("Fetched all activities: [count: {}]", response.getActivities().size());
        return response;
    }

    @PutMapping("/{id}")
    public void putActivity(@PathVariable("id") UUID id, @RequestBody PutActivityRequest request) {
        log.info("Request creating activity: [id: {}, activityType: {}, activityDate: {}]", id, request.getActivityType(), request.getActivityDate());
        try {
            service.createActivity(requestToActivity.apply(id, request));
            log.info("Activity created: [id: {}, activityType: {}, activityDate: {}]", id, request.getActivityType(), request.getActivityDate());
        } catch (ValidationException e) {
            onValidationExceptionOccurred(id, request.getActivityType(), request.getActivityDate(), e);
        }
    }

    @PatchMapping("/{id}")
    public void patchActivity(@PathVariable("id") UUID id, @RequestBody PatchActivityRequest request) {
        log.info("Request updating activity: [id: {}, activityType: {}, activityDate: {}]", id, request.getActivityType(), request.getActivityDate());
        try {
            ActivityEntity activityEntity = service.getActivityById(id);
            log.info("Activity found for update: [id: {}, activityType: {}, activityDate: {}]", activityEntity.getId(), activityEntity.getActivityType(), activityEntity.getActivityDate());
            service.updateActivity(requestToUpdateActivity.apply(activityEntity, request));
            log.info("Activity updated: [id: {}, activityType: {}, activityDate: {}]", activityEntity.getId(), request.getActivityType(), request.getActivityDate());
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        } catch (ValidationException e) {
            onValidationExceptionOccurred(id, request.getActivityType(), request.getActivityDate(), e);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable("id") UUID id) {
        log.info("Request deleting activity: [id: {}]", id);
        try {
            service.deleteActivity(id);
            log.info("Activity deleted: [id: {}]", id);
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        }
    }

    @PostMapping("/{id}/duplicate")
    public void duplicateActivity(@PathVariable("id") UUID id) {
        log.info("Request duplicating activity: [id: {}]", id);
        try {
            service.duplicateActivity(id);
            log.info("Activity duplicated: [id: {}]", id);
        } catch (NotFoundException e) {
            onNotFoundExceptionOccurred(id);
        } catch (ValidationException | DuplicationException e) {
            log.info("Activity duplication failed: [id: {}, reason: {}]", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private static void onNotFoundExceptionOccurred(UUID id) {
        log.info("Activity not found: [id: {}]", id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private static void onValidationExceptionOccurred(UUID id, String activityType, LocalDate activityDate, ValidationException e) {
        log.info("Activity validation failed: [id: {}, activityType: {}, activityDate: {}, reason: {}]", id, activityType, activityDate, e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
