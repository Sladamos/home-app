package com.sladamos.book.functions;

import com.sladamos.book.Book;
import com.sladamos.book.BookStatus;
import com.sladamos.book.dto.PutBookRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class RequestToBookFunction implements BiFunction<UUID, PutBookRequest, Book> {
    @Override
    public Book apply(UUID id, PutBookRequest request) {
        return Book.builder()
                .id(id)
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publisher(request.getPublisher())
                .description(request.getDescription())
                .pages(request.getPages())
                .coverImage(request.getCoverImage())
                .authors(request.getAuthors())
                .genres(request.getGenres())
                .lentTo(request.getLentTo())
                .rating(request.getRating())
                .isFavorite(request.isFavorite())
                .creationDate(Instant.now())
                .modificationDate(Instant.now())
                .readDate(request.getReadDate())
                .status(Optional.ofNullable(request.getStatus()).map(BookStatus::valueOf).orElse(null))
                .build();
    }
}
