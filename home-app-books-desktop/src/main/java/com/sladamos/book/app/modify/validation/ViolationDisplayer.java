package com.sladamos.book.app.modify.validation;

import com.sladamos.book.model.Book;
import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Label;

public interface ViolationDisplayer {
    void displayViolation(ConstraintViolation<Book> violation);
    Label label();
}