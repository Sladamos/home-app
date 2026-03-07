package com.sladamos.book.functions;

import com.sladamos.book.dto.GetBooksResponse;
import com.sladamos.book.model.Author;
import com.sladamos.book.model.Book;
import com.sladamos.book.model.Genre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class BooksToResponseFunction implements Function<List<Book>, GetBooksResponse> {
    @Override
    public GetBooksResponse apply(List<Book> books) {
        return GetBooksResponse.builder()
                .books(
                        books.stream()
                                .map(book -> GetBooksResponse.Book.builder()
                                        .id(book.getId())
                                        .title(book.getTitle())
                                        .isbn(book.getIsbn())
                                        .publisher(book.getPublisher())
                                        .authors(Optional.ofNullable(book.getAuthors()).orElse(List.of()).stream().map(Author::getName).toList())
                                        .pages(book.getPages())
                                        .coverImage(book.getCoverImage())
                                        .description(book.getDescription())
                                        .genres(Optional.ofNullable(book.getGenres()).orElse(List.of()).stream().map(Genre::getName).toList())
                                        .borrowedBy(book.getBorrowedBy())
                                        .rating(book.getRating())
                                        .favorite(book.isFavorite())
                                        .creationDate(book.getCreationDate())
                                        .modificationDate(book.getModificationDate())
                                        .readDate(book.getReadDate())
                                        .status(Optional.ofNullable(book.getStatus()).map(Enum::name).orElse(null))
                                        .build())
                                .toList()
                )
                .build();
    }
}
