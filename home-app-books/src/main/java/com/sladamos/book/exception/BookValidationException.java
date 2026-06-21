package com.sladamos.book.exception;

import com.sladamos.book.model.Book;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class BookValidationException extends Throwable {

    public Set<ConstraintViolation<Book>> violations;

    public BookValidationException(Set<ConstraintViolation<Book>> violations) {
        super(getReason(violations));
        this.violations = violations;
    }

    private static String getReason(Set<ConstraintViolation<Book>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}
