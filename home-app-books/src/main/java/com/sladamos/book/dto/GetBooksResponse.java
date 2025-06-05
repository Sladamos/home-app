package com.sladamos.book.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetBooksResponse {

    @Data
    @Builder
    public static class Book {
        private final UUID id;
        private final String title;
        private final String isbn;
        private final String publisher;
        private final String description;
        private final Integer pages;
        private final byte[] coverImage;
        private final List<String> authors;
        private final List<String> genres;
    }

    @Singular
    private List<Book> books;
}
