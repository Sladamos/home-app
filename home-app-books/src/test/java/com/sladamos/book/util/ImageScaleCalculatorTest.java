package com.sladamos.book.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.sladamos.book.Book.MAX_COVER_HEIGHT;
import static com.sladamos.book.Book.MAX_COVER_WIDTH;
import static org.assertj.core.api.Assertions.assertThat;

class ImageScaleCalculatorTest {

    private final ImageScaleCalculator imageScaleCalculator = new ImageScaleCalculator();

    @ParameterizedTest
    @MethodSource("shouldCalculateScaleProperlyArgs")
    void shouldCalculateScaleProperly(int width, int height, double expectedScale) {
        double scale = imageScaleCalculator.calculateScale(width, height);

        assertThat(scale).isEqualTo(expectedScale);
    }

    public static Stream<Arguments> shouldCalculateScaleProperlyArgs() {
        return Stream.of(
                Arguments.of(MAX_COVER_WIDTH, MAX_COVER_HEIGHT, 1.0),
                Arguments.of(MAX_COVER_WIDTH, 2 * MAX_COVER_HEIGHT, 1.0),
                Arguments.of(2 * MAX_COVER_WIDTH, MAX_COVER_HEIGHT, 1.0),
                Arguments.of(MAX_COVER_WIDTH / 4, MAX_COVER_HEIGHT / 2, 4),
                Arguments.of(MAX_COVER_WIDTH * 2, MAX_COVER_HEIGHT * 3, 0.5)
        );
    }

}