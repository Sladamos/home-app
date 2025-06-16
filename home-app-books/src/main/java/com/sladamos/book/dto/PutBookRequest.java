package com.sladamos.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutBookRequest {
    private String title;
    private String isbn;
    private String publisher;
    private String description;
    private Integer pages;
    private byte[] coverImage;
    private List<String> authors;
    private List<String> genres;
}
