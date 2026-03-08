package com.sladamos.book.app.util;

import com.sladamos.book.model.BookStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatusMessageKeyProviderTest {

    private final StatusMessageKeyProvider provider = new StatusMessageKeyProvider();

    @ParameterizedTest
    @CsvSource({
            "ON_SHELF, books.items.status.onShelf",
            "WANT_TO_READ, books.items.status.wantToRead",
            "CURRENTLY_READING, books.items.status.currentlyReading",
            "FINISHED_READING, books.items.status.finishedReading",
            "BORROWED, books.items.status.borrowed"
    })
    void shouldReturnCorrectDisplayStatusMessageKey(BookStatus status, String expectedKey) {
        String result = provider.getDisplayStatusMessageKey(status);

        assertThat(result).isEqualTo(expectedKey);
    }

    @ParameterizedTest
    @CsvSource({
            "ON_SHELF, books.selectStatus.onShelf",
            "WANT_TO_READ, books.selectStatus.wantToRead",
            "CURRENTLY_READING, books.selectStatus.currentlyReading",
            "FINISHED_READING, books.selectStatus.finishedReading",
            "BORROWED, books.selectStatus.borrowed"
    })
    void shouldReturnCorrectAddStatusMessageKey(BookStatus status, String expectedKey) {
        String result = provider.getAddStatusMessageKey(status);

        assertThat(result).isEqualTo(expectedKey);
    }
}