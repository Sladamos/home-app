package com.sladamos.book.functions;

import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.BookStatus;
import com.sladamos.book.model.Genre;
import com.sladamos.book.dto.PutBookRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class RequestToBookFunction implements BiFunction<UUID, PutBookRequest, Book> {
    @Override
    public Book apply(UUID id, PutBookRequest request) {
        LocalDateTime currentTime = LocalDateTime.now();
        return Book.builder()
                .id(id)
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publisher(request.getPublisher())
                .description(request.getDescription())
                .pages(request.getPages())
                .coverImage(request.getCoverImage())
                .authors(toAuthors(request.getAuthors()))
                .genres(toGenres(request.getGenres()))
                .borrowedBy(request.getBorrowedBy())
                .rating(request.getRating())
                .favorite(request.isFavorite())
                .creationDate(currentTime)
                .modificationDate(currentTime)
                .readDate(request.getReadDate())
                .status(Optional.ofNullable(request.getStatus()).map(BookStatus::valueOf).orElse(BookStatus.ON_SHELF))
                .build();
    }

    private Set<Author> toAuthors(List<String> names) {
        if (names == null) return Set.of();
        return names.stream()
                .filter(n -> n != null && !n.isBlank())
                .map(Author::new)
                .collect(Collectors.toSet());
    }

    private Set<Genre> toGenres(List<String> names) {
        if (names == null) return Set.of();
        return names.stream()
                .filter(n -> n != null && !n.isBlank())
                .map(Genre::new)
                .collect(Collectors.toSet());
    }
}
