package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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
        Book book = validBook();
        book.setTitle(" ");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getMessage().contains("cannot be blank"));
    }

    @Test
    void shouldReturnValidationErrorWhenIsbnIsInvalid() {
        Book book = validBook();
        book.setIsbn("12345");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("isbn")
                        && v.getMessage().contains("10 or 13 digits"));
    }

    @Test
    void shouldReturnValidationErrorWhenDescriptionIsTooLong() {
        Book book = validBook();
        book.setDescription("a".repeat(2001));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && v.getMessage().contains("cannot exceed 2000 characters"));
    }

    @Test
    void shouldReturnValidationErrorWhenPagesIsNegative() {
        Book book = validBook();
        book.setPages(-5);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("pages")
                        && v.getMessage().contains("cannot be negative"));
    }

    @Test
    void shouldNotReturnValidationErrorWhenAuthorsListIsEmpty() {
        Book book = validBook();
        book.setAuthors(List.of());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotReturnValidationErrorWhenGenresListIsEmpty() {
        Book book = validBook();
        book.setGenres(List.of());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorIsBlank() {
        Book book = validBook();
        book.setAuthors(List.of(" "));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("authors"));
    }

    @Test
    void shouldReturnValidationErrorWhenGenreIsBlank() {
        Book book = validBook();
        book.setGenres(List.of(" "));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("genres"));
    }

    private Book validBook() {
        return Book.builder()
                .id(1L)
                .title("Valid Title")
                .isbn("1234567890")
                .publisher("Valid Publisher")
                .description("This is a valid description.")
                .pages(100)
                .coverImage(new byte[]{})
                .authors(List.of("Author One", "Author Two"))
                .genres(List.of("Genre One", "Genre Two"))
                .build();
    }
}