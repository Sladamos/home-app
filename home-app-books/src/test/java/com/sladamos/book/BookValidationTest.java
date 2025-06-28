package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BookValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldReturnValidationErrorWhenTitleIsBlank() {
        Book book = createValidBook();
        book.setTitle(" ");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getMessage().contains("cannot be blank"));
    }

    @Test
    void shouldReturnValidationErrorWhenIsbnIsInvalid() {
        Book book = createValidBook();
        book.setIsbn("12345");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("isbn")
                        && v.getMessage().contains("10 or 13 digits"));
    }

    @Test
    void shouldReturnValidationErrorWhenDescriptionIsTooLong() {
        Book book = createValidBook();
        book.setDescription("a".repeat(301));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && v.getMessage().contains("cannot exceed 300 characters"));
    }

    @Test
    void shouldReturnValidationErrorWhenPagesIsNegative() {
        Book book = createValidBook();
        book.setPages(-5);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("pages")
                        && v.getMessage().contains("cannot be negative"));
    }

    @Test
    void shouldNotReturnValidationErrorWhenAuthorsListIsEmpty() {
        Book book = createValidBook();
        book.setAuthors(List.of());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotReturnValidationErrorWhenGenresListIsEmpty() {
        Book book = createValidBook();
        book.setGenres(List.of());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorIsBlank() {
        Book book = createValidBook();
        book.setAuthors(List.of(" "));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("authors"));
    }

    @Test
    void shouldReturnValidationErrorWhenGenreIsBlank() {
        Book book = createValidBook();
        book.setGenres(List.of(" "));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("genres"));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingIsLowerThanOne() {
        Book book = createValidBook();
        book.setRating(0);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("rating"));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingExceedsFive() {
        Book book = createValidBook();
        book.setRating(6);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("rating"));
    }

    private Book createValidBook() {
        return Book.builder()
                .id(UUID.randomUUID())
                .title("Valid Title")
                .isbn("1234567890")
                .publisher("Valid Publisher")
                .description("This is a valid description.")
                .pages(100)
                .coverImage(new byte[]{})
                .rating(3)
                .creationDate(Instant.now())
                .modificationDate(Instant.now())
                .readDate(LocalDate.now())
                .status(BookStatus.ON_SHELF)
                .authors(List.of("Author One", "Author Two"))
                .genres(List.of("Genre One", "Genre Two"))
                .build();
    }
}