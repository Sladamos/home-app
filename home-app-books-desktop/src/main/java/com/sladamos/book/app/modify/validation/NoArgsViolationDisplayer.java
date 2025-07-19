package com.sladamos.book.app.modify.validation;

import com.sladamos.app.util.messages.BindingsCreator;
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
        label.textProperty().bind(bindingsCreator.createBinding(violation.getMessage()));
        log.info("Displaying validation error for [field: {}, message: {}]", violation.getPropertyPath(), bindingsCreator.getMessage(violation.getMessage()));
    }
}
