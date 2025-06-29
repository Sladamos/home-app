package com.sladamos.book.app.common;

import com.sladamos.app.util.BindingsCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultipleFieldsControllerFactory {

    private final BindingsCreator bindingsCreator;

    public MultipleFieldsController createMultipleFieldsController(String labelKey) {
        return new MultipleFieldsController(bindingsCreator, labelKey);
    }
}
