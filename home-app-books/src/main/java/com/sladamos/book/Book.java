package com.sladamos.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must have 10 or 13 digits")
    private String isbn;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private String publisher;

    private String borrowedTo;

    @PositiveOrZero(message = "Number of pages cannot be negative")
    private Integer pages;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @Lob
    private byte[] coverImage;

    private boolean isFavorite;

    private Instant creationDate;

    private Instant modificationDate;

    private LocalDate readDate;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<@NotBlank String> authors;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<@NotBlank String> genres;
}
