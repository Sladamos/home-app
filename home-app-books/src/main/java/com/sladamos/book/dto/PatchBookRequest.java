package com.sladamos.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchBookRequest {
    private String title;
    private String isbn;
    private String publisher;
    private String description;
    private String borrowedBy;
    private String status;
    private Integer pages;
    private Integer rating;
    private byte[] coverImage;
    private Boolean favorite;
    private LocalDate readDate;
    private Set<String> authors;
    private Set<String> genres;
}
