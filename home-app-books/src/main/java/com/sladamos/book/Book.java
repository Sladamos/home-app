package com.sladamos.book;

import com.sladamos.book.validators.BorrowedByRequired;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@BorrowedByRequired(message = "book.validation.borrowedBy")
public class Book {

    public static final int MAX_RATING = 5;

    public static final int MIN_RATING = 0;

    public static final int MIN_NUMBER_OF_AUTHORS = 1;

    public static final int MIN_NUMBER_OF_GENRES = 0;

    public static final int MAX_DESCRIPTION_SIZE = 300;

    public static final int MAX_COVER_WIDTH = 200;

    public static final int MAX_COVER_HEIGHT = 300;

    @Id
    private UUID id;

    @NotBlank(message = "book.validation.title")
    private String title;

    @Pattern(regexp = "(\\d{10}|\\d{13})?", message = "book.validation.isbn")
    private String isbn;

    @Size(max = MAX_DESCRIPTION_SIZE, message = "book.validation.description")
    private String description;

    private String publisher;

    private String borrowedBy;

    @PositiveOrZero(message = "book.validation.pages")
    private Integer pages;

    @Min(value = MIN_RATING, message = "book.validation.rating.min")
    @Max(value = MAX_RATING, message = "book.validation.rating.max")
    private Integer rating;

    @Lob
    private byte[] coverImage;

    private boolean favorite;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    @PastOrPresent(message = "book.validation.readDate")
    private LocalDate readDate;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author")
    @Size(min = MIN_NUMBER_OF_AUTHORS, message = "book.validation.authors.min")
    private List<@NotBlank(message = "book.validation.authors.notBlank") String> authors;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<@NotBlank(message = "book.validation.genres.notBlank") String> genres;
}
