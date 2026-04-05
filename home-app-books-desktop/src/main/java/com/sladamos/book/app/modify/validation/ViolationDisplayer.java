package com.sladamos.book.app.modify.validation;

import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Label;

public interface ViolationDisplayer {
    void displayViolation(ConstraintViolation<?> violation);
    Label label();
}