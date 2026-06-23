package com.sladamos.book.functions;

import com.sladamos.book.dto.GetBookResponse;
import com.sladamos.book.model.AuthorEntity;
import com.sladamos.book.model.BookEntity;
import com.sladamos.book.model.GenreEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
public class BookToResponseFunction implements Function<BookEntity, GetBookResponse> {

    @Override
    public GetBookResponse apply(BookEntity book) {
        return GetBookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .pages(book.getPages())
                .coverImage(book.getCoverImage())
                .description(book.getDescription())
                .borrowedBy(book.getBorrowedBy())
                .rating(book.getRating())
                .favorite(book.isFavorite())
                .creationDate(book.getCreationDate())
                .modificationDate(book.getModificationDate())
                .readDate(book.getReadDate())
                .genres(Optional.ofNullable(book.getGenres()).orElse(Set.of()).stream().map(GenreEntity::getName).toList())
                .authors(Optional.ofNullable(book.getAuthors()).orElse(Set.of()).stream().map(AuthorEntity::getName).toList())
                .status(Optional.ofNullable(book.getStatus()).map(Enum::name).orElse(null))
                .build();
    }
}
