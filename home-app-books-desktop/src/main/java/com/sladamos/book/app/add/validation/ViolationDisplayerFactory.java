package com.sladamos.book.app.add.validation;

import com.sladamos.app.util.BindingsCreator;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViolationDisplayerFactory {

    private final BindingsCreator bindingsCreator;

    public NoArgsViolationDisplayer createNoArgsViolationsDisplayer(Label label) {
        return new NoArgsViolationDisplayer(bindingsCreator, label);
    }

    public <T> SingleArgViolationDisplayer<T> createSingleArgViolationsDisplayer(T messageArg, Label label) {
        return new SingleArgViolationDisplayer<>(bindingsCreator, label, messageArg);
    }
}
