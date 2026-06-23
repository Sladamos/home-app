package com.sladamos.common.string;

import com.sladamos.common.exception.DuplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class StringDuplicatorTest {

    private final StringDuplicator stringDuplicator = new StringDuplicator();

    @ParameterizedTest(name = "\"{0}\" -> \"{2}\"")
    @MethodSource("duplicateStringWithCounterScenarios")
    void shouldDuplicateBookWithCorrectTitle(String source, List<String> stringList, String expected) throws DuplicationException {
        String duplicated = stringDuplicator.duplicateStringWithCounter(source, stringList);

        assertThat(duplicated).isEqualTo(expected);
    }

    @Test
    void shouldThrowDuplicationExceptionWhenSourceTitleIsMissingFromExistingTitles() {
        String source = "Eragon";
        List<String> existingStrings = List.of("Other");

        assertThatThrownBy(() -> stringDuplicator.duplicateStringWithCounter(source, existingStrings))
                .isInstanceOf(DuplicationException.class)
                .hasMessageContaining("Eragon");
    }

    private static Stream<Arguments> duplicateStringWithCounterScenarios() {
        return Stream.of(
                Arguments.of("Eragon", List.of("Eragon"), "Eragon (1)"),
                Arguments.of("Eragon", List.of("Eragon", "Eragon (1)"), "Eragon (2)"),
                Arguments.of("Eragon (1)", List.of("Eragon", "Eragon (1)", "Eragon (2)"), "Eragon (3)"),
                Arguments.of("Eragon (1)", List.of("Eragon (1)", "Eragon (2)"), "Eragon (3)"),
                Arguments.of("Eragon (1)", List.of("Eragon (1)", "Eragon (3)"), "Eragon (4)"),
                Arguments.of("Eragon(3)test", List.of("Eragon(3)test"), "Eragon(3)test (1)"),
                Arguments.of("Eragontest (3)", List.of("Eragontest (3)"), "Eragontest (4)")
        );
    }
}