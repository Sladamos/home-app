package com.sladamos.book.functions;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.dto.PatchBookRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;

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
                .authors(Optional.ofNullable(patchBookRequest.getAuthors()).orElse(entity.getAuthors()))
                .genres(Optional.ofNullable(patchBookRequest.getGenres()).orElse(entity.getGenres()))
                .lentTo(Optional.ofNullable(patchBookRequest.getLentTo()).orElse(entity.getLentTo()))
                .rating(Optional.ofNullable(patchBookRequest.getRating()).orElse(entity.getRating()))
                .isFavorite(Optional.ofNullable(patchBookRequest.getIsFavorite()).orElse(entity.isFavorite()))
                .creationDate(entity.getCreationDate())
                .modificationDate(Instant.now())
                .readDate(Optional.ofNullable(patchBookRequest.getReadDate()).orElse(entity.getReadDate()))
                .status(Optional.ofNullable(patchBookRequest.getStatus())
                        .map(BookStatus::valueOf)
                        .orElse(entity.getStatus()))
                .build();
    }
}