package com.sladamos.book.app.add.validation;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.book.Book;
import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record NoArgsViolationDisplayer(BindingsCreator bindingsCreator, Label label) implements ViolationDisplayer {

    @Override
    public void displayViolation(ConstraintViolation<Book> violation) {
        label.setManaged(true);
        label.setVisible(true);
        String message = bindingsCreator.getMessage(violation.getMessage());
        label.setText(message);
        log.info("Displaying validation error for [field: {}, message: {}]", violation.getPropertyPath(), message);
    }
}
