package com.sladamos.common.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends Throwable {

    public Set<? extends ConstraintViolation<?>> violations;

    public ValidationException(Set<? extends ConstraintViolation<?>> violations) {
        super(getReason(violations));
        this.violations = violations;
    }

    private static String getReason(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}
