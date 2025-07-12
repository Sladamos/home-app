package com.sladamos.book.app.add.validation;

import com.sladamos.book.Book;
import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Label;

public interface ViolationDisplayer {
    void displayViolation(ConstraintViolation<Book> violation);
    Label label();
}