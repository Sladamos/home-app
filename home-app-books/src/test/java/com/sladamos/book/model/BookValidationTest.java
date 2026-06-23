package com.sladamos.book.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
        BookEntity book = createValidBook();

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnValidationErrorWhenTitleIsBlank() {
        BookEntity book = createValidBook();
        book.setTitle(" ");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && "book.validation.title".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenIsbnIsInvalid() {
        BookEntity book = createValidBook();
        book.setIsbn("12345");

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("isbn")
                        && "book.validation.isbn".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenDescriptionIsTooLong() {
        BookEntity book = createValidBook();
        book.setDescription("a".repeat(301));

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")
                        && "book.validation.description".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenPagesIsNegative() {
        BookEntity book = createValidBook();
        book.setPages(-5);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("pages")
                        && "book.validation.pages".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorsListIsEmpty() {
        BookEntity book = createValidBook();
        book.setAuthors(Set.of());

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("authors")
                        && "book.validation.authors.min".equals(v.getMessage()));
    }

    @Test
    void shouldNotReturnValidationErrorWhenGenresListIsEmpty() {
        BookEntity book = createValidBook();
        book.setGenres(Set.of());

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnValidationErrorWhenAuthorIsBlank() {
        BookEntity book = createValidBook();
        book.setAuthors(Set.of(new AuthorEntity(" ")));

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("authors")
                        && "author.validation.name".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenGenreIsBlank() {
        BookEntity book = createValidBook();
        book.setGenres(Set.of(new GenreEntity(" ")));

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("genres")
                        && "genre.validation.name".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingIsNegative() {
        BookEntity book = createValidBook();
        book.setRating(-1);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("rating")
                        && "book.validation.rating.min".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenRatingExceedsFive() {
        BookEntity book = createValidBook();
        book.setRating(6);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("rating")
                        && "book.validation.rating.max".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenBorrowedBookDoesNotHaveBorrower() {
        BookEntity book = createValidBook();
        book.setBorrowedBy("");
        book.setStatus(BookStatus.BORROWED);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().contains("borrowedBy")
                && "book.validation.borrowedBy".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenReadDateIsFromFuture() {
        BookEntity book = createValidBook();
        LocalDate readDate = LocalDate.now().plusDays(1);
        book.setReadDate(readDate);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("readDate")
                        && "book.validation.readDate".equals(v.getMessage()));
    }

    @Test
    void shouldReturnValidationErrorWhenStatusIsNotSet() {
        BookEntity book = createValidBook();
        book.setStatus(null);

        Set<ConstraintViolation<BookEntity>> violations = validator.validate(book);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("status")
                        && "book.validation.status".equals(v.getMessage()));
    }

    private BookEntity createValidBook() {
        LocalDateTime currentTime = LocalDateTime.now();
        return BookEntity.builder()
                .id(UUID.randomUUID())
                .title("Valid Title")
                .isbn("")
                .publisher("Valid Publisher")
                .description("This is a valid description.")
                .pages(100)
                .coverImage(new byte[]{})
                .rating(3)
                .creationDate(currentTime)
                .modificationDate(currentTime)
                .readDate(LocalDate.now())
                .status(BookStatus.ON_SHELF)
                .authors(Set.of(new AuthorEntity("Author One"), new AuthorEntity("Author Two")))
                .genres(Set.of(new GenreEntity("Genre One"), new GenreEntity("Genre Two")))
                .build();
    }
}