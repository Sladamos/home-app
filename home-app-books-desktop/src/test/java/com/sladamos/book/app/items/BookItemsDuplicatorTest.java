package com.sladamos.book.app.items;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookItemsDuplicatorTest {

    private BookItemsDuplicator duplicator;

    @BeforeEach
    void setUp() {
        duplicator = new BookItemsDuplicator();
    }

    @Nested
    class DuplicateTitle {

        @ParameterizedTest(name = "\"{0}\" in {1} → \"{2}\"")
        @MethodSource("titleScenarios")
        void shouldGenerateCorrectTitle(String inputTitle, List<String> existingTitles, String expectedTitle) {
            Book book = createBook(inputTitle);

            Book result = duplicator.duplicate(book, existingTitles);

            assertThat(result.getTitle()).isEqualTo(expectedTitle);
        }

        private static Stream<Arguments> titleScenarios() {
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

    @Nested
    class DuplicateValidation {

        @Test
        void shouldThrowWhenBookNotInExistingTitles() {
            Book book = createBook("Ganja(1)Kwi");

            assertThatThrownBy(() -> duplicator.duplicate(book, List.of("Ganja", "Ganja (1)", "Other")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ganja(1)Kwi");
        }

        @Test
        void shouldThrowWhenExistingTitlesEmpty() {
            Book book = createBook("Test");

            assertThatThrownBy(() -> duplicator.duplicate(book, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Test");
        }
    }

    @Nested
    class DuplicateFields {

        @Test
        void shouldGenerateNewUUID() {
            Book book = createBook("Test Book");

            Book result = duplicator.duplicate(book, List.of("Test Book"));

            assertThat(result.getId()).isNotEqualTo(book.getId());
            assertThat(result.getId()).isNotNull();
        }

        @Test
        void shouldSetNewCreationDate() {
            LocalDateTime oldDate = LocalDateTime.of(2020, 1, 1, 0, 0);
            Book book = createBook("Test").toBuilder().creationDate(oldDate).build();

            Book result = duplicator.duplicate(book, List.of("Test"));

            assertThat(result.getCreationDate()).isAfter(oldDate);
        }

        @Test
        void shouldSetNewModificationDate() {
            LocalDateTime oldDate = LocalDateTime.of(2020, 1, 1, 0, 0);
            Book book = createBook("Test").toBuilder().modificationDate(oldDate).build();

            Book result = duplicator.duplicate(book, List.of("Test"));

            assertThat(result.getModificationDate()).isAfter(oldDate);
        }

        @Test
        void shouldPreserveOtherFields() {
            Book book = createBook("Test Book");

            Book result = duplicator.duplicate(book, List.of("Test Book"));

            assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
            assertThat(result.getDescription()).isEqualTo(book.getDescription());
            assertThat(result.getPublisher()).isEqualTo(book.getPublisher());
            assertThat(result.getPages()).isEqualTo(book.getPages());
            assertThat(result.getRating()).isEqualTo(book.getRating());
            assertThat(result.getStatus()).isEqualTo(book.getStatus());
            assertThat(result.getAuthors()).isEqualTo(book.getAuthors());
            assertThat(result.getGenres()).isEqualTo(book.getGenres());
        }
    }

    private Book createBook(String title) {
        LocalDateTime now = LocalDateTime.now();
        return Book.builder()
                .id(UUID.randomUUID())
                .title(title)
                .isbn("1234567890")
                .description("A test description")
                .publisher("Test Publisher")
                .borrowedBy("")
                .pages(200)
                .rating(3)
                .favorite(false)
                .status(BookStatus.ON_SHELF)
                .coverImage(new byte[]{1, 2, 3})
                .readDate(LocalDate.now())
                .creationDate(now)
                .modificationDate(now)
                .authors(Set.of(new Author("Author")))
                .genres(Set.of(new Genre("Genre")))
                .build();
    }
}
