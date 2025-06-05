package com.sladamos.book.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatchBookRequest {
    private final String title;
    private final String isbn;
    private final String publisher;
    private final String description;
    private final Integer pages;
    private final byte[] coverImage;
    private final List<String> authors;
    private final List<String> genres;
}
