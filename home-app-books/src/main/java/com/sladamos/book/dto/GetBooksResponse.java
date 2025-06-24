package com.sladamos.book.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBooksResponse {

    @Data
    @Builder
    public static class Book {
        private UUID id;
        private String title;
        private String isbn;
        private String publisher;
        private String description;
        private String borrowedBy;
        private String status;
        private Integer pages;
        private Integer rating;
        private boolean favorite;
        private Instant creationDate;
        private Instant modificationDate;
        private LocalDate readDate;
        private List<String> authors;
        private List<String> genres;
        private byte[] coverImage;
    }

    @Singular
    private List<Book> books;
}
