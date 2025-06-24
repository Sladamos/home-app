package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class BookValidationException extends Throwable {

    private final Set<ConstraintViolation<Book>> violations;

    public BookValidationException(Set<ConstraintViolation<Book>> violations) {
        this.violations = violations;
    }

    public String getReason() {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}
