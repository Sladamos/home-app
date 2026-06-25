package com.sladamos.activity.model;

import com.sladamos.common.model.BaseEntity;
import com.sladamos.common.string.StringListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@Table(name = "ACTIVITY")
public class ActivityEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "activity.validation.activityType")
    @Column(nullable = false)
    private ActivityType activityType;

    @PastOrPresent(message = "activity.validation.activityDate")
    @Column(nullable = false)
    private LocalDate activityDate;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> team;

    @Column(length = 250)
    private String comment;

    private String location;

    @PositiveOrZero(message = "activity.validation.distanceM")
    private Integer distanceM;

    @PositiveOrZero(message = "activity.validation.durationSeconds")
    private Integer durationSeconds;

    // SWIMMING
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityPoolEntity> poolSegments = new ArrayList<>();

    // HIKING / BIKING
    private String routeName;
    private Integer elevationGain;
    @Column(columnDefinition = "TEXT")
    private String encodedPolyline;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> routePoints;
/*
    // GYM
    @Convert(converter = ExerciseListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Exercise> exercises;*/
}