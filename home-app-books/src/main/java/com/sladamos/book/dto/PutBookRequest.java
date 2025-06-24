package com.sladamos.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String lentTo;
    private String status;
    private Integer pages;
    private Integer rating;
    private byte[] coverImage;
    private boolean favorite;
    private LocalDate readDate;
    private List<String> authors;
    private List<String> genres;
}
