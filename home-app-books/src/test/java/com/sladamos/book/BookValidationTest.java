package com.sladamos.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
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

    private ValidatorFactory factory;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void shouldNotReturnValidationErrorForValidBook() {
        Book book = createValidBook();

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnValidationErrorWhenTitleIsBlank() {
        Book book = createValidBook();
        book.setTitle(" ");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && "book.validation.title".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenIsbnIsInvalid() {
        Book book = createValidBook();
        book.setIsbn("12345");

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("isbn")
                        && "book.validation.isbn".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenDescriptionIsTooLong() {
        Book book = createValidBook();
        book.setDescription("a".repeat(301));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && "book.validation.description".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenPagesIsNegative() {
        Book book = createValidBook();
        book.setPages(-5);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("pages")
                        && "book.validation.pages".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorsListIsEmpty() {
        Book book = createValidBook();
        book.setAuthors(List.of());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("authors")
                        && "book.validation.authors.min".equals(v.getMessage()));
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

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("authors")
                        && "book.validation.authors.notBlank".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenGenreIsBlank() {
        Book book = createValidBook();
        book.setGenres(List.of(" "));

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("genres")
                        && "book.validation.genres.notBlank".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingIsNegative() {
        Book book = createValidBook();
        book.setRating(-1);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("rating")
                        && "book.validation.rating.min".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingExceedsFive() {
        Book book = createValidBook();
        book.setRating(6);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("rating")
                        && "book.validation.rating.max".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenBorrowedBookDoesNotHaveBorrower() {
        Book book = createValidBook();
        book.setBorrowedBy("");
        book.setStatus(BookStatus.BORROWED);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("borrowedBy")
                && "book.validation.borrowedBy".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenReadDateIsFromFuture() {
        Book book = createValidBook();
        LocalDate readDate = LocalDate.now().plusDays(1);
        book.setReadDate(readDate);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("readDate")
                        && "book.validation.readDate".equals(v.getMessage()));
    }

    private Book createValidBook() {
        return Book.builder()
                .id(UUID.randomUUID())
                .title("Valid Title")
                .isbn("")
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