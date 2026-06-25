package com.sladamos.activity.handler;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;

public interface ActivityHandler {
    ActivityType supportedActivity();

    void handle(ActivityEntity activity);
}
