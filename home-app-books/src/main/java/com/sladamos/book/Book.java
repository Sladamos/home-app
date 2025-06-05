package com.sladamos.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String publisher;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @PositiveOrZero(message = "Number of pages cannot be negative")
    private Integer pages;

    @Lob
    private byte[] coverImage;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<@NotBlank String> authors;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<@NotBlank String> genres;
}
