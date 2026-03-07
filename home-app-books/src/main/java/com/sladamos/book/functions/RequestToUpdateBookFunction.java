package com.sladamos.book.functions;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import com.sladamos.book.dto.PatchBookRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class RequestToUpdateBookFunction implements BiFunction<Book, PatchBookRequest, Book> {

    @Override
    public Book apply(Book entity, PatchBookRequest patchBookRequest) {
        return Book.builder()
                .id(entity.getId())
                .title(Optional.ofNullable(patchBookRequest.getTitle()).orElse(entity.getTitle()))
                .isbn(Optional.ofNullable(patchBookRequest.getIsbn()).orElse(entity.getIsbn()))
                .publisher(Optional.ofNullable(patchBookRequest.getPublisher()).orElse(entity.getPublisher()))
                .description(Optional.ofNullable(patchBookRequest.getDescription()).orElse(entity.getDescription()))
                .pages(Optional.ofNullable(patchBookRequest.getPages()).orElse(entity.getPages()))
                .coverImage(Optional.ofNullable(patchBookRequest.getCoverImage()).orElse(entity.getCoverImage()))
                .authors(Optional.ofNullable(patchBookRequest.getAuthors()).map(this::toAuthors).orElse(entity.getAuthors()))
                .genres(Optional.ofNullable(patchBookRequest.getGenres()).map(this::toGenres).orElse(entity.getGenres()))
                .borrowedBy(Optional.ofNullable(patchBookRequest.getBorrowedBy()).orElse(entity.getBorrowedBy()))
                .rating(Optional.ofNullable(patchBookRequest.getRating()).orElse(entity.getRating()))
                .favorite(Optional.ofNullable(patchBookRequest.getFavorite()).orElse(entity.isFavorite()))
                .creationDate(entity.getCreationDate())
                .modificationDate(LocalDateTime.now())
                .readDate(Optional.ofNullable(patchBookRequest.getReadDate()).orElse(entity.getReadDate()))
                .status(Optional.ofNullable(patchBookRequest.getStatus())
                        .map(BookStatus::valueOf)
                        .orElse(entity.getStatus()))
                .build();
    }

    private List<Author> toAuthors(List<String> names) {
        if (names == null) return List.of();
        return names.stream()
                .filter(n -> n != null && !n.isBlank())
                .map(Author::new)
                .collect(Collectors.toList());
    }

    private List<Genre> toGenres(List<String> names) {
        if (names == null) return List.of();
        return names.stream()
                .filter(n -> n != null && !n.isBlank())
                .map(Genre::new)
                .collect(Collectors.toList());
    }
}