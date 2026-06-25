package com.sladamos.common.exception;

public class RuntimeValidationException extends RuntimeException {
    public RuntimeValidationException(ValidationException e) {
        super(e.getMessage(), e);
    }
}
