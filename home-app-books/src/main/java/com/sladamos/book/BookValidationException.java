package com.sladamos.book;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class BookValidationException extends Throwable {
    public BookValidationException(Set<ConstraintViolation<Book>> violations) {
    }
}
