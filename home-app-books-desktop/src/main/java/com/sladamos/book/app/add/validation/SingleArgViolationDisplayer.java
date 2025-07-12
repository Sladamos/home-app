package com.sladamos.book.app.add.validation;

import com.sladamos.app.util.BindingsCreator;
import com.sladamos.book.Book;
import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

@Slf4j
public record SingleArgViolationDisplayer<T>(BindingsCreator bindingsCreator,
                                             Label label,
                                             T messageArg) implements ViolationDisplayer {

    @Override
    public void displayViolation(ConstraintViolation<Book> violation) {
        label.setManaged(true);
        label.setVisible(true);
        String message = bindingsCreator.getMessage(violation.getMessage());
        String formatedMessage = MessageFormat.format(message, messageArg);
        label.setText(formatedMessage);
        log.info("Displaying validation error for [field: {}, message: {}]", violation.getPropertyPath(), message);
    }
}
