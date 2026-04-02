package com.sladamos.book.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NamedEntityFormatterTest {

    private NamedEntityFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new NamedEntityFormatter();
    }

    @Nested
    class Format {

        @Test
        void shouldFormatSetOfNamesToSortedCommaSeparatedString() {
            Set<String> names = new LinkedHashSet<>(Set.of("Charlie", "Alice", "Bob"));

            String result = formatter.format(names, s -> s);

            assertThat(result).isEqualTo("Alice, Bob, Charlie");
        }

        @Test
        void shouldReturnEmptyStringForEmptySet() {
            String result = formatter.format(Collections.emptySet(), Object::toString);

            assertThat(result).isEmpty();
        }

        @Test
        void shouldReturnEmptyStringForNullSet() {
            String result = formatter.format(null, Object::toString);

            assertThat(result).isEmpty();
        }

        @Test
        void shouldFormatSingleElement() {
            Set<String> names = Set.of("Alice");

            String result = formatter.format(names, s -> s);

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        void shouldExtractNameUsingProvidedFunction() {
            record Named(String name) {}
            Set<Named> entities = Set.of(new Named("Zoe"), new Named("Anna"));

            String result = formatter.format(entities, Named::name);

            assertThat(result).isEqualTo("Anna, Zoe");
        }
    }
}

