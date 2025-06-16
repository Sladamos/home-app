package com.sladamos.book.dto;

import lombok.*;

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
        private Integer pages;
        private byte[] coverImage;
        private List<String> authors;
        private List<String> genres;
    }

    @Singular
    private List<Book> books;
}
