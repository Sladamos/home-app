package com.sladamos.book.model;

import com.sladamos.book.validators.BorrowedByRequired;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
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

    @Valid
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Size(min = MIN_NUMBER_OF_AUTHORS, message = "book.validation.authors.min")
    private Set<Author> authors;

    @Valid
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    public void replace(Book book) {
        this.title = book.getTitle();
        this.isbn = book.getIsbn();
        this.description = book.getDescription();
        this.publisher = book.getPublisher();
        this.borrowedBy = book.getBorrowedBy();
        this.pages = book.getPages();
        this.rating = book.getRating();
        this.favorite = book.isFavorite();
        this.readDate = book.getReadDate();
        this.status = book.getStatus();
        this.coverImage = book.getCoverImage();
        this.modificationDate = LocalDateTime.now();
        if (this.authors != null) {
            this.authors.clear();
            this.authors.addAll(book.getAuthors());
        } else {
            this.authors = book.getAuthors();
        }
        if (this.genres != null) {
            this.genres.clear();
            this.genres.addAll(book.getGenres());
        } else {
            this.genres = book.getGenres();
        }
    }
}
