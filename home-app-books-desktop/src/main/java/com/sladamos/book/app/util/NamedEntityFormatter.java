package com.sladamos.book.app.util;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NamedEntityFormatter {

    private static final String SEPARATOR = ", ";

    public <T> String format(Set<T> entities, Function<T, String> nameExtractor) {
        if (entities == null || entities.isEmpty()) {
            return "";
        }
        return entities.stream()
                .map(nameExtractor)
                .sorted()
                .collect(Collectors.joining(SEPARATOR));
    }
}

