package com.sladamos.activity.repository;

import com.sladamos.activity.model.ActivityEntity;
import com.sladamos.activity.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findAllByActivityType(ActivityType activityType);
}
